package com.masjid.jemaah.di

import android.content.Context
import android.hardware.SensorManager
import com.masjid.core.env.DevEnvConfig
import com.masjid.core.env.EnvConfig
import com.masjid.core.env.ProdEnvConfig
import com.masjid.core.env.StagingEnvConfig
import com.masjid.jemaah.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideSensorManager(@ApplicationContext context: Context): SensorManager {
        return context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
}
