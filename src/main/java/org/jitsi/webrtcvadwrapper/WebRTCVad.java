/*
 * Copyright @ 2018 Atlassian Pty Ltd
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

package org.jitsi.webrtcvadwrapper;

/**
 * This class encapsulates the native WebRTCVad library, useful for doing
 * voice activity detection on mono, signed 16bit PCM audio files. Supported
 * frequencies are 8000, 16000, 32000 and 48000 Hz.
 *
 * @author Nik Vaessen
 */
public class WebRTCVad
{
    /*
     * Load the native library.
     */
    static
    {
        System.loadLibrary("webrtcvadwrapper");
    }

    /**
     * Converts an audio sample of PCM doubles (-1.0 between 1.0) to signed
     * 16 bit integer audio sample.
     *
     * @param doubleArray the array to convert to int's
     * @return the converted array
     */
    public static int[] convertPCMAudioTo16bitInt(double[] doubleArray)
    {
        int[] intArray = new int[doubleArray.length];

        for(int i = 0; i < doubleArray.length; i++)
        {
            intArray[i] = convertDoubleToSigned16BitInt(doubleArray[i]);
        }

        return intArray;
    }

    /**
     * Converts a double (which has a value between -1 and 1) to a signed
     * 16 bit integer representation as required for the WebRTCVad library.
     *
     * @param d the double which needs to be converted
     * @return an integer value between -32768 and 32767
     */
    public static int convertDoubleToSigned16BitInt(double d)
    {
        d = d * 32768;

        if(d > 32767)
        {
            d = 32767;
        }
        else if(d < -32768)
        {
            d = -32768;
        }

        return (int) d;
    }

    /**
     * Store a pointer to the native Vad object. This variable is accessed
     * directly by native code.
     */
    private long nativeVadPointer;

    /**
     * Create an object wrapping a native WebRTCVad object which is
     * capable of detecting speech in a given audio sample.
     *
     * The voice activity needs 2 parameters, sample rate and operating mode.
     *
     * Supported sample rates are 8, 16, 32 and 48 KHz.
     *
     * Valid modes are 0 ("quality"), 1 ("low bitrate"), 2 ("aggressive"), and 3
     * ("very aggressive").  The more aggressive the mode, the higher the
     * probability that active speech is detected, which also increases the
     * amount of wrong classification.
     *
     * @param sampleRate the sample rate of the audio which will be given
     * @param mode the mode of the WebRTCVad. Ranges from 0 to 3, with 0 being
     * very selective and 2 being very aggressive.
     */
    public WebRTCVad(int sampleRate, int mode)
    {
        nativeOpen(sampleRate, mode);
    }

    /**
     * Make sure to close to native memory allocation when this object
     * gets destroyed.
     */
    @Override
    protected void finalize() throws Throwable
    {
        nativeClose();
    }

    /**
     * Creates and opens the native WebRTCVad object with the specified
     * parameters.
     *
     * @param sampleRate the sampleRate of the audio which will be given
     * @param mode the mode of the voice activity detector
     */
    private native void nativeOpen(int sampleRate, int mode);

    /**
     * Close the native WebRTCVad object and delete it from memory.
     */
    private native void nativeClose();

    /**
     * Checks whether the native WebRTCVad object is still functional.
     * @return true when the object is still functional, false otherwise.
     */
    private native boolean nativeIsOpen();

    /**
     * Give the native WebRTCVad object an audio sample.
     *
     * @param audioSample 10, 20 or 30 ms of audio with the specified sample
     * rate
     * @return -1 when input was wrong, 0 when no speech was detected or 1 when
     * speech was detected
     */
    private native int nativeIsSpeech(int[] audioSample);

    /**
     * Perform voice activity detection on a given sample of audio. The sample
     * should be signed 16 bit mono PCM audio. The sample should have a length
     * of 10, 20 or 30 milliseconds of audio. If the specified sample rate was
     * 16000 Hz, the sample thus needs to be of length 160, 320 or 480.
     *
     * @param audioSample the audio sample
     * @return true when speech was detected in this sample, false otherwise.
     * @throws UnsupportedFrameLengthException when the length of the audio was
     * incorrect.
     * @throws VadClosedException when the native object was already closed.
     */
    public boolean isSpeech(int[] audioSample)
        throws UnsupportedFrameLengthException,
               VadClosedException
    {
        if(!nativeIsOpen())
        {
            throw new VadClosedException();
        }

        int result =  nativeIsSpeech(audioSample);

        if(result < 0)
        {
            throw new UnsupportedFrameLengthException();
        }

        return result == 1;
    }

    /**
     * @see WebRTCVad#isSpeech(int[])
     */
    public boolean isSpeech(double[] audioSample)
        throws UnsupportedFrameLengthException,
               VadClosedException
    {
        return isSpeech(convertPCMAudioTo16bitInt(audioSample));
    }

}
