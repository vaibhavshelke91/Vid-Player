package com.vaibhav.vplayer.viewmodels

import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vaibhav.vplayer.model.FolderModel
import com.vaibhav.vplayer.utils.videoUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FolderViewModel : ViewModel() {

    private val _data = MutableStateFlow<List<FolderModel>>(emptyList())

    val data = _data.asStateFlow();


    fun load(context: Context){
        viewModelScope.launch {
            _data.value=getAllFolders(context)
        }
    }

    private suspend fun getAllFolders(context: Context):List<FolderModel>{
        return withContext(Dispatchers.IO){
            val list = mutableListOf<FolderModel>()

            val cursor = context.contentResolver.query(videoUri,null,null,null,null)


            while (cursor?.moveToNext() == true){
                try {
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
                }catch (e:Exception){
                    e.printStackTrace()
                }

            }

          /*  try {
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
            }*/

            cursor?.let {
                it.close()
            }
            list
        }
    }
}