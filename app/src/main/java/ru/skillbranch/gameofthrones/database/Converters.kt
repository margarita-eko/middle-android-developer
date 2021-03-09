package ru.skillbranch.gameofthrones.database

import androidx.room.TypeConverter
import ru.skillbranch.gameofthrones.data.local.entities.HouseType

class Converters {

    companion object {

        @TypeConverter
        @JvmStatic
        fun listOfStringToString(list: List<String>): String {
            var string =""
            list.forEach{
                string = if (string=="") it else "$string, $it"
            }
            return string
        }

        @TypeConverter
        @JvmStatic
        fun stringToListOfString(string: String): List<String> {
            return string.split(",")
        }

        @TypeConverter
        @JvmStatic
        fun HouseTypeToString(houseType: HouseType): String {
            return houseType.title
        }

        @TypeConverter
        @JvmStatic
        fun StringToHouseType(houseString: String): HouseType {
            return HouseType.fromString(houseString)
        }
    }

}