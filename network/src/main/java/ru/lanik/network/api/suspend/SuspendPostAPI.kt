package ru.lanik.network.api.suspend

import kotlinx.serialization.json.JsonElement
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.lanik.network.api.dto.postDTO.PostListingDto

interface SuspendPostAPI {
    @GET("/r/{source}")
    suspend fun getPosts(
        @Path("source") source: String?,
        @Query("after") page: String = "",
    ): PostListingDto

    @GET("{url}")
    suspend fun getSinglePost(
        @Path("url") source: String?,
    ): List<JsonElement>
}