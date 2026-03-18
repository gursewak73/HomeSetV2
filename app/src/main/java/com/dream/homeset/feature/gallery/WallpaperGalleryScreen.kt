package com.dream.homeset.feature.gallery

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

private val PrimaryBlue = Color(0xFF3B19E6)
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
    val featuredPhoto by viewModel.featuredPhoto.collectAsStateWithLifecycle()
    val collections by viewModel.collections.collectAsStateWithLifecycle()

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
    collections: List<Collection>,
    modifier: Modifier = Modifier,
    onPhotoClick: (Photo, Int) -> Unit,
    onFeaturedClick: (Photo) -> Unit
) {
    Scaffold(
        topBar = { TopBar() },
        containerColor = BgDark
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Banner: Wallpaper of the Day
            featuredPhoto?.let { photo ->
                HeroBanner(photo = photo, onClick = { onFeaturedClick(photo) })
            }

            // Tab Navigation
            TabNavigation()

            // Trending Now Section
            SectionHeader(title = "Trending Now", onSeeAll = {})
            
            // Photo Grid (Inline implementation for simplicity in this file)
            TrendingPhotos(photos = photos, onPhotoClick = onPhotoClick)

            // Popular Collections
            if (collections.isNotEmpty()) {
                SectionHeader(title = "Popular Collections", onSeeAll = {})
                CollectionsCarousel(collections = collections)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgDark.copy(alpha = 0.8f))
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
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Apply Now", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun TabNavigation() {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Explore",
                color = PrimaryBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(2.dp)
                    .background(PrimaryBlue)
            )
        }
        Text(
            text = "Collections",
            color = Slate500,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 12.dp)
        )
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAll: () -> Unit) {
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
        Text(
            text = "VIEW ALL",
            color = PrimaryBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = onSeeAll)
        )
    }
}

@Composable
private fun TrendingPhotos(
    photos: LazyPagingItems<Photo>,
    onPhotoClick: (Photo, Int) -> Unit
) {
    // We'll show the top few items as a grid within the scrollable column.
    // For a true grid, we might need a fixed height or a custom layout since it's nested in a verticalScroll.
    // Let's use a simple Column with Rows for a 2x2 or 2xN preview.
    
    val count = if (photos.itemCount > 6) 6 else photos.itemCount
    
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        for (i in 0 until count step 2) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                photos[i]?.let { photo ->
                    PhotoGridItem(
                        photo = photo,
                        modifier = Modifier.weight(1f),
                        onClick = { onPhotoClick(photo, i) }
                    )
                }
                if (i + 1 < count) {
                    photos[i + 1]?.let { photo ->
                        PhotoGridItem(
                            photo = photo,
                            modifier = Modifier.weight(1f),
                            onClick = { onPhotoClick(photo, i + 1) }
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        if (photos.loadState.append is LoadState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp),
                color = PrimaryBlue
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
private fun CollectionCard(collection: Collection) {
    Box(
        modifier = Modifier
            .width(260.dp)
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

