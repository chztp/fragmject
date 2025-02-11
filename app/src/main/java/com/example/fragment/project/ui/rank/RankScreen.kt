package com.example.fragment.project.ui.rank

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fragment.project.R
import com.example.fragment.project.components.LoadingLayout
import com.example.fragment.project.components.SwipeRefresh

@Composable
fun RankScreen(
    viewModel: RankViewModel = viewModel(),
    onNavigateToUserInfo: (userId: String) -> Unit = {},
    onNavigateToWeb: (url: String) -> Unit = {},
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier.systemBarsPadding()
    ) {
        Box(
            modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .background(colorResource(R.color.theme))
        ) {
            IconButton(
                modifier = Modifier.height(45.dp),
                onClick = {
                    if (context is AppCompatActivity) {
                        context.onBackPressedDispatcher.onBackPressed()
                    }
                }
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = null,
                    tint = colorResource(R.color.white)
                )
            }
            Text(
                text = "积分排行榜",
                fontSize = 16.sp,
                color = colorResource(R.color.text_fff),
                modifier = Modifier.align(Alignment.Center)
            )
            IconButton(
                modifier = Modifier
                        .height(45.dp)
                        .padding(13.dp)
                        .align(Alignment.CenterEnd),
                onClick = { onNavigateToWeb("https://www.wanandroid.com/blog/show/2653") }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_rule),
                    contentDescription = null,
                    tint = colorResource(R.color.white)
                )
            }
        }
        LoadingLayout(uiState.refreshing && !uiState.loading) {
            SwipeRefresh(
                items = uiState.result,
                refreshing = uiState.refreshing,
                onRefresh = { viewModel.getHome() },
                loading = uiState.loading,
                onLoad = { viewModel.getNext() },
                modifier = Modifier
                        .background(colorResource(R.color.background))
                        .fillMaxSize(),
            ) { _, item ->
                Row(
                    modifier = Modifier
                            .background(colorResource(R.color.white))
                            .fillMaxWidth()
                            .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = item.getAvatarId()),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .clickable { onNavigateToUserInfo(item.userId) }
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = item.username,
                            fontSize = 14.sp,
                            color = colorResource(R.color.text_666),
                        )
                    }
                    Text(
                        text = item.coinCount,
                        fontSize = 14.sp,
                        color = colorResource(R.color.orange),
                    )
                }
                Spacer(
                    Modifier
                            .background(colorResource(R.color.line))
                            .fillMaxWidth()
                            .height(1.dp)
                )
            }
        }
    }

}