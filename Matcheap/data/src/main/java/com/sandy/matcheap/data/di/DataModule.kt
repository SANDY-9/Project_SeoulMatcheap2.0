package com.sandy.matcheap.data.di

import android.content.Context
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.sandy.matcheap.common.APP_DATABASE_NAME
import com.sandy.matcheap.common.FORECAST_API_KEY
import com.sandy.matcheap.common.FORECAST_BASE_URL
import com.sandy.matcheap.common.SEOUL_API_BASE_URL
import com.sandy.matcheap.common.TIMEOUT_DURATION
import com.sandy.matcheap.data.remote.forecast.ForecastServiceAPI
import com.sandy.matcheap.data.remote.menu.SeoulOpenAPI
<<<<<<< Updated upstream
import com.sandy.matcheap.data.repository.GetForecastRepositoryImpl
import com.sandy.matcheap.data.repository.GetStoreMenuRepositoryImpl
import com.sandy.matcheap.domain.repository.GetForecastRepository
import com.sandy.matcheap.domain.repository.GetStoreMenuRepository
=======
import com.sandy.matcheap.data.repository.MatcheapDatabaseRepositoryImpl
import com.sandy.matcheap.data.repository.forecast.GetForecastRepositoryImpl
import com.sandy.matcheap.data.repository.store.GetStoreDetailsRepositoryImpl
import com.sandy.matcheap.data.repository.menu.GetStoreMenuRepositoryImpl
import com.sandy.matcheap.data.repository.store.GetStoreListRepositoryImpl
import com.sandy.matcheap.data.room.StoreDatabase
import com.sandy.matcheap.data.room.dao.BookmarkDao
import com.sandy.matcheap.data.room.dao.CountDao
import com.sandy.matcheap.data.room.dao.MapDao
import com.sandy.matcheap.data.room.dao.MenuDao
import com.sandy.matcheap.data.room.dao.SearchDao
import com.sandy.matcheap.data.room.dao.StoreDao
import com.sandy.matcheap.domain.repository.forecast.GetForecastRepository
import com.sandy.matcheap.domain.repository.store.GetStoreDetailsRepository
import com.sandy.matcheap.domain.repository.store.GetStoreListRepository
import com.sandy.matcheap.domain.repository.menu.GetStoreMenuRepository
import com.sandy.matcheap.domain.repository.MatcheapDatabaseRepository
>>>>>>> Stashed changes
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

    @Singleton
    @Provides
    fun provideGetForecastRepository(api: ForecastServiceAPI): GetForecastRepository {
        return GetForecastRepositoryImpl(api)
    }

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

    @Singleton
    @Provides
    fun provideGetStoreMenuRepository(api: SeoulOpenAPI): GetStoreMenuRepository {
        return GetStoreMenuRepositoryImpl(api)
    }

<<<<<<< Updated upstream
=======
    @Singleton
    @Provides
    fun provideMatcheapDatabase(@ApplicationContext app: Context): StoreDatabase {
       return Room.databaseBuilder(app, StoreDatabase::class.java, APP_DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideStoreDao(db: StoreDatabase): StoreDao {
        return db.storeDao()
    }

    @Singleton
    @Provides
    fun provideCountDao(db: StoreDatabase): CountDao {
        return db.countDao()
    }

    @Singleton
    @Provides
    fun provideBookmarkDao(db: StoreDatabase): BookmarkDao {
        return db.bookmarkDao()
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
    fun provideGetStoreDetailsRepository(dao: StoreDao): GetStoreDetailsRepository {
        return GetStoreDetailsRepositoryImpl(dao)
    }

    @Singleton
    @Provides
    fun provideGetStoreListRepository(storeDao: StoreDao): GetStoreListRepository {
        return GetStoreListRepositoryImpl(storeDao)
    }

    fun provideMatchepDatabaseRepository(storeDao: StoreDao, mapDao: MapDao): MatcheapDatabaseRepository {
        return MatcheapDatabaseRepositoryImpl(storeDao, mapDao)
    }
>>>>>>> Stashed changes

}