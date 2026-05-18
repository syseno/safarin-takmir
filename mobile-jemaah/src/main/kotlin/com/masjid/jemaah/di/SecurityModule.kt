package com.masjid.jemaah.di

import android.content.Context
import com.masjid.core.security.TokenManager
import com.masjid.jemaah.security.EncryptedTokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideEncryptedTokenManager(@ApplicationContext context: Context): EncryptedTokenManager {
        return EncryptedTokenManager(context)
    }

    @Provides
    @Singleton
    fun provideTokenManager(encryptedTokenManager: EncryptedTokenManager): TokenManager {
        return encryptedTokenManager
    }
}
