package network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class RetrofitClient(serverUrl: String) {


    companion object {
        private val gson = GsonBuilder()
       //     .registerTypeAdapter(ApiKindsResponse::class.java, DiscoveryTypeAdapterKinds())
            //       .setLenient()
            .create()
        val LoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)


        private val unsafeOkHttpClient: OkHttpClient
            get() = try {
                // Create a trust manager that does not validate certificate chains
                val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }
                    }
                )

                // Install the all-trusting trust manager
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())

                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory = sslContext.socketFactory
                val builder = OkHttpClient.Builder()
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier(HostnameVerifier { hostname, session -> true })
                builder.addInterceptor(RequestInterceptor)
                //builder.addInterceptor(LoggingInterceptor)
                builder.build()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        private val safeOkHttpClient = OkHttpClient()
            .newBuilder()
            .addInterceptor(LoggingInterceptor)
            //.addInterceptor(RequestInterceptor)
            .build()



        fun getClient(serverUrl: String): Retrofit =
            Retrofit.Builder()
                .client(safeOkHttpClient)
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        fun getUnsafeClient(serverUrl: String): Retrofit =
            Retrofit.Builder()
                .client(unsafeOkHttpClient)
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()


        }
    }
