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

import org.jitsi.webrtcvadwrapper.*;
import org.junit.*;

/**
 * Unit test for whether an object can be created. This requires
 * that the shared libraries are correctly loaded according to
 * java.library.path
 *
 * @author Nik Vaessen
 */
public class CreationTest
{
    @Test
    public void testCreationObject()
    {
        WebRTCVad vad = new WebRTCVad(16000, 1);
        Assert.assertNotNull(vad);
    }
}
