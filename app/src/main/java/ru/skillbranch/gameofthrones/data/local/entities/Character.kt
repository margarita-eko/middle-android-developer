package ru.skillbranch.gameofthrones.data.local.entities

import androidx.annotation.NonNull
import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters_table")
data class Character(
    @PrimaryKey
    @NonNull
    val id: String,
    val name: String,
    val gender: String,
    val culture: String,
    val born: String,
    val died: String,
    val titles: List<String> = listOf(),
    val aliases: List<String> = listOf(),
    val father: String, //rel
    val mother: String, //rel
    val spouse: String,
    val houseId: String//rel
)
@DatabaseView("SELECT character.id, character.houseId AS house, character.name," +
                        "character.titles, character.aliases FROM characters_table as character ")
data class CharacterItem(
    val id: String,
    val house: HouseType, //rel
    val name: String,
    val titles: List<String>,
    val aliases: List<String>
)

@DatabaseView("SELECT character.id, character.name, character.born, character.died," +
                            "character.titles, character.aliases, character.houseId AS house, " +
                            "houses.words, " +
                            "mother.id AS mother_id, mother.name AS mother_name, mother.houseId AS mother_house, " +
                            "father.id AS father_id, father.name AS father_name, father.houseId AS father_house " +
                    "FROM characters_table AS character " +
                    "LEFT JOIN houses_table AS houses on character.houseId = houses.id " +
                    "LEFT JOIN characters_table AS mother on character.mother = mother.id " +
                    "LEFT JOIN characters_table AS father on character.father = father.id ")

data class CharacterFull(
    val id: String,
    val name: String,
    val words: String,
    val born: String,
    val died: String,
    val titles: List<String>,
    val aliases: List<String>,
    val house:HouseType, //rel
    @Embedded(prefix = "father_")
    val father: RelativeCharacter?,
    @Embedded(prefix = "mother_")
    val mother: RelativeCharacter?
)

data class RelativeCharacter(
    val id: String,
    val name: String,
    val house:HouseType //rel
)