package com.vaibhav.vplayer.nav

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.RequestBuilderTransform
import com.bumptech.glide.integration.compose.Transition
import com.bumptech.glide.integration.compose.placeholder
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.vaibhav.vplayer.R
import com.vaibhav.vplayer.activities.PlayerActivity
import com.vaibhav.vplayer.model.VideoModel
import com.vaibhav.vplayer.utils.FOLDER_ID
import com.vaibhav.vplayer.utils.INDEX
import com.vaibhav.vplayer.utils.IS_FROM_FOLDER
import com.vaibhav.vplayer.utils.getSizeFromBytes
import com.vaibhav.vplayer.utils.getTimeFromMillis
import com.vaibhav.vplayer.viewmodels.VideoViewModel

@Composable
fun HomeScreen(viewModel : VideoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()){

    val context = LocalContext.current
    val state = viewModel.data.collectAsState().value

    LazyColumn(){
        itemsIndexed(state, key = {index,item -> item.id }){ index,item->
            VideoLinearSingleLayout(videoModel = item,index)
        }
    }
    Log.d("Videos","${state.size}")
    DisposableEffect(Unit){
        viewModel.load(context)
        onDispose {  }
    }
    
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun VideoLinearSingleLayout(videoModel: VideoModel,index:Int,isFromFolder :Boolean=false){
    val context = LocalContext.current
    val size by remember {
        mutableStateOf(getSizeFromBytes(videoModel.size))
    }
    val duration by remember {
        mutableStateOf(getTimeFromMillis(videoModel.duration))
    }

    Card(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(10.dp).
    clickable {
        context.startActivity(
            Intent(
                context,
                PlayerActivity::class.java
            ).apply {
                Log.d("Bucket Id",videoModel.bucketId.toString())
                putExtra(INDEX,index)
                putExtra(FOLDER_ID,videoModel.bucketId)
                putExtra(IS_FROM_FOLDER,isFromFolder)
            }
        )
    }) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            GlideImage(model = videoModel.path,
                contentDescription = "thumb",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp)),

            )

            Column(modifier = Modifier
                .padding(10.dp)
                .weight(1f)) {
                Text(text = "${videoModel.title}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1)

                Text(text = "${videoModel.folder}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1)

                Text(text = "$duration   |   $size",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1)

            }

            IconButton(onClick = {  }, modifier = Modifier.padding(end = 5.dp),
            ) {
                Icon(painterResource(id = R.drawable.more), contentDescription = "more",
                    tint = MaterialTheme.colorScheme.primary)
            }


        }
    }

}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HomePreview(){
   /* VideoLinearSingleLayout(videoModel = VideoModel(title = "This is Video Title",
        folder = "Videos", duration = 4516664264, size = 41668785668))

    */
    Test()
}

@Composable
fun Test(){

    Card(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = R.drawable.shine_or_go_crazy),
                contentDescription = "thumb",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))

            )

            Column(modifier = Modifier
                .padding(10.dp)
                .weight(1f)) {
                Text(text = "Title of Video",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1)

                Text(text = "Videos",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1)

                Text(text = "45:30   |   103 MB",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1)

            }

            IconButton(onClick = {  }, modifier = Modifier.padding(end = 5.dp),
                ) {
                Icon(painterResource(id = R.drawable.more), contentDescription = "more",
                    tint = MaterialTheme.colorScheme.primary)
            }


        }
    }
}