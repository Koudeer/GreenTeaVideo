package com.koudeer.lib.widget

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import com.koudeer.lib.R
import com.koudeer.lib.enum.Status
import com.koudeer.lib.enum.type

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
//            this.mImgStartPause.visibility = View.INVISIBLE
        }
        Status.PLAYING -> {
            this.mImgStartPause.setImageResource(R.mipmap.pause)
//            this.mImgStartPause.visibility = View.INVISIBLE
        }
    }
}

fun GreenTeaVideo.supportGesture(): GestureDetector =
    GestureDetector(context.applicationContext,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                Log.d(TAG, "onDoubleTap: ${mState.type}")
                mImgStartPause.performClick()
                imgUIToggle(mState)
                return super.onDoubleTap(e)
            }

            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                Log.d(TAG, "onSingleTapConfirmed: ")
                if (mState == Status.NORMAL) {
                    mImgStartPause.performClick()
                } else {
                    //显示所有控制器
                    allControllerEvent()
                }
                return super.onSingleTapConfirmed(e)
            }
        })

fun GreenTeaVideo.allControllerEvent() {
    if (mImgStartPause.visibility == View.INVISIBLE) {
        mImgStartPause.visibility = View.VISIBLE
        startControllerViewTimer()
    } else {
        mImgStartPause.visibility = View.INVISIBLE
    }
}

fun GreenTeaVideo.allControllerInvisible(): Unit {
    post {
        mImgStartPause.visibility = View.INVISIBLE
    }
}