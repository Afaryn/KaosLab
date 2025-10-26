package com.afaryn.kaoslab.di

import com.afaryn.kaoslab.data.remote.MidtransApi
import com.afaryn.kaoslab.domain.repository.UserRepository
import com.afaryn.kaoslab.data.repository.UserRepositoryImpl
import com.afaryn.kaoslab.domain.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        midtransApi: MidtransApi,
        notificationRepository: NotificationRepository
    ): UserRepository {
        return UserRepositoryImpl(auth, firestore, storage, midtransApi, notificationRepository)
    }
}
