package com.dream.homeset.feature.gallery.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.dream.homeset.core.domain.model.Photo
import com.dream.homeset.core.domain.model.WallpaperDestination
import com.dream.homeset.feature.gallery.viewmodel.WallpaperGalleryViewModel
import net.engawapg.lib.zoomable.ZoomState
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

const val ROUTE_WALLPAPER_PREVIEW = "wallpaper_preview"

private val PrimaryBlue = Color(0xFF3B19E6)
private val GlassBg = Color(0x99141121)
private val SheetBg = Color(0xF2141121)
private val Slate100 = Color(0xFFF1F5F9)
private val Slate300 = Color(0xFFCBD5E1)
private val Slate500 = Color(0xFF64748B)
private val Slate200 = Color(0xFFE2E8F0)

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

    LaunchedEffect(wallpaperSetSuccess) {
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
    photo: Photo,
    onBack: () -> Unit,
    isSettingWallpaper: Boolean = false,
    onSetHome: () -> Unit,
    onSetLock: () -> Unit,
    onSetBoth: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isUiVisible by remember { mutableStateOf(true) }
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

        // Overlay to restore UI if user taps the wallpaper itself
        if (!isUiVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isUiVisible = true }
            )
        }

        AnimatedVisibility(
            visible = isUiVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Top Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
                            )
                        )
                        .align(Alignment.TopCenter)
                )

                // Bottom Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))
                            )
                        )
                        .align(Alignment.BottomCenter)
                )

                // Top Navigation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 48.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassIconButton(icon = Icons.AutoMirrored.Filled.ArrowBack, onClick = onBack)
                    Text(
                        text = "Wallpaper Preview",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.size(40.dp))
                }

                // Central Clock Preview
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 160.dp)
                        .align(Alignment.TopCenter)
                ) {
                    Text(
                        text = "12:45",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = "Monday, Oct 24",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Bottom Controls
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Pill / Collapsed State
                    AnimatedVisibility(
                        visible = !isExpanded,
                        enter = slideInVertically { it } + fadeIn(),
                        exit = slideOutVertically { it } + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Photo Credit UI
                            photo.user?.name?.let { userName ->
                                Box(
                                    modifier = Modifier
                                        .padding(bottom = 16.dp)
                                        .background(GlassBg, CircleShape)
                                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Photo by ",
                                            color = Slate200,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = userName,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Action Bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(GlassBg, RoundedCornerShape(16.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { isExpanded = true },
                                    enabled = !isSettingWallpaper,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = PrimaryBlue
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Set Wallpaper", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }

                                IconButton(
                                    onClick = { isUiVisible = false },
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Hide Options", tint = Color.White)
                                }
                            }
                        }
                    }

                    // Expanded Sheet
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = slideInVertically { it } + fadeIn(),
                        exit = slideOutVertically { it } + fadeOut(),
                        modifier = Modifier.zIndex(1f) // Ensure sheet is always on top
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SheetBg, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                .padding(horizontal = 24.dp, vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(48.dp)
                                    .height(4.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            )
                            Spacer(Modifier.height(24.dp))
                            Text(
                                "Where would you like to set this?",
                                color = Slate100,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(24.dp))

                            QuickSetItem(
                                title = "Home Screen",
                                icon = Icons.Default.Home,
                                trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                isHighlighted = false,
                                onClick = onSetHome,
                                isEnabled = !isSettingWallpaper
                            )
                            QuickSetItem(
                                title = "Lock Screen",
                                icon = Icons.Default.Lock,
                                trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                isHighlighted = false,
                                onClick = onSetLock,
                                isEnabled = !isSettingWallpaper
                            )
                            QuickSetItem(
                                title = "Set Both",
                                icon = Icons.Default.Phone,
                                trailingIcon = Icons.Default.CheckCircle,
                                isHighlighted = true,
                                onClick = onSetBoth,
                                isEnabled = !isSettingWallpaper
                            )

                            Spacer(Modifier.height(32.dp))
                            Text(
                                text = "CANCEL",
                                modifier = Modifier
                                    .clickable { isExpanded = false }
                                    .padding(8.dp),
                                color = Slate500,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
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
private fun GlassIconButton(icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(GlassBg, CircleShape)
            .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Slate100, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun QuickSetItem(
    title: String,
    icon: ImageVector,
    trailingIcon: ImageVector,
    isHighlighted: Boolean,
    onClick: () -> Unit,
    isEnabled: Boolean
) {
    val bgColor = if (isHighlighted) PrimaryBlue.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
    val borderColor = if (isHighlighted) PrimaryBlue.copy(alpha = 0.3f) else Color.Transparent
    val txtColor = if (isHighlighted) Color.White else Slate100

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = isEnabled) { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (isHighlighted) Color.White else PrimaryBlue,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = title,
            color = txtColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        Icon(
            trailingIcon,
            contentDescription = null,
            tint = if (isHighlighted) Color.White else Slate500
        )
    }
}

@Composable
private fun WallpaperImageContent(
    photo: Photo,
    zoomState: ZoomState,
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
        contentScale = ContentScale.Crop,
        modifier = modifier,
        content = {
            when (painter.state) {
                is AsyncImagePainter.State.Loading -> {
                    LoadingState()
                }
                is AsyncImagePainter.State.Error -> {
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
private fun WallpaperSettingLoader() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(GlassBg, RoundedCornerShape(16.dp))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .padding(32.dp)
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

