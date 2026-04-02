package com.example.schoolmanagementsystem.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.ktor.client.plugins.HttpTimeout
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @OptIn(SupabaseInternal::class)
    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://xnksrnvgvhgzntbsqplv.supabase.co",
            supabaseKey = "sb_publishable_S5p0EZHyfj90QyW3B09wjw_5PgQ-8j8"
        ) {
            install(GoTrue)
            install(Postgrest)
            install(Storage)
            
            httpConfig {
                install(HttpTimeout) {
                    requestTimeoutMillis = 30000L // 30 seconds
                    connectTimeoutMillis = 30000L
                    socketTimeoutMillis = 30000L
                }
            }
        }
    }

    @Provides
    @Singleton
    fun provideSupabaseGoTrue(client: SupabaseClient): GoTrue = client.gotrue

    @Provides
    @Singleton
    fun provideSupabasePostgrest(client: SupabaseClient): Postgrest = client.postgrest

    @Provides
    @Singleton
    fun provideSupabaseStorage(client: SupabaseClient): Storage = client.storage
}
