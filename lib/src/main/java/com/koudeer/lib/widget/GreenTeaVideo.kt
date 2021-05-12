package com.koudeer.lib.widget

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.TextureView
import android.widget.FrameLayout
import com.koudeer.lib.R
import com.koudeer.lib.enum.Status
import com.koudeer.lib.enum.show

class GreenTeaVideo : FrameLayout, IVideo {
    private val TAG = "GreenTeaVideo"

    private lateinit var mTextureView: TextureView
    private lateinit var mMedia: MediaInterface

    private lateinit var mSurfaceContainer: FrameLayout

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
    }

    private fun initView(): Unit {
        mSurfaceContainer = findViewById(R.id.surface_container)
    }

    public fun setUp(url: String) {
        if (TextUtils.isEmpty(url)) throw IllegalArgumentException("url must not be null")
        this.mUrl = url
    }

    public fun startVideo(): Unit {
        mMedia = GreenTeaMedia(this)
        addTextureView()
    }

    private fun addTextureView(): Unit {
        Log.d(TAG, "addTextureView: ")
        if (mSurfaceContainer.childCount > 0) mSurfaceContainer.removeAllViews()

        mTextureView = TextureView(context.applicationContext)
        //设置监听
        mTextureView.surfaceTextureListener = mMedia
        mSurfaceContainer.addView(mTextureView)
    }

    override fun getUrl(): String {
        return mUrl
    }

    override fun onSurfaceTexture(mSurfaceTexture: SurfaceTexture) {
        Log.d(TAG, "onSurfaceTexture: ")
        mTextureView.setSurfaceTexture(mSurfaceTexture)
    }

    /**
     * 准备状态回调
     */
    override fun onPrepare() {
        Log.d(TAG, "onPrepare:")
        mState = Status.PREPARE
        mMedia.start()
    }

    override fun onInfo(what: Int, extra: Int) {
        Log.d(TAG, "onInfo: $what $extra")
        //MEDIA_INFO_VIDEO_RENDERING_START 表示mediaplay开始渲染第一帧
        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){

        }
    }
}