package com.vaibhav.vplayer.utils

import android.net.Uri
import android.os.Build
import android.provider.MediaStore

const val V_NAME=MediaStore.Video.Media.DISPLAY_NAME+" ASC"
const val V_NAME_REVERSE=MediaStore.Video.Media.DISPLAY_NAME+" DESC"
const val V_LATEST=MediaStore.Video.Media.DATE_ADDED+" DESC"
const val V_OLDEST=MediaStore.Video.Media.DATE_ADDED+" ASC"
const val V_SIZE=MediaStore.Video.Media.SIZE+" DESC"
const val V_SIZE_REVERSE=MediaStore.Video.Media.SIZE+" ASC"

const val INDEX = "index"
const val FOLDER_ID="folder_id"
const val IS_FROM_FOLDER="is_from_folder"

val videoUri: Uri =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }