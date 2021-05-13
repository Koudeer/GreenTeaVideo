package com.koudeer.lib.widget

import android.graphics.SurfaceTexture
import android.view.TextureView

interface IVideo {

    fun getUrl(): String

    fun onSurfaceTexture(mSurfaceTexture: SurfaceTexture)

    fun onPrepare()

    fun onInfo(what: Int, extra: Int)

    fun onCompletion()

}