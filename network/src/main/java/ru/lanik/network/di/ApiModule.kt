package ru.lanik.network.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import ru.lanik.network.api.reactive.RxPostAPI
import ru.lanik.network.api.reactive.RxSubredditsAPI
import ru.lanik.network.api.suspend.SuspendPostAPI
import ru.lanik.network.api.suspend.SuspendSubredditsAPI
import ru.lanik.network.constants.ApiBaseConst
import ru.lanik.network.di.qualifier.LoggingInterceptorQualifier
import ru.lanik.network.di.qualifier.RxRetrofitQualifier
import ru.lanik.network.di.qualifier.SuspendRetrofitQualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {
    private val format = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @LoggingInterceptorQualifier
    fun provideLoginInterceptor(): Interceptor =
        HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    @Singleton
    fun provideMediaType(): MediaType = "application/json".toMediaType()

    @Provides
    @Singleton
    fun provideRedditClient(
        @LoggingInterceptorQualifier loggingInterceptor: Interceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(loggingInterceptor)
        .followRedirects(true)
        .build()

    @Provides
    @RxRetrofitQualifier
    fun provideRxRetrofit(okhttpClient: OkHttpClient, type: MediaType): Retrofit = Retrofit.Builder()
        .baseUrl(ApiBaseConst.BASE_URL.notAuth)
        .addConverterFactory(format.asConverterFactory(type))
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .client(okhttpClient)
        .build()

    @Provides
    @Singleton
    fun provideRxApiSubreddits(
        @RxRetrofitQualifier retrofit: Retrofit,
    ): RxSubredditsAPI =
        retrofit.create(RxSubredditsAPI::class.java)

    @Provides
    @Singleton
    fun provideRxApiPost(
        @RxRetrofitQualifier retrofit: Retrofit,
    ): RxPostAPI =
        retrofit.create(RxPostAPI::class.java)

    @Provides
    @SuspendRetrofitQualifier
    fun provideSuspendRetrofit(okhttpClient: OkHttpClient, type: MediaType): Retrofit = Retrofit.Builder()
        .baseUrl(ApiBaseConst.BASE_URL.notAuth)
        .addConverterFactory(format.asConverterFactory(type))
        .client(okhttpClient)
        .build()

    @Provides
    @Singleton
    fun provideSuspendApiSubreddits(
        @SuspendRetrofitQualifier retrofit: Retrofit,
    ): SuspendSubredditsAPI =
        retrofit.create(SuspendSubredditsAPI::class.java)

    @Provides
    @Singleton
    fun provideSuspendApiPost(
        @SuspendRetrofitQualifier retrofit: Retrofit,
    ): SuspendPostAPI =
        retrofit.create(SuspendPostAPI::class.java)
}