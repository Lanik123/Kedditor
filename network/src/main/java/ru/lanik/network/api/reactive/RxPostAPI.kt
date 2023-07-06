package ru.lanik.network.api.reactive

import io.reactivex.rxjava3.core.Single
import kotlinx.serialization.json.JsonElement
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.lanik.network.api.dto.postDTO.PostListingDto

interface RxPostAPI {
    @GET("/r/{source}")
    fun getPosts(
        @Path("source") source: String?,
        @Query("after") page: String = "",
    ): Single<PostListingDto>

    @GET("{url}")
    fun getSinglePost(
        @Path("url") source: String?,
    ): Single<List<JsonElement>>
}