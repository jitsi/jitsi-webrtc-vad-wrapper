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
