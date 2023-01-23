package com.oyelekeokiki.dentreality_task.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import java.util.Collections.max

class Task3Implementation {
    private val routes = mapOf<Int, LatLng>() // TODO: Initialiase routes

    fun main(): List<Int> {
        val perm = routes.keys.toList().permutations()

        perm.forEach {
            println(it.joinToString())
        }

//    var start = routeDistances[0][perm[0][0]]
        val totals = mutableListOf<Float>()

        perm.forEachIndexed { index, ints ->
            var start: Float = 0.0f
            val currentRoute = perm[index]
            currentRoute.forEachIndexed { _, i ->
                start += getDistanceBetween(i, currentRoute[i - 1])
            }
            totals.add(start)
        }

        val shortestPath = totals.indexOf(max(totals))

        // Map to the coordinates
        return perm[shortestPath]
    }

    private fun getDistanceBetween(keyOne: Int, keyTwo: Int): Float {
        val locationOne = routes[keyOne]!!
        val locationTwo = routes[keyTwo]!!


        val results = floatArrayOf(0.0f, 0.0f, 0.0f)
        Location.distanceBetween(
            locationOne.latitude, locationOne.longitude,
        locationTwo.latitude, locationTwo.longitude,
            results
        )
        return results[0]
    }

    val result = main()
    // println("result is $result")

    fun <T> List<T>.permutations(): List<List<T>> =
        if (isEmpty()) listOf(emptyList()) else mutableListOf<List<T>>().also { result ->
            for (i in this.indices) {
                (this - this[i]).permutations().forEach {
                    result.add(it + this[i])
                }
            }
        }

    /**
     *  is mirror function to prevent recomputation
     */

}