package ru.skillbranch.gameofthrones.data.remote.res

import android.util.Log
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.data.local.entities.House

data class HouseRes(
    val url: String,
    val name: String,
    val region: String,
    val coatOfArms: String,
    val words: String,
    val titles: List<String> = listOf(),
    val seats: List<String> = listOf(),
    val currentLord: String,
    val heir: String,
    val overlord: String,
    val founded: String,
    val founder: String,
    val diedOut: String,
    val ancestralWeapons: List<String> = listOf(),
    val cadetBranches: List<Any> = listOf(),
    val swornMembers: List<String> = listOf()
) {

    val shortName: String get() = shortName()

    private fun shortName(): String {
        val shName = name.split("of").first().trim()
                .split(" ").last()
        Log.i("HouseRes", "shortName(): $name to : $shName")
        return if (shName.isNullOrBlank()) name else shName
    }

    fun toHouse(): House {
        return House(
            shortName,
            name,
            region,
            coatOfArms,
            words,
            titles,
            seats,
            currentLord,
            heir,
            overlord,
            founded,
            founder,
            diedOut,
            ancestralWeapons
        )
    }
}