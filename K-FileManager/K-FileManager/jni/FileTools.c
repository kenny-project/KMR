#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <dirent.h>
#include <time.h>
#include <android/log.h>
#include <stdio.h>
#include <string.h>
#include <sys/stat.h>
#include <dirent.h>
#include <android/log.h>
#include "android/log.h"
//#ifdef __cplusplus
//extern "C" {
//#endif
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "NDK", __VA_ARGS__)
/**
 * File_Create_Dir--创建文件目录
 * 返回0--成功
 */
unsigned short File_Create_Dir(char* dirName) {
	if (dirName == NULL) {
		return 100;
	}
	return mkdir(dirName, S_IRWXU);
}

/**
 * File_ISExist--判断文件是否存在
 * 0存在  -1不存在
 */
unsigned short File_ISExist(char* path) {
	if (path == NULL) {
		return 100;
	}
	return access(path, 0);
}
/**
 * 获取对应文件的路径
 *  ENOENT         参数file_name指定的文件不存在
    ENOTDIR        路径中的目录存在但却非真正的目录
    ELOOP          欲打开的文件有过多符号连接问题，上限为16符号连接
    EFAULT         参数buf为无效指针，指向无法存在的内存空间
    EACCESS        存取文件时被拒绝
    ENOMEM         核心内存不足
    ENAMETOOLONG   参数file_name的路径名称太长
 */
struct stat mStat;//状态
struct stat File_stat(char* path)
{
    int result=stat(path, &mStat);
    return mStat;
}
int File_IsDirectory(char* path)
{
	if(path!=NULL)
	{
		mStat=File_stat(path);
	}
	return S_ISDIR (mStat.st_mode);
}
long File_getFileSize(char* path)
{
	if(path!=NULL)
	{
		mStat=File_stat(path);
	}
	return mStat.st_size;
}

/**
 * File_GetSize--得到文件长度
 * FileSize--返回文件长度
 */
unsigned short File_GetSize(FILE* FileHandle, unsigned long* FileSize) {
	if (FileHandle == NULL) {
		return 100;
	}
	fseek(FileHandle, 0L, SEEK_END);
	*FileSize = ftell(FileHandle);
	return 0;
}

/**
 * File_Seek--给文件偏移量
 * offset--偏移量
 * origin--偏移方向
 * SeekLen--返回已偏移的长度
 * 返回值0--成功,其他失败
 */
unsigned short File_Seek(FILE* FileHandle, long offset, short origin,
		unsigned long* SeekLen) {
	unsigned short seek_result = fseek(FileHandle, offset, origin);
	if (seek_result == 0) {
		*SeekLen = offset;
	}
	return seek_result;
}
/**
 * File_Delete--删除文件
 * 0--删除成功  -1--删除失败
 */
unsigned short File_Delete(char* name) {
	if (name == NULL) {
		return 100;
	}
	return remove(name);
}
unsigned short File_GetSizes(char* fileFolder, unsigned long* FileSize,
		long *FileCount) {
	FILE* fp = NULL;
	DIR *pDir = NULL;
	struct dirent *dmsg;
	char* szFileName=NULL;
	char* szFolderName=NULL;
	int result = 0;
	FILE *fpWFile=NULL;
	int nfileFolderSize=strlen(fileFolder);
	//LOGD("fdsa");
	//__android_log_print(ANDROID_LOG_INFO, "NDK", "%s", fileFolder);
	if ((pDir = opendir(fileFolder)) != NULL) {
		szFolderName=malloc(nfileFolderSize+10);
		szFileName=malloc(nfileFolderSize+512);
		strcpy(szFolderName, fileFolder);
		strcat(szFolderName, "/%s");
		// 遍历目录并删除文件
		while ((dmsg = readdir(pDir)) != NULL) {
			if (strcmp(dmsg->d_name, ".") == 0 || 0 == strcmp(dmsg->d_name, "..")) {
				continue;
			}
			if (dmsg->d_type == DT_DIR) {
				sprintf(szFileName, szFolderName, dmsg->d_name);
				File_GetSizes(szFileName, FileSize, FileCount);
				continue;
			} else {
				sprintf(szFileName, szFolderName, dmsg->d_name);
				fp = fopen(szFileName, "r");
				if (fp == NULL)
					continue;
				fseek(fp, 0L, SEEK_END);
				*FileSize += ftell(fp);
				*FileCount += 1;
				fclose(fp);

//				fpWFile = fopen("/mnt/sdcard/testNdk.txt", "at+");
//				fwrite(szFileName,1,strlen(szFileName),fpWFile);
//				fputs("\n",fpWFile);
//
//				fclose(fpWFile);

			}
			//free(dmsg);
		}
		closedir(pDir);
//		free(pDir);
	} else {
		fp = fopen(fileFolder, "r");
		if (fp == NULL) {
			return -1;
		}
		fseek(fp, 0L, SEEK_END);
		*FileSize += ftell(fp);
		*FileCount += 1;
		fclose(fp);
//		fpWFile = fopen("/mnt/sdcard/testNdk.txt", "at+");
//		fwrite(szFileName,1,strlen(szFileName),fpWFile);
//		fputs("\n",fpWFile);
//		fclose(fpWFile);
	}
	if(szFileName!=NULL)
	{
		free(szFileName);
	}
	if(szFolderName!=NULL)
	{
		free(szFolderName);
	}
	return 1;
}

// 删除指定文件夹内所有文件
int File_DeleteFiles(char* fileFolder) {
	DIR *pDir = NULL;
	struct dirent *dmsg;
	char *szFileName=NULL;
	char *szFolderName=NULL;
	int result = 0;
	int nfileFolderSize=strlen(fileFolder);
	szFolderName=malloc(nfileFolderSize+10);
	szFileName=malloc(nfileFolderSize+512);

	strcpy(szFolderName, fileFolder);
	strcat(szFolderName, "/%s");

	if ((pDir = opendir(fileFolder)) != NULL) {
		// 遍历目录并删除文件
		while ((dmsg = readdir(pDir)) != NULL) {
			if (!strcmp(dmsg->d_name, ".") || !strcmp(dmsg->d_name, ".."))
				continue;
			if (dmsg->d_type == DT_DIR) {
				sprintf(szFileName, szFolderName, dmsg->d_name);
				File_DeleteFiles(szFileName);
				continue;
			}
			//if (strcmp(dmsg->d_name, ".") != 0 && strcmp(dmsg->d_name, "..") != 0)
			{
				sprintf(szFileName, szFolderName, dmsg->d_name);
				remove(szFileName);
				result++;
			}
		}
	}
	if (pDir != NULL) {
		closedir(pDir);
		remove(fileFolder);
		result++;
	} else {
		remove(fileFolder);
		result++;
	}
	return 1;
}
//#ifdef __cplusplus
//}
//#endif
