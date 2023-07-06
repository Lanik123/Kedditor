package ru.lanik.kedditor.repository

import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.StateFlow
import ru.lanik.kedditor.model.SettingsModel
import ru.lanik.kedditor.model.source.SubredditSource
import ru.lanik.kedditor.utils.SchedulerPolicy
import ru.lanik.kedditor.utils.extension.applySchedulerPolicy
import ru.lanik.kedditor.utils.extension.fixAuth
import ru.lanik.network.api.reactive.RxSubredditsAPI
import ru.lanik.network.constants.ApiBaseConst
import ru.lanik.network.extension.toListSubreddit
import ru.lanik.network.extension.toSubreddit
import ru.lanik.network.models.Subreddit

class RxSubredditsRepository(
    private val rxSubredditsAPI: RxSubredditsAPI,
    private val schedulerPolicy: SchedulerPolicy,
    private val settingsStateFlow: StateFlow<SettingsModel>,
) : SubredditsRepository.Reactive {

    override fun fetchSubreddits(
        source: SubredditSource,
        page: String,
    ): Single<List<Subreddit>> {
        val direct = source.mainSrc.fixAuth(settingsStateFlow.value.isAuth)
        return rxSubredditsAPI.getSubredditListing(direct, page)
            .applySchedulerPolicy(schedulerPolicy)
            .map {
                it.data.children.toListSubreddit()
            }
    }

    override fun getSubredditInfo(source: SubredditSource): Single<Subreddit> {
        val direct = source.mainSrc
        return rxSubredditsAPI.getSubredditInfo(direct, auth = "".fixAuth(settingsStateFlow.value.isAuth))
            .applySchedulerPolicy(schedulerPolicy)
            .map {
                return@map it.toSubreddit()
            }
    }

    override fun getSubredditsByName(
        query: String,
        limit: Int,
    ): Single<List<Subreddit>> {
        val cmd = if (settingsStateFlow.value.isAuth) {
            ApiBaseConst.SEARCH_SUBREDDIT.auth
        } else {
            ApiBaseConst.SEARCH_SUBREDDIT.notAuth
        }
        return rxSubredditsAPI.getSubredditsByName(cmd, query, limit)
            .applySchedulerPolicy(schedulerPolicy)
            .map {
                it.data.children.toListSubreddit()
            }
    }
}