package ru.lanik.kedditor.ui.screen.sublist

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.lanik.kedditor.constants.DefaultError
import ru.lanik.kedditor.model.SublistModel
import ru.lanik.kedditor.model.path.SubredditPath
import ru.lanik.kedditor.repository.SettingsManager
import ru.lanik.kedditor.repository.SubredditsRepository
import ru.lanik.network.constants.DefaultSubredditSource
import ru.lanik.network.models.Subreddit
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class SublistViewModel(
    private val compositeDisposable: CompositeDisposable,
    private val subredditsRepository: SubredditsRepository.Reactive,
    private val navController: NavController,
    private val settingsManager: SettingsManager.Reactive,
) : ViewModel() {
    private val settingsStateFlow = settingsManager.getStateFlow()
    private var defaultPath = SubredditPath(
        mainSrc = settingsStateFlow.value.defaultSubredditSource.name.lowercase(),
    )
    private val _sublistViewState = MutableStateFlow(SublistModel(isLoading = true))
    val sublistViewState: StateFlow<SublistModel> = _sublistViewState.asStateFlow()

    fun onSearching(str: String) {
        subredditsRepository.getSubredditsByName(
            query = str,
            limit = 5,
        ).subscribe({
            _sublistViewState.value = onSubredditSubscribe(
                newValue = it,
                isSearch = true,
            )
        }, {
            onError(it)
        }).addTo(compositeDisposable)
    }

    fun onNavigateBack() {
        compositeDisposable.clear()
        navController.navigateUp()
    }

    fun fetchSubreddits(
        newSource: DefaultSubredditSource? = null,
    ) {
        setIsLoading(true)
        newSource?.let {
            if (it.name.lowercase() != defaultPath.mainSrc) {
                defaultPath = SubredditPath.fromEnum(it)
            }
        }
        var afterId = ""
        _sublistViewState.value.subreddits?.let {
            if (it.isNotEmpty() && !_sublistViewState.value.isLoading) {
                afterId = it.last().id
            }
        }
        subredditsRepository.fetchSubreddits(defaultPath, afterId).subscribe({
            _sublistViewState.value = onSubredditSubscribe(
                newValue = it,
                isSearch = false,
            )
        }, {
            onError(it)
            setIsLoading(false)
        }).addTo(compositeDisposable)
    }

    private fun onSubredditSubscribe(
        newValue: List<Subreddit>,
        isSearch: Boolean,
    ): SublistModel {
        return if (isSearch) {
            _sublistViewState.value.copy(
                subredditSearch = newValue,
                errorState = DefaultError.NO,
                isLoading = false,
            )
        } else {
            SublistModel(
                subreddits = newValue,
                errorState = DefaultError.NO,
                isLoading = false,
            )
        }
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

    private fun setErrorType(errorType: DefaultError?) {
        if (_sublistViewState.value.errorState != errorType) {
            _sublistViewState.value.errorState = errorType
        }
    }

    private fun setIsLoading(isLoading: Boolean) {
        if (_sublistViewState.value.isLoading != isLoading) {
            _sublistViewState.value = _sublistViewState.value.copy(
                isLoading = isLoading,
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}