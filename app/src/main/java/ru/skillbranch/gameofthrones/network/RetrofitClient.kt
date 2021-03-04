package ru.skillbranch.gameofthrones.network

import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.skillbranch.gameofthrones.AppConfig

object RetrofitClient {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(AppConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getJSONApi(): GameOfThronesApi {
        return retrofit.create(GameOfThronesApi::class.java)
    }

}