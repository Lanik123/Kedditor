package ru.lanik.kedditor.ui.screen.main

import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.lanik.kedditor.R
import ru.lanik.kedditor.constants.DefaultError
import ru.lanik.kedditor.model.PostModel
import ru.lanik.kedditor.model.path.PostPath
import ru.lanik.kedditor.model.path.SubredditPath
import ru.lanik.kedditor.repository.PostRepository
import ru.lanik.kedditor.repository.SettingsManager
import ru.lanik.kedditor.repository.SubredditsRepository
import ru.lanik.network.constants.DefaultPostSort
import ru.lanik.network.models.Post
import ru.lanik.network.models.Subreddit
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class MainViewModel(
    private val compositeDisposable: CompositeDisposable,
    private val postRepository: PostRepository.Reactive,
    private val subredditsRepository: SubredditsRepository.Reactive,
    private val navController: NavController,
    private val settingsManager: SettingsManager.Reactive,
) : ViewModel() {
    private val settingsStateFlow = settingsManager.getStateFlow()
    private var retryCount = 0
    private var defaultPostPath = PostPath(
        mainSrc = settingsStateFlow.value.defaultPostSource.name.lowercase(),
        sortType = settingsStateFlow.value.defaultPostSort,
    )
    private var defaultSubredditPath = SubredditPath(
        mainSrc = settingsStateFlow.value.defaultSubredditSource.name.lowercase(),
    )
    private val _mainViewState = MutableStateFlow(PostModel(isLoading = true))
    val mainViewState: StateFlow<PostModel> = _mainViewState.asStateFlow()

    fun getSource(): String {
        return defaultPostPath.mainSrc
    }

    fun getSort(): String {
        return defaultPostPath.sortToStr()
    }

    fun setSource(
        newSource: String? = null,
        newSort: DefaultPostSort? = null,
    ) {
        newSource?.let {
            if (it != defaultPostPath.mainSrc) {
                defaultPostPath = defaultPostPath.copy(
                    mainSrc = it,
                )
            }
        }
        newSort?.let {
            if (it != defaultPostPath.sortType) {
                defaultPostPath = defaultPostPath.copy(
                    sortType = it,
                )
            }
        }
    }

    fun isAuth(): Boolean = settingsStateFlow.value.isAuth

    fun fetchPosts(
        newSource: String? = null,
        newSort: DefaultPostSort? = null,
        isUpdate: Boolean = false,
    ) {
        setIsLoading(true)
        setSource(newSource, newSort)
        val afterId = _mainViewState.value.lastPostId ?: ""
        postRepository.fetchPosts(defaultPostPath, afterId).subscribe({
            _mainViewState.value = onPostSubscribe(
                newValue = it,
                isUpdate = isUpdate,
            )
        }, {
            onError(it)
            setIsLoading(false)
        }).addTo(compositeDisposable)
    }

    fun fetchPostsForUpdate() {
        fetchPosts(isUpdate = true)
    }

    fun fetchSubreddits() {
        subredditsRepository.fetchSubreddits(defaultSubredditPath, "").subscribe({
            _mainViewState.value = onSubredditSubscribe(it)
        }, {
            onError(it)
        }).addTo(compositeDisposable)
    }

    fun onNavigateToComments(
        url: String,
        parentSubredditName: String,
    ) {
        val bundle = bundleOf("post_url" to url)
        bundle.putString("parent_sub", parentSubredditName)
        compositeDisposable.clear()
        navController.navigate(R.id.action_main_to_view, bundle)
    }

    fun onNavigateTo(commandId: Int) {
        compositeDisposable.clear()
        navController.navigate(commandId)
    }

    private fun onError(error: Throwable) {
        if (error is UnknownHostException) {
            setErrorType(DefaultError.NO_INTERNET)
        } else if (error.message!!.contains("HTTP 404")) {
            setErrorType(DefaultError.UNKNOWN_HOST)
        } else if (error.message!!.contains("HTTP 403")) {
            setErrorType(DefaultError.PRIVATE)
        } else if (error is SocketTimeoutException) {
            setErrorType(DefaultError.UNKNOWN_HOST)
        } else { error.printStackTrace() }
    }

    private fun onPostSubscribe(
        newValue: List<Post>,
        isUpdate: Boolean,
    ): PostModel {
        val newList = mutableListOf<Post>()
        if (newValue.isEmpty() && retryCount < 5) {
            fetchPosts()
            retryCount++
            return PostModel(
                isLoading = true,
            )
        }
        if (isUpdate) {
            _mainViewState.value.posts?.let {
                if (it.isNotEmpty()) {
                    if (newValue.last().id == it.last().id) {
                        return _mainViewState.value
                    } else {
                        newList.addAll(it)
                    }
                }
            }
        }
        newList.addAll(newValue)
        val pageId = if (newList.isNotEmpty()) newList.last().id else ""
        retryCount = 0
        return mainViewState.value.copy(
            posts = newList,
            lastPostId = pageId,
            errorState = DefaultError.NO,
            isLoading = false,
        )
    }

    private fun onSubredditSubscribe(newValue: List<Subreddit>): PostModel {
        return mainViewState.value.copy(
            subreddits = newValue,
            errorState = DefaultError.NO,
        )
    }

    private fun setIsLoading(isLoading: Boolean) {
        if (_mainViewState.value.isLoading != isLoading) {
            _mainViewState.value = _mainViewState.value.copy(
                isLoading = isLoading,
            )
        }
    }

    private fun setErrorType(errorType: DefaultError?) {
        if (_mainViewState.value.errorState != errorType) {
            _mainViewState.value = _mainViewState.value.copy(
                errorState = errorType,
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}