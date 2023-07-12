package ru.lanik.kedditor.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.lanik.kedditor.model.path.PostPath
import ru.lanik.kedditor.repository.PostRepository
import ru.lanik.kedditor.utils.extension.await
import ru.lanik.network.models.Post

class PostSource(
    private val postRepository: PostRepository.Reactive,
    private val defaultPostPath: PostPath,
) : PagingSource<String, Post>() {
    override fun getRefreshKey(state: PagingState<String, Post>): String? {
        return state.pages.let {
            if (it.isEmpty()) "" else it.last().nextKey
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Post> {
        val page = params.key ?: ""
        val posts: List<Post> = postRepository.fetchPosts(defaultPostPath, page).await()
        return LoadResult.Page(
            data = posts,
            prevKey = if (posts.isEmpty()) "" else posts.first().id,
            nextKey = if (posts.isEmpty()) "" else posts.last().id,
        )
    }
}