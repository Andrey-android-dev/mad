package ru.skillbranch.kotlinexample


import androidx.annotation.VisibleForTesting
import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import kotlin.text.StringBuilder

/**
 * Type description here....
 *
 * Created by Andrey on 20.03.2021
 */
class User private constructor(
    private val firstName: String,
    private val lastName: String?,
    email: String? = null,
    rawPhone: String? = null,
    meta: Map<String, Any>? = null
) {

    val userInfo: String
    private val fullName: String
        get() = listOfNotNull(firstName, lastName)
            .joinToString(" ")
            .capitalize(Locale.getDefault())

    private val initials: String
        get() = listOfNotNull(firstName, lastName)
            .map { it.first().toUpperCase() }
            .joinToString(" ")

    var phone: String? = null
        set(value) {
            field = value?.replace("[^+0-9]".toRegex(), "")
        }

    private var _login: String? = null
    var login: String
        set(value) {
            _login = value.toLowerCase(Locale.getDefault())
        }
        get() = _login!!


    private var salt: String? = null

    private lateinit var passwordHash: String

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    var accessCode: String? = null

    // for mail
    constructor(
        firstName: String,
        lastName: String?,
        email: String,
        password: String
    ) : this(firstName, lastName, email = email, meta = mapOf("auth" to "password")){
        println("Secondary mail constructor")
        passwordHash = encrypt(password)
    }

    // for phone
    constructor(
        firstName: String,
        lastName: String?,
        rawPhone: String
    ) : this(firstName, lastName, rawPhone = rawPhone, meta = mapOf("auth" to "sms")){
        println("Secondary phone constructor")
        val code = generateAccessCode()
        passwordHash = encrypt(code)
        accessCode = code
        sendAccessCodeToUser(rawPhone, code)
    }

    // for csv import
    constructor(
        firstName: String,
        lastName: String?,
        email: String?,
        rawPhone: String?,
        salt:String?,
        hash:String
    ) : this(firstName, lastName, email = email, meta = mapOf("auth" to "password", "src" to "svc")){
        passwordHash = hash
        this.salt = salt
    }

    init {
        println("First init block, primary constructor was called")

        check(firstName.isNotBlank()) { "First name must be not blank" }
        check(email.isNullOrBlank() || rawPhone.isNullOrBlank()) { "Email or phone must be not blank" }

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

    fun checkPassword(pass:String) = encrypt(pass) == passwordHash

    fun changePassword(oldPass: String, newPass: String) {
        if(checkPassword(oldPass)) passwordHash = encrypt(newPass)
        else throw IllegalArgumentException("The entered password does not match the current password")
    }

    fun requestAccessCode(){
        val code = generateAccessCode()
        passwordHash = encrypt(code)
        accessCode = code
    }

    private fun encrypt(password: String): String {
        if (salt.isNullOrEmpty()) {
            salt = ByteArray(16).also {
                SecureRandom().nextBytes(it)
            }.toString()
        }
        println("Salt while encrypt: $salt")
        return password.plus(salt).md5().also {
            println("Hash with salt: $it")
        }
    }

    private fun generateAccessCode(): String {
        val possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopkrstuvwxyz0123456789"

        return StringBuilder().apply {
            repeat(6){
                (possible.indices).random().also { index ->
                    append(possible[index])
                }
            }
        }.toString()
    }

    private fun sendAccessCodeToUser(phone: String?, code: String) {
        println("..... sending access code: $code on $phone")
    }

    private fun String.md5() : String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(toByteArray())
        val hexString = BigInteger(1,digest).toString(16)
        return hexString.padStart(32, '0')

    }


    companion object Factory{

        fun makeUser(
            fullName: String,
            email: String? = null,
            password: String? = null,
            phone:String? = null
        ): User {
            val (firstName, lastName) = fullName.fullNameToPair()

            return when {
                !phone.isNullOrBlank() -> User(firstName, lastName, phone)
                !email.isNullOrBlank() && !password.isNullOrBlank() -> User(firstName, lastName, email, password)
                else -> throw IllegalArgumentException("Email or phone must be not null or blank")
            }
        }

        fun makeUserFromString(
            str: String
        ): User {
            val items = str.split(";")

            val fullName = items[0]
            val email = items[1]
            val salt = items[2].split(":")[0]
            val hash = items[2].split(":")[1]
            val phone = items[3]

            val (firstName, lastName) = fullName.fullNameToPair()

            val user = User(firstName, lastName, email, phone, salt, hash)

            println("""
                fullName = $fullName
                email = $email
                salt = $salt
                hash = $hash
                phone = $phone
            """.trimIndent())
            return user
        }

        private fun String.fullNameToPair() : Pair<String, String?> {
            return this.split(" ")
                .filter { it.isNotBlank() }
                .run {
                    when(size){
                        1 -> first() to null
                        2 -> first() to last()
                        else -> throw IllegalArgumentException(
                            "Fullname must contain only first name and last name, current split result ${this@fullNameToPair}"
                        )
                    }
                }
        }
    }

}