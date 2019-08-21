package com.bigcommerce.mytvapp.api

import android.content.Context
import com.bigcommerce.mytvapp.BuildConfig
import com.bigcommerce.mytvapp.api.models.PinResponse
import com.bigcommerce.mytvapp.api.models.Thermostat
import com.bigcommerce.mytvapp.api.models.TokenResponse
import com.bigcommerce.mytvapp.persistence.SharedPrefsHelper
import kotlinx.coroutines.delay
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ApiHelper {
    var service = ServiceGenerator.createService()
    var accessToken: String? = null

    /**
     * Call Retrofit as a coroutine
     */
    private suspend fun <T> Call<T>.callAsync() : T? = suspendCoroutine { cont->
        println("callAsync(): ${this.request().url()}")
        enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                println("onFailure")
                t.printStackTrace()
                cont.resume(null)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                println("success: ${response.code()} ${response.body()} (${response.message()}) ${response.errorBody()?.string()}")
                cont.resume(response.body())
            }
        })
    }

    /**
     * Requests a pin number from Ecobee
     * User will enter this into their control panel to register the app
     * Requires: api key
     *
     * Query String: ?response_type=ecobeePin&client_id=<api_key>&scope=smartWrite
     */
    suspend fun getPin(): PinResponse? {
        val responseType = "ecobeePin"
        val scope = "smartWrite"
        val apiKey = BuildConfig.ECOBEE_API_KEY
        val queryMap = mapOf(
            Pair("response_type", responseType),
            Pair("client_id", apiKey),
            Pair("scope", scope)
        )
        return service.authorize(queryMap).callAsync()
    }

    /**
     * Requests a token from Ecobee
     */
    suspend fun getToken(context: Context, pinResponse: PinResponse): TokenResponse {
        println("getToken()")
        var tokenResponse: TokenResponse? = null
        val code = pinResponse.code

        while (tokenResponse == null) {
            val query = mapOf(
                Pair("grant_type", "ecobeePin"),
                Pair("code", code),
                Pair("client_id", BuildConfig.ECOBEE_API_KEY)
            )
            val response = service.getToken(query).callAsync()
            tokenResponse = response
            delay(10000)    //wait 10 seconds before trying again
        }
        onTokenRefreshed(context, tokenResponse)
        refreshToken(context)

        return tokenResponse
    }

    suspend fun refreshToken(context: Context): Boolean {
        println("refreshToken()")
        val refreshToken = SharedPrefsHelper.getToken(context) ?: return false
        val query = mapOf(
            Pair("grant_type", "refresh_token"),
            Pair("code", refreshToken),
            Pair("client_id", BuildConfig.ECOBEE_API_KEY)
        )
        val response = service.getToken(query).callAsync() ?: return false
        onTokenRefreshed(context, response)
        return true
    }

    private fun onTokenRefreshed(context: Context, tokenResponse: TokenResponse) {
        println("onTokenRefreshed()")
        accessToken = tokenResponse.access_token
        SharedPrefsHelper.setToken(context, tokenResponse.refresh_token)
        ServiceGenerator.accessToken = accessToken
    }

    suspend fun getThermostats(context: Context): List<Thermostat> {
        println("getThermostats()")
        val selection = JSONObject().apply {
            put("selectionType", "registered")
            put("selectionMatch", "")
            put("includeRuntime", true)
        }
        val jsonBody = JSONObject().apply {
            put("selection", selection)
        }
        val query = mapOf(
            Pair("format", "json"),
            Pair("body", jsonBody.toString())
        )
        var thermostats = service.getThermostats(query).callAsync()?.thermostatList
        if (thermostats == null) {
            refreshToken(context)
            thermostats = service.getThermostats(query).callAsync()?.thermostatList
        }
        return thermostats ?: ArrayList()
    }
}