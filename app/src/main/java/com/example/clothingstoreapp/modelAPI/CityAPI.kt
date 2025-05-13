package com.example.clothingstoreapp.modelAPI

import com.example.address.model.City
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CityAPI {
    companion object{
        const val BASE_URL = "https://provinces.open-api.vn/api/"
    }

    // lấy tất cả thành phố
    @GET("p/")
    fun getAllCities():Call<ArrayList<City>>
    @GET("p/{code}")
    fun  getCityBycode(
        @Path("code") code:Int,
        @Query("depth") depth:Int = 2
    ):Call<City>

    // lấy quận theo mã
    @GET("d/{code}")
    fun getDistriByCode(
        @Path("code") code: Int,
        @Query("depth") depth: Int = 2
    ):Call<City.District>

}