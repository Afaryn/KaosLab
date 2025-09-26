package com.afaryn.kaoslab.di

import com.afaryn.kaoslab.domain.repository.DesignerRepository
import com.afaryn.kaoslab.data.repository.DesignerRepositoryImpl
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
object DesignerModule {

    @Provides
    @Singleton
    fun provideDesignerRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): DesignerRepository {
        return DesignerRepositoryImpl(auth, firestore, storage)
    }
}
