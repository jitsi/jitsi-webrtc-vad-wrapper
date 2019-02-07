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

import java.util.*;

/**
 * An audio segment which is stored as 16 bit signed PCM audio, but represented
 * in byte pairs, where the first byte carries the least significant bits.
 *
 * @author Nik Vaessen
 */
public class ByteSignedPcmAudioSegment
    implements AudioSegment
{
    /**
     * Convert an array of bytes into an array of 16-bit integers.
     * It's assumed the input array contains an even amount of bytes,
     * and for each pair of bytes, the first byte is the least-significant.
     *
     * @param audio the array to convert
     * @return the converted array
     */
    private static int[] convertByteArrayTo16bitIntArray(byte[] audio)
    {
        int[] converted = new int[audio.length / 2];

        for(int i = 0; i < converted.length; i++)
        {
            /*
             * We convert 2 bytes to a 16 bit integer into the following manner:
             *
             * We assume big-endian, so the first bit is the highest value
             * (256) and the last bit is the lowest value (1).
             *
             * We also assume that the byte-pair the byte pair comes in least-
             * significant order. That is, byte 1 contains the bits for 2^0
             * to 2^7 and byte 2 contains the bits for 2^8 to 2^15.
             *
             * For this example, let's assume:
             *
             * b1: 0000 0001
             * b2: 0010 0000
             *
             * In order to create the integer, we first shift the second byte
             * 8 bits to the left (with b2 << 8). An example:
             *
             *           0010 0000 << 8 =
             * 0010 0000 0000 0000
             *
             * We then simply do inclusive OR on the resulting byte + 16 bit int
             *
             * b1: 0000 0000 0000 0001 (implicitly converted)
             * b2: 0010 0000 0000 0000
             *
             *  r: 0010 0000 0000 0001
             *
             * where r is now our converted 16 bit integer!
             */
            byte b1 = audio[i*2];
            byte b2 = audio[i*2+1];
            converted[i] = b1 | (b2 << 8);
        }

        return converted;
    }

    /**
     * Merge a {@link List} of
     * {@link ByteSignedPcmAudioSegment} into a single instance.
     * @param segments the list of {@link ByteSignedPcmAudioSegment}
     * @return every segment of the list merged into a single segment
     */
    public static ByteSignedPcmAudioSegment merge(
        List<ByteSignedPcmAudioSegment> segments)
    {
        int totalLength = 0;
        for(int i = 0; i < segments.size(); i++)
        {
            totalLength += segments.get(i).getAudio().length;
        }

        byte[] merged = new byte[totalLength];
        int marker = 0;
        for(ByteSignedPcmAudioSegment segment : segments)
        {
            byte[] audio = segment.getAudio();
            System.arraycopy(audio, 0, merged, marker, audio.length);
            marker += audio.length;
        }

        return new ByteSignedPcmAudioSegment(merged);
    }

    /**
     * The audio in the segment.
     */
    private byte[] audio;

    /**
     * Construct a {@link ByteSignedPcmAudioSegment}.
     *
     * @param audio the audio segment as an array of bytes
     */
    public ByteSignedPcmAudioSegment(byte[] audio)
    {
        this.audio = audio;
    }

    @Override
    public int[] to16bitPCM()
    {
        return convertByteArrayTo16bitIntArray(this.audio);
    }

    /**
     * Get the original audio array.
     *
     * @return the audio in a byte array
     */
    public byte[] getAudio()
    {
        return audio;
    }
}
