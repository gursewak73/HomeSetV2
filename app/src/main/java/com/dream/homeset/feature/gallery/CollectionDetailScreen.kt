package com.dream.homeset.feature.gallery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import com.dream.homeset.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.dream.homeset.core.domain.model.Collection
import com.dream.homeset.core.domain.model.Photo
import com.dream.homeset.feature.gallery.ui.ROUTE_WALLPAPER_PREVIEW

@Composable
fun CollectionDetailRoute(
    navController: NavController,
    viewModel: WallpaperGalleryViewModel,
    onBack: () -> Unit
) {
    val selectedCollection by viewModel.selectedCollection.collectAsStateWithLifecycle()
    val collectionPhotos = viewModel.collectionPhotosPagingData.collectAsLazyPagingItems()
    val favoritePhotos by viewModel.favoritePhotos.collectAsStateWithLifecycle(emptyList())

    if (selectedCollection != null) {
        CollectionDetailView(
            collection = selectedCollection!!,
            photos = collectionPhotos,
            favoritePhotos = favoritePhotos,
            viewModel = viewModel,
            onPhotoClick = { photo, index ->
                val photoList = (0 until collectionPhotos.itemCount).mapNotNull { collectionPhotos[it] }
                viewModel.setPreviewData(photoList, index)
                navController.navigate(ROUTE_WALLPAPER_PREVIEW)
            },
            onBackClick = onBack
        )
    }
}

@Composable
private fun CollectionDetailView(
    collection: Collection,
    photos: LazyPagingItems<Photo>,
    favoritePhotos: List<Photo>,
    viewModel: WallpaperGalleryViewModel,
    onPhotoClick: (Photo, Int) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = BgDark
    ) { paddingValues ->
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.desc_back),
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(onClick = onBackClick)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = collection.title,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = stringResource(R.string.format_wallpapers_count, collection.totalPhotos),
                            color = Slate500,
                            fontSize = 12.sp
                        )
                    }
                }
            }

        val itemCount = photos.itemCount

        if (photos.loadState.refresh is LoadState.Error) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.msg_failed_load_photos), color = Slate500)
                }
            }
        }

        items(count = (itemCount + 1) / 2) { rowIndex ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val leftIndex = rowIndex * 2
                val rightIndex = rowIndex * 2 + 1
                
                photos[leftIndex]?.let { photo ->
                    PhotoGridItem(
                        photo = photo,
                        isFavorite = favoritePhotos.any { it.id == photo.id },
                        modifier = Modifier.weight(1f),
                        onClick = { onPhotoClick(photo, leftIndex) },
                        onFavoriteClick = { viewModel.toggleFavorite(photo) }
                    )
                } ?: Spacer(modifier = Modifier.weight(1f))

                if (rightIndex < itemCount) {
                    photos[rightIndex]?.let { photo ->
                        PhotoGridItem(
                            photo = photo,
                            isFavorite = favoritePhotos.any { it.id == photo.id },
                            modifier = Modifier.weight(1f),
                            onClick = { onPhotoClick(photo, rightIndex) },
                            onFavoriteClick = { viewModel.toggleFavorite(photo) }
                        )
                    } ?: Spacer(modifier = Modifier.weight(1f))
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        if (photos.loadState.append is LoadState.Loading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            }
        }
    }
}