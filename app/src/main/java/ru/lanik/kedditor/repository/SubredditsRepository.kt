package ru.lanik.kedditor.repository

import io.reactivex.rxjava3.core.Single
import ru.lanik.kedditor.model.path.SubredditPath
import ru.lanik.network.models.Subreddit

interface SubredditsRepository {
    interface Reactive {
        fun fetchSubreddits(
            source: SubredditPath,
            page: String = "",
        ): Single<List<Subreddit>>

        fun getSubredditInfo(
            source: SubredditPath,
        ): Single<Subreddit>

        fun getSubredditsByName(
            query: String,
            limit: Int,
        ): Single<List<Subreddit>>
    }
}