package com.vaibhav.vplayer.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vaibhav.vplayer.nav.FolderLinearLayout
import com.vaibhav.vplayer.nav.VideoLinearSingleLayout
import com.vaibhav.vplayer.ui.theme.VPlayerTheme
import com.vaibhav.vplayer.viewmodels.SearchViewModel
import kotlinx.coroutines.flow.asStateFlow


class SearchActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val context = LocalContext.current
                    val viewModel : SearchViewModel = viewModel()


                    Scaffold(
                        contentWindowInsets = WindowInsets.systemBars,
                        // modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {

                            TopAppBar(
                                modifier = Modifier.fillMaxWidth(),
                                title = {
                                    var text = viewModel.text.collectAsState().value
                                    TextField(
                                        value = text,
                                        onValueChange = {
                                            viewModel.setQuery(context,it)
                                        },
                                        placeholder = { Text(text = "Search Here")},
                                        modifier = Modifier.fillMaxWidth(),
                                        textStyle = TextStyle(fontSize = 16.sp),
                                        trailingIcon = {
                                                       if (text.isNotBlank()){
                                                           Icon(
                                                               imageVector = Icons.Default.Close,
                                                               contentDescription = "close",
                                                               modifier = Modifier.clickable {
                                                                   viewModel.setQuery(context,"")
                                                               }
                                                           )
                                                       }else{
                                                           null
                                                       }
                                        },

                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent
                                        )
                                    )
                                },
                                navigationIcon = { FilledIconButton(onClick = { finish() },
                                    modifier = Modifier.padding(5.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "back"
                                    )
                                }

                                },
                            )

                        }

                    ) { it ->

                        Column(
                            modifier = Modifier
                                .padding(it),
                        ) {

                            val videos = viewModel.videos.collectAsState().value
                            val folders = viewModel.folders.collectAsState().value

                            LazyColumn(){
                                itemsIndexed(videos, key = {index,item -> item.id }){ index,item->
                                    VideoLinearSingleLayout(videoModel = item,index)
                                }
                                items(folders,key = {item -> item.id }){
                                    FolderLinearLayout(folderModel = it)
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview5() {
    VPlayerTheme {

    }
}