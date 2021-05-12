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

        mMediaPlayer = MediaPlayer()

        mMediaHandler.post {
            try {
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
        return false
    }

    override fun release() {

    }

    override fun getCurrentPosition(): Long {
        return 0L
    }

    override fun getDuration(): Long {
        return 0L
    }

    //SurfaceTextureListener
    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surface
            prepare()
        } else {
            iVideo.onSurfaceTexture(surface)
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
    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        iVideo.onInfo(what, extra)
        return false
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
    }

    override fun onSeekComplete(mp: MediaPlayer?) {
    }

    override fun onVideoSizeChanged(mp: MediaPlayer?, width: Int, height: Int) {

    }


}