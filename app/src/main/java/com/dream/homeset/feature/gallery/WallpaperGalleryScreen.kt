package com.dream.homeset.feature.gallery

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.dream.homeset.core.model.UnsplashPhoto
import com.dream.homeset.core.model.UnsplashUrls
import com.dream.homeset.ui.theme.HomeSetTheme

@Composable
fun WallpaperGalleryRoute(
    modifier: Modifier = Modifier,
    viewModel: WallpaperGalleryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    WallpaperGalleryScreen(
        uiState = uiState,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun WallpaperGalleryRoutePreview() {
    HomeSetTheme {
        WallpaperGalleryScreen(
            uiState = GalleryUiState.Success(
                photos = List(15) { index ->
                    UnsplashPhoto(
                        id = index.toString(),
                        color = "#CCCCCC",
                        width = 1080,
                        height = 1920,
                        urls = UnsplashUrls(
                            raw = null,
                            full = null,
                            regular = null,
                            small = null,
                            thumb = null
                        )
                    )
                }
            )
        )
    }
}

@Composable
fun WallpaperGalleryScreen(
    uiState: GalleryUiState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (uiState) {
            is GalleryUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is GalleryUiState.Error -> {
                Text(
                    text = uiState.message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }

            is GalleryUiState.Success -> {
                PhotoGrid(photos = uiState.photos)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PhotoGrid(
    photos: List<UnsplashPhoto>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(4.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(photos, key = { it.id }) { photo ->
            PhotoGridItem(photo = photo)
        }
    }
}

@Composable
private fun PhotoGridItem(
    photo: UnsplashPhoto,
    modifier: Modifier = Modifier
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
            .clickable { }
    )
}

