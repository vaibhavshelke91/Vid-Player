package com.vaibhav.vplayer.nav

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.items
import com.vaibhav.vplayer.R
import com.vaibhav.vplayer.activities.FolderVideosActivity
import com.vaibhav.vplayer.model.FolderModel
import com.vaibhav.vplayer.viewmodels.FolderViewModel

@Composable
fun FolderScreen(viewModel : FolderViewModel = viewModel()){

    val context = LocalContext.current
    val state = viewModel.data.collectAsState().value

    LazyColumn(){
        items(state, key = {item -> item.id }){item->
            FolderLinearLayout(folderModel = item)
        }
    }

    DisposableEffect(Unit){
        viewModel.load(context)
        onDispose {
            Log.d("OnDispose","Called")
        }
    }
}


@Composable
fun FolderLinearLayout(folderModel: FolderModel){

    val context = LocalContext.current
    Card(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(10.dp)
        .clickable {
            context.startActivity(
                Intent(context,FolderVideosActivity::class.java)
                    .apply {
                        putExtra("folderId",folderModel.id)
                        putExtra("folderName",folderModel.name)
                    }
            )
        }) {

        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            Image(imageVector = Icons.Default.Folder,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
                contentDescription = "thumb",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
                    .padding(10.dp)


            )

            Column(modifier = Modifier
                .padding(10.dp)
                .weight(1f)) {

                Text(text = "${folderModel.name}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1)

                Spacer(modifier = Modifier.padding(2.dp))

                Text(text = "${folderModel.count} Videos",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1)


            }

            IconButton(onClick = {  }, modifier = Modifier.padding(end = 5.dp),
            ) {
                Icon(
                    painterResource(id = R.drawable.more), contentDescription = "more",
                    tint = MaterialTheme.colorScheme.primary)
            }


        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun FolderLinearList(){
    Card(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(10.dp)
    )
    {

        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            Image(imageVector = Icons.Default.Folder,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
                contentDescription = "thumb",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
                    .padding(10.dp)


            )

            Column(modifier = Modifier
                .padding(10.dp)
                .weight(1f)) {

                Text(text = "Folder",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1)

                Spacer(modifier = Modifier.padding(2.dp))

                Text(text = "15 Videos",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1)


            }

            IconButton(onClick = {  }, modifier = Modifier.padding(end = 5.dp),
            ) {
                Icon(
                    painterResource(id = R.drawable.more), contentDescription = "more",
                    tint = MaterialTheme.colorScheme.primary)
            }


        }
    }
}