package ru.lanik.kedditor.ui.screen.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.lanik.kedditor.R
import ru.lanik.kedditor.ui.helper.ErrorHandlerView
import ru.lanik.kedditor.ui.helper.PostViewItem
import ru.lanik.kedditor.ui.helper.StyledTopScreenBar
import ru.lanik.kedditor.ui.theme.KedditorTheme
import ru.lanik.kedditor.utils.extension.simpleVerticalScrollbar
import ru.lanik.kedditor.utils.extension.toFormatStr
import ru.lanik.network.models.Comments

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
    viewModel: CommentsViewModel,
    onFragmentResult: () -> Unit = {},
) {
    val viewState by viewModel.commentsViewState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .background(KedditorTheme.colors.primaryBackground),
    ) {
        StyledTopScreenBar(
            scrollBehavior = scrollBehavior,
            isLoading = viewState.isLoading,
            navIcon = {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = KedditorTheme.colors.tintColor,
                )
            },
            onNavClick = {
                viewModel.onNavigateBack()
                viewState.postWithComments?.let {
                    onFragmentResult()
                }
            },
            showActionButton = false,
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = R.string.comments_fragment_name).uppercase(),
                    color = KedditorTheme.colors.primaryText,
                    style = KedditorTheme.typography.body,
                )
            }
        }
        ErrorHandlerView(
            errorState = viewState.errorState,
            loadingState = viewState.postWithComments == null,
            modifier = Modifier.weight(1f),
        ) {
            viewState.postWithComments?.let { data ->
                val rememberColumnState = rememberLazyListState()
                LazyColumn(
                    state = rememberColumnState,
                    modifier = Modifier
                        .fillMaxSize()
                        .simpleVerticalScrollbar(
                            state = rememberColumnState,
                            scrollbarColor = KedditorTheme.colors.tintColor,
                        ),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item {
                        PostViewItem(
                            post = data.post,
                            isAuth = viewModel.isAuth(),
                        )
                    }
                    item {
                        data.post.selfText?.let {
                            Column(
                                modifier = Modifier.padding(KedditorTheme.shapes.generalPadding),
                            ) {
                                Text(
                                    text = it,
                                    color = KedditorTheme.colors.primaryText,
                                    style = KedditorTheme.typography.body,
                                )
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    data.comments.forEach {
                        if (it.utcTimeStamp > 1.0 && it.commentsBody != null) {
                            item {
                                CommentsViewItem(
                                    comments = it,
                                    backgroundColor = KedditorTheme.colors.secondaryBackground,
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentsViewItem(
    comments: Comments,
    backgroundColor: Color = KedditorTheme.colors.primaryBackground,
) {
    Surface(
        color = backgroundColor,
        shape = KedditorTheme.shapes.cornersStyle,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KedditorTheme.shapes.generalPadding),
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = comments.author ?: "",
                    color = KedditorTheme.colors.primaryText,
                    style = KedditorTheme.typography.body,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = comments.getDate(),
                    color = KedditorTheme.colors.secondaryText,
                    style = KedditorTheme.typography.caption,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = comments.commentsBody!!,
                    color = KedditorTheme.colors.primaryText,
                    style = KedditorTheme.typography.body,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if ((comments.ups ?: 0) > 1) {
                    Text(
                        text = "ups: " + comments.ups!!.toFormatStr("."),
                        color = KedditorTheme.colors.primaryText,
                        style = KedditorTheme.typography.body,
                    )
                }
            }
        }
    }
}