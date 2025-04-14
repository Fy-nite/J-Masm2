package org.finite;

/**
 * This file is just a reference for the C/C++ implementer.
 * It shows the exact function signatures needed for JNI compatibility.
 * 
 * The C function signatures should match these names exactly.
 */
public class MicroAsmJNI {
    
    /*
     * Expected C function declarations:
     *
     * JNIEXPORT jlong JNICALL Java_org_finite_MicroAsmRuntime_createInterpreterInternal
     *   (JNIEnv* env, jclass clazz, jint ramSize, jint debugMode);
     *
     * JNIEXPORT void JNICALL Java_org_finite_MicroAsmRuntime_destroyInterpreterInternal
     *   (JNIEnv* env, jclass clazz, jlong handle);
     *
     * JNIEXPORT jint JNICALL Java_org_finite_MicroAsmRuntime_loadBytecodeInternal
     *   (JNIEnv* env, jclass clazz, jlong handle, jstring bytecodeFile);
     *
     * JNIEXPORT jint JNICALL Java_org_finite_MicroAsmRuntime_executeInternal
     *   (JNIEnv* env, jclass clazz, jlong handle, jobjectArray args);
     *
     * JNIEXPORT jint JNICALL Java_org_finite_MicroAsmRuntime_getRegisterInternal
     *   (JNIEnv* env, jclass clazz, jlong handle, jint registerIndex);
     *
     * JNIEXPORT jint JNICALL Java_org_finite_MicroAsmRuntime_readRamIntInternal
     *   (JNIEnv* env, jclass clazz, jlong handle, jint address);
     *
     * JNIEXPORT jint JNICALL Java_org_finite_MicroAsmRuntime_writeRamIntInternal
     *   (JNIEnv* env, jclass clazz, jlong handle, jint address, jint value);
     *
     * JNIEXPORT jstring JNICALL Java_org_finite_MicroAsmRuntime_getLastError
     *   (JNIEnv* env, jclass clazz);
     */
    
    // This file is just documentation - no actual code needed
}
