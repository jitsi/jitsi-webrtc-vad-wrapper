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

package org.jitsi.webrtcvadwrapper;

import org.apache.commons.collections4.queue.*;
import org.jitsi.webrtcvadwrapper.Exceptions.*;
import org.jitsi.webrtcvadwrapper.audio.*;

import java.util.*;

/**
 * A speech detector to determine whether a certain sequence of audio (a window)
 * contains speech. To determine speech, this detector can be given sequential
 * segments of audio. These segments are 10, 20 or 30 milliseconds. The
 * detector can then be configured to look at a certain amount of these segments
 * (the window) to determine whether these segments are speech. When the
 * given amount of sequential segments exceeds the configured size of the
 * window, the segment which was given first is discarded and not considered
 * anymore. This can be seen as the window sliding to the right.
 *
 * @author Nik Vaessen
 */
public class SpeechDetector<T extends AudioSegment>
{
    /**
     * The voice activity detector used for determining whether there is
     * speech in a certain segment of the window.
     */
    private final WebRTCVad vad;

    /**
     * A circular first in, first out queue for storing the window. When the
     * window slides to the next segment, the segment leaving the window
     * is discarded.
     */
    private final CircularFifoQueue<T> window;

    /**
     * The audio segment size which is expected to be given to this
     * speech detector. The value will only contain values given by
     * {@link WebRTCVad#getValidAudioSegmentLengths(int)}, which depends
     * on the sample rate of the audio which is being given.
     */
    private final int segmentSize;

    /**
     * The threshold to determine whether a given window contains speech.
     * As a window of the audio contains N number of audio segments, we will
     * declare this window to contain speech when 0 < T <= N segments in
     * the window are speech according to the {@link WebRTCVad} VAD detector.
     */
    private final int threshold;

    /**
     * The number of audio segments in the window.
     */
    private final int windowSizeInSegments;

    /**
     * The sample rate of the audio which will be given to this
     * {@link SpeechDetector}.
     */
    private final int sampleRate;

    /**
     * How many segments in the window contain speech according to the
     * {@link WebRTCVad} VAD detector.
     */
    private int speechSegmentCount = 0;

    /**
     * Create a {@link SpeechDetector} which will be given (a window of)
     * audio segments and which will determine whether this window of audio
     * segments contains speech.
     *
     * @param sampleRate the sample rate of audio in the audio segments
     * @param vadMode the VAD mode to use for the {@link WebRTCVad}
     * @param segmentSizeInMs the length of the audio segments which will be
     * given. Valid values are 10, 20 or 30 milliseconds.
     * @param windowSizeInMs the size of the window in milliseconds. This should
     * be a multiple of the given segment size in milliseconds.
     * @param threshold how many segments in the window should be speech to
     * determine whether a certain audio window contains speech
     * @throws UnsupportedSampleRateException when the given sample rate is
     * invalid
     * @throws UnsupportedVadModeException when the given vad more is invalid
     * @throws UnsupportedSegmentLengthException when the given segment size is
     * invalid
     * @throws UnsupportedWindowSizeException when the given window size in
     * invalid
     * @throws UnsupportedThresholdException when the given threshold is invalid
     */
    public SpeechDetector(int sampleRate,
                          int vadMode,
                          int segmentSizeInMs,
                          int windowSizeInMs,
                          int threshold)
        throws UnsupportedSampleRateException,
               UnsupportedVadModeException,
               UnsupportedSegmentLengthException,
               UnsupportedWindowSizeException,
               UnsupportedThresholdException
    {
        this.vad = new WebRTCVad(sampleRate, vadMode);
        this.sampleRate = sampleRate;

        this.segmentSize = (int) (sampleRate * (segmentSizeInMs / 1000d));
        if(!this.vad.isValidLength(segmentSize))
        {
            throw new UnsupportedSegmentLengthException(segmentSize,
                        WebRTCVad.getValidAudioSegmentLengths(sampleRate));
        }

        // window size needs to be a multiple of a segment so we can fit
        // x segments in the window
        if(windowSizeInMs % segmentSizeInMs != 0
            || windowSizeInMs < segmentSizeInMs)
        {
            throw new UnsupportedWindowSizeException(windowSizeInMs,
                                                     segmentSizeInMs);
        }
        this.windowSizeInSegments = windowSizeInMs / segmentSizeInMs;
        this.window
            = new CircularFifoQueue<>(windowSizeInSegments * segmentSizeInMs);

        if(threshold < 0 || threshold > this.windowSizeInSegments)
        {
            throw new UnsupportedThresholdException(threshold,
                                                    0,
                                                    this.windowSizeInSegments);
        }
        this.threshold = threshold;
    }

    /**
     * Advance the sliding window of the audio by giving it a new segment.
     *
     * @param segmentHolder the segment of audio to add to the window.
     */
    public void nextSegment(T segmentHolder)
    {
        int[] segment = segmentHolder.to16bitPCM();

        if(segment.length != this.segmentSize)
        {
            throw new UnsupportedSegmentLengthException(segment.length,
                        WebRTCVad.getValidAudioSegmentLengths(this.sampleRate));
        }

        if(vad.isSpeech(segment))
        {
            speechSegmentCount
                = Math.min(windowSizeInSegments, speechSegmentCount + 1);
        }
        else
        {
            speechSegmentCount =
                Math.max(0, speechSegmentCount - 1);
        }

        window.add(segmentHolder);
    }

    /**
     * Get the current window of audio segments in a single array.
     *
     * @return the window of audio segments.
     */
    public List<T> getLatestSegments()
    {
        return new ArrayList<>(window);
    }

    /**
     * Get whether the current window is determined to contain speech
     *
     * @return true the current window of audio segments is speech, false
     * otherwise
     */
    public boolean isSpeech()
    {
        return speechSegmentCount >= threshold;
    }

}
