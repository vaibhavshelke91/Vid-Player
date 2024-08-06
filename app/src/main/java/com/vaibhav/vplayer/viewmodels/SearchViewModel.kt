package com.vaibhav.vplayer.viewmodels

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vaibhav.vplayer.model.FolderModel
import com.vaibhav.vplayer.model.VideoModel
import com.vaibhav.vplayer.utils.videoUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class SearchViewModel : ViewModel() {
    private val _text = MutableStateFlow<String>("")
    val text = _text.asStateFlow()

    private val _videos = MutableStateFlow<List<VideoModel>>(emptyList())
    private val _folders = MutableStateFlow<List<FolderModel>>(emptyList())

    val videos = _videos.asStateFlow()
    val folders = _folders.asStateFlow()

    fun setQuery(context: Context,text :String){
        _text.value=text
        if (text.isEmpty()){
            _videos.value= emptyList()
            _folders.value= emptyList()
            return
        }
        viewModelScope.launch {
            _videos.value=getAllVideos(context,text)
        }
        viewModelScope.launch {
            _folders.value=getAllFolders(context,text)
        }
    }



    private suspend fun getAllVideos(context: Context, query: String):List<VideoModel>{
        return withContext(Dispatchers.IO){
            val list = mutableListOf<VideoModel>()
            val selection = "${MediaStore.Video.Media.DISPLAY_NAME} like ?"
            val cursor = context.contentResolver.query(videoUri,null,selection, arrayOf("%${query}%"),null)

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
            }catch (e: Exception){
                e.printStackTrace()
            }

            cursor?.close()

            list
        }
    }

    private suspend fun getAllFolders(context: Context,query: String):List<FolderModel>{
        return withContext(Dispatchers.IO){
            val list = mutableListOf<FolderModel>()
            val selection = "${MediaStore.Video.Media.BUCKET_DISPLAY_NAME} like ?"
            val cursor = context.contentResolver.query(videoUri,null,selection, arrayOf("%${query}%"),null)

            try {
                while (cursor?.moveToNext() == true){
                    val folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                    val folderId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID))
                    // val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))

                    val model = FolderModel(folderId,folder)
                    if (list.contains(model)){
                        val index = list.indexOf(model)
                        list[index].count++
                    }else{
                        model.count++
                        list.add(model)
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }

            cursor?.let {
                it.close()
            }
            list
        }
    }


}