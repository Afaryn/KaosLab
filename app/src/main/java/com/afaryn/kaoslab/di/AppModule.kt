package com.afaryn.kaoslab.di

import android.content.Context
import com.afaryn.kaoslab.BuildConfig
import com.afaryn.kaoslab.data.remote.MidtransApi
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.midtrans.sdk.uikit.external.UiKitApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore() = Firebase.firestore

    @Provides
    @Singleton
    fun provideFirebaseStorage() = Firebase.storage

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.SUPABASE_API_KEY}")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient) = Retrofit.Builder()
        .baseUrl(BuildConfig.MIDTRANS_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideMidtransApi(retrofit: Retrofit) = retrofit.create(MidtransApi::class.java)

    @Provides
    @Singleton
    fun provideUiKitApi(@ApplicationContext context: Context): UiKitApi {
        return UiKitApi.Builder()
            .withContext(context)
            .withMerchantClientKey(BuildConfig.CLIENT_KEY)
            .withMerchantUrl(BuildConfig.MIDTRANS_BASE_URL)
            .enableLog(true)
            .build()
    }
}