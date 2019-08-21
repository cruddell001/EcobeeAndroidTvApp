package com.bigcommerce.mytvapp.api.models

data class ThermostatResponse (
    val page: Page,
    val thermostatList: List<Thermostat>
)

data class Page (
    val page: Int,
    val totalPages: Int,
    val pageSize: Int,
    val total: Int
)

data class Thermostat (
    val identifier: String,
    val name: String,
    val thermostatRev: String,
    val isRegistered: Boolean,
    val modelNumber: String,
    val runtime: Runtime
)

data class Runtime (
    val connected: Boolean,
    val actualTemperature: Int,
    val actualHumidity: Int,
    val desiredTemperature: Int,
    val desiredHeat: Int,
    val desiredCool: Int,
    val desiredHumidity: Int,
    val desiredDehumdity: Int,
    val desiredFanMode: String
)