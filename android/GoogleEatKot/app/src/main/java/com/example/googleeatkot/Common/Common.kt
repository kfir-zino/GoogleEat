package com.example.googleeatkot.Common

import com.example.googleeatkot.Model.Results
import com.example.googleeatkot.remote.IGoogleAPIservice
import com.example.googleeatkot.remote.RetrofitClient

object Common {
    private val GOOGLE_API_URL = "https://maps.googleapis.com/"

    var currentResult: Results? = null
    var placesResults: Array<Results>? = null

    val googleApiService: IGoogleAPIservice
    get() = RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIservice::class.java)
}