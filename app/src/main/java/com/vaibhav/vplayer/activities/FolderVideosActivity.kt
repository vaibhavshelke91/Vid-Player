package com.vaibhav.vplayer.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.vaibhav.vplayer.nav.VideoLinearSingleLayout
import com.vaibhav.vplayer.ui.theme.VPlayerTheme
import com.vaibhav.vplayer.viewmodels.FolderVideosViewModel
import com.vaibhav.vplayer.viewmodels.FolderViewModel


class FolderVideosActivity : ComponentActivity() {
    val folderId by lazy { intent.getLongExtra("folderId",0) }
    val folderName by lazy { intent.getStringExtra("folderName") }
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

                    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
                        rememberTopAppBarState()
                    )
                    Scaffold(
                        contentWindowInsets = WindowInsets.systemBars,
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {

                            TopAppBar(
                                title = {
                                    Text(
                                        text = "$folderName",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                },
                                navigationIcon = { FilledIconButton(onClick = { finish() },
                                    modifier = Modifier.padding(10.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "back"
                                    )
                                }
                                    
                                },
                                
                                scrollBehavior = scrollBehavior
                            )

                        }

                    ) {it->

                        Box(
                            modifier = Modifier
                                .padding(it),
                        ) {
                            FolderMainScreen(id = folderId.toString())
                        }

                    }

                }
            }
        }
    }
}

@Composable
fun FolderMainScreen(id:String,viewModel : FolderVideosViewModel = androidx.lifecycle.viewmodel.compose.viewModel()){
    val state = viewModel.data.collectAsState().value
    val context = LocalContext.current

    LazyColumn(){
        itemsIndexed(state, key = {index,item -> item.id }){ index,item->
            VideoLinearSingleLayout(videoModel = item,index,isFromFolder = true)
        }
    }
    Log.d("Videos","${state.size}")
    DisposableEffect(Unit){
        viewModel.load(context,id)
        onDispose {  }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview4() {
    VPlayerTheme {

    }
}