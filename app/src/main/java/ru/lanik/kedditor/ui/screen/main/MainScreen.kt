package ru.lanik.kedditor.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.launch
import ru.lanik.kedditor.R
import ru.lanik.kedditor.model.ActionVariant
import ru.lanik.kedditor.model.DropdownMenuModel
import ru.lanik.kedditor.ui.helper.DropdownMenuItem
import ru.lanik.kedditor.ui.helper.ErrorHandlerView
import ru.lanik.kedditor.ui.helper.InfinityPostView
import ru.lanik.kedditor.ui.helper.StyledTopScreenBar
import ru.lanik.kedditor.ui.helper.SubredditRow
import ru.lanik.kedditor.ui.theme.KedditorTheme
import ru.lanik.network.constants.DefaultPostSort
import ru.lanik.network.constants.DefaultPostSource
import ru.lanik.network.models.Subreddit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
) {
    val viewState by viewModel.mainViewState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val drawerScope = rememberCoroutineScope()
    val isDropdownMoreOpen = remember { mutableStateOf(false) }
    val isDropdownSortOpen = remember { mutableStateOf(false) }
    val isAccountSelectOpen = remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
    val screenWidth = LocalConfiguration.current.screenWidthDp

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.fetchPosts()
                scrollBehavior.state.heightOffset = 0.0f
            }
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    val closeDrawer = {
        drawerScope.launch {
            drawerState.close()
        }
    }

    val openDrawer = {
        drawerScope.launch {
            drawerState.open()
        }
    }

    if ((viewState.subreddits?.size ?: 0) < 1) {
        viewModel.fetchSubreddits()
    }

    if (isAccountSelectOpen.value) {
        Dialog(
            onDismissRequest = {
                isAccountSelectOpen.value = false
            },
            content = { // TODO
                Column(
                    modifier = Modifier
                        .background(KedditorTheme.colors.primaryBackground)
                        .width(((screenWidth * 5) / 6).dp)
                        .height(((screenWidth * 4) / 6).dp)
                        .padding(KedditorTheme.shapes.generalPadding),
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { isAccountSelectOpen.value = false }
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            tint = KedditorTheme.colors.tintColor,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Default",
                            color = KedditorTheme.colors.primaryText,
                            style = KedditorTheme.typography.body,
                        )
                    }
                }
            },
        )
    }

    ModalNavigationDrawer(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                subreddits = viewState.subreddits,
                onHomeClicked = {
                    viewModel.fetchPosts(DefaultPostSource.POPULAR.name.lowercase())
                    closeDrawer()
                },
                onPopularClicked = {
                    viewModel.fetchPosts(DefaultPostSource.POPULAR.name.lowercase())
                    closeDrawer()
                },
                onAllClicked = {
                    viewModel.fetchPosts(DefaultPostSource.ALL.name.lowercase())
                    closeDrawer()
                },
                onSettingsClicked = {
                    viewModel.onNavigateTo(R.id.action_main_to_sett)
                    closeDrawer()
                },
                onSubredditClicked = {
                    val newStr = it.lowercase()
                    viewModel.fetchPosts(newStr)
                    closeDrawer()
                },
            )
        },
        content = {
            Column(
                modifier = Modifier.background(KedditorTheme.colors.primaryBackground),
            ) {
                Column {
                    StyledTopScreenBar(
                        scrollBehavior = scrollBehavior,
                        isLoading = viewState.isLoading,
                        navIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Menu,
                                contentDescription = null,
                                tint = KedditorTheme.colors.tintColor,
                            )
                        },
                        onNavClick = { openDrawer() },
                        onActionClick = {
                            isDropdownMoreOpen.value = true
                        },
                    ) {
                        TitleContent(
                            source = viewModel.getSource().uppercase(),
                            sort = viewModel.getSort().uppercase(),
                            onTitleClick = {
                                isDropdownSortOpen.value = true
                            },
                        )
                    }
                    DropdownMenuItem(
                        model = DropdownMenuModel(
                            values = listOf(
                                stringResource(id = R.string.settings_option_post_sort_hot),
                                stringResource(id = R.string.settings_option_post_sort_new),
                                stringResource(id = R.string.settings_option_post_sort_top),
                                stringResource(id = R.string.settings_option_post_sort_rising),
                            ),
                        ),
                        isDropdownOpen = isDropdownSortOpen.value,
                        onItemClick = {
                            when (it) {
                                0 -> viewModel.fetchPosts(newSort = DefaultPostSort.HOT)
                                1 -> viewModel.fetchPosts(newSort = DefaultPostSort.NEW)
                                2 -> viewModel.fetchPosts(newSort = DefaultPostSort.TOP)
                                3 -> viewModel.fetchPosts(newSort = DefaultPostSort.RISING)
                                else -> throw NotImplementedError("No valid value for this $it")
                            }
                            isDropdownSortOpen.value = false
                        },
                        onDismiss = {
                            isDropdownSortOpen.value = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    DropdownMenuItem(
                        model = DropdownMenuModel(
                            values = listOf(
                                stringResource(id = R.string.more_dropdown_reset),
                            ),
                        ),
                        isDropdownOpen = isDropdownMoreOpen.value,
                        onItemClick = {
                            when (it) {
                                0 -> viewModel.fetchPosts()
                                else -> throw NotImplementedError("No valid value for this $it")
                            }
                            isDropdownMoreOpen.value = false
                        },
                        onDismiss = {
                            isDropdownMoreOpen.value = false
                        },
                        offset = DpOffset(screenWidth.dp, 0.dp),
                    )
                }
                ErrorHandlerView(
                    errorState = viewState.errorState,
                    loadingState = viewState.posts == null,
                    onResetClick = {
                        viewModel.fetchPosts(isUpdate = it)
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    InfinityPostView(
                        posts = viewState.posts,
                        onPostClick = {
                            viewModel.onNavigateToComments(
                                url = it,
                                parentSubredditName = viewModel.getSource(),
                            )
                        },
                        onLoadMore = viewModel::fetchPostsForUpdate,
                        isAuth = viewModel.isAuth(),
                    )
                }
                BottomActionBar(
                    actionList = listOf(
                        ActionVariant(
                            icon = Icons.Rounded.Home,
                            command = {
                                viewModel.fetchPosts(DefaultPostSource.POPULAR.name.lowercase())
                            },
                        ),
                        ActionVariant(
                            icon = Icons.Rounded.List,
                            command = {
                                viewModel.onNavigateTo(R.id.action_main_to_sub)
                            },
                        ),
                        ActionVariant(
                            icon = Icons.Rounded.Person,
                            command = {
                                isAccountSelectOpen.value = true
                            },
                        ),
                    ),
                )
            }
        },
    )
}

@Composable
fun DrawerContent(
    subreddits: List<Subreddit>? = null,
    onHomeClicked: () -> Unit = {},
    onPopularClicked: () -> Unit = {},
    onAllClicked: () -> Unit = {},
    onSettingsClicked: () -> Unit = {},
    onSubredditClicked: (String) -> Unit = {},
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .width(((screenWidth * 5) / 6).dp)
            .padding(KedditorTheme.shapes.generalPadding)
            .background(KedditorTheme.colors.primaryBackground)
            .fillMaxHeight(),
    ) {
        OutlinedButton(
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = KedditorTheme.colors.primaryBackground,
            ),
            onClick = onHomeClicked,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Home,
                    contentDescription = null,
                    tint = KedditorTheme.colors.tintColor,
                )
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = stringResource(R.string.main_button_draw_home),
                    color = KedditorTheme.colors.primaryText,
                    style = KedditorTheme.typography.body,
                )
            }
        }
        OutlinedButton(
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = KedditorTheme.colors.primaryBackground,
            ),
            onClick = onPopularClicked,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Home,
                    contentDescription = null,
                    tint = KedditorTheme.colors.tintColor,
                )
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = stringResource(R.string.main_button_draw_popular),
                    color = KedditorTheme.colors.primaryText,
                    style = KedditorTheme.typography.body,
                )
            }
        }
        OutlinedButton(
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = KedditorTheme.colors.primaryBackground,
            ),
            onClick = onAllClicked,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Home,
                    contentDescription = null,
                    tint = KedditorTheme.colors.tintColor,
                )
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = stringResource(R.string.main_button_draw_all),
                    color = KedditorTheme.colors.primaryText,
                    style = KedditorTheme.typography.body,
                )
            }
        }
        Divider(
            color = KedditorTheme.colors.secondaryText,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 0.dp,
                    vertical = KedditorTheme.shapes.generalPadding,
                ),
        )
        OutlinedButton(
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = KedditorTheme.colors.primaryBackground,
            ),
            onClick = onSettingsClicked,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = null,
                    tint = KedditorTheme.colors.tintColor,
                )
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = stringResource(R.string.main_button_draw_settings),
                    color = KedditorTheme.colors.primaryText,
                    style = KedditorTheme.typography.body,
                )
            }
        }
        Divider(
            color = KedditorTheme.colors.secondaryText,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 0.dp,
                    vertical = KedditorTheme.shapes.generalPadding,
                ),
        )
        Text(
            text = stringResource(R.string.main_text_draw_subscriptions),
            color = KedditorTheme.colors.secondaryText,
            style = KedditorTheme.typography.toolbar,
        )
        LazyColumn {
            subreddits?.forEach {
                item {
                    SubredditRow(
                        subredditName = it.name,
                        subredditSubs = it.subscribers ?: 0,
                        subredditIcon = it.imageUrl,
                        onClick = onSubredditClicked,
                    )
                }
            }
        }
    }
}

@Composable
fun TitleContent(
    source: String,
    sort: String,
    onTitleClick: () -> Unit = {},
) {
    Button(
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = KedditorTheme.colors.primaryBackground,
        ),
        onClick = { onTitleClick() },
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = source,
                color = KedditorTheme.colors.primaryText,
                style = KedditorTheme.typography.body,
            )
            Text(
                text = sort,
                color = KedditorTheme.colors.secondaryText,
                style = KedditorTheme.typography.toolbar,
            )
        }
    }
}

@Composable
fun BottomActionBar(
    actionList: List<ActionVariant>,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = KedditorTheme.colors.primaryBackground,
        tonalElevation = 12.dp,
        shadowElevation = 12.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            userScrollEnabled = false,
            modifier = modifier.fillMaxWidth(),
        ) {
            actionList.forEach { actionItem ->
                item {
                    IconButton(onClick = { actionItem.command() }) {
                        Icon(
                            imageVector = actionItem.icon,
                            contentDescription = null,
                            tint = KedditorTheme.colors.tintColor,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MainScreenPreview() {
    KedditorTheme(
        darkTheme = true,
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .background(KedditorTheme.colors.primaryBackground),
        ) {
            StyledTopScreenBar(
                isLoading = true,
            ) {
                TitleContent(
                    source = "Test",
                    sort = "HOT",
                )
            }
            BottomActionBar(
                actionList = listOf(
                    ActionVariant(
                        icon = Icons.Rounded.Home,
                    ),
                    ActionVariant(
                        icon = Icons.Rounded.List,
                    ),
                    ActionVariant(
                        icon = Icons.Rounded.Person,
                    ),
                ),
            )
        }
    }
}