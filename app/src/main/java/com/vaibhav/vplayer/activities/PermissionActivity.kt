package com.vaibhav.vplayer.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vaibhav.vplayer.R

import com.vaibhav.vplayer.ui.theme.VPlayerTheme

class PermissionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SplashUi()
                }
            }
        }
    }
}


@Composable
fun SplashUi(modifier: Modifier = Modifier) {

    val activity = (LocalContext.current as? Activity)

    var isPermission by remember {
        mutableStateOf(false)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            isPermission = it
        })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 30.dp)
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.systemBars)

    ) {
        Text(
            text = "Hello there !",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.padding(top = 5.dp))
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Welcome to ",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Text(
                text = "Vid Player",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Divider(
            modifier = Modifier.padding(top = 20.dp)
        )

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 20.dp),
            elevation = CardDefaults.cardElevation(
                5.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Storage Permission",
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.padding(top = 10.dp))

                Text(
                    text = "The app needs permission to access your device storage for playing videos",
                    lineHeight = 20.sp,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.padding(top = 10.dp))

                OutlinedButton(
                    onClick = {
                              if (Build.VERSION.SDK_INT >= 33){
                                  launcher.launch(Manifest.permission.READ_MEDIA_VIDEO)
                              }else{
                                  launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                              }
                    },
                    modifier=modifier.fillMaxWidth(),
                    enabled = !isPermission
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.sd_card),
                        contentDescription = "sd_card"
                    )

                    Spacer(modifier = Modifier.padding(end = 10.dp))
                    Text(text = "Grant Access")
                }
            }
        }

        Button(
            onClick = {
                      activity?.startActivity(
                          Intent(activity,
                              MainActivity::class.java)
                      )
                activity?.finish()

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp),
            enabled = isPermission
        ) {
            Text(text = "Let's Go")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview2() {
    VPlayerTheme {
        SplashUi()
    }
}