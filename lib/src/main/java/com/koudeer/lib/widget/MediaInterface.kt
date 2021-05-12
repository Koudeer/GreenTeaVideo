package com.koudeer.lib.widget

import android.graphics.SurfaceTexture
import android.os.Handler
import android.os.HandlerThread
import android.view.TextureView

abstract class MediaInterface(val iVideo: IVideo) : TextureView.SurfaceTextureListener {

    var mSurfaceTexture: SurfaceTexture? = null
    lateinit var mHandler: Handler
    lateinit var mHandlerThread: HandlerThread
    lateinit var mMediaHandler: Handler

    abstract fun prepare()

    abstract fun start()

    abstract fun pause()

    abstract fun isPlaying(): Boolean

    abstract fun release()

    abstract fun getCurrentPosition(): Long

    abstract fun getDuration(): Long

}