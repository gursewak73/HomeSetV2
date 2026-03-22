package com.dream.homeset.feature.gallery

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import com.dream.homeset.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.dream.homeset.core.domain.model.Collection
import com.dream.homeset.core.domain.model.Photo
import com.dream.homeset.feature.gallery.ui.ROUTE_WALLPAPER_PREVIEW

const val ROUTE_GALLERY = "gallery"
const val ROUTE_COLLECTION_DETAIL = "collection_detail"

// Shared constants and components moved to GalleryComponents.kt

@Composable
fun WallpaperGalleryRoute(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: WallpaperGalleryViewModel = viewModel(),
    onCloseClick: () -> Unit
) {
    val photos = viewModel.photosPagingData.collectAsLazyPagingItems()
    val collections = viewModel.collectionsPagingData.collectAsLazyPagingItems()
    val featuredPhoto by viewModel.featuredPhoto.collectAsStateWithLifecycle()
    val favoritePhotos by viewModel.favoritePhotos.collectAsStateWithLifecycle(emptyList())

    WallpaperGalleryScreen(
        photos = photos,
        featuredPhoto = featuredPhoto,
        collections = collections,
        favoritePhotos = favoritePhotos,
        viewModel = viewModel,
        modifier = modifier,
        onCloseClick = onCloseClick,
        onPhotoClick = { photo, index ->
            val photoList = (0 until photos.itemCount).mapNotNull { photos[it] }
            viewModel.setPreviewData(photoList, index)
            navController.navigate(ROUTE_WALLPAPER_PREVIEW)
        },
        onFeaturedClick = { photo ->
            viewModel.setSelectedPhoto(photo)
            navController.navigate(ROUTE_WALLPAPER_PREVIEW)
        },
        onCollectionClick = { collection ->
            viewModel.selectCollection(collection)
            navController.navigate(ROUTE_COLLECTION_DETAIL)
        },
        onFavoriteClick = {
            navController.navigate(ROUTE_FAVORITES)
        }
    )
}

@Composable
fun WallpaperGalleryScreen(
    photos: LazyPagingItems<Photo>,
    featuredPhoto: Photo?,
    collections: LazyPagingItems<Collection>,
    favoritePhotos: List<Photo>,
    viewModel: WallpaperGalleryViewModel,
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit,
    onPhotoClick: (Photo, Int) -> Unit,
    onFeaturedClick: (Photo) -> Unit,
    onCollectionClick: (Collection) -> Unit,
    onFavoriteClick: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = { 
            TopBar(
                onCloseClick = onCloseClick,
                onFavoriteClick = onFavoriteClick
            ) 
        },
        containerColor = BgDark
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Navigation (Persistent at top below TopBar)
            TabNavigation(
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it }
            )

            // Dynamic Content
            Crossfade(
                targetState = selectedTabIndex,
                label = "TabContent",
                modifier = Modifier.weight(1f)
            ) { index ->
                when (index) {
                    0 -> {
                        ExploreView(
                            photos = photos,
                            featuredPhoto = featuredPhoto,
                            onPhotoClick = onPhotoClick,
                            onFeaturedClick = onFeaturedClick,
                            viewModel = viewModel,
                            favoritePhotos = favoritePhotos
                        )
                    }
                    1 -> {
                        CollectionsView(
                            collections = collections,
                            onCollectionClick = onCollectionClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreView(
    photos: LazyPagingItems<Photo>,
    featuredPhoto: Photo?,
    onPhotoClick: (Photo, Int) -> Unit,
    onFeaturedClick: (Photo) -> Unit,
    viewModel: WallpaperGalleryViewModel,
    favoritePhotos: List<Photo>
) {
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Banner: Wallpaper of the Day
        item {
            if (featuredPhoto != null) {
                HeroBanner(photo = featuredPhoto, onClick = { onFeaturedClick(featuredPhoto) })
            } else {
                // Shimmer/Placeholder for the banner while loading
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .aspectRatio(16f / 10f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue.copy(alpha = 0.5f))
                }
            }
        }

        // Trending Header
        item {
            SectionHeader(
                title = stringResource(R.string.header_trending),
                onSeeAll = null
            )
        }

        // Photos Grid (Chunked into pairs for LazyColumn)
        val itemCount = photos.itemCount

        // Handle initial load error
        if (photos.loadState.refresh is LoadState.Error) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.msg_no_data),
                        color = Slate500,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Handle empty list after success
        if (photos.loadState.refresh is LoadState.NotLoading && photos.itemCount == 0) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.msg_no_wallpapers), color = Slate500)
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
                
                // Left Photo
                photos[leftIndex]?.let { photo ->
                    PhotoGridItem(
                        photo = photo,
                        isFavorite = favoritePhotos.any { it.id == photo.id },
                        modifier = Modifier.weight(1f),
                        onClick = { onPhotoClick(photo, leftIndex) },
                        onFavoriteClick = { viewModel.toggleFavorite(photo) }
                    )
                } ?: Spacer(modifier = Modifier.weight(1f))

                // Right Photo
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
        }
    }
}

@Composable
private fun CollectionsView(
    collections: LazyPagingItems<Collection>,
    onCollectionClick: (Collection) -> Unit
) {
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Combined Header and View All logic removed as requested
        
        // Handle initial load error
        if (collections.loadState.refresh is LoadState.Error) {
            item {
                Box(
                    modifier = Modifier.fillParentMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.msg_no_data),
                        color = Slate500,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Handle empty list after success
        if (collections.loadState.refresh is LoadState.NotLoading && collections.itemCount == 0) {
            item {
                Box(
                    modifier = Modifier.fillParentMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.msg_no_collections), color = Slate500)
                }
            }
        }

        items(count = collections.itemCount) { index ->
            collections[index]?.let { collection ->
                CollectionCard(
                    collection = collection,
                    onClick = { onCollectionClick(collection) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        if (collections.loadState.append is LoadState.Loading) {
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


@Composable
private fun CollectionsCarousel(collections: List<Collection>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        collections.forEach { collection ->
            CollectionCard(collection = collection)
        }
    }
}

@Composable
private fun CollectionCard(
    collection: Collection,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .height(130.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = collection.coverPhoto?.urls?.thumb,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                        startY = 100f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = collection.title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = stringResource(R.string.format_wallpapers_count, collection.totalPhotos),
                color = Slate300,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


