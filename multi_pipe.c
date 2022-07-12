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
        printf("Usage: %s <input_file> <clone_directory>\n",argv[0]);
        exit(1);
    }
    int csv_count = 0;
    char * input_path = argv[1];
    char * clone_path = argv[2];
    char url[50];
    FILE* fp;
    fp = fopen(input_path, "r");

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
        if (url[strlen(url)-1] == '\n')
                url[strlen(url)-1] = '\0';
        char temp [50];
        strcpy(temp, url);
        pid_t pid = fork();
        if (pid == 0) {
            
            char * token = strtok(temp, "/");
            token = strtok(NULL, "/");
            token = strtok(NULL, "/");
            token = strtok(NULL, "/");
            
            
            
            
            printf("Executing pipeline for %s\n", url);
            char * command = malloc(sizeof(char) * (strlen(clone_path) + strlen(token) + 100));
            strcpy(command,"make pipe url=");
            strcat(command, url);
            strcat(command, " wp=");
            strcat(command, clone_path);
            strcat(command, " proj=");
            strcat(command, token);

            printf("%s\n",command);
            int r = system(command);
            return r;
            //execl("/Users/leechanggong/Project/KISTI2022/CPMiner/makefile", "make","pipe","url=", url,"wp=",clone_path,"proj=",token, NULL);
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

