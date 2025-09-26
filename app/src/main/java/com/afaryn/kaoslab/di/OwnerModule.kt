package com.afaryn.kaoslab.di

import com.afaryn.kaoslab.domain.repository.OwnerRepository
import com.afaryn.kaoslab.data.repository.OwnerRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
        firestore: FirebaseFirestore
    ): OwnerRepository {
        return OwnerRepositoryImpl(auth, firestore)
    }
}
