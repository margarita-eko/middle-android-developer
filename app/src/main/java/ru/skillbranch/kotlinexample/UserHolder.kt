package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting

object UserHolder {

    private val map = mutableMapOf<String,User>()

    fun registerUser(
            fullName: String,
            email: String,
            password: String
    ): User {
        val newUser = User.makeUser(fullName, email = email, password = password)
        if (map.containsKey(newUser.login)) {
            throw IllegalArgumentException("A user with this email already exists")
        } else {
            return newUser.also { user -> map[user.login] = user }
        }
    }

    fun registerUserByPhone(fullName: String, rawPhone: String): User {
        val inputPhone = rawPhone.clearPhoneString()
        if (inputPhone.isValidPhoneNumber()) {
            if (map.containsKey(inputPhone)) {
                throw  IllegalArgumentException("A user with this phone already exists")

            } else {
                return User.makeUser(fullName, phone = rawPhone).also { user -> map[user.login] = user }
            }
        } else {
            throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
        }
    }

    fun loginUser(login: String, password: String) : String?{
        var findUser = map[login.trim()]
        if (findUser == null){
            findUser = map[login.trim().clearPhoneString()]
        }
        return  findUser?.let{
            if (it.checkPassword(password)) it.userInfo
            else null
        }
    }

    fun requestAccessCode(login: String) {
        var findUser = map[login.trim()]
        if (findUser == null){
            findUser = map[login.trim().clearPhoneString()]
        }
        findUser?.let {user ->
            user.getUserPhone()?.let {
                user.setNewAccessCode(it)
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder(){
        map.clear()
    }

    private fun String.clearPhoneString(): String{
        return this.replace("[^+\\d]".toRegex(), "")
    }

    private fun String.isValidPhoneNumber(): Boolean{
        val regex = "^\\+\\d{11}".toRegex()
        return this.matches(regex)
    }

}
