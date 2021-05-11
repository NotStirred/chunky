#ifndef CHUNKY_UTIL_H
#define CHUNKY_UTIL_H

#include <cstdint>

namespace chunky {
    namespace jni {
        inline void throwException(JNIEnv* env, const char* msg)  {
            jclass clazz = env->FindClass("se/llbit/util/NativeException");

            env->Throw((jthrowable) env->NewObject(
                    clazz,
                    env->GetMethodID(clazz, "<init>", "(Ljava/lang/String;)V"),
                    env->NewStringUTF(msg)
            ));
        }
    }

    namespace util {
        template<typename T> constexpr T min(T a, T b) {
            return a < b ? a : b;
        }

        template<typename T> constexpr T max(T a, T b) {
            return a > b ? a : b;
        }

        template<typename T> constexpr T clamp(T val, T min, T max) {
            return val > min ? val < max ? val : max : min;
        }

        template<typename T> constexpr T abs(T a) {
            return a < 0 ? -a : a;
        }

        inline constexpr float signed_to_float(int32_t in) {
            const union {
                int32_t i;
                float d;
            } u = {in};
            return u.d;
        }
    }
}

#endif //CHUNKY_UTIL_H
