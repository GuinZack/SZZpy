#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <ctype.h>
#include <sys/wait.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/shm.h>
#include <sys/mman.h>

int main(int argc, char *argv[]) {

    if (argc != 3) {
        printf("Usage: ./multi_pipe <input_file> <clone_directory>\n");
        exit(1);
    }
    int csv_count = 0;
    char * input_path = argv[1];
    char * clone_path = argv[2];
    char url[50];
    FILE* fp;
    fp = fopen(input_path, "r");
    pid_t pid;

    if (NULL == fp) {
        printf("file can't be opened \n");
        exit(1);
    }

    while (fgets(url, 100, fp) != NULL) { 
        csv_count++;
    }
    fclose(fp);
    FILE* fp2;
    fp2 = fopen(input_path, "r");
    for(int i = 0; i < csv_count; i++) {
        fgets(url, 100, fp2);
        pid = fork();
        printf("%d", 1);
        if (pid == 0) {
            printf("%d", 1);
            char * token = strtok(url, "/");
            char * proj_name;
            while( token != NULL ) {
                token = strtok(NULL, "/");
                strcpy(proj_name, token);
            }
            
            printf("Executing pipeline for %s\n", url);
            execl("make", "make","pipe","url=", url,"wp=",clone_path,"proj=",proj_name);
            exit(0);
        } else if (pid < 0) {
            printf("fork failed\n");
            exit(1);
        }
        
    }


      fclose(fp2);
    for (int i = 0; i < csv_count; i++) {
        wait(NULL);
    }
    return 0;
}

