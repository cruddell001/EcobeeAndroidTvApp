package com.bigcommerce.mytvapp.api

import com.bigcommerce.mytvapp.api.models.PinResponse
import com.bigcommerce.mytvapp.api.models.ThermostatResponse
import com.bigcommerce.mytvapp.api.models.TokenResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface EcobeeApiService {

    @GET("authorize")
    fun authorize(@QueryMap options: Map<String, String>): Call<PinResponse>

    @POST("token")
    fun getToken(@QueryMap options: Map<String, String>): Call<TokenResponse>

    @GET("/1/thermostat")
    fun getThermostats(@QueryMap options: Map<String, String>): Call<ThermostatResponse>
}