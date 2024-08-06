package com.vaibhav.vplayer.activities

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.compose.EqualityDelegate
import coil.request.ImageRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.placeholder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.vaibhav.vplayer.model.VideoModel
import com.vaibhav.vplayer.ui.theme.VPlayerTheme
import com.vaibhav.vplayer.utils.BlurTransformationCoil
import com.vaibhav.vplayer.utils.FOLDER_ID
import com.vaibhav.vplayer.utils.INDEX
import com.vaibhav.vplayer.utils.IS_FROM_FOLDER
import com.vaibhav.vplayer.utils.PlayerController
import com.vaibhav.vplayer.utils.SeeController
import com.vaibhav.vplayer.utils.SubTitleContainer
import com.vaibhav.vplayer.utils.currentPositionFlow
import com.vaibhav.vplayer.viewmodels.FolderVideosViewModel
import com.vaibhav.vplayer.viewmodels.VideoViewModel
import jp.wasabeef.transformers.glide.BlurTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class PlayerActivity : ComponentActivity() {

    private val index by lazy { intent.getIntExtra(INDEX, 0) }
    private val folderId by lazy { intent.getLongExtra(FOLDER_ID, 0) }
    private val isFromFolder by lazy { intent.getBooleanExtra(IS_FROM_FOLDER, false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            VPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExoPlayerComp(index = index, folderId = folderId, isFromFolder = isFromFolder)
                }
            }
        }
        //  hideSystemUI()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d("New Configurations", newConfig.toString())
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("Orientation", "Landscape")
            hideSystemBars()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d("Orientation", "Portrait")
            showSystemBars()
        }
        super.onConfigurationChanged(newConfig)
    }

    fun hideSystemBars() {
        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    fun showSystemBars() {
        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
    }
}

@kotlin.OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@OptIn(UnstableApi::class)
@Composable
fun ExoPlayerComp(
    index: Int = 0,
    folderId: Long = 0,
    isFromFolder: Boolean = false
) {

    val context = LocalContext.current
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    val configuration = LocalConfiguration.current
    val viewModel: VideoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val folderViewModel: FolderVideosViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    val exoPlayer = rememberExoplayer()

    var path by remember {
        mutableStateOf("")
    }

    var name by remember {
        mutableStateOf("")
    }

    var frameDuration by remember {
        mutableStateOf(0L)
    }



    /*GlideImage(
        model = path,
        contentDescription = "thumb",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .blur(90.dp),
        transition = CrossFade(animationSpec = tween(3000))
    ) {

        it.frame(10000)
    }*/

    var currentTime by remember { mutableStateOf(0L) }
    var prevDrawable by remember {
        mutableStateOf<Drawable?>(null)
    }

    key(currentTime){
        Log.d("Current Frame Time","$currentTime")
        GlideImage(
            model = path, contentDescription = "",
            contentScale = ContentScale.Crop,
            loading = placeholder(prevDrawable),
            modifier = Modifier
                .fillMaxSize()
                .blur(60.dp),

            ){
            it.override(50,50)
            it.transition(DrawableTransitionOptions.withCrossFade())
            it.frame(currentTime*1000)
            it.addListener(object :RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    prevDrawable=resource
                    return true
                }

            })
        }
    }


    /*
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(backGroundImage)
            .crossfade(5000)
            .build(),

        onSuccess = {

        },
        contentDescription = "bg",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
            .blur(90.dp),

    )*/


    var autoPlay by rememberSaveable { mutableStateOf(true) }
    var currentIndex by rememberSaveable { mutableStateOf(index) }
    var position by rememberSaveable { mutableStateOf(0L) }
    var isFirstTime by rememberSaveable {
        mutableStateOf(true)
    }

    fun updateState() {
        autoPlay = exoPlayer.playWhenReady
        currentIndex = exoPlayer.currentMediaItemIndex
        position = exoPlayer.contentPosition
        isFirstTime = false
    }


    var totalDuration by remember { mutableStateOf(0L) }



    var bufferedPercentage by remember { mutableStateOf(0) }

    var subtitleList by remember {
        mutableStateOf(emptyList<Format>())
    }

    var audioList by remember {
        mutableStateOf(emptyList<Format>())
    }


    DisposableEffect(
        key1 = exoPlayer,
        effect = {
            Log.d("DisposableEffect", "Effect Called")
            fun intExoplayer(list: List<VideoModel>) {
                for (i in list) {
                    Log.d("ExoplayerMediaItem", "${i.title}")
                    exoPlayer.addMediaItem(MediaItem.Builder()
                        .setUri(Uri.parse(i.path))
                        .setMediaMetadata(MediaMetadata.Builder()
                            .setTitle(i.title)
                            .setReleaseYear(i.year.toInt())
                            .setExtras(Bundle().apply {
                                putString("path", i.path)
                                putString("name", i.title)
                            }

                            )
                            .build()).build())
                }
                exoPlayer.apply {
                    playWhenReady = autoPlay
                    seekTo(currentIndex, position)
                    prepare()
                }
            }

            if (!isFromFolder) {
                if (isFirstTime) {
                    viewModel.load(context)
                }
                viewModel.data.asLiveData().observe(lifecycleOwner.value) {
                    intExoplayer(it)
                }
            } else {
                Log.d("Folder Id", folderId.toString())
                if (isFirstTime) {
                    folderViewModel.load(context, folderId.toString())
                }
                folderViewModel.data.asLiveData().observe(lifecycleOwner.value) {
                    intExoplayer(it)
                    Log.d("FolderData", "${it}")
                }
            }

            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {

                        exoPlayer.play()
                    }

                    Lifecycle.Event.ON_PAUSE -> {
                        updateState()
                        exoPlayer.pause()
                    }

                    else -> {}
                }
            }

            val playerListeners = object : Player.Listener {

                override fun onTracksChanged(tracks: Tracks) {
                    super.onTracksChanged(tracks)
                    val trackGroup = tracks.groups
                    Log.d("Tracks", "${trackGroup}")
                    val subTitleList = mutableListOf<Format>()
                    val audioListNew = mutableListOf<Format>()
                    for (i in 0 until trackGroup.size) {
                        val tg = trackGroup[i]
                        for (j in 0 until tg.length) {
                            val format = tg.getTrackFormat(j)
                            Log.d("Tracks Format", "${format.toString()}")
                            if (format.sampleMimeType?.equals(
                                    "application/x-subrip",
                                    ignoreCase = true
                                ) == true
                            ) {
                                subTitleList.add(format)
                            }
                            if (format.sampleMimeType?.contains("audio") == true) {
                                audioListNew.add(format)
                            }
                        }
                    }
                    subtitleList = subTitleList
                    audioList = audioListNew
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    Log.d("onMediaMetadataChanged", "Called")
                    path = mediaMetadata.extras?.getString("path", "") ?: ""
                    name = mediaMetadata.extras?.getString("name", "") ?: ""
                    super.onMediaMetadataChanged(mediaMetadata)
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    Log.d("MediaItemTransition", "Called")
                    super.onMediaItemTransition(mediaItem, reason)
                }

                override fun onEvents(player: Player, events: Player.Events) {

                    super.onEvents(player, events)
                    totalDuration = player.duration.coerceAtLeast(0L)
                    currentTime = player.currentPosition.coerceAtLeast(0L)
                    bufferedPercentage = player.bufferedPercentage

                    /*
                    lifecycleOwner.value.lifecycleScope.launch {
                        backGroundImage = getVideoFrame(context, Uri.parse(path),currentTime)
                    }*/


                    Log.d("Player Events", "${currentTime} : ${totalDuration}")
                }

                override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                    Log.d("TimeLine", "Called")
                    super.onTimelineChanged(timeline, reason)
                }
            }

            /*  val progressJob = lifecycleOwner.value.lifecycleScope.launch {
                  exoPlayer.currentPositionFlow().collect{
                      Log.d("Current Duration","$it")
                  }
              }*/


            val lifecycle = lifecycleOwner.value.lifecycle
            lifecycle.addObserver(observer)
            exoPlayer.addListener(playerListeners)

            onDispose {
                updateState()
                exoPlayer.removeListener(playerListeners)
                exoPlayer.release()
                lifecycle.removeObserver(observer)
            }
        }
    )

    var isControllerVisible by remember {
        mutableStateOf(false)
    }
    var isSeekControllerVisible by remember {
        mutableStateOf(false)
    }

    var isSubtitleControllerVisible by remember {
        mutableStateOf(false)
    }

    val interactionSource = remember { MutableInteractionSource() }

    var clickCount by remember { mutableStateOf(0) }
    var lastClickTime by remember { mutableStateOf(0L) }
    val clickThreshold = 300L

    var rightClick by remember {

        mutableStateOf(0)
    }

    var leftClick by remember {

        mutableStateOf(0)
    }

    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                keepScreenOn = true
                layoutParams =
                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                useController = false
            }


        },
        modifier = Modifier
            .fillMaxSize()
            .indication(interactionSource, rememberRipple())
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        val press = PressInteraction.Press(offset)
                        interactionSource.emit(press)
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastClickTime > clickThreshold) {
                            clickCount = 1
                        } else {
                            clickCount++
                        }
                        lastClickTime = currentTime
                        interactionSource.emit(PressInteraction.Release(press))
                        delay(200)
                        when (clickCount) {
                            1 -> {
                                isControllerVisible = !isControllerVisible
                            }

                            else -> {
                                isControllerVisible = false
                                isSubtitleControllerVisible = false
                                isSeekControllerVisible = true
                                val left = size.width / 2
                                if (offset.x > left) {
                                    // Right
                                    rightClick += 10000
                                    exoPlayer.seekTo(exoPlayer.currentPosition + 10000)
                                } else {
                                    // Left
                                    leftClick += 10000
                                    exoPlayer.seekTo(exoPlayer.currentPosition - 10000)
                                }
                            }
                        }
                    }
                )
            }
    )

    LaunchedEffect(clickCount) {
        if (clickCount > 0 && clickCount != 1) {
            delay(600)
            Log.d("ClickEffect", "Ended")
            isSeekControllerVisible = false
            rightClick = 0
            leftClick = 0
        }
    }


    AnimatedVisibility(
        visible = isControllerVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        val list = Brush.verticalGradient(
            listOf(
                Color.Black.copy(alpha = 0.9f), Color.Transparent, Color.Black.copy(alpha = 0.9f)
            )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(list)
        ) {
            PlayerController(exoPlayer, name,
                onAudioClick = {


                }, onSubtitleClick = {
                    isSubtitleControllerVisible = true

                })
        }

    }

    AnimatedVisibility(visible = isSeekControllerVisible) {
        SeeController(rightClick, leftClick)
    }
    val density = LocalDensity.current

    AnimatedVisibility(visible = isSubtitleControllerVisible,
        enter = slideInHorizontally(initialOffsetX = {
            it / 2
        }),
        exit = slideOutHorizontally(targetOffsetX = {
            it / 2
        })
    ) {

        isControllerVisible = false
        isSeekControllerVisible = false
        SubTitleContainer(subtitles = subtitleList,
            onSelect = {

            }, onClose = {
                isSubtitleControllerVisible = false
            })

    }

    LaunchedEffect(key1 = isControllerVisible) {
        if (isControllerVisible && exoPlayer.isPlaying) {
            delay(3000)
            isControllerVisible = !isControllerVisible
        }
    }


    var isPlaying by remember { mutableStateOf(true) }


}


@Composable
fun rememberExoplayer(): ExoPlayer {
    val context = LocalContext.current
    return remember {
        ExoPlayer.Builder(context).build()
    }
}


suspend fun getVideoFrame(context: Context?, uri: Uri?, time: Long): Bitmap? {
    return withContext(Dispatchers.IO) {
        var bitmap: Bitmap? = null
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            bitmap = retriever.getFrameAtTime(
                TimeUnit.MICROSECONDS.convert(time, TimeUnit.MILLISECONDS),
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            )
        } catch (ex: RuntimeException) {
            ex.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (ex: RuntimeException) {
                ex.printStackTrace()
            }
        }
        bitmap
    }

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    VPlayerTheme {

    }
}