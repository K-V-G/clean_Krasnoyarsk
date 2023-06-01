package com.example.cleankrasnoyark.airVisualService
import com.google.gson.annotations.SerializedName

data class AirVisualResponse(
    @SerializedName("data")
    val data: Data
) {
    data class Data(
        @SerializedName("current")
        val current: Current
    ) {
        data class Current(
            @SerializedName("pollution")
            val pollution: Pollution
        ) {
            data class Pollution(
                @SerializedName("aqius")
                val aqius: Int
            )
        }
    }
}