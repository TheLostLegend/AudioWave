package com.serwylo.beatgame.audio.fft

import com.example.lab3.Mp3Data
import javazoom.jl.decoder.Bitstream
import javazoom.jl.decoder.Header
import javazoom.jl.decoder.MP3Decoder
import javazoom.jl.decoder.OutputBuffer
import org.apache.commons.math3.transform.DftNormalization
import org.apache.commons.math3.transform.FastFourierTransformer
import org.apache.commons.math3.transform.TransformType
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.math.min

fun calculateMp3FFTWithValues(mp3InputStream: InputStream): FFTResultWithValues {

    val mp3Data = readPcm(mp3InputStream)

    val windowSize = 1024

    val numWindows = mp3Data.pcmSamples.size / windowSize
    val windows = ArrayList<FFTWindowWithValues>(numWindows)
    for (windowIndex in 0..numWindows) {
        val frequencyValues = calculateFFTWindow(mp3Data, windowIndex, windowSize)
        windows.add(FFTWindowWithValues.create(windowIndex, frequencyValues))
    }

    return FFTResultWithValues(mp3Data, windowSize, windows)

}

private fun calculateFFTWindow(mp3Data: Mp3Data , windowIndex: Int , windowSize: Int): List<FrequencyValue> {

    val startSample = windowIndex * windowSize
    val endSample = min(mp3Data.pcmSamples.size, startSample + windowSize)

    val samples = DoubleArray(windowSize)
    val samplesToCast = mp3Data.pcmSamples.slice(IntRange(startSample, endSample - 1))

    for (i in samplesToCast.indices) {
        samples[i] = samplesToCast[i].toDouble()
    }

    // For the case where we ran up against the end of the music file, and we didn't fill
    // the buffer. We still require the data to be a power of two, so continue filling 0's
    // as per the commons-math documentation suggests.
    for (i in samplesToCast.size until windowSize) {
        samples[i] = 0.0
    }

    // Interpreting the x axis of FFT results.
    // https://stackoverflow.com/a/4371627
    val fft = FastFourierTransformer(DftNormalization.STANDARD)
    val fftResult = fft.transform(samples, TransformType.FORWARD)

    // The second half of the results are the mirror image of the first half
    val size = samples.size / 2 + 1
    val values = ArrayList<FrequencyValue>(size)
    for (i in 0 until size) {
        values.add(FrequencyValue(
            frequency = i.toDouble() * mp3Data.sampleRate / windowSize,
            absValue = fftResult[i].abs()
        ))
    }

    return values

}

/**
 * Originally from libgdx Mp3.Sound class (Licensed as Apache 2.0)
 */
private fun readPcm(mp3InputStream: InputStream): Mp3Data {

    val output = ByteArrayOutputStream(4096)

    val bitstream = Bitstream(mp3InputStream)
    val decoder = MP3Decoder()

    try {
        var outputBuffer: OutputBuffer? = null
        var sampleRate = -1
        var channels = -1
        while (true) {
            val header = bitstream.readFrame() ?: break
            if (outputBuffer == null) {
                channels = if (header.mode() == Header.SINGLE_CHANNEL) 1 else 2
                outputBuffer = OutputBuffer(channels, true)
                decoder.setOutputBuffer(outputBuffer)
                sampleRate = header.sampleRate
            }
            try {
                decoder.decodeFrame(header, bitstream)
            } catch (ignored: Exception) {
                // JLayer's decoder throws ArrayIndexOutOfBoundsException sometimes!?
            }
            bitstream.closeFrame()
            output.write(outputBuffer.buffer, 0, outputBuffer.reset())
        }
        bitstream.close()
        return Mp3Data(output.toByteArray(), channels, sampleRate)
    } catch (e: java.lang.Exception) {
        return Mp3Data(ByteArray(0), 0, 0)
    }

}
