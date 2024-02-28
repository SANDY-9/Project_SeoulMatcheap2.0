package com.sandy.matcheap.data.di

import com.google.gson.GsonBuilder
import com.sandy.matcheap.common.FORECAST_API_KEY
import com.sandy.matcheap.common.FORECAST_BASE_URL
import com.sandy.matcheap.common.SEOUL_API_BASE_URL
import com.sandy.matcheap.common.TIMEOUT_DURATION
import com.sandy.matcheap.data.remote.forecast.ForecastServiceAPI
import com.sandy.matcheap.data.remote.menu.SeoulOpenAPI
import com.sandy.matcheap.data.repository.GetForecastRepositoryImpl
import com.sandy.matcheap.data.repository.GetStoreMenuRepositoryImpl
import com.sandy.matcheap.domain.repository.GetForecastRepository
import com.sandy.matcheap.domain.repository.GetStoreMenuRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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


}