import logging as log
import traceback
from typing import List, Set, Dict, AnyStr
from time import time as ts
from git import Commit
from pydriller import Repository

from szz.core.abstract_szz import AbstractSZZ, ImpactedFile


class AGSZZ(AbstractSZZ):
    """
    Annotation-Graph SZZ implementation.
    """

    def __init__(self, repo_full_name: str, repo_url: str, repos_dir: str = None):
        super().__init__(repo_full_name, repo_url, repos_dir)

    def _exclude_commits_by_change_size(self, commit_hash: str, max_change_size: int = 20) -> Set[str]:
        to_exclude = set()
        repo_mining = Repository(self.repository_path, to_commit=commit_hash, order='reverse').traverse_commits()
        for commit in repo_mining:
            try:
                if len(commit.modified_files) > max_change_size:
                    to_exclude.add(commit.hash)
                else:
                    break
            except Exception as e:
                log.error(f'unable to analyze commit: {self.repository_path} {commit.hash}')

        if len(to_exclude) > 0:
            log.info(f'count of commits excluded by change size > {max_change_size}: {len(to_exclude)}')

        return to_exclude

    def _ag_annotate(self, impacted_files, **kwargs) -> Set[Commit]:
        blame_data = set()
        for imp_file in impacted_files:
            try:
                blame_info = self._blame(
                    rev='HEAD^',
                    file_path=imp_file.file_path,
                    modified_lines=imp_file.modified_lines,
                    ignore_whitespaces=True,
                    skip_comments=True,
                    **kwargs
                )
                blame_data.update(blame_info)
            except:
                print(traceback.format_exc())
        return blame_data

    def dict_ag_annotate(self, impacted_files, **kwargs) -> Dict[AnyStr, Dict[Commit, List]]:
        blame_data = dict()
        for imp_file in impacted_files:
            try:
                blame_info = self._blame(
                    rev='HEAD^',
                    file_path=imp_file.file_path,
                    modified_lines=imp_file.modified_lines,
                    ignore_whitespaces=True,
                    skip_comments=True,
                    **kwargs
                )
                if len(blame_info) == 0:
                    continue
                imp_file_info = {imp_file.file_path: blame_info}
                blame_data.update(imp_file_info)
            except:
                print(traceback.format_exc())
        return blame_data

    # TODO: add type check on kwargs
    def find_bic(self, fix_commit_hash: str, impacted_files: List['ImpactedFile'], **kwargs) -> Set[Commit]:
        """
        Find bug introducing commits candidates.

        :param str fix_commit_hash: hash of fix commit to scan for buggy commits
        :param List[ImpactedFile] impacted_files: list of impacted files in fix commit
        :key ignore_revs_file_path (str): specify ignore revs file for git blame to ignore specific commits.
        :key max_change_size (int): if the number of modified files exceeds the threshold, the commit will be excluded (default 20)
        :key exclude_merge_commits (bool): if true, merge commits will be excluded (default False)
        :returns Set[Commit] a set of bug introducing commits candidates, represented by Commit object
        """

        log.info(f"find_bic() kwargs: {kwargs}")

        self._set_working_tree_to_commit(fix_commit_hash)

        max_change_size = kwargs.get('max_change_size', 20)

        params = dict()
        params['ignore_revs_file_path'] = kwargs.get('ignore_revs_file_path', None)
        params['ignore_revs_list'] = list()

        log.info("staring blame")
        to_blame = True
        start = ts()
        blame_data = list()
        commits_to_ignore = set()
        while to_blame:
            log.info(f"excluding commits: {params['ignore_revs_list']}")
            blame_data = self._ag_annotate(impacted_files, **params)

            new_commits_to_ignore = set()
            for bd in blame_data:
                if bd.commit.hexsha not in new_commits_to_ignore:
                    if bd.commit.hexsha not in commits_to_ignore:
                        new_commits_to_ignore.update(self._exclude_commits_by_change_size(bd.commit.hexsha, max_change_size=max_change_size))

            if len(new_commits_to_ignore) == 0:
                to_blame = False
            elif ts() - start > (60 * 60 * 1):  # 1 hour max time
                log.error(f"blame timeout for {self.repository_path}")
                to_blame = False

            commits_to_ignore.update(new_commits_to_ignore)
            params['ignore_revs_list'] = list(commits_to_ignore)

        bic = set([bd.commit for bd in blame_data if bd.commit.hexsha not in self._exclude_commits_by_change_size(bd.commit.hexsha, max_change_size)])
    
        if 'issue_date_filter' in kwargs and kwargs['issue_date_filter']:
            before = len(bic)
            bic = [c for c in bic if c.authored_date <= kwargs['issue_date']]
            log.info(f'Filtering by issue date returned {len(bic)} out of {before}')
        else:
            log.info("Not filtering by issue date.")
        
        return bic

    # TODO: add type check on kwargs
    def find_bic_dict(self, fix_commit_hash: str, impacted_files: List['ImpactedFile'], **kwargs) \
            -> Dict[AnyStr, Set[Commit]]:
        """
        Find bug introducing commits candidates.

        :param str fix_commit_hash: hash of fix commit to scan for buggy commits
        :param List[ImpactedFile] impacted_files: list of impacted files in fix commit
        :key ignore_revs_file_path (str): specify ignore revs file for git blame to ignore specific commits.
        :key max_change_size (int): if the number of modified files exceeds the threshold, the commit will be excluded (default 20)
        :key exclude_merge_commits (bool): if true, merge commits will be excluded (default False)
        :returns Set[Commit] a set of bug introducing commits candidates, represented by Commit object
        """

        log.info(f"find_bic() kwargs: {kwargs}")

        self._set_working_tree_to_commit(fix_commit_hash)

        max_change_size = kwargs.get('max_change_size', 20)

        params = dict()
        params['ignore_revs_file_path'] = kwargs.get('ignore_revs_file_path', None)
        params['ignore_revs_list'] = list()

        log.info("staring blame")
        to_blame = True
        start = ts()
        blame_data = dict()
        commits_to_ignore = set()
        while to_blame:
            log.info(f"excluding commits: {params['ignore_revs_list']}")
            blame_data = self.dict_ag_annotate(impacted_files, **params)

            # Dict[AnyStr, Dict[Commit, List]]
            new_commits_to_ignore = set()
            for file in blame_data.keys():
                for bd in blame_data[file].keys():
                    if bd.commit.hexsha not in new_commits_to_ignore:
                        if bd.commit.hexsha not in commits_to_ignore:
                            new_commits_to_ignore.update(
                                self._exclude_commits_by_change_size(bd.commit.hexsha, max_change_size=max_change_size))

            if len(new_commits_to_ignore) == 0:
                to_blame = False
            elif ts() - start > (60 * 60 * 1):  # 1 hour max time
                log.error(f"blame timeout for {self.repository_path}")
                to_blame = False

            commits_to_ignore.update(new_commits_to_ignore)
            params['ignore_revs_list'] = list(commits_to_ignore)

        bic = dict() # Dict[file, Dict[Commit, StringList]]
        for file in blame_data.keys():
            for bd in blame_data[file].keys():
                if bd.commit.hexsha not in self._exclude_commits_by_change_size(bd.commit.hexsha, max_change_size):
                    if file in bic:
                        bic[file].update({bd.commit : blame_data[file][bd]})
                    else :
                        _set = {bd.commit : blame_data[file][bd]}
                        bic[file] = _set

        if 'issue_date_filter' in kwargs and kwargs['issue_date_filter']:
            before = len(bic)
            bic = [c for c in bic if c.authored_date <= kwargs['issue_date']]
            log.info(f'Filtering by issue date returned {len(bic)} out of {before}')
        else:
            #log.info("Not filtering by issue date.")

        return bic