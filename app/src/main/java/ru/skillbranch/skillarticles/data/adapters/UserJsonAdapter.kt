package ru.skillbranch.skillarticles.data.adapters

import org.json.JSONObject
import ru.skillbranch.skillarticles.data.local.User

class UserJsonAdapter() : JsonAdapter<User>{
    override fun fromJson(json: String): User? {
        if (json.isBlank()) return null
        val jsonObj = JSONObject(json)
        val userMap = mutableMapOf<String, Any?>()
        User::class.java.declaredFields.forEach {
            if (jsonObj.has(it.name)) {
                userMap.put(it.name, if(jsonObj[it.name] == "null") null else jsonObj[it.name])
            }else{
                throw error("Invalid format for User saved pref")
            }
        }
        return User(
            userMap["id"] as String,
            userMap["name"] as String,
            userMap["avatar"] as String?,
            userMap["rating"] as Int,
            userMap["respect"] as Int,
            userMap["about"] as String?
            )
    }

    override fun toJson(obj: User?): String {
        obj ?: return ""
        val jsonObject = JSONObject()
        jsonObject.put("id", obj.id)
        jsonObject.put("name", obj.name)
        jsonObject.put("avatar", obj.avatar ?: "null")
        jsonObject.put("rating", obj.rating)
        jsonObject.put("respect", obj.respect)
        jsonObject.put("about", obj.about ?: "null")
        return jsonObject.toString()
    }
}