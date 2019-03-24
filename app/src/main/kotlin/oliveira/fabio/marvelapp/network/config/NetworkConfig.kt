package oliveira.fabio.marvelapp.network.config

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import oliveira.fabio.marvelapp.util.Constants
import oliveira.fabio.marvelapp.util.extensions.toMD5
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


private const val CONNECT_TIME_OUT: Long = 4
private const val READ_TIME_OUT: Long = 5
private const val WRITE_TIME_OUT: Long = 6
private const val TIME_STAMP_PARAMETER = "ts"
private const val API_KEY_PARAMETER = "apikey"
private const val HASH_PARAMETER = "hash"

fun <T> provideApi(clazz: Class<T>, url: String): T {
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(provideGson()))
        .client(provideOkHttpClient())
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
    return retrofit.create(clazz)
}

private fun provideGson(): Gson {
    val gsonBuilder = GsonBuilder()
    gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    return gsonBuilder.create()
}

private fun provideOkHttpClient(): OkHttpClient {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BASIC
    return OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val original = chain.request()
                val originalHttpUrl = original.url()

                val url = originalHttpUrl.newBuilder()
                    .addQueryParameter(TIME_STAMP_PARAMETER, Constants.API_TIMESTAMP_KEY)
                    .addQueryParameter(API_KEY_PARAMETER, Constants.API_PUBLIC_KEY)
                    .addQueryParameter(
                        HASH_PARAMETER,
                        (Constants.API_TIMESTAMP_KEY + Constants.API_PRIVATE_KEY + Constants.API_PUBLIC_KEY).toMD5()
                    )
                    .build()

                val requestBuilder = original.newBuilder()
                    .url(url)

                val request = requestBuilder.build()
                return chain.proceed(request)
            }
        })
        .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
        .build()
}