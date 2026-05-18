package com.masjid.takmir.di

import com.masjid.core.env.DevEnvConfig
import com.masjid.core.env.EnvConfig
import com.masjid.core.env.ProdEnvConfig
import com.masjid.core.env.StagingEnvConfig
import com.masjid.takmir.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideEnvConfig(): EnvConfig {
        return when (BuildConfig.ENV) {
            "staging" -> StagingEnvConfig()
            "prod" -> ProdEnvConfig()
            else -> DevEnvConfig()
        }
    }
}
