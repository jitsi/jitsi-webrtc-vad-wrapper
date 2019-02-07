/*
 * Copyright @ 2019-Present 8x8, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <iostream>
#include "org_jitsi_webrtcvadwrapper_WebRTCVad.h"
#include "fvad.h"

const static char* VARIABLE_NAME_VAD_POINTER = "nativeVadPointer";
const static char* VARIABLE_TYPE_VAD_POINTER = "J"; //means long

void setPointerToVadObject(JNIEnv* env, jobject thisObj, Fvad* vadPtr)
{
    // first we need to know which class we need to access
    jclass thisClass = (*env).GetObjectClass(thisObj);

    // then we need to know the ID
    jfieldID fieldIDVadPtr = (*env).GetFieldID(thisClass,
                                                 VARIABLE_NAME_VAD_POINTER,
                                                 VARIABLE_TYPE_VAD_POINTER);

    // then we can set the field
    (*env).SetLongField(thisObj, fieldIDVadPtr, reinterpret_cast<jlong>(vadPtr));
}

Fvad* getPointerToVadObject(JNIEnv* env, jobject thisObj)
{
    // first we need to know which class we need to access
    jclass thisClass = (*env).GetObjectClass(thisObj);

    // then we need to know the ID
    jfieldID fieldIDVadPtr = (*env).GetFieldID(thisClass,
                                                 VARIABLE_NAME_VAD_POINTER,
                                                 VARIABLE_TYPE_VAD_POINTER);

    // then we can get the field
    return reinterpret_cast<Fvad*>((*env).GetLongField(thisObj, fieldIDVadPtr));
}

void Java_org_jitsi_webrtcvadwrapper_WebRTCVad_nativeOpen(
        JNIEnv* env,
        jobject thisObj,
        jint sampleRate,
        jint mode)
{
    // Create a Vad object
    Fvad* vadPtr = fvad_new();
    fvad_set_sample_rate(vadPtr, (int) sampleRate);
    fvad_set_mode(vadPtr, (int) mode);


    // Store the pointer to the Vad object.
    setPointerToVadObject(env, thisObj, vadPtr);
}

void Java_org_jitsi_webrtcvadwrapper_WebRTCVad_nativeClose(
        JNIEnv* env,
        jobject thisObj)
{
    // Get the Vad object pointer
    Fvad* vadPtr = getPointerToVadObject(env, thisObj);

    // Close the Vad object
    fvad_free(vadPtr);

    // Set the pointer to null in the java object
    setPointerToVadObject(env, thisObj, nullptr);
}

jboolean Java_org_jitsi_webrtcvadwrapper_WebRTCVad_nativeIsOpen(JNIEnv* env, jobject thisObj)
{
    Fvad* vadPrt = getPointerToVadObject(env, thisObj);
    if(vadPrt)
    {
        return (jboolean) true;
    }
    else
    {
        return (jboolean) false;
    }
}

jint Java_org_jitsi_webrtcvadwrapper_WebRTCVad_nativeIsSpeech(
        JNIEnv* env,
        jobject thisObj,
        jintArray javaAudioSample)
{
    jsize len = (*env).GetArrayLength(javaAudioSample);
    jint* javaArray = (*env).GetIntArrayElements(javaAudioSample, nullptr);

    auto* audioSample = new int16_t[len];
    for(int i = 0; i < len; i++)
    {
        audioSample[i] = javaArray[i];
    }

    Fvad* vadPtr = getPointerToVadObject(env, thisObj);
    int result = fvad_process(vadPtr, audioSample, (size_t ) len);

    delete[] audioSample;

    return result;
}