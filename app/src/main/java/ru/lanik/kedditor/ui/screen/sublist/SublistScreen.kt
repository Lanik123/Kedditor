package ru.lanik.kedditor.ui.screen.sublist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import ru.lanik.kedditor.R
import ru.lanik.kedditor.model.DropdownMenuModel
import ru.lanik.kedditor.ui.helper.CustomPaddingTextField
import ru.lanik.kedditor.ui.helper.CustomTextFieldColors
import ru.lanik.kedditor.ui.helper.DropdownMenuItem
import ru.lanik.kedditor.ui.helper.ErrorHandlerView
import ru.lanik.kedditor.ui.helper.StyledTopScreenBar
import ru.lanik.kedditor.ui.helper.SubredditRow
import ru.lanik.kedditor.ui.theme.KedditorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SublistScreen(
    viewModel: SublistViewModel,
    onFragmentResult: (String) -> Unit,
) {
    val searchVal = remember { mutableStateOf("") }
    val isDropdownMoreOpen = remember { mutableStateOf(false) }
    val viewState by viewModel.sublistViewState.collectAsState()
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.fetchSubreddits()
            }
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .background(KedditorTheme.colors.primaryBackground),
    ) {
        Column {
            StyledTopScreenBar(
                scrollBehavior = scrollBehavior,
                containerColor = KedditorTheme.colors.secondaryBackground,
                isLoading = viewState.isLoading,
                navIcon = {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = null,
                        tint = KedditorTheme.colors.tintColor,
                    )
                },
                onNavClick = viewModel::onNavigateBack,
                onActionClick = {
                    isDropdownMoreOpen.value = true
                },
            ) {
                TitleContent(
                    text = searchVal.value,
                    isLoading = viewState.isLoading,
                    onTextChange = {
                        searchVal.value = it
                        viewModel.onSearching(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            }
            DropdownMenuItem(
                model = DropdownMenuModel(
                    values = listOf(
                        stringResource(id = R.string.more_dropdown_reset),
                    ),
                ),
                isDropdownOpen = isDropdownMoreOpen.value,
                onItemClick = {
                    when (it) {
                        0 -> viewModel.fetchSubreddits()
                        else -> throw NotImplementedError("No valid value for this $it")
                    }
                    isDropdownMoreOpen.value = false
                },
                onDismiss = {
                    isDropdownMoreOpen.value = false
                },
                offset = DpOffset(screenWidth.dp, 0.dp),
                backgroundColor = KedditorTheme.colors.secondaryBackground,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        ErrorHandlerView(
            errorState = viewState.errorState,
            loadingState = viewState.subreddits == null,
            onResetClick = { viewModel.fetchSubreddits() },
            modifier = Modifier.weight(1f),
        ) {
            LazyColumn {
                if (searchVal.value.isNotEmpty()) {
                    viewState.subredditSearch?.forEach {
                        item {
                            SubredditRow(
                                subredditName = it.name,
                                subredditSubs = it.subscribers ?: 0,
                                subredditIcon = it.imageUrl,
                                onClick = {
                                    onFragmentResult(it)
                                    viewModel.onNavigateBack()
                                },
                            )
                        }
                    }
                } else {
                    viewState.subreddits?.forEach {
                        item {
                            SubredditRow(
                                subredditName = it.name,
                                subredditSubs = it.subscribers ?: 0,
                                subredditIcon = it.imageUrl,
                                onClick = {
                                    onFragmentResult(it)
                                    viewModel.onNavigateBack()
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TitleContent(
    text: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit = {},
) {
    CustomPaddingTextField(
        value = text,
        placeholderValue = stringResource(id = R.string.sublist_search_placeholder),
        readOnly = isLoading,
        onValueChange = {
            onTextChange(it)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
        ),
        contentPadding = PaddingValues(
            horizontal = KedditorTheme.shapes.textHorizontalPadding,
            vertical = KedditorTheme.shapes.textVerticalPadding,
        ),
        colors = CustomTextFieldColors(
            textColor = KedditorTheme.colors.primaryText,
            placeholderColor = KedditorTheme.colors.primaryText,
            cursorColor = KedditorTheme.colors.tintColor,
        ),
        textStyle = KedditorTheme.typography.toolbar,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SublistScreenPreview() {
    KedditorTheme(
        darkTheme = true,
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .background(KedditorTheme.colors.primaryBackground),
        ) {
            StyledTopScreenBar(
                isLoading = true,
            ) {
                TitleContent(
                    text = "",
                    isLoading = true,
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LazyColumn {
                item {
                    SubredditRow("Test")
                }
            }
        }
    }
}