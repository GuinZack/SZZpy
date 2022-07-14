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
    char url[100];

    FILE* fp;
    fp = fopen(argv[1], "r");

    if (NULL == fp) {
        printf("file can't be opened \n");
        exit(1);
    }

    while (fgets(url, 100, fp) != NULL) { 
        csv_count++;
    }
    fclose(fp);

    FILE* fp2;
    fp2 = fopen(argv[1], "r");
    for(int i = 0; i < csv_count; i++) {
        fgets(url, 100, fp2);
        pid_t pid = fork();
        if (pid == 0) {
            char temp [50];
            if (url[strlen(url)-1] == '\n') strncpy(temp, url, strlen(url)-2);
            else strcpy(temp, url);
            printf("Executing pipeline for %s\n", temp);
            char command[1000];
            strcpy(command,"make pipe url=");
            strcat(command, temp);
            strcat(command, " wp=");
            strcat(command, argv[2]);
            
            char * token = strtok(temp, "/");
            token = strtok(NULL, "/");
            token = strtok(NULL, "/");
            token = strtok(NULL, "/");
            
            strcat(command, " proj=");
            strcat(command, token);

            printf("%s\n\n",command);
            int r = system(command);
            return r;
        
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

