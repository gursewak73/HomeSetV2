package com.dream.homeset.feature.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import com.dream.homeset.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dream.homeset.core.domain.model.Photo

val PrimaryBlue = Color(0xFF0066FF)
val BgDark = Color(0xFF141121)
val Slate300 = Color(0xFFCBD5E1)
val Slate500 = Color(0xFF64748B)

@Composable
fun TopBar(onCloseClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgDark.copy(alpha = 0.8f))
            .statusBarsPadding()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Close,
            contentDescription = stringResource(R.string.desc_close),
            tint = Color.White,
            modifier = Modifier.clickable(onClick = onCloseClick)
        )
        Text(
            text = stringResource(R.string.label_wallpapers),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.size(24.dp))
    }
}

@Composable
fun HeroBanner(
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
                    text = stringResource(R.string.label_featured),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
            Text(
                text = stringResource(R.string.hero_title),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.hero_subtitle), color = Slate300, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun TabNavigation(
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
                text = stringResource(R.string.tab_explore),
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
                text = stringResource(R.string.tab_collections),
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

        // Favorites Tab
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onTabSelected(2) }
        ) {
            Text(
                text = stringResource(R.string.tab_favorites),
                color = if (selectedTabIndex == 2) PrimaryBlue else Slate500,
                fontSize = 14.sp,
                fontWeight = if (selectedTabIndex == 2) FontWeight.ExtraBold else FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            if (selectedTabIndex == 2) {
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
fun PhotoGridItem(
    photo: Photo,
    isFavorite: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onFavoriteClick: (() -> Unit)? = null
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
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable { onFavoriteClick?.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                tint = if (isFavorite) Color.Red else Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAll: (() -> Unit)? = null) {
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
                text = stringResource(R.string.btn_view_all),
                color = PrimaryBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onSeeAll)
            )
        }
    }
}
