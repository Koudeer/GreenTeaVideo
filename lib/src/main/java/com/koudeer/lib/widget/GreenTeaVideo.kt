package com.koudeer.lib.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.TextureView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import com.koudeer.lib.R
import com.koudeer.lib.enum.Status
import com.koudeer.lib.enum.type
import java.util.*

class GreenTeaVideo : FrameLayout, IVideo, View.OnClickListener, View.OnTouchListener {
    val TAG = "GreenTeaVideo"

    private lateinit var mTextureView: TextureView
    private lateinit var mMedia: MediaInterface

    private var mGesture: GestureDetector

    private lateinit var mSurfaceContainer: FrameLayout
    lateinit var mImgStartPause: ImageView
    lateinit var mProgress: ProgressBar
    lateinit var mSeekBar: SeekBar

    private var mTimer: Timer? = null
    private var mTimerTask: ALLTimerTask? = null

    private var mProgressTimer: Timer? = null
    private var mProgressTask: ALLTimerTask? = null

    var mUrl = ""
    var mState: Int = Status.NORMAL
    var ON_PAUSE_PLAYING = 0 //按下home键时，视频状态是处于PAUSE还是PLAYING状态

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        inflate(context, R.layout.layout_video, this)
        mGesture = supportGesture()
        initView()
        initEvent()
    }

    private fun initView() {
        mSurfaceContainer = findViewById(R.id.surface_container)
        mImgStartPause = findViewById(R.id.img_start_pause)
        mProgress = findViewById(R.id.progress)
        mSeekBar = findViewById(R.id.seekbar)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initEvent() {
        mImgStartPause.setOnClickListener(this)
        mSurfaceContainer.setOnTouchListener(this)
        //不然Gesture只会触发longpress
        mSurfaceContainer.isClickable = true
    }

    public fun setUrl(url: String) {
        if (TextUtils.isEmpty(url)) throw IllegalArgumentException("url must not be null")
        this.mUrl = url
    }

    public fun startVideo(url: String) {
        setUrl(url)
        startVideo()
    }

    public fun startVideo() {
        if (TextUtils.isEmpty(mUrl)) throw RuntimeException("pls invoke #setUrl(String) function add video url")
        mMedia = GreenTeaMedia(this)
        addTextureView()
    }

    /**
     * 在Pause生命周期使用
     */
    public fun onLifeCyclePause() {
        when (mState) {
            Status.PLAYING -> {
                mMedia.pause()
                ON_PAUSE_PLAYING = Status.PLAYING
            }
            Status.PAUSE -> {
                mMedia.pause()
                ON_PAUSE_PLAYING = Status.PAUSE
            }
        }
    }

    /**
     * 在Resume生命周期使用
     */
    public fun onLifeCycleResume() {
        when (ON_PAUSE_PLAYING) {
            Status.PLAYING -> {
                mMedia.start()
                mState = Status.PLAYING
            }
            Status.PAUSE -> {
                mMedia.pause()
                mState = Status.PAUSE
            }
        }
        ON_PAUSE_PLAYING = 0
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

    private fun imgClick() {
        Log.d(TAG, "imgClick: ${mState.type}")
        when (mState) {
            Status.NORMAL -> {
                startVideo()
                imgUIToggle(mState)
            }
            Status.PLAYING -> {
                mMedia.pause()
                imgUIToggle(mState)
                mState = Status.PAUSE
            }
            Status.PAUSE -> {
                mMedia.start()
                imgUIToggle(mState)
                mState = Status.PLAYING
            }
        }
        Log.d(TAG, "imgClick: ${mState.type}")
    }

    public fun startControllerViewTimer(): Unit {
        cancelControllerViewTimer()
        mTimer = Timer();
        mTimerTask = ALLTimerTask { allControllerInvisible() }
        mTimer?.schedule(mTimerTask, 5000)
    }

    public fun cancelControllerViewTimer(): Unit {
        mTimer?.cancel()
        mTimerTask?.cancel()
    }

    fun startProgressTimer(): Unit {
        cancelProgressTimer()
        mProgressTimer = Timer()
        mProgressTask = ALLTimerTask { updateSeekProgress() }
        mProgressTimer?.schedule(mProgressTask, 0, 300)
    }

    fun cancelProgressTimer(): Unit {
        mProgressTimer?.cancel()
        mProgressTask?.cancel()
    }

    fun getCurrentPosition(): Long {
        if (mState == Status.PAUSE || mState == Status.PLAYING) {
            return mMedia.getCurrentPosition()
        }
        return 0L
    }

    fun getDuration(): Long {
        if (mState == Status.PAUSE || mState == Status.PLAYING) {
            return mMedia.getDuration()
        }
        return 0L
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_start_pause -> imgClick()
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (v?.id == R.id.surface_container) {

            mGesture.onTouchEvent(event!!)
        }
        return false
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
            //这里先不考虑进度记忆
            if (mState == Status.PREPARE) {
                Log.d(TAG, "onInfo: PLAYING ${Thread.currentThread().name}")
                mState = Status.PLAYING
                mProgress.visibility = INVISIBLE
                startProgressTimer()
            }
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            Log.d(TAG, "onInfo: 开始缓冲")
            //开始缓冲
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            Log.d(TAG, "onInfo: 结束缓冲")
            //结束缓冲
        }
    }

    override fun onSurfaceTexture(mSurfaceTexture: SurfaceTexture) {
        mTextureView.setSurfaceTexture(mSurfaceTexture)
    }

    override fun onCompletion() {
        Log.d(TAG, "onCompletion: ")
    }

    /**
     * 所有TimerTask
     */
    class ALLTimerTask(val fn: () -> Unit) : TimerTask() {

        override fun run() {
            fn.invoke()
        }
    }
}