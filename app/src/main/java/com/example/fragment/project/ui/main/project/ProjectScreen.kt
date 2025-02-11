package com.example.fragment.project.ui.main.project

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fragment.project.components.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProjectScreen(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    projectTreeViewModel: ProjectTreeViewModel = viewModel(),
    projectViewModel: ProjectViewModel = viewModel(),
    onNavigateToLogin: () -> Unit = {},
    onNavigateToSystem: (cid: String) -> Unit = {},
    onNavigateToUser: (userId: String) -> Unit = {},
    onNavigateToWeb: (url: String) -> Unit = {},
) {
    val projectTreeUiState by projectTreeViewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    Column {
        TabBar(
            data = projectTreeUiState.result,
            textMapping = { it.name },
            pagerState = pagerState,
            onClick = { coroutineScope.launch { pagerState.animateScrollToPage(it) } },
        )
        HorizontalPager(
            pageCount = projectTreeUiState.result.size,
            state = pagerState,
        ) { page ->
            val pageCid = projectTreeUiState.result[page].id
            val projectUiState by projectViewModel.uiState.collectAsStateWithLifecycle()
            val listState = rememberLazyListState()
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_START) {
                        projectViewModel.init(pageCid)
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }
            LoadingLayout(
                projectTreeUiState.isLoading
                        || (projectUiState.getRefreshing(pageCid) && !projectUiState.getLoading(pageCid))
            ) {
                SwipeRefresh(
                    items = projectUiState.getResult(pageCid),
                    refreshing = projectUiState.getRefreshing(pageCid),
                    onRefresh = { projectViewModel.getHome(pageCid) },
                    loading = projectUiState.getLoading(pageCid),
                    onLoad = { projectViewModel.getNext(pageCid) },
                    modifier = Modifier.fillMaxSize(),
                    listState = listState,
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    key = { _, item -> item.id },
                ) { _, item ->
                    ArticleCard(
                        data = item,
                        onNavigateToLogin = onNavigateToLogin,
                        onNavigateToSystem = onNavigateToSystem,
                        onNavigateToUser = onNavigateToUser,
                        onNavigateToWeb = onNavigateToWeb
                    )
                }
            }
        }
    }
}