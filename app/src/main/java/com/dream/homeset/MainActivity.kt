package com.dream.homeset

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.dream.homeset.ui.theme.HomeSetTheme
import com.dream.homeset.feature.gallery.WallpaperGalleryRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeSetTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WallpaperGalleryRoute()
                }
            }
        }
    }
}