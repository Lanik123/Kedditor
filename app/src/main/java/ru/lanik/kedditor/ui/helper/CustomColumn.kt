package ru.lanik.kedditor.ui.helper

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.lanik.kedditor.R
import ru.lanik.kedditor.constants.DefaultError
import ru.lanik.kedditor.model.SnackbarModel
import ru.lanik.kedditor.ui.theme.KedditorTheme
import ru.lanik.network.models.Post

@Composable
fun InfinityPostView(
    modifier: Modifier = Modifier,
    isAuth: Boolean = false,
    onPostClick: (String) -> Unit = {},
    backgroundColor: Color = KedditorTheme.colors.primaryBackground,
    posts: Flow<PagingData<Post>>? = null,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
    val postListItems: LazyPagingItems<Post>? = posts?.collectAsLazyPagingItems()

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                coroutineScope.launch {
                    listState.scrollToItem(0)
                }
            }
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
    LazyColumn(
        state = listState,
        modifier = modifier
            .background(backgroundColor),
    ) {
        postListItems?.let { notNull ->
            items(
                count = notNull.itemCount,
                key = notNull.itemKey { it.id },
            ) { index ->
                notNull[index]?.let { post ->
                    PostViewItem(
                        post = post,
                        onPostClick = onPostClick,
                        isAuth = isAuth,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun ErrorHandlerView(
    errorState: DefaultError?,
    modifier: Modifier = Modifier,
    loadingState: Boolean = false,
    backgroundColor: Color = KedditorTheme.colors.primaryBackground,
    errorTextColor: Color = KedditorTheme.colors.primaryText,
    onResetClick: (Boolean) -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    val snackbarState = remember { mutableStateOf(SnackbarModel()) }
    val coroutineScope = rememberCoroutineScope()
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
    ) {
        errorState?.let {
            if (loadingState && errorState != DefaultError.NO) {
                Column(
                    modifier = modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    when (errorState) {
                        DefaultError.NO_INTERNET -> {
                            val snackbarMessage = stringResource(id = R.string.error_snackbar_no_internet)
                            val snackbarActionLabel = stringResource(id = R.string.error_snackbar_reset)
                            ErrorViewItem(
                                imageId = R.drawable.where_connect,
                                errorMessage = stringResource(id = R.string.error_no_internet),
                                errorTextColor = errorTextColor,
                            )
                            snackbarState.let {
                                coroutineScope.launch {
                                    it.value = SnackbarModel(
                                        messageBody = snackbarMessage,
                                        actionLabel = snackbarActionLabel,
                                        isShow = true,
                                        isUpdate = false,
                                    )
                                }
                            }
                        }
                        DefaultError.UNKNOWN_HOST -> {
                            val snackbarMessage = stringResource(id = R.string.error_snackbar_no_internet)
                            val snackbarActionLabel = stringResource(id = R.string.error_snackbar_reset)
                            ErrorViewItem(
                                imageId = R.drawable.where_connect,
                                errorMessage = stringResource(id = R.string.error_unknown_host),
                                errorTextColor = errorTextColor,
                            )
                            snackbarState.let {
                                coroutineScope.launch {
                                    it.value = SnackbarModel(
                                        messageBody = snackbarMessage,
                                        actionLabel = snackbarActionLabel,
                                        isShow = true,
                                        isUpdate = false,
                                    )
                                }
                            }
                        }
                        else -> {
                            val snackbarMessage = stringResource(id = R.string.error_snackbar_other)
                            val snackbarActionLabel = stringResource(id = R.string.error_snackbar_reset)
                            ErrorViewItem(
                                imageId = R.drawable.where_connect,
                                errorMessage = stringResource(id = R.string.error_other),
                                errorTextColor = errorTextColor,
                            )
                            snackbarState.let {
                                coroutineScope.launch {
                                    it.value = SnackbarModel(
                                        messageBody = snackbarMessage,
                                        actionLabel = snackbarActionLabel,
                                        isShow = true,
                                        isUpdate = false,
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                content()
                if (errorState != DefaultError.NO) {
                    when (errorState) {
                        DefaultError.NO_INTERNET -> {
                            val snackbarMessage = stringResource(id = R.string.error_snackbar_no_internet)
                            val snackbarActionLabel = stringResource(id = R.string.error_snackbar_reset)
                            snackbarState.let {
                                coroutineScope.launch {
                                    it.value = SnackbarModel(
                                        messageBody = snackbarMessage,
                                        actionLabel = snackbarActionLabel,
                                        isShow = true,
                                        isUpdate = true,
                                    )
                                }
                            }
                        }
                        DefaultError.UNKNOWN_HOST -> {
                            val snackbarMessage = stringResource(id = R.string.error_snackbar_no_internet)
                            val snackbarActionLabel = stringResource(id = R.string.error_snackbar_reset)
                            snackbarState.let {
                                coroutineScope.launch {
                                    it.value = SnackbarModel(
                                        messageBody = snackbarMessage,
                                        actionLabel = snackbarActionLabel,
                                        isShow = true,
                                        isUpdate = true,
                                    )
                                }
                            }
                        }
                        else -> {
                            val snackbarMessage = stringResource(id = R.string.error_snackbar_other)
                            val snackbarActionLabel = stringResource(id = R.string.error_snackbar_reset)
                            snackbarState.let {
                                coroutineScope.launch {
                                    it.value = SnackbarModel(
                                        messageBody = snackbarMessage,
                                        actionLabel = snackbarActionLabel,
                                        isShow = true,
                                        isUpdate = true,
                                    )
                                }
                            }
                        }
                    }
                } else {
                    snackbarState.value = SnackbarModel(
                        isShow = false,
                    )
                }
            }
        }
    }
    if (snackbarState.value.isShow) {
        Snackbar(
            containerColor = KedditorTheme.colors.secondaryBackground,
            shape = KedditorTheme.shapes.cornersStyle,
            modifier = Modifier.padding(KedditorTheme.shapes.generalPadding),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val snackbarValue = snackbarState.value
                snackbarValue.messageBody?.let {
                    Text(
                        text = it,
                        color = KedditorTheme.colors.secondaryText,
                        style = KedditorTheme.typography.body,
                        modifier = Modifier.weight(1f),
                    )
                }
                snackbarValue.actionLabel?.let {
                    OutlinedButton(
                        onClick = {
                            onResetClick(snackbarValue.isUpdate)
                        },
                    ) {
                        Text(
                            text = it,
                            color = KedditorTheme.colors.secondaryText,
                            style = KedditorTheme.typography.body,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorViewItem(
    imageId: Int,
    errorMessage: String,
    errorTextColor: Color = KedditorTheme.colors.primaryText,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val gifWidth = ((screenWidth * 3) / 4).dp
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Image(
        painter = rememberAsyncImagePainter(imageId, imageLoader),
        contentDescription = null,
        modifier = Modifier.width(gifWidth),
    )
    Text(
        text = errorMessage,
        color = errorTextColor,
        style = KedditorTheme.typography.body,
        modifier = Modifier.width(gifWidth),
        textAlign = TextAlign.Center,
    )
}