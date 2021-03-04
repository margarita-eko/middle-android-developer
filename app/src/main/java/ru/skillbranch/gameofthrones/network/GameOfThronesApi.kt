package ru.skillbranch.gameofthrones.network

import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes

interface GameOfThronesApi {
    @GET("houses")
    suspend fun getHousesList(): Response<List<HouseRes>>

    @GET("houses")
    suspend fun getHouseByName(@Query("name") houseName: String): Response<List<HouseRes>>

    @GET("{url}")
    suspend fun getCharacterByUrl(@Path("url") characterUrl: String): Response<CharacterRes>
}