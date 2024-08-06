package com.vaibhav.vplayer.utils

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Format
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.launch
import java.util.Locale


@Composable
fun PlayerTopBar(name: String,
                 onSubtitleClick : () -> Unit,
                 onAudioClick : () -> Unit){

        Row(modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {

            Icon(modifier = Modifier.padding(start = 10.dp)
                ,imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "down",
                tint = Color.White)

            Text(text = "$name",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp, end = 10.dp),
                maxLines = 2,
                fontWeight = FontWeight.Bold,
                color = Color.White)

            Icon(modifier = Modifier
                .padding(end = 20.dp)
                .clickable {
                    onAudioClick.invoke()
                },
                imageVector = Icons.Default.Audiotrack,
                contentDescription = "audio",
                tint = Color.White)

            Icon(modifier = Modifier
                .padding(end = 20.dp)
                .clickable {
                    onSubtitleClick.invoke()
                },
                imageVector = Icons.Default.Subtitles,
                contentDescription = "subtitle",
                tint = Color.White)

            Icon(modifier = Modifier
                .padding(end = 10.dp)
                .clickable {

                },
                imageVector = Icons.Default.MoreVert,
                contentDescription = "more",
                tint = Color.White)
        }

}


@Composable
fun PlayerCentralController(exoPlayer: ExoPlayer){

    var isPlaying by remember {
        mutableStateOf(exoPlayer.isPlaying)
    }
        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically) {

            Icon(modifier = Modifier
                .padding(start = 10.dp)
                .height(55.dp)
                .width(55.dp)
                .background(
                    Color.Black.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .clickable {
                    exoPlayer.seekToPreviousMediaItem()
                }
                .padding(5.dp),
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "prev",
                tint = Color.White)

            Icon(modifier = Modifier
                .padding(start = 10.dp)
                .height(65.dp)
                .width(65.dp)
                .background(
                    Color.Black.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .padding(5.dp)
                .clickable {
                    if (isPlaying) {
                        isPlaying = false
                        exoPlayer.pause()
                    } else {
                        isPlaying = true
                        exoPlayer.play()
                    }
                },
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "play",
                tint = Color.White)

            Icon(modifier = Modifier
                .padding(end = 10.dp)
                .height(55.dp)
                .width(55.dp)
                .background(
                    Color.Black.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .clickable {
                    exoPlayer.seekToNextMediaItem()
                }
                .padding(5.dp),
                imageVector = Icons.Default.SkipNext,
                contentDescription = "next",
                tint = Color.White)
        }

}


@Composable
fun BottomController(exoPlayer: ExoPlayer){

    val lifecycleOwner = rememberUpdatedState(newValue = LocalLifecycleOwner.current)

    val currentProgress = remember {
        mutableStateOf("00:00")
    }
    val finalProgress = remember {
        mutableStateOf("00:00")
    }
    val progress = remember {
        mutableStateOf(0L)
    }
    val maxProgress = remember {
        mutableStateOf(0L)
    }

    DisposableEffect(key1 = Unit){
       lifecycleOwner.value.lifecycleScope.launch {
            exoPlayer.currentPositionFlow().collect{
                progress.value=it
                currentProgress.value=it.toFormattedTime()
                maxProgress.value=exoPlayer.duration
                finalProgress.value=exoPlayer.duration.toFormattedTime()
            }
        }
        onDispose {  }
    }


    Row(modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
        .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically) {

        Column(modifier = Modifier.fillMaxSize()) {

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "${currentProgress.value} / ${finalProgress.value}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp)

                Icon(imageVector = Icons.Default.Fullscreen,
                    tint = Color.White,
                    contentDescription = "fullscreen")
            }



            Slider(modifier = Modifier.padding(5.dp, bottom = 10.dp)
                ,value = progress.value.toFloat(),
                onValueChange = {
                                progress.value=it.toLong()
                },
                onValueChangeFinished = {
                                        exoPlayer.seekTo(progress.value)
                },
                enabled = true,
                valueRange = 0f..maxProgress.value.toFloat()
            )
        }
    }
}

@Composable
fun PlayerController(exoPlayer: ExoPlayer,name:String,onAudioClick: () -> Unit,onSubtitleClick: () -> Unit){

        Column(modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
            .windowInsetsPadding(WindowInsets.systemBars),
            verticalArrangement = Arrangement.SpaceBetween) {
            PlayerTopBar(name,onSubtitleClick, onAudioClick)
            PlayerCentralController(exoPlayer)
            BottomController(exoPlayer)
        }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubTitleContainer(subtitles:List<Format>,onClose : () -> Unit,onSelect : (Format) -> Unit){
    val brush = Brush.horizontalGradient(listOf(
        Color.Transparent,   Color.Black
    ))
    var checkedId by remember {
        mutableStateOf("0")
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .background(brush),
        horizontalAlignment = Alignment.End
        ) {

        Row( modifier =Modifier
            .padding(top = 15.dp)
            .width(250.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Subtitles",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 5.dp))

            IconButton(onClick = { onClose.invoke() }) {
                Icon(imageVector = Icons.Default.Close,
                    tint = Color.White,
                    contentDescription = "")
            }
        }

        LazyColumn(){
            items(subtitles,key = {item ->item.id ?: ""}){item->
                OutlinedCard(onClick = { checkedId = item.id ?: "0"
                                       onSelect.invoke(item)},
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .width(250.dp)
                        .height(50.dp)
                        .padding(5.dp)
                        ) {
                    Row(modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround) {
                        Text(text = Locale("${item.language}").displayLanguage,
                            maxLines = 1,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 9.dp, end = 9.dp))

                        Checkbox(checked = checkedId == (item.id ?: "0"),
                            onCheckedChange = {
                                              checkedId=item.id ?: "0"
                                onSelect.invoke(item)
                            },
                        )
                    }

                }
            }
        }
    }

}

@Composable
fun SeeController(right:Int,left:Int){
    Row(modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically) {


        Text(text = if (left==0) "" else "-${left/1000} Seconds",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 20.sp)
        Text(text = if (right==0) "" else "+${right/1000} Seconds" ,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 20.sp)
    }
}



@Preview(showBackground = true, showSystemUi = true,
    device = "spec:parent=pixel_5,orientation=landscape"
)
@Composable
fun PlayerControllerPreview(){

}