package com.sandy.seoul_matcheap.util.module

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.sandy.seoul_matcheap.util.constants.TIMEOUT_DURATION
import com.sandy.seoul_matcheap.util.helper.SelfCertificationHelper
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit
import javax.net.ssl.X509TrustManager


/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-16
 * @desc
 */


@GlideModule
class GlideModule: AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val calculator = MemorySizeCalculator.Builder(context)
            .setMemoryCacheScreens(2f)
            .setBitmapPoolScreens(3f)
            .build()
        builder.setMemoryCache(LruResourceCache(calculator.memoryCacheSize.toLong()))
            .setBitmapPool(LruBitmapPool(calculator.bitmapPoolSize.toLong()))
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        // settings to able to OkHttpClient Connect of HTTPS that exist self-certification
        val selfCertificationHelper = SelfCertificationHelper.getInstance(context)
        val builder = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_DURATION * 2, TimeUnit.SECONDS)
            .sslSocketFactory(
                selfCertificationHelper.goodpriceSslContext.socketFactory,
                selfCertificationHelper.goodpriceTmf.trustManagers[0] as X509TrustManager
            )
            .sslSocketFactory(
                selfCertificationHelper.pstaticSslContext.socketFactory,
                selfCertificationHelper.pstaticTmf.trustManagers[0] as X509TrustManager
            )
            .build()

        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(builder))
    }



}