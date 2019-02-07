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

package org.jitsi.webrtcvadwrapper.Exceptions;

import org.jitsi.webrtcvadwrapper.*;

/**
 * This exception will be thrown when the native VAD library fails to
 * handle an audio segment given by the {@link WebRTCVad} object.
 *
 * @author Nik Vaessen
 */
public class NativeLibraryException
    extends RuntimeException
{

    /**
     * Create a new {@link NativeLibraryException}.
     */
    public NativeLibraryException()
    {
        super("The native VAD library failed to handle this segment. This is" +
                  " most likely due to the audio not having the correct length"+
                  " or format");
    }
}