package com.afaryn.kaoslab.di

import com.afaryn.kaoslab.data.DesignerRepository
import com.afaryn.kaoslab.data.DesignerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DesignerModule {

    @Binds
    @Singleton
    abstract fun provideDesignerRepository(
        designerRepositoryImpl: DesignerRepositoryImpl
    ): DesignerRepository
}
