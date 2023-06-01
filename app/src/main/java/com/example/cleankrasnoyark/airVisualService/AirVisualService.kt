package com.example.cleankrasnoyark.airVisualService

import retrofit2.http.GET
import retrofit2.http.Query

interface AirVisualService {
    @GET("v2/nearest_city")
    suspend fun getNearestCity(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("key") apiKey: String
    ): AirVisualResponse
}