package com.koudeer.lib.widget

import android.graphics.SurfaceTexture

interface IVideo {

    fun getUrl(): String

//    @Deprecated(message = "本来用来修复home键之后返回app黑屏的问题")
//    fun onSurfaceTexture(mSurfaceTexture: SurfaceTexture)

    fun onPrepare()

    fun onInfo(what: Int, extra: Int);
}