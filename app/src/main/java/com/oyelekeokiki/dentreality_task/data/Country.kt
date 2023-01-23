package com.oyelekeokiki.dentreality_task.data

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName("timezones") var timezones: ArrayList<String> = arrayListOf(),
    @SerializedName("latlng") var latlngArray: ArrayList<Double> = arrayListOf(),
    @SerializedName("name") var name: String? = "",
    @SerializedName("country_code") var countryCode: String? = "",
    @SerializedName("capital") var capital: String? = ""
) {
    val latLng by lazy { LatLng(latlngArray[0], latlngArray[1]) }
}