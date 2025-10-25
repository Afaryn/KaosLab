package com.afaryn.kaoslab.di

import android.content.Context
import com.afaryn.kaoslab.BuildConfig
import com.afaryn.kaoslab.data.local.room.NotificationDao
import com.afaryn.kaoslab.data.local.room.NotificationDatabase
import com.afaryn.kaoslab.data.remote.MidtransApi
import com.afaryn.kaoslab.data.remote.NotificationService
import com.afaryn.kaoslab.data.repository.NotificationRepositoryImpl
import com.afaryn.kaoslab.domain.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
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
import javax.inject.Named
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
    fun provideFcm() = FirebaseMessaging.getInstance()

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
    @Named("FirebaseRetrofit")
    fun provideFirebaseRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/") // ✅ FCM endpoint
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

    @Provides
    @Singleton
    fun provideNotificationService(@Named("FirebaseRetrofit") retrofit: Retrofit) = retrofit.create(NotificationService::class.java)

    @Provides
    @Singleton
    fun provideNotificationDatabase(
        @ApplicationContext context: Context,
    ): NotificationDatabase {
        return NotificationDatabase.getInstance(context)
    }

    @Provides
    fun provideNotificationDao(
        db: NotificationDatabase
    ): NotificationDao {
        return db.notificationDao
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        @ApplicationContext context: Context,
        firebaseAuth: FirebaseAuth,
        notificationService: NotificationService,
        notificationDao: NotificationDao
    ): NotificationRepository {
        return NotificationRepositoryImpl(context, firebaseAuth, notificationService, notificationDao)
    }
}