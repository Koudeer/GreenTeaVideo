package com.koudeer.lib.widget

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.Surface
import java.lang.reflect.Method

class GreenTeaMedia(iVideo: IVideo) : MediaInterface(iVideo), MediaPlayer.OnPreparedListener,
    MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnInfoListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener,
    MediaPlayer.OnVideoSizeChangedListener {

    private val TAG = "GreenTeaMedia"

    private lateinit var mMediaPlayer: MediaPlayer

    override fun prepare() {
        Log.d(TAG, "GreenTeaMedia prepare: ${iVideo.getUrl()}")
        mHandler = Handler(Looper.getMainLooper())
        mHandlerThread = HandlerThread("VIDEO")
        mHandlerThread.start()
        mMediaHandler = Handler(mHandlerThread.looper)

        mMediaHandler.post {
            try {
                mMediaPlayer = MediaPlayer()

                mMediaPlayer.setOnPreparedListener(this)
                mMediaPlayer.setOnBufferingUpdateListener(this)
                mMediaPlayer.setOnInfoListener(this)
                mMediaPlayer.setOnErrorListener(this)
                mMediaPlayer.setOnCompletionListener(this)
                mMediaPlayer.setOnSeekCompleteListener(this)
                mMediaPlayer.setOnVideoSizeChangedListener(this)
                mMediaPlayer.setDataSource(iVideo.getUrl())
                mMediaPlayer.prepareAsync()
                mMediaPlayer.setSurface(Surface(mSurfaceTexture))
            } catch (e: Exception) {
                Log.d(TAG, "prepare: ${e.message}")
                e.printStackTrace()
            }
        }

    }

    override fun start() {
        mMediaHandler.post { mMediaPlayer.start() }
    }

    override fun pause() {
        mMediaHandler.post { mMediaPlayer.pause() }
    }

    override fun isPlaying(): Boolean {
        return mMediaPlayer.isPlaying
    }

    override fun release() {
        mMediaHandler.post { mMediaPlayer.release() }
    }

    /**
     * 获取视频当前位置
     */
    override fun getCurrentPosition(): Long {
        return mMediaPlayer.currentPosition.toLong()
    }

    /**
     * 获取总时长
     */
    override fun getDuration(): Long {
        return mMediaPlayer.duration.toLong()
    }

    //SurfaceTextureListener
    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surface
            prepare()
        } else {
//            iVideo.onSurfaceTexture(surface)
            //修复 home键之后返回app导致黑屏问题
            mMediaHandler.post { mMediaPlayer.setSurface(Surface(surface)) }
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

    }
    //SurfaceTextureListener

    override fun onPrepared(mp: MediaPlayer?) {
        Log.d(TAG, "onPrepared: GreenTeaMedia")
        mHandler.post { iVideo.onPrepare() }
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        Log.d(TAG, "onBufferingUpdate: $percent")
    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d(TAG, "onInfo: $what $extra")
        iVideo.onInfo(what, extra)
        return false
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d(TAG, "onError: $what $extra")
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Log.d(TAG, "onCompletion: ")
    }

    override fun onSeekComplete(mp: MediaPlayer?) {
        Log.d(TAG, "onSeekComplete: ")
    }

    override fun onVideoSizeChanged(mp: MediaPlayer?, width: Int, height: Int) {
        Log.d(TAG, "onVideoSizeChanged: $width $height")
    }


}