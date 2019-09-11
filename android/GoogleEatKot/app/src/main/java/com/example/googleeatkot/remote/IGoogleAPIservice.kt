package com.example.googleeatkot.remote

import com.example.googleeatkot.Model.PlaceDetail
import com.example.googleeatkot.Model.myPlaces
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface IGoogleAPIservice {
    @GET
    fun getNearByPlaces(@Url url: String): Call<myPlaces>
    @GET
    fun getDetailPlace(@Url url: String) : Call<PlaceDetail>
}