package com.koudeer.lib.widget

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.koudeer.lib.R
import com.koudeer.lib.enum.Status

/**
 * 播放IMG切换图片
 */
fun GreenTeaVideo.imgUIToggle(type: Int) {
    when (type) {
        Status.NORMAL -> {
            this.mImgStartPause.setImageResource(R.mipmap.pause)
            this.mImgStartPause.visibility = View.INVISIBLE
            this.mProgress.visibility = View.VISIBLE
        }
        Status.PAUSE -> {
            this.mImgStartPause.setImageResource(R.mipmap.start)
            this.mImgStartPause.visibility = View.INVISIBLE
            mState = Status.PLAYING
        }
        Status.PLAYING -> {
            this.mImgStartPause.setImageResource(R.mipmap.pause)
            this.mImgStartPause.visibility = View.INVISIBLE
            this.mState = Status.PAUSE
        }
    }
}

fun GreenTeaVideo.supportGesture(): GestureDetector =
    GestureDetector(context.applicationContext,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                Log.d(TAG, "onDoubleTap: ")
                if (mState == Status.PLAYING || mState == Status.PAUSE) {
                    mImgStartPause.performClick()
                }
                return super.onDoubleTap(e)
            }

            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                Log.d(TAG, "onSingleTapConfirmed: ")
                if (mState == Status.NORMAL) {
                    mImgStartPause.performClick()
                } else {

                }
                return super.onSingleTapConfirmed(e)
            }
        })