package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import java.util.*

/**
 * Type description here....
 *
 * Created by Andrey on 20.03.2021
 */
object UserHolder {

    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User {
        return User.makeUser(fullName, email = email, password = password)
            .also { user ->
                if (!addUserToMap(user)) {
                    throw IllegalArgumentException("A user with this email already exists")
                }
            }
    }

    fun registerUserByPhone(fullName: String, rawPhone: String): User {
        if (getPhoneLogin(rawPhone) == null) {
            throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
        }
        return User.makeUser(fullName, phone = rawPhone)
            .also { user ->
                if (!addUserToMap(user)) {
                    throw IllegalArgumentException("A user with this phone already exists")
                }
            }
    }

    fun loginUser(login: String, password: String): String? {

        println(">>> loginUser ================================")
        println("login=$login password=$password")
        println("<<< login ================================")

        val realLogin = getPhoneLogin(login) ?: login.toLowerCase(Locale.getDefault())
        return map[realLogin.trim()]?.run {
            if (checkPassword(password)) this.userInfo
            else null
        }
    }

    fun requestAccessCode(login: String) {
        val realLogin = getPhoneLogin(login) ?: login.toLowerCase(Locale.getDefault())
        val user = map[realLogin]
        user?.requestAccessCode()
    }

    fun importUsers(list: List<String>): List<User> {
        val users = mutableListOf<User>()

        list.forEach {

            println(">>> importUsers ================================")
            println("str = $it")
            println("<<< login ================================")

            val user = User.makeUserFromString(it)
            addUserToMap(user)
            users.add(user)
        }
        return users.toList()
    }

    private fun addUserToMap(user: User): Boolean {
        return if (map.contains(user.login)) {
            false
        } else {
            map[user.login] = user
            true
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }

    private fun getPhoneLogin(rawPhone: String): String? {
        val purePhone = rawPhone.replace(Regex("[^+0-9]"), "")
        if (purePhone.length == 12 && purePhone[0] == '+') {
            return purePhone
        }
        return null
    }

}