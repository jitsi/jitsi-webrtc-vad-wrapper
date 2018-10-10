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

package org.jitsi.webrtcvadwrapper.Exceptions;

import org.jitsi.webrtcvadwrapper.*;

/**
 * This exception is thrown when the {@link WebRTCVad} is asked
 * to detect speech on an audio segment with an incorrect length given the
 * sample frequency. Note that the given audio segment always has to be 10, 20
 * or 30 milliseconds.
 *
 * @author Nik Vaessen
 */
public class UnsupportedSegmentLengthException extends IllegalArgumentException
{
}
