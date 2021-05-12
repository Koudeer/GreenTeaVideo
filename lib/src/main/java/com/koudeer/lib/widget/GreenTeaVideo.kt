package com.koudeer.lib.widget

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.TextureView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.koudeer.lib.R
import com.koudeer.lib.enum.Status
import com.koudeer.lib.enum.type
import java.lang.RuntimeException

class GreenTeaVideo : FrameLayout, IVideo, View.OnClickListener {
    private val TAG = "GreenTeaVideo"

    private lateinit var mTextureView: TextureView
    private lateinit var mMedia: MediaInterface

    private lateinit var mSurfaceContainer: FrameLayout
    private lateinit var mImgStartPause: ImageView

    private var mUrl = ""
    private var mState: Int = Status.NORMAL

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        inflate(context, R.layout.layout_video, this)
        initView()
        initEvent()
    }

    private fun initView() {
        mSurfaceContainer = findViewById(R.id.surface_container)
        mImgStartPause = findViewById(R.id.img_start_pause)
    }

    private fun initEvent() {
        mImgStartPause.setOnClickListener(this)
    }

    public fun setUp(url: String) {
        if (TextUtils.isEmpty(url)) throw IllegalArgumentException("url must not be null")
        this.mUrl = url
    }

    public fun startVideo(): Unit {
        if (TextUtils.isEmpty(mUrl)) throw RuntimeException("pls invoke #setUp(String) function add video url")
        mMedia = GreenTeaMedia(this)
        addTextureView()
    }

    /**
     * 将TextureView 添加进surface_container
     */
    private fun addTextureView(): Unit {
        Log.d(TAG, "addTextureView: ")
        if (mSurfaceContainer.childCount > 0) mSurfaceContainer.removeAllViews()

        mTextureView = TextureView(context.applicationContext)
        //设置监听
        mTextureView.surfaceTextureListener = mMedia
        mSurfaceContainer.addView(mTextureView)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_start_pause -> startVideo()
        }
    }

    override fun getUrl(): String = mUrl

    /**
     * MediaPlay准备状态回调
     */
    override fun onPrepare() {
        Log.d(TAG, "onPrepare:")
        mState = Status.PREPARE
        mMedia.start()
    }

    /**
     * MediaPlay信息回调
     */
    override fun onInfo(what: Int, extra: Int) {
        Log.d(TAG, "onInfo: $what $extra ${mState.type}")
        //MEDIA_INFO_VIDEO_RENDERING_START 表示mediaplay开始渲染第一帧 说明进入PLAYING状态
        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            if (mState == Status.PREPARE) {
                mState = Status.PLAYING
            }
        }
    }
}