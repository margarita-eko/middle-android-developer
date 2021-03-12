package ru.skillbranch.gameofthrones.database

import androidx.room.*
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes

@Dao
interface DaoInterface {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHouses(houses: List<House>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCharacters(houses: List<Character>)

    @Query("DELETE From houses_table")
    fun clearTableHouses()

    @Query("DELETE From characters_table")
    fun clearTableCharacters()

    //@Query("SELECT character.* FROM CharacterItem as character INNER JOIN houses_table as houses on character.house = houses.id  where houses.name = :houseName")
    @Query("SELECT * FROM CharacterItem  where house = :houseName")
    fun findCharactersByHouseName(houseName: String): List<CharacterItem>

    @Query("SELECT character.* FROM CharacterFull as character where character.id = :id")
    fun findCharacterFullById(id: String): List<CharacterFull>

    @Query("SELECT COUNT(1) FROM houses_table")
    fun housesCount(): Int

    @Query("SELECT COUNT(1) FROM characters_table")
    fun charactersCount(): Int

    @Transaction
    fun isDbEmpty(): Boolean{
        return housesCount() == 0 || charactersCount() == 0
    }

    @Transaction
    fun dropDB(){
        clearTableCharacters()
        clearTableHouses()
    }
}