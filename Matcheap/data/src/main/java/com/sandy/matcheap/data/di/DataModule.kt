package com.sandy.matcheap.data.di

import android.content.Context
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.sandy.matcheap.common.*
import com.sandy.matcheap.data.remote.forecast.ForecastServiceAPI
import com.sandy.matcheap.data.remote.menu.SeoulOpenAPI
import com.sandy.matcheap.data.remote.notice.AppNoticeDataSource
import com.sandy.matcheap.data.repository.MatcheapDatabaseRepositoryImpl
import com.sandy.matcheap.data.repository.bookmark.BookmarkRepositoryImpl
import com.sandy.matcheap.data.repository.forecast.GetForecastRepositoryImpl
import com.sandy.matcheap.data.repository.map.GetMapPolygonsRepositoryImpl
import com.sandy.matcheap.data.repository.menu.GetStoreMenuRepositoryImpl
import com.sandy.matcheap.data.repository.notice.GetAppNoticeRepositoryImpl
import com.sandy.matcheap.data.repository.search.GetSearchAutocompleteListRepositoryImpl
import com.sandy.matcheap.data.repository.search.GetSearchStoreRepositoryImpl
import com.sandy.matcheap.data.repository.search.SearchHistoryRepositoryImpl
import com.sandy.matcheap.data.repository.store.GetRecommendStoreRepositoryImpl
import com.sandy.matcheap.data.repository.store.GetStoreCountRepositoryImpl
import com.sandy.matcheap.data.repository.store.GetStoreDetailsRepositoryImpl
import com.sandy.matcheap.domain.repository.menu.GetStoreMenuRepository
import com.sandy.matcheap.data.repository.store.GetStoreListRepositoryImpl
import com.sandy.matcheap.data.room.StoreDatabase
import com.sandy.matcheap.data.room.dao.*
import com.sandy.matcheap.domain.repository.forecast.GetForecastRepository
import com.sandy.matcheap.domain.repository.store.GetStoreDetailsRepository
import com.sandy.matcheap.domain.repository.store.GetStoreListRepository
import com.sandy.matcheap.domain.repository.MatcheapDatabaseRepository
import com.sandy.matcheap.domain.repository.bookmark.BookmarkRepository
import com.sandy.matcheap.domain.repository.map.GetMapPolygonsRepository
import com.sandy.matcheap.domain.repository.notice.GetAppNoticeRepository
import com.sandy.matcheap.domain.repository.search.GetSearchAutocompleteListRepository
import com.sandy.matcheap.domain.repository.search.GetSearchStoreRepository
import com.sandy.matcheap.domain.repository.search.SearchHistoryRepository
import com.sandy.matcheap.domain.repository.store.GetRecommendStoreRepository
import com.sandy.matcheap.domain.repository.store.GetStoreCountRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {

    //!-- retrofit
    // Forecast
    @Singleton
    @Provides
    fun provideForecastApi(): ForecastServiceAPI {
        val requestInterceptor  = Interceptor {
            val url = it.request()
                .url()
                .newBuilder()
                .addQueryParameter("serviceKey", FORECAST_API_KEY)
                .build()
            val request = it.request()
                .newBuilder()
                .url(url)
                .build()
            return@Interceptor it.proceed(request)
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .connectTimeout(TIMEOUT_DURATION, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_DURATION, TimeUnit.SECONDS)
            .callTimeout(TIMEOUT_DURATION, TimeUnit.SECONDS)
            .build()
        val gson = GsonBuilder()
            .setLenient()
            .create()
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(FORECAST_BASE_URL)
            .build()
            .create(ForecastServiceAPI::class.java)
    }

    //!-- Menu
    @Singleton
    @Provides
    fun provideSeoulOpenApi(): SeoulOpenAPI {
        val requestInterceptor  = Interceptor {
            val url = it.request()
                .url()
                .newBuilder()
                .build()
            val request = it.request()
                .newBuilder()
                .url(url)
                .build()
            return@Interceptor it.proceed(request)
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .connectTimeout(TIMEOUT_DURATION/2, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_DURATION/2, TimeUnit.SECONDS)
            .callTimeout(TIMEOUT_DURATION/2, TimeUnit.SECONDS)
            .build()
        val gson = GsonBuilder()
            .setLenient()
            .create()
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(SEOUL_API_BASE_URL)
            .build()
            .create(SeoulOpenAPI::class.java)
    }

    // Notice DataSource
    @Singleton
    @Provides
    fun provideAppNoticeDataSource(): AppNoticeDataSource {
        return AppNoticeDataSource()
    }

    //!-- Room
    @Singleton
    @Provides
    fun provideMatcheapDatabase(@ApplicationContext app: Context): StoreDatabase {
       return Room.databaseBuilder(app, StoreDatabase::class.java, APP_DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    //!-- Room - dao
    @Singleton
    @Provides
    fun provideBookmarkDao(db: StoreDatabase): BookmarkDao {
        return db.bookmarkDao()
    }

    @Singleton
    @Provides
    fun provideCountDao(db: StoreDatabase): CountDao {
        return db.countDao()
    }

    @Singleton
    @Provides
    fun provideMapDao(db: StoreDatabase): MapDao {
        return db.mapDao()
    }

    @Singleton
    @Provides
    fun provideMenuDao(db: StoreDatabase): MenuDao {
        return db.menuDao()
    }

    @Singleton
    @Provides
    fun provideSearchDao(db: StoreDatabase): SearchDao {
        return db.searchDao()
    }

    @Singleton
    @Provides
    fun provideStoreDao(db: StoreDatabase): StoreDao {
        return db.storeDao()
    }

    // !-- Repository
    // Bookmark
    @Singleton
    @Provides
    fun provideBookmarkRepository(bookmarkDao: BookmarkDao, menuDao: MenuDao, menuRepository: GetStoreMenuRepository): BookmarkRepository {
        return BookmarkRepositoryImpl(bookmarkDao, menuDao, menuRepository)
    }

    // Forecast
    @Singleton
    @Provides
    fun provideGetForecastRepository(api: ForecastServiceAPI): GetForecastRepository {
        return GetForecastRepositoryImpl(api)
    }

    // Map
    @Singleton
    @Provides
    fun provideGetPolygonsRepository(dao: MapDao): GetMapPolygonsRepository {
        return GetMapPolygonsRepositoryImpl(dao)
    }

    // Menu
    @Singleton
    @Provides
    fun provideGetStoreMenuRepository(api: SeoulOpenAPI): GetStoreMenuRepository {
        return GetStoreMenuRepositoryImpl(api)
    }

    // Notice
    @Singleton
    @Provides
    fun provideAppNoticeRepository(dataSource: AppNoticeDataSource): GetAppNoticeRepository {
        return GetAppNoticeRepositoryImpl(dataSource)
    }

    // Search - 자동완성
    @Singleton
    @Provides
    fun provideGetSearchAutocompleteListRepository(dao: SearchDao): GetSearchAutocompleteListRepository {
        return GetSearchAutocompleteListRepositoryImpl(dao)
    }
    // Search
    @Singleton
    @Provides
    fun provideGetSearchStoreRepository(dao: SearchDao): GetSearchStoreRepository {
        return GetSearchStoreRepositoryImpl(dao)
    }
    // Search - 검색어 히스토리
    @Singleton
    @Provides
    fun provideSearchHistoryRepository(dao: SearchDao): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(dao)
    }

    // Store - 추천 착한가격업소(notification)
    @Singleton
    @Provides
    fun provideGetRecommendStoreRepository(dao: StoreDao): GetRecommendStoreRepository {
        return GetRecommendStoreRepositoryImpl(dao)
    }
    // Store - Store Count
    @Singleton
    @Provides
    fun provideGetStoreCountRepository(dao: CountDao): GetStoreCountRepository {
        return GetStoreCountRepositoryImpl(dao)
    }
    // Store - Store Details
    @Singleton
    @Provides
    fun provideGetStoreDetailsRepository(dao: StoreDao): GetStoreDetailsRepository {
        return GetStoreDetailsRepositoryImpl(dao)
    }
    // Store - Store List
    @Singleton
    @Provides
    fun provideGetStoreListRepository(storeDao: StoreDao): GetStoreListRepository {
        return GetStoreListRepositoryImpl(storeDao)
    }

    // Matcheap Store Database
    @Singleton
    @Provides
    fun provideMatchepDatabaseRepository(storeDao: StoreDao, mapDao: MapDao): MatcheapDatabaseRepository {
        return MatcheapDatabaseRepositoryImpl(storeDao, mapDao)
    }

}