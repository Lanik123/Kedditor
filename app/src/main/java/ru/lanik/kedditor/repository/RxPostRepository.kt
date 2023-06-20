package ru.lanik.kedditor.repository

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.ReplaySubject
import ru.lanik.kedditor.model.PostFetch
import ru.lanik.kedditor.model.PostSource
import ru.lanik.kedditor.utils.SchedulerPolicy
import ru.lanik.kedditor.utils.extension.applySchedulerPolicy
import ru.lanik.kedditor.utils.extension.fixAuth
import ru.lanik.network.api.PostAPI
import ru.lanik.network.extension.toListPost

class RxPostRepository(
    private val postAPI: PostAPI,
    private val schedulerPolicy: SchedulerPolicy,
    private val compositeDisposable: CompositeDisposable,
) : PostRepository.Reactive {
    override val postFetchData: ReplaySubject<PostFetch> = ReplaySubject.create(1)

    override fun fetchPosts(
        source: PostSource,
        after: String,
    ) {
        val direct = source.toPath().fixAuth(false)
        postAPI.getPosts(direct, after)
            .applySchedulerPolicy(schedulerPolicy)
            .subscribe({ data ->
                val postList = data.data.children.toListPost()
                postFetchData.onNext(
                    PostFetch(
                        postSrc = source,
                        posts = postList,
                        isUpdate = postFetchData.value?.postSrc == source,
                    ),
                )
            }, { error ->
                handleError(error)
            }).also { compositeDisposable.add(it) }
    }

    override fun handleError(error: Throwable) {
        // TODO: Processing error
        postFetchData.onError(error)
    }
}