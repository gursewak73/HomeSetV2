package com.dream.homeset.feature.gallery

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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

private val PrimaryBlue = Color(0xFF0066FF)
private val BgDark = Color(0xFF141121)
private val Slate300 = Color(0xFFCBD5E1)
private val Slate500 = Color(0xFF64748B)

@Composable
fun WallpaperGalleryRoute(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: WallpaperGalleryViewModel = viewModel()
) {
    val photos = viewModel.photosPagingData.collectAsLazyPagingItems()
    val collections = viewModel.collectionsPagingData.collectAsLazyPagingItems()
    val featuredPhoto by viewModel.featuredPhoto.collectAsStateWithLifecycle()

    WallpaperGalleryScreen(
        photos = photos,
        featuredPhoto = featuredPhoto,
        collections = collections,
        modifier = modifier,
        onPhotoClick = { photo, index ->
            val photoList = (0 until photos.itemCount).mapNotNull { photos[it] }
            viewModel.setPreviewData(photoList, index)
            navController.navigate(ROUTE_WALLPAPER_PREVIEW)
        },
        onFeaturedClick = { photo ->
            viewModel.setSelectedPhoto(photo)
            navController.navigate(ROUTE_WALLPAPER_PREVIEW)
        }
    )
}

@Composable
fun WallpaperGalleryScreen(
    photos: LazyPagingItems<Photo>,
    featuredPhoto: Photo?,
    collections: LazyPagingItems<Collection>,
    modifier: Modifier = Modifier,
    onPhotoClick: (Photo, Int) -> Unit,
    onFeaturedClick: (Photo) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = { TopBar() },
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
                if (index == 0) {
                    // Explore View
                    ExploreView(
                        photos = photos,
                        featuredPhoto = featuredPhoto,
                        onPhotoClick = onPhotoClick,
                        onFeaturedClick = onFeaturedClick
                    )
                } else {
                    // Collections View
                    CollectionsView(
                        collections = collections
                    )
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
    onFeaturedClick: (Photo) -> Unit
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
                title = "Trending Now",
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
                        text = "No data found",
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
                    Text("No wallpapers found", color = Slate500)
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
                        modifier = Modifier.weight(1f),
                        onClick = { onPhotoClick(photo, leftIndex) }
                    )
                } ?: Spacer(modifier = Modifier.weight(1f))

                // Right Photo
                if (rightIndex < itemCount) {
                    photos[rightIndex]?.let { photo ->
                        PhotoGridItem(
                            photo = photo,
                            modifier = Modifier.weight(1f),
                            onClick = { onPhotoClick(photo, rightIndex) }
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
    collections: LazyPagingItems<Collection>
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
                        text = "No data found",
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
                    Text("No collections found", color = Slate500)
                }
            }
        }

        items(count = collections.itemCount) { index ->
            collections[index]?.let { collection ->
                CollectionCard(
                    collection = collection,
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
private fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgDark.copy(alpha = 0.8f))
            .statusBarsPadding()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
        Text(
            text = "Wallpapers",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun HeroBanner(
    photo: Photo,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .aspectRatio(16f / 10f)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = photo.urls.regular,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, BgDark.copy(alpha = 0.9f)),
                        startY = 300f
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Surface(
                color = PrimaryBlue,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "FEATURED",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
            Text(
                text = "Wallpaper of the Day",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Ethereal Peaks", color = Slate300, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun TabNavigation(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Explore Tab
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onTabSelected(0) }
        ) {
            Text(
                text = "Explore",
                color = if (selectedTabIndex == 0) PrimaryBlue else Slate500,
                fontSize = 14.sp,
                fontWeight = if (selectedTabIndex == 0) FontWeight.ExtraBold else FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            if (selectedTabIndex == 0) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(2.dp)
                        .background(PrimaryBlue)
                )
            }
        }
        
        // Collections Tab
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onTabSelected(1) }
        ) {
            Text(
                text = "Collections",
                color = if (selectedTabIndex == 1) PrimaryBlue else Slate500,
                fontSize = 14.sp,
                fontWeight = if (selectedTabIndex == 1) FontWeight.ExtraBold else FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            if (selectedTabIndex == 1) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(2.dp)
                        .background(PrimaryBlue)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAll: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        if (onSeeAll != null) {
            Text(
                text = "VIEW ALL",
                color = PrimaryBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onSeeAll)
            )
        }
    }
}


@Composable
private fun PhotoGridItem(
    photo: Photo,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(3f / 4f)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = photo.urls.small,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
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
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(PrimaryBlue.copy(alpha = 0.2f), Color.Transparent)
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = collection.title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "${collection.totalPhotos} Wallpapers",
                color = Slate500,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

