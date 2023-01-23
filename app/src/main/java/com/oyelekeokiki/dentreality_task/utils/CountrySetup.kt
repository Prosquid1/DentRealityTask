package com.oyelekeokiki.dentreality_task.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.oyelekeokiki.dentreality_task.data.Country
import java.io.IOException

class CountrySetup {
    companion object {
        fun Context.fetchCountries(): List<Country> {
            val json = loadGeoJsonFromAsset("countries.json")
            return convertJSONToCountries(json)
        }

        /**
         * Load GeoJSON files from the assets folder.
         */
        private fun Context.loadGeoJsonFromAsset(fileName: String): String? {
            return try {
                val inputStream = assets.open(fileName)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                String(buffer, Charsets.UTF_8)
            } catch (ex: IOException) {
                ex.printStackTrace()
                null
            }
        }

        private fun convertJSONToCountries(data: String?): List<Country> {
            return data?.let {
                val myType = object : TypeToken<List<Country>>() {}.type
                return Gson().fromJson(it, myType)
            } ?: arrayListOf()
        }
    }
}