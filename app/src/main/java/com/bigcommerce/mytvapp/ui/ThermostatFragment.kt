package com.bigcommerce.mytvapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.app.BrandedSupportFragment
import androidx.lifecycle.Observer
import com.bigcommerce.mytvapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ThermostatFragment : BrandedSupportFragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main
    var currentTempField: TextView? = null
    private lateinit var viewModel: ThermostatViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.thermostat_fragment, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        currentTempField = view?.findViewById(R.id.degreesLabel)

        val activity = activity ?: return
        viewModel = activity.getThermostatViewModel()

        viewModel.currentTemp.observe(this, Observer {
            currentTempField?.text = it
        })

        launch {
            while (true) {
                activity.getThermostatViewModel().refreshData(activity)
                delay(5000)
            }
        }
    }

}
