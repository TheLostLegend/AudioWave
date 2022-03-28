package com.example.lab3.GameCore

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log

class Ball(
    var ballX: Int , var ballY: Int , val ballRadius: Int , val ballSpeed: Int, var ballAngle: Int , val Col:Boolean){
    var Act:Boolean = true

    fun moveBall(
        canvas: Canvas? ,
        mPaint: Paint? ,
        platformWidth: Int ,
        platformX: Float ,
        height: Int ,
        isAct: Boolean
    ) {
        if (Col) mPaint!!.color=Color.GRAY
        else mPaint!!.color=Color.GREEN
        mPaint!!.style=Paint.Style.FILL
        canvas?.drawCircle(ballX.toFloat() , ballY.toFloat() , ballRadius.toFloat() , mPaint!!)
        if (isAct) {
            ballX+=(ballSpeed * Math.cos(Math.toRadians(ballAngle.toDouble()))).toInt()
            ballY-=(ballSpeed * Math.sin(Math.toRadians(ballAngle.toDouble()))).toInt()
            Log.v(Integer.toString(ballY) , Integer.toString(ballX))
            if (((ballY + ballRadius >= height - 100 && ballY + ballRadius <= height - 99 + ballSpeed)||(ballY >= height - 100 && ballY<= height - 99 + ballSpeed)||(ballY - ballRadius >= height - 100 && ballY - ballRadius <= height - 99 + ballSpeed)) && ballX >= platformX - platformWidth / 2 && ballX <= platformX + platformWidth / 2) {
                Act = false
                if (Col) score -= 20
                else score += 50
            } else if (ballY > height+100) {Act = false}
            else return
        }

    }
}