package com.example.schoolmanagementsystem.backend.data.remote.interceptors

import com.example.schoolmanagementsystem.backend.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val userPreferences: UserPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Block to get the latest token from DataStore
        val token = runBlocking {
            userPreferences.userToken.first()
        }

        val newRequest = if (!token.isNullOrBlank()) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer \$token")
                .build()
        } else {
            request
        }

        return chain.proceed(newRequest)
    }
}
