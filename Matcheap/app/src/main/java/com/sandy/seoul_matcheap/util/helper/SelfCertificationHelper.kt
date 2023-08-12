package com.sandy.seoul_matcheap.util.helper

import android.content.Context
import com.sandy.seoul_matcheap.R
import java.io.IOException
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-16
 * @desc
 */

class SelfCertificationHelper(context: Context) {
    lateinit var goodpriceTmf: TrustManagerFactory
    lateinit var goodpriceSslContext: SSLContext
    lateinit var pstaticTmf: TrustManagerFactory
    lateinit var pstaticSslContext: SSLContext
    init {
        try {

            val cf = CertificateFactory.getInstance("X.509")
            val goodpriceCertification = getCertification(context, R.raw.goodprice_go_kr, cf)
            val pstaticCertification = getCertification(context, R.raw.pstatic_net, cf)

            goodpriceTmf = goodpriceCertification.first
            goodpriceSslContext = goodpriceCertification.second

            pstaticTmf = pstaticCertification.first
            pstaticSslContext = pstaticCertification.second

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCertification(context: Context, resId: Int, cf: CertificateFactory) = run {
        val keyStore = getKeyStore(context, resId, cf)

        val tmf = getTmf(keyStore)
        val sslContext = getSslContext(tmf)

        tmf to sslContext
    }

    private fun getKeyStore(context: Context, resId: Int, cf: CertificateFactory) : KeyStore {
        val caInput = context.resources.openRawResource(resId)
        val ca = cf.generateCertificate(caInput)
        println("ca = ${(ca as X509Certificate).subjectDN}")

        val keyStoreType = KeyStore.getDefaultType()
        return KeyStore.getInstance(keyStoreType).apply {
            load(null, null)
            setCertificateEntry("ca", ca)
            caInput.close()
        }
    }
    private fun getTmf(keyStore: KeyStore) : TrustManagerFactory {
        // Create a TrustManager that trusts the CAs in our KeyStore
        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
        tmf.init(keyStore)
        return tmf
    }
    private fun getSslContext(tmf: TrustManagerFactory) : SSLContext {
        // Create a SSLContext that uses our TrustManager
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, tmf.trustManagers, java.security.SecureRandom())
        return sslContext
    }

    companion object {
        private var _instance: SelfCertificationHelper? = null
        private val instance: SelfCertificationHelper get() = _instance!!
        fun getInstance(context: Context) : SelfCertificationHelper {
            if(_instance == null) _instance = SelfCertificationHelper(context)
            return instance
        }
    }

}