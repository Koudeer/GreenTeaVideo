package com.koudeer.lib.widget

import android.widget.ImageView
import com.koudeer.lib.R
import com.koudeer.lib.enum.Status

fun ImageView.toggle(type: Int) {
    when (type) {
        Status.PAUSE -> {
            this.setImageResource(R.mipmap.start)
        }
        Status.PLAYING -> {
            this.setImageResource(R.mipmap.pause)
        }
        Status.NORMAL -> {
            this.setImageResource(R.mipmap.pause)
        }
    }
}