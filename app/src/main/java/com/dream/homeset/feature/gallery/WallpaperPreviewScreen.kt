package com.dream.homeset.feature.gallery

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.dream.homeset.core.model.UnsplashPhoto
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

const val ROUTE_WALLPAPER_PREVIEW = "wallpaper_preview"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WallpaperPreviewRoute(
    viewModel: WallpaperGalleryViewModel,
    onBack: () -> Unit
) {
    val previewPhotos by viewModel.previewPhotos.collectAsStateWithLifecycle(initialValue = emptyList())
    val previewIndex by viewModel.previewIndex.collectAsStateWithLifecycle(initialValue = 0)
    val isSettingWallpaper by viewModel.isSettingWallpaper.collectAsStateWithLifecycle(initialValue = false)
    val wallpaperSetSuccess by viewModel.wallpaperSetSuccess.collectAsStateWithLifecycle(initialValue = false)
    val context = LocalContext.current

    // Show toast when wallpaper is set successfully
    androidx.compose.runtime.LaunchedEffect(wallpaperSetSuccess) {
        if (wallpaperSetSuccess) {
            Toast.makeText(context, "Wallpaper set successfully!", Toast.LENGTH_SHORT).show()
            viewModel.resetWallpaperSetSuccess()
        }
    }

    if (previewPhotos.isNotEmpty()) {
        val pagerState = rememberPagerState(
            pageCount = { previewPhotos.size },
            initialPage = previewIndex
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = !isSettingWallpaper
        ) { page ->
            val photo = previewPhotos[page]
            WallpaperPreviewScreen(
                photo = photo,
                onBack = onBack,
                isSettingWallpaper = isSettingWallpaper,
                onSetHome = { viewModel.setWallpaper(context, photo, WallpaperDestination.HOME) },
                onSetLock = { viewModel.setWallpaper(context, photo, WallpaperDestination.LOCK) },
                onSetBoth = { viewModel.setWallpaper(context, photo, WallpaperDestination.BOTH) }
            )
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color.White)
                Button(
                    onClick = onBack,
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    Text("Back to Gallery")
                }
            }
        }
    }
}

@Composable
fun WallpaperPreviewScreen(
    photo: UnsplashPhoto,
    onBack: () -> Unit,
    isSettingWallpaper: Boolean = false,
    onSetHome: () -> Unit,
    onSetLock: () -> Unit,
    onSetBoth: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val zoomState = rememberZoomState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        WallpaperImageContent(
            photo = photo,
            zoomState = zoomState,
            isEnabled = !isSettingWallpaper
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            WallpaperControlPanel(
                isExpanded = isExpanded,
                onExpandedChange = { isExpanded = it },
                onSetHome = onSetHome,
                onSetLock = onSetLock,
                onSetBoth = onSetBoth,
                onBack = onBack,
                isEnabled = !isSettingWallpaper
            )
        }

        // Interaction blocker overlay
        if (isSettingWallpaper) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            )
            WallpaperSettingLoader()
        }
    }
}

@Composable
private fun WallpaperImageContent(
    photo: UnsplashPhoto,
    zoomState: net.engawapg.lib.zoomable.ZoomState,
    isEnabled: Boolean = true
) {
    val imageUrl = photo.urls.full ?: photo.urls.regular ?: photo.urls.small ?: photo.urls.thumb

    val modifier = if (isEnabled) {
        Modifier
            .fillMaxSize()
            .zoomable(zoomState)
    } else {
        Modifier.fillMaxSize()
    }

    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier,
        content = {
            when (painter.state) {
                is coil.compose.AsyncImagePainter.State.Loading -> {
                    LoadingState()
                }
                is coil.compose.AsyncImagePainter.State.Error -> {
                    ErrorState()
                }
                else -> {
                    SubcomposeAsyncImageContent()
                }
            }
        }
    )
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}

@Composable
private fun ErrorState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Failed to load image",
            color = Color.White
        )
    }
}

@Composable
private fun WallpaperControlPanel(
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSetHome: () -> Unit,
    onSetLock: () -> Unit,
    onSetBoth: () -> Unit,
    onBack: () -> Unit,
    isEnabled: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .padding(16.dp)
    ) {
        if (!isExpanded) {
            CollapsedControlPanel(
                onSetBoth = onSetBoth,
                onExpandClick = { onExpandedChange(true) },
                isEnabled = isEnabled
            )
        } else {
            ExpandedControlPanel(
                onSetHome = onSetHome,
                onSetLock = onSetLock,
                onSetBoth = onSetBoth,
                onCollapseClick = { onExpandedChange(false) },
                isEnabled = isEnabled
            )
        }

        BackButton(onBack = onBack, isEnabled = isEnabled)
    }
}

@Composable
private fun CollapsedControlPanel(
    onSetBoth: () -> Unit,
    onExpandClick: () -> Unit,
    isEnabled: Boolean = true
) {
    Button(
        onClick = onSetBoth,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(text = "Set as wallpaper")
    }

    Button(
        onClick = onExpandClick,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Text(text = "More options")
    }
}

@Composable
private fun ExpandedControlPanel(
    onSetHome: () -> Unit,
    onSetLock: () -> Unit,
    onSetBoth: () -> Unit,
    onCollapseClick: () -> Unit,
    isEnabled: Boolean = true
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Set as wallpaper",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }

    Button(
        onClick = onSetHome,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = "Set Home Screen")
    }

    Button(
        onClick = onSetLock,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = "Set Lock Screen")
    }

    Button(
        onClick = onSetBoth,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(text = "Set Both")
    }

    Button(
        onClick = onCollapseClick,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Text(text = "Hide options")
    }
}

@Composable
private fun BackButton(onBack: () -> Unit, isEnabled: Boolean = true) {
    Button(
        onClick = onBack,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Text(text = "Back to Gallery")
    }
}

@Composable
private fun WallpaperSettingLoader() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            CircularProgressIndicator(color = Color.White)
            Text(
                text = "Setting wallpaper...",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

