package com.masjid.takmir.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.masjid.core.env.EnvConfig
import com.masjid.core.network.AuthApiClient
import com.masjid.core.network.KtorClientFactory
import com.masjid.core.network.TakmirApiClient
import com.masjid.core.security.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(
        @ApplicationContext context: Context,
        env: EnvConfig,
        tokenManager: TokenManager
    ): HttpClient {
        return KtorClientFactory.create(
            env,
            tokenManager,
            OkHttp.create {
                if (env.name == "dev" || env.name == "staging") {
                    addInterceptor(ChuckerInterceptor(context))
                }
            }
        )
    }

    @Provides
    @Singleton
    fun provideTakmirApiClient(client: HttpClient): TakmirApiClient {
        return TakmirApiClient(client)
    }

    @Provides
    @Singleton
    fun provideAuthApiClient(client: HttpClient): AuthApiClient {
        return AuthApiClient(client)
    }
}
