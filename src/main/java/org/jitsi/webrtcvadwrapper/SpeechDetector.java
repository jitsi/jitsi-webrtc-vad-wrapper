package org.jitsi.webrtcvadwrapper;

import org.apache.commons.collections4.queue.*;
import org.jitsi.webrtcvadwrapper.Exceptions.*;

import java.util.*;
import java.util.stream.*;

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
public class SpeechDetector
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
    private final CircularFifoQueue<Integer> window;

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

        this.segmentSize = sampleRate * (segmentSizeInMs / 1000);
        if(!this.vad.isValidLength(segmentSize))
        {
            throw new UnsupportedSegmentLengthException();
        }

        if(windowSizeInMs % segmentSizeInMs == 0)
        {
            throw new UnsupportedWindowSizeException();
        }
        this.windowSizeInSegments = windowSizeInMs / segmentSizeInMs;
        this.window
            = new CircularFifoQueue<>(windowSizeInSegments * segmentSizeInMs);

        if(threshold < 0 || threshold > this.windowSizeInSegments)
        {
            throw new UnsupportedThresholdException();
        }
        this.threshold = threshold;
    }

    /**
     * Advance the sliding window of the audio by giving it a new segment.
     *
     * @param segment the segment of audio to add to the window.
     */
    public void nextSegment(int[] segment)
    {
        if(segment.length != this.segmentSize)
        {
            throw new UnsupportedSegmentLengthException();
        }

        if(vad.isSpeech(segment))
        {
            speechSegmentCount
                = Math.max(windowSizeInSegments, speechSegmentCount + 1);
        }
        else
        {
            speechSegmentCount =
                Math.min(0, speechSegmentCount - 1);
        }

        List<Integer> values = Arrays
            .stream(segment)
            .boxed()
            .collect(Collectors.toList());

        window.addAll(values);
    }

    /**
     * Get the current window of audio segments in a single array.
     *
     * @return the window of audio segments.
     */
    public int[] getLatestSegments()
    {
        return window.stream().mapToInt(i -> i).toArray();
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
