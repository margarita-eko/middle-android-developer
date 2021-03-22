package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import java.lang.StringBuilder
import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.IllegalArgumentException

class User(
        private val firstName: String,
        private val lastName: String?,
        email: String? = null,
        rawPhone: String? = null,
        meta: Map<String, Any>? = null
) {
    val userInfo: String
    private val fullName: String
        get() = listOfNotNull(firstName, lastName)
                .joinToString (" " )
                .capitalize()
    private val initials: String
        get() = listOfNotNull(firstName, lastName)
                .map{it.first().toUpperCase()}
                .joinToString (" ")
    private var phone: String? = null
        set(value) {
            field = value?.replace("[^+\\d]".toRegex(), "")
        }

    private var _login: String? = null
    var login: String
        set(value) {
            _login = value?.toLowerCase()
        }
        get() = _login!!
    private val salt: String by lazy {
        ByteArray(16).also { SecureRandom().nextBytes(it) }.toString()
    }
    private lateinit var passwordHash: String

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    var accessCode: String?  = null

    constructor(
            firstName: String,
            lastName: String?,
            email: String?,
            password: String
    ): this (firstName, lastName, email = email, meta = mapOf("auth" to "password")){
        passwordHash = encrypt(password)
    }

    constructor(
            firstName: String,
            lastName: String?,
            rawPhone: String
    ): this (firstName, lastName, rawPhone = rawPhone, meta = mapOf("auth" to "sms")){
        setNewAccessCode(rawPhone)
        /*val code = generateAccessCode()
        passwordHash = encrypt(code)
        accessCode = code
        sendAccessCodeToUser(rawPhone,code)*/
    }

    init {

        check(firstName.isNotBlank()) { "FirstName must not be blank" }
        check(!email.isNullOrBlank() || !rawPhone.isNullOrBlank()) { "Email or phone must not be null or blank" }

        phone = rawPhone
        login = email ?: phone!!


        userInfo = """
            firstName: $firstName
            lastName: $lastName
            login: $login
            fullName: $fullName
            initials: $initials
            email: $email
            phone: $phone
            meta: $meta
        """.trimIndent()
    }

    fun checkPassword(pass: String) = encrypt(pass) == passwordHash.also {

    }

    fun getUserPhone(): String? = this.phone

    fun setNewAccessCode(phone: String){
        val code = generateAccessCode()
        passwordHash = encrypt(code)
        accessCode = code
        sendAccessCodeToUser(phone,code)
    }


    fun changePassword(oldPass: String, newPass: String) {
        if (checkPassword(oldPass)){
            passwordHash = encrypt(newPass)
            if (!accessCode.isNullOrEmpty()) accessCode = newPass
            println("Password $oldPass has been changed on new password $newPass")
        }else throw IllegalArgumentException("The entered password does not match the current password")
    }

    private fun encrypt(password: String): String = salt.plus(password).md5()

    private fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(toByteArray()) // 16 byte
        val hexString = BigInteger(1,digest).toString(16)
        return hexString.padStart(32,'0')
    }

    private fun generateAccessCode(): String {
        val possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890"
        return StringBuilder().apply {
            repeat(6){
                (possible.indices).random().also {index ->
                    append(possible[index])
                }
            }
        }.toString()
    }

    fun sendAccessCodeToUser(phone: String, code: String) {
        println(".......sending access code: $code on $phone")
    }

    companion object Factory {
        fun makeUser(
                fullName: String,
                email: String? = null,
                password: String? = null,
                phone: String? = null
        ): User {
            val (firstName, lastName) = fullName.fullNameToPair()

            return when {
                !phone.isNullOrBlank() -> User(firstName, lastName, phone)
                !email.isNullOrBlank() && !password.isNullOrBlank() -> {
                    User(firstName, lastName, email, password)
                }
                else -> throw IllegalArgumentException(" Email or phone must not be null or blank")
            }
        }

        private fun String.fullNameToPair(): Pair<String,String?>{
            return this.split(" ")
                    .filter { !isNullOrBlank() }
                    .run {
                        when (size){
                            1 -> first() to null
                            2 -> first() to last()
                            else -> throw IllegalArgumentException(
                                    "Fullname must contain only first name and last name, current split result: ${this@fullNameToPair}"
                            )
                        }
                    }
        }

    }

}