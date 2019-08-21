package com.bigcommerce.mytvapp.api

import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceGenerator {
    const val BASE_URL = "https://api.ecobee.com"

    var accessToken: String? = null
    private var retrofit: Retrofit
    private var builder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .also { retrofit = it.build() }

    /**
     * Adds an additional function into the Builder class
     * If the accessToken has been set on this ServiceGenerator object, add it as a header
     */
    fun Request.Builder.addAccessToken(accessToken: String?): Request.Builder {
        if (accessToken != null) addHeader("Authorization", "Bearer $accessToken")
        return this
    }

    /**
     * Creates an instance of the service.
     * We add an intercepter here to add in a header before the request is sent off
     * This way, we can add in the bearer token if it's available
     */
    fun createService(): EcobeeApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addAccessToken(accessToken)
                    .build()
                chain.proceed(request)
            }
            .build()
        builder.client(client)
        retrofit = builder.build()

        return retrofit.create(EcobeeApiService::class.java)
    }

}