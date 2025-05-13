package com.example.address.model

import com.google.gson.annotations.SerializedName

class City {

    @SerializedName("code")
    var code: Int

    @SerializedName("name")
    var name: String

    @SerializedName("division_type")
    var divisionType: String

    @SerializedName("codename")
    var codename: String

    @SerializedName("phone_code")
    var phoneCode: Int? = null // chỉ áp dụng với Tỉnh/Thành phố

    @SerializedName("districts")
    var districts: ArrayList<District>? = null

    constructor(
        code: Int,
        name: String,
        divisionType: String,
        codename: String,
        phoneCode: Int?,
        districts: ArrayList<District>?
    ) {
        this.code = code
        this.name = name
        this.divisionType = divisionType
        this.codename = codename
        this.phoneCode = phoneCode
        this.districts = districts
    }

    class District {

        @SerializedName("code")
        var code: Int

        @SerializedName("name")
        var name: String

        @SerializedName("division_type")
        var divisionType: String

        @SerializedName("codename")
        var codename: String

        @SerializedName("wards")
        var wards: ArrayList<Ward>? = null

        constructor(
            code: Int,
            name: String,
            divisionType: String,
            codename: String,
            wards: ArrayList<Ward>?
        ) {
            this.code = code
            this.name = name
            this.divisionType = divisionType
            this.codename = codename
            this.wards = wards
        }
    }

    class Ward {

        @SerializedName("code")
        var code: Int

        @SerializedName("name")
        var name: String

        @SerializedName("division_type")
        var divisionType: String

        @SerializedName("codename")
        var codename: String

        constructor(
            code: Int,
            name: String,
            divisionType: String,
            codename: String
        ) {
            this.code = code
            this.name = name
            this.divisionType = divisionType
            this.codename = codename
        }
    }
}
