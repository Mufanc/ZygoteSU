#include <jni.h>
#include <csignal>
#include <cstdlib>
#include <android/log.h>
#include <sys/unistd.h>

#define ALOGI(...) (__android_log_print(ANDROID_LOG_INFO, "ZygoteSU", __VA_ARGS__))

void handler(int signal) {
    if (signal != SIGSYS) return;
    ALOGI("Failed to setuid!");
}


extern "C" JNIEXPORT jint JNICALL
Java_xyz_mufanc_zsu_RootService_fork(JNIEnv *, jobject) {
    return fork();
}


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *) {
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    signal(SIGSYS, handler);

    gid_t id_all = 0;
    setuid((uid_t) id_all);
    setgid(id_all), setgroups(1, &id_all);
    if (geteuid() == 0) ALOGI("Pwned!");

    return JNI_VERSION_1_6;
}
