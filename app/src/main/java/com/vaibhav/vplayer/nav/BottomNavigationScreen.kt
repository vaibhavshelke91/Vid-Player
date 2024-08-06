package com.vaibhav.vplayer.nav


import com.vaibhav.vplayer.R


sealed class BottomNavigationScreens(val route: String, val resource:String, val icon: Int) {
    object Home : BottomNavigationScreens("Home", "Home", R.drawable.home)
    object Folder : BottomNavigationScreens("Folders", "Folders", R.drawable.folder)
}