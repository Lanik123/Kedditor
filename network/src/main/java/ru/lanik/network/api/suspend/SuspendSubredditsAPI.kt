package ru.lanik.network.api.suspend

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.lanik.network.api.dto.subredditDTO.SubredditDto
import ru.lanik.network.api.dto.subredditDTO.SubredditListingDto

interface SuspendSubredditsAPI {
    @GET("/subreddits/{source}")
    suspend fun getSubredditListing(
        @Path("source") source: String,
        @Query("after") page: String = "",
    ): SubredditListingDto

    @GET("/r/{source}/about{auth}")
    suspend fun getSubredditInfo(
        @Path("source") source: String?,
        @Path("auth") auth: String = "",
    ): SubredditDto

    @GET("/api/{command}")
    suspend fun getSubredditsByName(
        @Path("command") command: String,
        @Query("query") query: String = "",
        @Query("limit") limit: Int = 5,
    ): SubredditListingDto
}