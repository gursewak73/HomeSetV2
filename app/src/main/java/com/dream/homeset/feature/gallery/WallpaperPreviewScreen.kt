package com.dream.homeset.feature.gallery

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.dream.homeset.core.domain.model.Photo
import com.dream.homeset.core.domain.model.WallpaperDestination
import net.engawapg.lib.zoomable.ZoomState
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

const val ROUTE_WALLPAPER_PREVIEW = "wallpaper_preview"

private val PrimaryBlue = Color(0xFF3B19E6)
private val GlassBg = Color(0x99141121)
private val SheetBg = Color(0xF2141121)

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
            CircularProgressIndicator(color = Color.White)
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
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!isExpanded) {
                    isUiVisible = !isUiVisible
                }
            }
    ) {
        // Wallpaper Image
        WallpaperImageContent(photo = photo, zoomState = zoomState, isEnabled = !isExpanded)

        // Gradient Overlay
        AnimatedVisibility(
            visible = isUiVisible && !isExpanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.6f),
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    )
            )
        }

        // Top Navigation
        AnimatedVisibility(
            visible = isUiVisible && !isExpanded,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut()
        ) {
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
        }

        // Action Panel
        AnimatedVisibility(
            visible = isUiVisible,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (!isExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Photo Credit
                        photo.user?.name?.let { author ->
                            Row(
                                modifier = Modifier
                                    .padding(bottom = 24.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.4f))
                                    .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Photo by ",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = author,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Bottom Action Bar
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .fillMaxWidth()
                                .height(72.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(GlassBg)
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { isExpanded = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .padding(horizontal = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = PrimaryBlue
                                ),
                                shape = RoundedCornerShape(16.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                            ) {
                                Text(
                                    text = "Set Wallpaper",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
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
                    modifier = Modifier.zIndex(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                            .background(SheetBg)
                            .padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .align(Alignment.CenterHorizontally)
                        )

                        Text(
                            text = "Set as wallpaper",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                        )

                        Text(
                            text = "Choose where you want to apply this wallpaper",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        OptionItem(
                            title = "Home Screen",
                            subtitle = "Show only on your home screen",
                            onClick = {
                                onSetHome()
                                isExpanded = false
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = Color.White.copy(alpha = 0.05f)
                        )
                        OptionItem(
                            title = "Lock Screen",
                            subtitle = "Show only on your lock screen",
                            onClick = {
                                onSetLock()
                                isExpanded = false
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = Color.White.copy(alpha = 0.05f)
                        )
                        OptionItem(
                            title = "Both Screens",
                            subtitle = "Apply to home and lock screens",
                            onClick = {
                                onSetBoth()
                                isExpanded = false
                            }
                        )

                        Button(
                            onClick = { isExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp, bottom = 12.dp)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.1f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Cancel", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Success State Overlay (Optional)
        if (isSettingWallpaper) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

@Composable
private fun GlassIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.3f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun OptionItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
        }
    }
}

@Composable
private fun WallpaperImageContent(
    photo: Photo,
    zoomState: ZoomState,
    isEnabled: Boolean = true
) {
    SubcomposeAsyncImage(
        model = photo.urls.full ?: photo.urls.regular,
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .zoomable(zoomState),
        contentScale = ContentScale.Crop
    ) {
        val state = painter.state
        if (state is coil.compose.AsyncImagePainter.State.Loading || state is coil.compose.AsyncImagePainter.State.Error) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        try {
                            Color(android.graphics.Color.parseColor(photo.color ?: "#141121"))
                        } catch (_: Exception) {
                            Color(0xFF141121)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White.copy(alpha = 0.3f))
            }
        } else {
            SubcomposeAsyncImageContent()
        }
    }
}
