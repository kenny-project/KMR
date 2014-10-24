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
JNIEXPORT jobject JNICALL Java_com_kenny_file_util_NFileTools_getFileSizes(JNIEnv *env, jclass obj, jstring fileFolder)
{
	const char *file_folder = (*env)->GetStringUTFChars(env, fileFolder, 0);
	unsigned long fileSize=0l,fileCount=0l;
	unsigned short result=File_GetSizes(file_folder,&fileSize,&fileCount);
    // 获取Java中的实例类
    jclass poiObjClass = (*env)->FindClass(env, "com/kenny/file/bean/FileDetailsBean");
    if(NULL == poiObjClass) {
    	return NULL;
    }
    jfieldID jfTotalFileSize = (*env)->GetFieldID(env, poiObjClass, "TotalFileSize", "J");// 经度
    if(NULL == jfTotalFileSize) {
    	return NULL;
    }

    jfieldID jfTotalFileCount = (*env)->GetFieldID(env, poiObjClass, "TotalFileCount", "J");// 经度
    if(NULL == jfTotalFileCount) {
    	return NULL;
    }
    __android_log_print(ANDROID_LOG_INFO, "NDK", "%ld,%ld", fileSize,fileCount);
    jobject poiObj = (*env)->AllocObject(env, poiObjClass);
    (*env)->SetLongField(env, poiObj, jfTotalFileSize,fileSize);
    (*env)->SetLongField(env, poiObj, jfTotalFileCount,fileCount);
	return poiObj;
}
