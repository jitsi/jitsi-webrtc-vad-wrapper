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

import java.util.*;

/**
 * This exception will be thrown when the {@link WebRTCVad}
 * is given a vad mode which is invalid.
 *
 * @author Nik Vaessen
 */
public class UnsupportedVadModeException
    extends IllegalArgumentException
{
    public UnsupportedVadModeException(int vadMode, int[] valid)
    {
        super(String.format("Given VAD mode %d is invalid, needs to be" +
                                " a value of %s",
                            vadMode,
                            Arrays.toString(valid)));
    }
}
