#include <jni.h>
#include <string>
#include <android/native_window_jni.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_zjj_vectordrawable_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
