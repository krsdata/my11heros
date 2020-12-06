package com.my11heros.network

import android.content.Context
import com.my11heros.BuildConfig
import com.my11heros.utils.BindingUtils
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class WebServiceClient(val context: Context) {
    private lateinit var interceptor: HttpLoggingInterceptor
    private lateinit var okHttpClient: OkHttpClient
    private var retrofit: Retrofit? = null

    val client: Retrofit
        get() {

            interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            if (BuildConfig.DEBUG) {
                okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .addInterceptor(object : Interceptor {
                        @Throws(IOException::class)
                        override fun intercept(chain: Interceptor.Chain): Response {
                            val original = chain.request()
                            val builder = original.newBuilder()
                            builder.addHeader("Accept", "application/json")
                                .addHeader("Authorization", "Bearer " + MyPreferences.getToken(context))
                                .addHeader("app_version",
                                    MyUtils.getAppVersionName(context).toString()
                                )
                            val request = builder.build()

                            BindingUtils.logD(
                                "ServiceGen",
                                "headrs: " + request.headers.toString()
                            )
                            return chain.proceed(request)
                        }
                    })
                    .connectionSpecs(
                        Arrays.asList(
                            ConnectionSpec.MODERN_TLS,
                            ConnectionSpec.CLEARTEXT
                        )
                    )
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .retryOnConnectionFailure(true)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .cache(null)
                    .build()
            } else {
                okHttpClient = OkHttpClient.Builder()
                    //.addInterceptor(interceptor)
                    .addInterceptor(object : Interceptor {
                        @Throws(IOException::class)
                        override fun intercept(chain: Interceptor.Chain): Response {
                            val original = chain.request()
                            val builder = original.newBuilder()
                            builder.addHeader("Accept", "application/json")
                                .addHeader("Authorization", "Bearer " + MyPreferences.getToken(context))
                                .addHeader("app_version",
                                    MyUtils.getAppVersionName(context).toString()
                                )
                            val request = builder.build()
                            return chain.proceed(request)
                        }
                    })
                    .connectionSpecs(
                        Arrays.asList(
                            ConnectionSpec.MODERN_TLS,
                            ConnectionSpec.CLEARTEXT
                        )
                    )
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .retryOnConnectionFailure(true)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .cache(null)
                    .build()
            }
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BindingUtils.BASE_URL_API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
            }
            return retrofit!!
        }
}