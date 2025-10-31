package com.afaryn.kaoslab.di

import com.afaryn.kaoslab.domain.repository.OwnerRepository
import com.afaryn.kaoslab.data.repository.OwnerRepositoryImpl
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
object OwnerModule {

    @Provides
    @Singleton
    fun provideOwnerRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        notificationRepository: NotificationRepository
    ): OwnerRepository {
        return OwnerRepositoryImpl(auth, firestore, storage, notificationRepository)
    }
}
