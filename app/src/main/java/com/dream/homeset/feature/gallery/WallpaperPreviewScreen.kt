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
import androidx.compose.ui.res.stringResource
import com.dream.homeset.R
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
import com.dream.homeset.feature.gallery.WallpaperGalleryViewModel
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

    val wallpaperSuccessMsg = stringResource(R.string.msg_wallpaper_set_success)
    LaunchedEffect(wallpaperSetSuccess) {
        if (wallpaperSetSuccess) {
            Toast.makeText(context, wallpaperSuccessMsg, Toast.LENGTH_SHORT).show()
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
                    Text(stringResource(R.string.btn_back_to_gallery))
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
    var selectedDestination by remember { mutableStateOf<WallpaperDestination?>(null) }
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
                        text = stringResource(R.string.title_wallpaper_preview),
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
                        text = stringResource(R.string.sample_time),
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = stringResource(R.string.sample_date),
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
                                            text = stringResource(R.string.label_photo_by),
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
                                    Text(stringResource(R.string.btn_set_wallpaper), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }

                                IconButton(
                                    onClick = { isUiVisible = false },
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.desc_hide_options), tint = Color.White)
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
                                stringResource(R.string.title_set_wallpaper_where),
                                color = Slate100,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(24.dp))

                             QuickSetItem(
                                title = stringResource(R.string.option_home_screen),
                                icon = Icons.Default.Home,
                                isHighlighted = selectedDestination == WallpaperDestination.HOME,
                                onClick = { selectedDestination = WallpaperDestination.HOME },
                                isEnabled = !isSettingWallpaper
                            )
                            QuickSetItem(
                                title = stringResource(R.string.option_lock_screen),
                                icon = Icons.Default.Lock,
                                isHighlighted = selectedDestination == WallpaperDestination.LOCK,
                                onClick = { selectedDestination = WallpaperDestination.LOCK },
                                isEnabled = !isSettingWallpaper
                            )
                            QuickSetItem(
                                title = stringResource(R.string.option_both),
                                icon = Icons.Default.Phone,
                                isHighlighted = selectedDestination == WallpaperDestination.BOTH,
                                onClick = { selectedDestination = WallpaperDestination.BOTH },
                                isEnabled = !isSettingWallpaper
                            )

                            Spacer(Modifier.height(32.dp))
                            
                            // Apply Button
                            Button(
                                onClick = {
                                    when (selectedDestination) {
                                        WallpaperDestination.HOME -> onSetHome()
                                        WallpaperDestination.LOCK -> onSetLock()
                                        WallpaperDestination.BOTH -> onSetBoth()
                                        else -> {}
                                    }
                                },
                                enabled = !isSettingWallpaper && selectedDestination != null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryBlue,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(stringResource(R.string.btn_apply), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.btn_cancel),
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
    isHighlighted: Boolean,
    onClick: () -> Unit,
    isEnabled: Boolean
) {
    val bgColor = if (isHighlighted) PrimaryBlue.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
    val borderColor = if (isHighlighted) PrimaryBlue.copy(alpha = 0.3f) else Color.Transparent
    val txtColor = Color.White

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
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = title,
            color = txtColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        if (isHighlighted) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .border(2.dp, Slate100, CircleShape)
            )
        }
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
            text = stringResource(R.string.msg_failed_load_image),
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
                text = stringResource(R.string.msg_setting_wallpaper),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
