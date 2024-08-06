package com.vaibhav.vplayer.viewmodels

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vaibhav.vplayer.utils.V_LATEST

import com.vaibhav.vplayer.model.VideoModel
import com.vaibhav.vplayer.utils.videoUri

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.lang.NullPointerException

class VideoViewModel : ViewModel() {
    private val _data = MutableStateFlow<List<VideoModel>>(emptyList())
    val data = _data.asStateFlow()

    fun load(context: Context,sortOrder:String= V_LATEST){
        viewModelScope.launch {
            _data.value=getAllVideos(context,sortOrder)
        }
    }

    private suspend fun getAllVideos(context: Context,sortOrder: String):List<VideoModel>{
        return withContext(Dispatchers.IO){
            val list = mutableListOf<VideoModel>()
            val cursor = context.contentResolver.query(videoUri,null,null,null,sortOrder)

            try {
                while (cursor?.moveToNext() == true){
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)) ?: 0
                    val contentUri = ContentUris.withAppendedId(videoUri,id)
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)) ?: ""
                    val folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)) ?: ""
                    val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)) ?: ""
                    val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)) ?: 0
                    val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)) ?: 0
                    val year = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.YEAR)) ?: 0
                    val height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)) ?: 0
                    val width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)) ?: 0
                    val folderId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)) ?: 0
                    list.add(
                        VideoModel(
                            id, contentUri, title, folder, path, duration, size, year,height,width,folderId
                        )
                    )
                }
            }catch (e:NullPointerException){
                e.printStackTrace()
            }
            catch (e:Exception){
                e.printStackTrace()
            }

            cursor?.close()

            list
        }
    }
}