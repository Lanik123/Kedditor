package ru.lanik.kedditor.model

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.lanik.kedditor.constants.DefaultError
import ru.lanik.network.models.Post
import ru.lanik.network.models.Subreddit

data class PostModel(
    val posts: Flow<PagingData<Post>>? = null,
    val lastPostId: String? = null,
    val subreddits: List<Subreddit>? = null,
    var errorState: DefaultError? = null,
    var isLoading: Boolean = false,
)
