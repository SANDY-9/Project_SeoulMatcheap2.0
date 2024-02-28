package com.sandy.seoul_matcheap.util.module

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.room.Room
import com.google.android.gms.location.LocationServices
import com.google.gson.GsonBuilder
import com.sandy.seoul_matcheap.data.*
import com.sandy.seoul_matcheap.data.forecast.ForecastDataSource
import com.sandy.seoul_matcheap.data.forecast.ForecastRepository
import com.sandy.seoul_matcheap.data.forecast.ForecastServiceAPI
import com.sandy.seoul_matcheap.data.store.*
import com.sandy.seoul_matcheap.data.store.dao.BookmarkDao
import com.sandy.seoul_matcheap.data.store.dao.MapDao
import com.sandy.seoul_matcheap.data.store.dao.SearchDao
import com.sandy.seoul_matcheap.data.store.dao.StoreDao
import com.sandy.seoul_matcheap.data.store.repository.BookmarkRepository
import com.sandy.seoul_matcheap.data.store.repository.MapRepository
import com.sandy.seoul_matcheap.data.store.repository.SearchRepository
import com.sandy.seoul_matcheap.data.store.repository.StoreRepository
import com.sandy.seoul_matcheap.util.helper.MapUtils
import com.sandy.seoul_matcheap.util.*
import com.sandy.seoul_matcheap.util.constants.*
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
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2021-10-22
 * @desc
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModules {

    @Singleton
    @Provides
    fun provideMatcheapDatabase(@ApplicationContext app: Context) =
        Room.databaseBuilder(app, StoreDatabase::class.java, APP_DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideStoreDao(db: StoreDatabase) = db.storeDao()
    @Singleton
    @Provides
    fun provideSearchDao(db: StoreDatabase) = db.searchDao()
    @Singleton
    @Provides
    fun provideMapDao(db: StoreDatabase) = db.mapDao()
    @Singleton
    @Provides
    fun provideBookmarkDao(db: StoreDatabase) = db.bookmarkDao()

    @Singleton
    @Provides
    fun provideStoreRepository(dao: StoreDao) = StoreRepository(dao)
    @Singleton
    @Provides
    fun provideSearchRepository(dao: SearchDao) = SearchRepository(dao)
    @Singleton
    @Provides
    fun provideMapRepository(dao: MapDao) = MapRepository(dao)
    @Singleton
    @Provides
    fun provideBookmarkRepository(dao: BookmarkDao, dataSource: SeoulOpenAPIDataSource) = BookmarkRepository(dao, dataSource)

    @Singleton
    @Provides
    fun provideLocationManager(@ApplicationContext app: Context) =
        app.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @Singleton
    @Provides
    fun provideFusedLocationClient(@ApplicationContext app: Context) =
        LocationServices.getFusedLocationProviderClient(app)

    @Singleton
    @Provides
    fun provideGeocoder(@ApplicationContext app: Context) = Geocoder(app)

    @Singleton
    @Provides
    fun provideInputManager(@ApplicationContext app: Context) =
        app.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    @Singleton
    @Provides
    fun provideNotificationManager(@ApplicationContext app: Context) =
        app.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager

    @Singleton
    @Provides
    @Named(APP_PREFS_SETTINGS)
    fun provideSettingsSharedPreferences(@ApplicationContext app: Context) =
        app.getSharedPreferences(APP_PREFS_SETTINGS, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideSeoulOpenAPIService() : SeoulOpenAPI {
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
            .connectTimeout(TIMEOUT_DURATION /2, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_DURATION /2, TimeUnit.SECONDS)
            .callTimeout(TIMEOUT_DURATION /2, TimeUnit.SECONDS)
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
    fun provideSeoulOpenAPIDataSource(api: SeoulOpenAPI) = SeoulOpenAPIDataSource(api)

    @Singleton
    @Provides
    fun provideInflater(@ApplicationContext app: Context) = app.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    @Singleton
    @Provides
    fun provideMapUtils(@ApplicationContext app: Context) = MapUtils(app)

    @Singleton
    @Provides
    fun provideConnectivityManager(@ApplicationContext app: Context) = app.getSystemService(ConnectivityManager::class.java)

}