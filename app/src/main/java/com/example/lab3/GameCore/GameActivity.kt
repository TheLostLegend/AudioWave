package com.example.lab3.GameCore

import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.lab3.CurrentSongPosition
import com.example.lab3.R
import com.example.lab3.SongInfo
import com.example.lab3.myListSong
import kotlinx.android.synthetic.main.activity_game.*

var score: Int = 0
var last: Int = -1

class GameActivity : AppCompatActivity() {
    var sensorManager: SensorManager?=null
    var orientationSensor: Sensor?=null
    var gameSurfaceView: GameSurfaceView?=null
    var notificationView: TextView?=null
    var timeView: TextView?=null
    var scoreView: TextView?=null
    var pauseView: ImageView?=null
    var leaveView: ImageView?=null
    var timerActive=false
    var name: String?=null

    companion object {
        var mp: MediaPlayer? = null
    }
    private var totalTime: Int = 0
    private var song: SongInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        song = myListSong[CurrentSongPosition]
        mp?.reset()
        mp = MediaPlayer.create(this, song!!.SongURI)
        mp?.isLooping = false
        mp?.setVolume(0.5f, 0.5f)
        totalTime = mp?.duration!!

        name=intent.extras!!["name"] as String?

        gameSurfaceView=
            (findViewById<View>(R.id.frame_layout) as FrameLayout).getChildAt(0) as GameSurfaceView
        notificationView=findViewById(R.id.notification)

        pauseView=findViewById(R.id.pause)
        leaveView=findViewById(R.id.leave)
        timeView=findViewById(R.id.timer)
        scoreView=findViewById(R.id.score2)
        with(leaveView) { this?.setOnClickListener { v: View? -> summonMenu() } }


        with(notificationView) {
            this?.setText("Tap to PLAY")
            this?.setOnClickListener(View.OnClickListener { v: View? ->
                gameSurfaceView!!.isActive=true
                timerActive=true
                pause.setVisibility(View.VISIBLE)
                setVisibility(View.GONE)

                var thread=Thread(object : Runnable {
                    override fun run() {
                        var i34=0
                        BallList.forEach {
                            while(!gameSurfaceView?.isActive!!) Thread.sleep(1)
                            i34++
                            BallsOnScreen.add(it)
                            if (i34 % 2 == 0) {
                                Thread.sleep(250)
                            }
                        }
                    }
                })
                var thread2=Thread(object : Runnable {
                    override fun run() {
                        Thread.sleep(1_000)
                        mp?.start()
                    }
                })
                thread.start()
                thread2.start()
            })
        }
        val handler=Handler()
        val runnable: Runnable=object : Runnable {
            @SuppressLint("SetTextI18n")
            override fun run() {
                try {
                    if (gameSurfaceView!!.lose) {
                        with(notificationView) {
                            this?.setText(
                                """Nice Song 
                                    Your result is ${score}
                                    Tap to see champions dashboard"""
                            )
                            this?.setVisibility(View.VISIBLE)
                        }
                        with(pauseView) { this?.setVisibility(View.GONE)}
                        with(timeView) { this?.setVisibility(View.GONE) }
                        timerActive=false
                        val db=baseContext.openOrCreateDatabase("app.db" , MODE_PRIVATE , null)
                        with(notificationView) {
                            db.execSQL("INSERT INTO players (name, result) VALUES ('$name', '$score')")
                            this?.setOnClickListener { summonDashboard() }
                        }
                        gameSurfaceView!!.lose=false
                    }
                    handler.postDelayed(this , 0)
                } catch (ed: IllegalStateException) {
                }
            }
        }
        handler.postDelayed(runnable , 0)
        val timerHandler=Handler()
        val timerRunnable: Runnable=object : Runnable {
            override fun run() {
                try {
                    if (timerActive) {
                        var seconds=Math.round((totalTime - mp?.currentPosition!!).toDouble()/1000)
                        val hours=seconds / 3600
                        val minutes=seconds % 3600 / 60
                        seconds%=60
                        with(timeView) {
                            seconds%=60
                            this?.setText(
                                String.format(
                                    "%02d:%02d:%02d" ,
                                    hours ,
                                    minutes ,
                                    seconds
                                )
                            )
                        }
                        Log.d("bababab", mp?.currentPosition!!.toString())
                        Log.d("bababab", totalTime.toString())
                        if (totalTime - mp?.currentPosition!! == 0 || (last == mp?.currentPosition!! && last != 0)) {
                            gameSurfaceView!!.lose = true
                            gameSurfaceView!!.isActive = false}
                        last = mp?.currentPosition!!
                        with(scoreView){
                            this?.setText(score.toString())
                        }
                    }
                    timerHandler.postDelayed(this , 250)
                } catch (ed: IllegalStateException) {
                }
            }
        }
        timerHandler.postDelayed(timerRunnable , 0)
    }

    fun summonMenu() {
        val intent=Intent(this , MainActivity2::class.java)
        startActivity(intent)
    }

    fun summonDashboard() {
        val intent=Intent(this , ChampionDashboard::class.java)
        startActivity(intent)
    }

    fun omMenuPlayed(view: View?) {
        if (gameSurfaceView?.isActive == true) {
            mp?.pause()
            gameSurfaceView!!.isActive = false
            timerActive=false
            pauseView!!.setImageDrawable(getDrawable(R.drawable.ic_baseline_play_arrow_42))
            leaveView!!.visibility=View.VISIBLE
        } else {
            mp?.start()
            gameSurfaceView?.isActive = true
            timerActive=true
            pauseView!!.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause_42))
            leaveView!!.visibility=View.GONE
        }
    }
}