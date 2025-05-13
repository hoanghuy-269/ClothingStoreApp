package com.example.address.model

import com.google.gson.annotations.SerializedName

class CityList {
    @SerializedName("list")
    lateinit var list:ArrayList<City>
}
