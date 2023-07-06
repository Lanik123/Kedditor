package ru.lanik.kedditor.di

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.rxjava3.RxDataStore
import androidx.datastore.rxjava3.RxDataStoreBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import ru.lanik.kedditor.di.qualifier.CommentsCompositeDisposableQualifier
import ru.lanik.kedditor.di.qualifier.MainCompositeDisposableQualifier
import ru.lanik.kedditor.di.qualifier.SublistCompositeDisposableQualifier
import ru.lanik.kedditor.model.SettingsModel
import ru.lanik.kedditor.repository.PostRepository
import ru.lanik.kedditor.repository.RxPostRepository
import ru.lanik.kedditor.repository.RxSettingsManager
import ru.lanik.kedditor.repository.RxSubredditsRepository
import ru.lanik.kedditor.repository.SettingsManager
import ru.lanik.kedditor.repository.SubredditsRepository
import ru.lanik.kedditor.ui.screen.comments.CommentsViewModelFactory
import ru.lanik.kedditor.ui.screen.main.MainViewModelFactory
import ru.lanik.kedditor.ui.screen.settings.SettingsViewModelFactory
import ru.lanik.kedditor.ui.screen.sublist.SublistViewModelFactory
import ru.lanik.kedditor.utils.NetworkScheduler
import ru.lanik.kedditor.utils.SchedulerPolicy
import ru.lanik.network.api.reactive.RxPostAPI
import ru.lanik.network.api.reactive.RxSubredditsAPI
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {
    @Provides
    @Singleton
    fun provideSchedulerPolicy(): SchedulerPolicy = NetworkScheduler()

    @Provides
    @Singleton
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @MainCompositeDisposableQualifier
    fun provideMainCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @SublistCompositeDisposableQualifier
    fun provideSublistCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @CommentsCompositeDisposableQualifier
    fun provideCommentsCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context,
        settingsModelSerializer: Serializer<SettingsModel>,
    ): RxDataStore<SettingsModel> = RxDataStoreBuilder(
        context = context,
        fileName = "settings.json",
        serializer = settingsModelSerializer,
    ).build()

    @Provides
    @Singleton
    fun provideSettingsStateFlow(
        settingsManager: SettingsManager.Reactive,
    ): StateFlow<SettingsModel> = settingsManager.getStateFlow()

    @Provides
    @Singleton
    fun provideSettingsModelSerializer(): Serializer<SettingsModel> = object : Serializer<SettingsModel> {
        override val defaultValue: SettingsModel = SettingsModel()
        override suspend fun readFrom(input: InputStream): SettingsModel {
            try {
                return Json.decodeFromString(
                    SettingsModel.serializer(),
                    input.readBytes().decodeToString(),
                )
            } catch (serialization: SerializationException) {
                throw CorruptionException("Unable to read SettingsModel", serialization)
            }
        }
        override suspend fun writeTo(t: SettingsModel, output: OutputStream) {
            output.write(
                Json.encodeToString(SettingsModel.serializer(), t)
                    .encodeToByteArray(),
            )
        }
    }

    @Provides
    @Singleton
    fun provideRxSubredditsRepository(
        rxSubredditsAPI: RxSubredditsAPI,
        schedulerPolicy: SchedulerPolicy,
        settingsStateFlow: StateFlow<SettingsModel>,
    ): SubredditsRepository.Reactive = RxSubredditsRepository(rxSubredditsAPI, schedulerPolicy, settingsStateFlow)

    @Provides
    @Singleton
    fun provideRxPostRepository(
        rxPostAPI: RxPostAPI,
        rxSubredditsAPI: RxSubredditsAPI,
        schedulerPolicy: SchedulerPolicy,
        settingsStateFlow: StateFlow<SettingsModel>,
    ): PostRepository.Reactive = RxPostRepository(rxPostAPI, rxSubredditsAPI, schedulerPolicy, settingsStateFlow)

    @Provides
    @Singleton
    fun provideRxSettingsManager(
        settingsDataStore: RxDataStore<SettingsModel>,
        compositeDisposable: CompositeDisposable,
    ): SettingsManager.Reactive = RxSettingsManager(settingsDataStore, compositeDisposable)

    @Provides
    @Singleton
    fun provideSublistViewModelFactory(
        @SublistCompositeDisposableQualifier compositeDisposable: CompositeDisposable,
        subredditsRepository: SubredditsRepository.Reactive,
    ): SublistViewModelFactory = SublistViewModelFactory(compositeDisposable, subredditsRepository)

    @Provides
    @Singleton
    fun provideMainViewModelFactory(
        @MainCompositeDisposableQualifier compositeDisposable: CompositeDisposable,
        postRepository: PostRepository.Reactive,
        subredditsRepository: SubredditsRepository.Reactive,
    ): MainViewModelFactory = MainViewModelFactory(compositeDisposable, postRepository, subredditsRepository)

    @Provides
    @Singleton
    fun provideSettingsViewModelFactory(
        compositeDisposable: CompositeDisposable,
    ): SettingsViewModelFactory = SettingsViewModelFactory(compositeDisposable)

    @Provides
    @Singleton
    fun provideCommentsViewModelFactory(
        @CommentsCompositeDisposableQualifier compositeDisposable: CompositeDisposable,
        postRepository: PostRepository.Reactive,
    ): CommentsViewModelFactory = CommentsViewModelFactory(postRepository, compositeDisposable)
}