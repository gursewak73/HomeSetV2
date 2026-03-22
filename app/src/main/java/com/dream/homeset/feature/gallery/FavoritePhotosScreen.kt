package com.dream.homeset.feature.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.dream.homeset.R
import com.dream.homeset.core.domain.model.Photo
import com.dream.homeset.feature.gallery.ui.ROUTE_WALLPAPER_PREVIEW

const val ROUTE_FAVORITES = "favorites"

@Composable
fun FavoritePhotosRoute(
    navController: NavController,
    viewModel: WallpaperGalleryViewModel,
    onBack: () -> Unit
) {
    val favoritePhotos by viewModel.favoritePhotos.collectAsStateWithLifecycle(emptyList())

    FavoritePhotosScreen(
        favoritePhotos = favoritePhotos,
        viewModel = viewModel,
        onBack = onBack,
        onPhotoClick = { photo, index ->
            viewModel.setPreviewData(favoritePhotos, index)
            navController.navigate(ROUTE_WALLPAPER_PREVIEW)
        }
    )
}

@Composable
fun FavoritePhotosScreen(
    favoritePhotos: List<Photo>,
    viewModel: WallpaperGalleryViewModel,
    onBack: () -> Unit,
    onPhotoClick: (Photo, Int) -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgDark.copy(alpha = 0.8f))
                    .statusBarsPadding()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.desc_back),
                    tint = Color.White,
                    modifier = Modifier.clickable(onClick = onBack)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.tab_favorites),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        },
        containerColor = BgDark
    ) { paddingValues ->
        if (favoritePhotos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.msg_no_favorites),
                    color = Slate500,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                items(count = (favoritePhotos.size + 1) / 2) { rowIndex ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val leftIndex = rowIndex * 2
                        val rightIndex = rowIndex * 2 + 1
                        
                        // Left Photo
                        val leftPhoto = favoritePhotos[leftIndex]
                        PhotoGridItem(
                            photo = leftPhoto,
                            isFavorite = true,
                            modifier = Modifier.weight(1f),
                            onClick = { onPhotoClick(leftPhoto, leftIndex) },
                            onFavoriteClick = { viewModel.toggleFavorite(leftPhoto) }
                        )

                        // Right Photo
                        if (rightIndex < favoritePhotos.size) {
                            val rightPhoto = favoritePhotos[rightIndex]
                            PhotoGridItem(
                                photo = rightPhoto,
                                isFavorite = true,
                                modifier = Modifier.weight(1f),
                                onClick = { onPhotoClick(rightPhoto, rightIndex) },
                                onFavoriteClick = { viewModel.toggleFavorite(rightPhoto) }
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
