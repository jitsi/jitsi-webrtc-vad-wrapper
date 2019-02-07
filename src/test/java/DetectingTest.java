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

import org.jitsi.webrtcvadwrapper.*;
import org.junit.*;

import java.io.*;
import java.nio.file.*;

/**
 * Unit test for detecting audio speech.
 *
 * @author Nik Vaessen
 */
public class DetectingTest
{
    private final static Path pathToTestFile
        = Paths.get("src/test/resources/wave_file_doubles.txt");
    private final static Path pathToAnswerFile
        = Paths.get("src/test/resources/detected_speech.txt");

    @Test
    public void testVadDetection()
    {
        double[] audioData;
        try(BufferedReader reader = new BufferedReader(
            new FileReader(pathToTestFile.toFile())))
        {
            audioData = reader.lines()
                .mapToDouble(Double::valueOf)
                .toArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException("unable to find required files");
        }

        int[] answers;
        try(BufferedReader reader = new BufferedReader(
            new FileReader(pathToAnswerFile.toFile())
        ))
        {
            answers = reader.lines()
                .mapToInt(Integer::valueOf)
                .toArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException("unable to read required test files");
        }

        WebRTCVad vad = new WebRTCVad(16000, 1);

        int answerIdx = 0;
        int binSize = 160;
        double[] audioSample = new double[160];
        int binIdx = 0;
        for(int i = 0; i < audioData.length; i++)
        {
            double currentSample = audioData[i];
            binIdx = i % binSize;

            //we have filled a bin, let's see if there's speech in it
            if(binIdx == 0 && i > 0)
            {
                try
                {
                    boolean isSpeech = vad.isSpeech(audioSample);
                    boolean isSpeechNativeRun = answers[answerIdx] == 1;

                    Assert.assertEquals(isSpeech, isSpeechNativeRun);
                    answerIdx++;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            audioSample[binIdx] = currentSample;
        }

    }
}
