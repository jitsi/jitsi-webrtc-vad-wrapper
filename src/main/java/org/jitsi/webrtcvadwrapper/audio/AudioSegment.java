package org.jitsi.webrtcvadwrapper.audio;

import org.jitsi.webrtcvadwrapper.*;

/**
 * The {@link WebRTCVad} object can only handle 16 bit pcm audio files. However,
 * audio can be stored in different formats. The {@link AudioSegment} class
 * represent a wrapper over audio, and implements an interface
 * {@link AudioSegment#to16bitPCM} to provide the audio in the required format.
 *
 * @author Nik Vaessen
 */
public interface AudioSegment
{
    /**
     * Transform the audio in a {@link AudioSegment} to 16 bit PCM.
     *
     * @return the audio in 16 bit PCM format
     */
    int[] to16bitPCM();
}
