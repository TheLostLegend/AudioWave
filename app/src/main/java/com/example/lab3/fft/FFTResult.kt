package com.serwylo.beatgame.audio.fft

import com.example.lab3.Mp3Data

data class FFTResult(
        val mp3Data: Mp3Data ,
        val windowSize: Int ,
        val windows: List<FFTWindow>
)
