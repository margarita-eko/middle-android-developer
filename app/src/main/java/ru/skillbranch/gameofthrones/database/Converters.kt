package ru.skillbranch.gameofthrones.database

import androidx.room.TypeConverter

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
    }

}