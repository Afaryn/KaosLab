package com.afaryn.kaoslab.di

import com.afaryn.kaoslab.domain.repository.AuthRepository
import com.afaryn.kaoslab.data.AuthRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthenticationRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository = AuthRepositoryImpl(auth, firestore)
}