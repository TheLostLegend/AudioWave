package com.example.lab3.GameCore

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.concurrent.CopyOnWriteArrayList

var BallsOnScreen: CopyOnWriteArrayList<Ball> = CopyOnWriteArrayList<Ball>()

class GameSurfaceView(context: Context? , attrs: AttributeSet?) :
    SurfaceView(context) , SurfaceHolder.Callback {
    private var mMyThread: MyThread?=null
    private var mPaint: Paint?=null
    var platform: Rect?=null
    var platformWidth=0
    var platformX=0f
    var platformSpeed=0f
    var isActive: Boolean = false
    var lose:Boolean = false


    override fun surfaceCreated(holder: SurfaceHolder) {
        mPaint=Paint()
        initPlatform()
//        runBlocking { initBalls() }





        mMyThread=MyThread(getHolder() , this)
        mMyThread!!.setRunning(true)
        mMyThread!!.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder , format: Int , width: Int , height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry=true
        mMyThread?.setRunning(false)
        while (retry) {
            try {
                mMyThread?.join()
                retry=false
            } catch (e: InterruptedException) {
            }
        }
    }

    fun initPlatform() {
        platform=Rect()
        platformWidth=200
        platformX=(width / 2).toFloat()
        platformSpeed=7f
    }

//    suspend fun initBalls() {
//        var i34= 0
//        BallList.forEach {
//            i34++
//            BallsOnScreen.add(it)
//            if (i34 % 2 == 0) delay(1000L)
//        }
//
//    }

    @JvmName("setPlatformX1")
    fun setPlatformX(platformX: Float) {
        if (platformX >= width - platformWidth / 2) {
            this.platformX=(width - platformWidth / 2).toFloat()
        } else if (platformX <= platformWidth / 2) {
            this.platformX=(platformWidth / 2).toFloat()
        } else this.platformX=platformX
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        setPlatformX(event!!.x)
        invalidate()
        return true
    }



    public override fun onDraw(canvas: Canvas?) {
        if (canvas != null) {
            canvas?.drawColor(Color.WHITE)
            BallsOnScreen.forEach{
                if (it.Act) it.moveBall(canvas, mPaint, platformWidth, platformX, height, isActive)
            }
            mPaint!!.color=Color.BLUE
            platform!![Math.round(platformX - platformWidth / 2) , height - 100 , Math.round(
                platformX + platformWidth / 2
            )]=
                height - 50
            canvas.drawRect(platform!! , mPaint!!)
        }
    }

    init {
        holder.addCallback(this)
    }
}