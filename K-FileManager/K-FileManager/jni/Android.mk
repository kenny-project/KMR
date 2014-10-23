LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := FileTools
LOCAL_SRC_FILES := FileTools.cpp

include $(BUILD_SHARED_LIBRARY)
