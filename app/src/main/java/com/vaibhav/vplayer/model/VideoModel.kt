package com.vaibhav.vplayer.model

import android.net.Uri
import java.io.Serializable

class VideoModel(
    val id : Long =0,
    val contentUri : Uri?=null,
    val title :String ="",
    val folder : String="",
    val path : String="",
    val duration : Long=0,
    val size : Long =0,
    val year : Long =0,
    val height : Int =0,
    val width :Int=0,
    val bucketId:Long=0
):Serializable