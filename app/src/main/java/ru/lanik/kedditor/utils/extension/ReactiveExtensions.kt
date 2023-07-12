package ru.lanik.kedditor.utils.extension

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import ru.lanik.kedditor.utils.SchedulerPolicy
import kotlin.coroutines.resumeWithException

fun <T : Any> Single<T>.applySchedulerPolicy(scheduler: SchedulerPolicy): Single<T> {
    return this.subscribeOn(scheduler.backThread())
        .observeOn(scheduler.mainThread())
}

fun <T : Any> Observable<T>.applySchedulerPolicy(scheduler: SchedulerPolicy): Observable<T> {
    return this.subscribeOn(scheduler.backThread())
        .observeOn(scheduler.mainThread())
}

fun <T : Any> Observable<T>.performOnBack(scheduler: SchedulerPolicy): Observable<T> {
    return this.subscribeOn(scheduler.backThread())
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T : Any> Single<T>.await(): T {
    return suspendCancellableCoroutine { cont ->
        val disposable = subscribe({ cont.resume(it) {} }, { cont.resumeWithException(it) })
        cont.invokeOnCancellation { disposable.dispose() }
    }
}