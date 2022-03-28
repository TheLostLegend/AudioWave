package com.example.lab3.GameCore

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx.files
import com.example.lab3.*
import com.example.lab3.fft.extractFeaturesFromSeries
import com.example.lab3.fft.seriesFromFFTWindows
import com.example.lab3.fft.smoothSeriesMedian
import com.serwylo.beatgame.audio.fft.FFTWindow
import com.serwylo.beatgame.audio.fft.calculateMp3FFTWithValues

var BallList: MutableList<Ball> = mutableListOf()

class MainActivity2 : AppCompatActivity() {

    private var song: SongInfo? = null
    private var totalTime: Int = 0
    companion object {
        var mp: MediaPlayer? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        BallList.clear()
        BallsOnScreen.clear()
        score = 0
        Log.d("test", "in onCreate!!!")
        db?.execSQL("CREATE TABLE IF NOT EXISTS players (name TEXT, result INT)")
        val query=db?.rawQuery("SELECT COUNT(*) FROM players;" , null)
        query?.moveToNext()
        if (query != null) {
            if (query.getInt(0) == 0) {
                db?.execSQL("INSERT INTO players (name, result) VALUES ('NOOB', 50)")
                db?.execSQL("INSERT INTO players (name, result) VALUES ('MASTER', 1000)")
                db?.execSQL("INSERT INTO players (name, result) VALUES ('GOD', 10000)")
            }
        }
        if (CurrentSongPosition < myListSong.size) {
            song = myListSong[CurrentSongPosition]


        }
        mp?.reset()
        mp = MediaPlayer.create(this, song!!.SongURI)
        mp?.isLooping = true
        mp?.setVolume(0.5f, 0.5f)
        totalTime = mp?.duration!!
        val fromDisk = loadFromDisk(song!!)
    }


    fun onPlayClicked(view: View?) {
        val name=(findViewById<View>(R.id.textInputEditText) as TextView).text.toString()
        if (name.isEmpty()) return
        val query=db!!.rawQuery("SELECT name FROM players WHERE name='$name'" , null)
        query.moveToNext()
        if (query.count > 0) {
            val toast=
                Toast.makeText(applicationContext , "Player already exists" , Toast.LENGTH_SHORT)
            toast.show()
        } else {
            val intent=Intent(this , GameActivity::class.java)
            intent.putExtra("name" , name)
            startActivity(intent)
        }
    }

    fun onDashboardClicked(view: View?) {
        val intent=Intent(this , ChampionDashboard::class.java)
        startActivity(intent)
    }

    fun onDashboardClicked2(view: View?) {
        val intent=Intent(this , MainActivity::class.java)
        startActivity(intent)
    }

    fun getFiles(): Files? {
        return files
    }

    fun loadFromDisk(music: SongInfo): List<Ball> {
        val spectogram=calculateMp3FFTWithValues(contentResolver.openInputStream(music.SongURI!!)!!)

        val extractors = arrayOf(
            { window: FFTWindow -> window.meanFirst } ,
            { window: FFTWindow -> window.meanSecond } ,
            { window: FFTWindow -> window.meanThird }
        )
        val features = extractors.map {
            val featureSeries = seriesFromFFTWindows(spectogram.windows, it)
            val smoothFeatureSeries = smoothSeriesMedian(featureSeries, 13)
            extractFeaturesFromSeries(smoothFeatureSeries, spectogram.windowSize, spectogram.mp3Data.sampleRate)
        }
        var i = 0.0
        var k1: Int
        val numbers: Array<Float> = arrayOf(0.0F, 0.0F, 0.0F)
        while(i<=totalTime){
            k1 = 0
            for(list in features){
                var F1: Feature?= null
                for(feature in list){
                    if(feature.startTimeInSeconds*1000 <= i) F1 = feature
                    else break
                }
                if (F1 == null) numbers[k1] = 0.0F
                else {
                    if(i<= F1.startTimeInSeconds*1000 + F1.durationInSeconds*1000)
                        numbers[k1] = F1.strength
                    else numbers[k1] = 0.0F
                }
               k1++
            }
            var max: Int
            var min: Int
            if (numbers[0] < numbers [1])
            {min = 0; max = 1}
            else
            {min = 1; max = 0}
            if (numbers[2] < numbers [min]) min = 2;
            else if (numbers[2] > numbers [max]) max = 2
            val min1 = convert(min)
            val max1 = convert(max)
            BallList.add(Ball( (max1 * 100).toInt(), 100, 100, 26, 270, false))
            BallList.add(Ball( (min1 * 100).toInt() , 100, 100, 26, 270, true))
            i+=250
        }
        i = 0.0
        return BallList
    }

    fun convert(value: Int): Double {
        var vall:Double
        if (value == 0) vall = 1.5
        else if (value == 1) vall = 5.0
        else vall = 9.5
        return vall
    }
}