#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include <time.h>
#include <android/log.h>
#include "FileTools.c"
//#include "NaviDMInterface.h"
// 删除指定文件夹内所有文件
JNIEXPORT jint JNICALL Java_com_kenny_file_util_NFileTools_deleteFiles(JNIEnv *env, jclass obj, jstring fileFolder)
{
	const char *file_folder = (*env)->GetStringUTFChars(env, fileFolder, 0);
	int result=File_DeleteFiles(file_folder);
	(*env)->ReleaseStringUTFChars(env, fileFolder, file_folder);
	return result;
}
JNIEXPORT jlong JNICALL Java_com_kenny_file_util_NFileTools_getFileSizes(JNIEnv *env, jclass obj, jstring fileFolder)
{
	const char *file_folder = (*env)->GetStringUTFChars(env, fileFolder, 0);
	unsigned long result=File_GetSizes(file_folder);
	(*env)->ReleaseStringUTFChars(env, fileFolder, file_folder);
	return result;
}
