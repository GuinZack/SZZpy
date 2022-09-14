import logging as log
from operator import attrgetter
from typing import List, Set, Dict, AnyStr

from git import Commit

from szz.core.abstract_szz import ImpactedFile
from szz.ma_szz import MASZZ


class RSZZ(MASZZ):
    """
    Recent-SZZ implementation.
    """

    def __init__(self, repo_full_name: str, repo_url: str, repos_dir: str = None):
        super().__init__(repo_full_name, repo_url, repos_dir)

    # TODO: add parse and type check on kwargs
    def find_bic(self, fix_commit_hash: str, impacted_files: List['ImpactedFile'], **kwargs) -> Dict[AnyStr, Set[Commit]]:
        bic_candidates = super().find_bic_dict(fix_commit_hash, impacted_files, **kwargs)
        latest_bic = dict()
        if len(bic_candidates) > 0:
            for file in bic_candidates.keys():
                for _bic_dict in bic_candidates.get(file):
                    _bic_candidates = _bic_dict.keys()
                    if len(_bic_candidates) > 0:
                        _latest_bic = max(_bic_candidates, key=attrgetter('committed_date'))
                        bic = {file: {_latest_bic : bic_candidates.get(file).get(_latest_bic)}}
                        latest_bic.update(bic)
                        log.info(f"selected bug introducing commit: {_latest_bic.hexsha}")

        return latest_bic
