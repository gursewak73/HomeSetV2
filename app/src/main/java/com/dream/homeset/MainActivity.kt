package com.dream.homeset

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dream.homeset.feature.gallery.ui.ROUTE_GALLERY
import com.dream.homeset.feature.gallery.ui.ROUTE_WALLPAPER_PREVIEW
import com.dream.homeset.feature.gallery.ui.WallpaperGalleryRoute
import com.dream.homeset.feature.gallery.viewmodel.WallpaperGalleryViewModel
import com.dream.homeset.feature.gallery.ui.WallpaperPreviewRoute
import com.dream.homeset.ui.theme.HomeSetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeSetTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel = viewModel<WallpaperGalleryViewModel>()
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = ROUTE_GALLERY
                    ) {
                        composable(ROUTE_GALLERY) {
                            WallpaperGalleryRoute(
                                navController = navController,
                                viewModel = viewModel
                            )
                        }
                        composable(ROUTE_WALLPAPER_PREVIEW) {
                            WallpaperPreviewRoute(
                                viewModel = viewModel,
                                onBack = {
                                    viewModel.clearSelectedPhoto()
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}