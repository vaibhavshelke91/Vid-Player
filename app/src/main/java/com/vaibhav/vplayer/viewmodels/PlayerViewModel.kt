package com.vaibhav.vplayer.viewmodels

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow

class PlayerViewModel : ViewModel() {

    private lateinit var context : Context
    private var _exoplayer = MutableStateFlow<ExoPlayer?>(null)
    val exoPlayer = _exoplayer

    fun init(context: Context){
        this.context=context
        _exoplayer.value = ExoPlayer.Builder(context).build()
    }

}