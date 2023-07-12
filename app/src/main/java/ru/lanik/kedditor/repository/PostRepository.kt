package ru.lanik.kedditor.repository

import io.reactivex.rxjava3.core.Single
import ru.lanik.kedditor.model.path.PostPath
import ru.lanik.network.models.Post
import ru.lanik.network.models.PostWithComments

interface PostRepository {
    interface Reactive {
        fun fetchPosts(
            source: PostPath,
            after: String = "",
        ): Single<List<Post>>
        fun fetchPostWithComments(url: String): Single<PostWithComments>
    }
}