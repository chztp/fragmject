package com.example.fragment.module.wan.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fragment.library.base.compose.FullScreenLoading
import com.example.fragment.library.base.compose.theme.WanTheme
import com.example.fragment.library.base.model.BaseViewModel
import com.example.fragment.library.common.constant.Keys
import com.example.fragment.library.common.constant.Router
import com.example.fragment.library.common.fragment.RouterFragment
import com.example.fragment.module.wan.R
import com.example.fragment.module.wan.model.NavigationViewModel
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

class NavigationFragment : RouterFragment() {

    companion object {
        @JvmStatic
        fun newInstance(): NavigationFragment {
            return NavigationFragment()
        }
    }

    private val tabs = arrayOf("导航", "体系")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                WanTheme {
                    NavigationScreen()
                }
            }
        }
    }

    override fun initView() {}

    override fun initViewModel(): BaseViewModel? {
        return null
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun NavigationScreen(
        viewModel: NavigationViewModel = viewModel()
    ) {
        val pagerState = rememberPagerState(viewModel.getTabIndex())
        val coroutineScope = rememberCoroutineScope()
        DisposableEffect(Unit) {
            onDispose {
                viewModel.updateTabIndex(pagerState.currentPage)
            }
        }
        Column {
            NavigationTab(
                pagerState = pagerState,
                onTabClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(it)
                    }
                },
                data = tabs
            )
            NavigationPager(count = tabs.size, pagerState = pagerState, viewModel)
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun NavigationTab(
        pagerState: PagerState,
        onTabClick: (index: Int) -> Unit,
        data: Array<String>
    ) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            backgroundColor = colorResource(R.color.white),
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                    color = colorResource(R.color.theme)
                )
            },
            divider = {
                TabRowDefaults.Divider(color = colorResource(R.color.transparent))
            }
        ) {
            data.forEachIndexed { index, text ->
                Tab(
                    text = { Text(text) },
                    onClick = { onTabClick(index) },
                    selected = pagerState.currentPage == index,
                    selectedContentColor = colorResource(R.color.theme),
                    unselectedContentColor = colorResource(R.color.text_999)
                )
            }
        }
        TabRowDefaults.Divider(color = colorResource(R.color.line))
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun NavigationPager(
        count: Int,
        pagerState: PagerState,
        viewModel: NavigationViewModel
    ) {
        HorizontalPager(
            count = count,
            state = pagerState,
        ) { page ->
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            if (uiState.loading) {
                FullScreenLoading()
            } else {
                if (tabs[page] == "导航") {
                    NavigationLinkContent(viewModel)
                } else if (tabs[page] == "体系") {
                    NavigationSystemContent(viewModel)
                }
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun NavigationLinkContent(
        viewModel: NavigationViewModel
    ) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val scrollState = rememberScrollState(viewModel.getListScrollOffset("导航"))
        DisposableEffect(Unit) {
            onDispose {
                viewModel.updateListScrollOffset(scrollState.value, "导航")
            }
        }
        Row {
            LazyColumn(
                modifier = Modifier.width(150.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp),
            ) {
                itemsIndexed(uiState.navigationResult.toList()) { index, item ->
                    Box(
                        modifier = Modifier
                            .background(
                                colorResource(if (item.isSelected) R.color.gray else R.color.white)
                            )
                            .fillMaxWidth()
                            .height(45.dp)
                            .clickable {
                                viewModel.updateSelectNavigation(index)
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = item.name,
                            color = colorResource(id = R.color.text_333),
                            fontSize = 16.sp,
                        )
                    }
                }
            }
            FlowRow(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                uiState.articlesResult.forEach {
                    Box(modifier = Modifier.padding(5.dp, 0.dp, 5.dp, 0.dp)) {
                        Button(
                            onClick = {
                                navigation(
                                    Router.WEB,
                                    bundleOf(Keys.URL to Uri.encode(it.link))
                                )
                            },
                            elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = colorResource(R.color.gray_e5),
                                contentColor = colorResource(R.color.text_999)
                            ),
                            contentPadding = PaddingValues(10.dp, 0.dp, 10.dp, 0.dp)
                        ) {
                            Text(
                                text = it.title,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
    @Composable
    fun NavigationSystemContent(
        viewModel: NavigationViewModel
    ) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            uiState.systemTreeResult.forEach {
                stickyHeader {
                    Text(
                        text = it.name,
                        modifier = Modifier
                            .background(colorResource(R.color.gray))
                            .fillMaxWidth()
                            .padding(15.dp, 5.dp, 15.dp, 5.dp),
                        color = colorResource(R.color.text_666),
                        fontSize = 13.sp
                    )
                }
                it.children?.let { children ->
                    item {
                        FlowRow(
                            modifier = Modifier
                                .background(colorResource(R.color.white))
                                .fillMaxWidth()
                        ) {
                            children.forEach { children ->
                                Box(modifier = Modifier.padding(5.dp, 0.dp, 5.dp, 0.dp)) {
                                    Button(
                                        onClick = {
                                            navigation(
                                                Router.SYSTEM,
                                                bundleOf(Keys.CID to children.id)
                                            )
                                        },
                                        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
                                        shape = RoundedCornerShape(50),
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = colorResource(R.color.gray_e5),
                                            contentColor = colorResource(R.color.text_999)
                                        ),
                                        contentPadding = PaddingValues(10.dp, 0.dp, 10.dp, 0.dp)
                                    ) {
                                        Text(
                                            text = children.name,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}