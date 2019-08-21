package com.bigcommerce.mytvapp.ui

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.bigcommerce.mytvapp.api.ApiHelper
import com.bigcommerce.mytvapp.persistence.SharedPrefsHelper

class ThermostatViewModel : ViewModel() {

    var currentTemp = MutableLiveData<String>()

    suspend fun refreshData(context: Context) {
        if (SharedPrefsHelper.getToken(context) == null) registerApp(context)

        //get temp
        val thermostats = ApiHelper.getThermostats(context)
        var displayValue = ""
        thermostats.forEach {
            displayValue += "\n${it.name} : ${it.runtime.actualTemperature.toDegrees()}"
        }
        currentTemp.postValue(displayValue)
    }

    private suspend fun registerApp(context: Context) {
        val pinResponse = ApiHelper.getPin() ?: return
        currentTemp.postValue("Please go to ecobee.com/consumerportal and add an app with this code:\n${pinResponse.ecobeePin}")
        val token = ApiHelper.getToken(context, pinResponse)
        currentTemp.postValue(token.refresh_token)
    }
}

fun Int.toDegrees(): String = "${this.toFloat()/10f} °"
fun FragmentActivity.getThermostatViewModel() = ViewModelProviders.of(this).get(ThermostatViewModel::class.java)
