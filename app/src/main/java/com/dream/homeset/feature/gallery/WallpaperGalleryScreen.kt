package com.dream.homeset.feature.gallery

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.dream.homeset.core.model.UnsplashPhoto
import com.dream.homeset.core.model.UnsplashUrls
import com.dream.homeset.ui.theme.HomeSetTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.dream.homeset.feature.gallery.WallpaperDestination

@Composable
fun WallpaperGalleryRoute(
    modifier: Modifier = Modifier,
    viewModel: WallpaperGalleryViewModel = viewModel()
) {
    var selectedPhoto by remember { mutableStateOf<UnsplashPhoto?>(null) }
    val photos = viewModel.photosPagingData.collectAsLazyPagingItems()
    val context = LocalContext.current

    if (selectedPhoto == null) {
        WallpaperGalleryScreen(
            photos = photos,
            modifier = modifier,
            onPhotoClick = { photo -> selectedPhoto = photo }
        )
    } else {
        WallpaperDetailScreen(
            photo = selectedPhoto!!,
            onBack = { selectedPhoto = null },
            onSetHome = { viewModel.setWallpaper(context, selectedPhoto!!, WallpaperDestination.HOME) },
            onSetLock = { viewModel.setWallpaper(context, selectedPhoto!!, WallpaperDestination.LOCK) },
            onSetBoth = { viewModel.setWallpaper(context, selectedPhoto!!, WallpaperDestination.BOTH) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WallpaperGalleryRoutePreview() {
    HomeSetTheme {
        Text(text = "Wallpaper gallery preview")
    }
}

@Composable
fun WallpaperGalleryScreen(
    photos: LazyPagingItems<UnsplashPhoto>,
    modifier: Modifier = Modifier,
    onPhotoClick: (UnsplashPhoto) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val loadState = photos.loadState

        when {
            loadState.refresh is LoadState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            loadState.refresh is LoadState.Error -> {
                val error = loadState.refresh as LoadState.Error
                Text(
                    text = error.error.message ?: "Something went wrong",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }

            else -> {
                PhotoGrid(
                    photos = photos,
                    onPhotoClick = onPhotoClick
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PhotoGrid(
    photos: LazyPagingItems<UnsplashPhoto>,
    modifier: Modifier = Modifier,
    onPhotoClick: (UnsplashPhoto) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(4.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(photos.itemCount) { index ->
            val photo = photos[index]
            if (photo != null) {
                PhotoGridItem(
                    photo = photo,
                    onClick = { onPhotoClick(photo) }
                )
            }
        }
    }
}

@Composable
private fun PhotoGridItem(
    photo: UnsplashPhoto,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val placeholderColor = try {
        Color(android.graphics.Color.parseColor(photo.color ?: "#CCCCCC"))
    } catch (_: IllegalArgumentException) {
        Color(0xFFCCCCCC)
    }

    AsyncImage(
        model = photo.urls.small ?: photo.urls.thumb ?: photo.urls.regular,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .padding(2.dp)
            .aspectRatio(2f / 3f)
            .background(placeholderColor)
            .clickable(onClick = onClick)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WallpaperDetailScreen(
    photo: UnsplashPhoto,
    onBack: () -> Unit,
    onSetHome: () -> Unit,
    onSetLock: () -> Unit,
    onSetBoth: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var isExpanded by remember { mutableStateOf(false) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        if (scale > 1f) {
            offsetX += panChange.x
            offsetY += panChange.y
        } else {
            offsetX = 0f
            offsetY = 0f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AsyncImage(
            model = photo.urls.regular ?: photo.urls.small ?: photo.urls.thumb,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                )
                .transformable(transformableState)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                .padding(16.dp)
        ) {
            if (!isExpanded) {
                Button(
                    onClick = {
                        // Default behavior: set both screens
                        onSetBoth()
                    },
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
                    onClick = { isExpanded = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(text = "More options")
                }
            } else {
                Text(
                    text = "Set as wallpaper",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 8.dp)
                )

                Button(
                    onClick = onSetHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(text = "Set Home Screen")
                }

                Button(
                    onClick = onSetLock,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(text = "Set Lock Screen")
                }

                Button(
                    onClick = onSetBoth,
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
                    onClick = { isExpanded = false },
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

            Button(
                onClick = onBack,
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
    }
}

