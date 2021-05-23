package ru.skillbranch.skillarticles.data.adapters


import ru.skillbranch.skillarticles.data.local.User
import ru.skillbranch.skillarticles.extensions.data.asMap

class UserJsonAdapter() : JsonAdapter<User> {

    override fun fromJson(json: String): User? {
        val props = json
            .replace(Regex("[{}]"), "")
            .split(',')

        var id: String? = null
        var name: String? = null
        var avatar: String? = null
        var rating: Int? = null
        var respect: Int? = null
        var about: String? = null
        props.forEach {
            val (prop, value) = it.split(':')
            val clearProp = prop.replace("\"", "")

            when (clearProp) {
                "id" -> id = value
                "name" -> name = value
                "avatar" -> avatar = value
                "rating" -> rating = value.toIntOrNull()
                "respect" -> respect = value.toIntOrNull()
                "about" -> about = value
            }
        }
        val user = User(
            id = id ?: "",
            name = name ?: "",
            avatar = avatar ?: "",
            rating = rating ?: 0,
            respect = respect ?: 0,
            about = about ?: ""
        )
        return user
    }

    override fun toJson(obj: User?): String {
        if (obj == null) return "{}"
        val map = obj.asMap()
        return """
            {"id":"${map["id"]}",
            "name":"${map["name"]}",
            "avatar":"${map["avatar"]}",
            "rating":${map["rating"]},
            "respect":${map["respect"]},
            "about":"${map["about"]}"}            
        """.trimIndent()
    }
}