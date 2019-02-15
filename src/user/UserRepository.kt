package com.erikschouten.user

import com.erikschouten.Authority
import com.erikschouten.User
import org.mindrot.jbcrypt.BCrypt
import java.util.*

object UserRepository {

    fun get(id: UUID) : User? = User(id, "erik@erikschouten.com", listOf(Authority.USER))

    fun get(email: String) : User? = User(UUID.randomUUID(), email, listOf(Authority.USER))

    fun getAll() = listOf(User(UUID.randomUUID(), "erik@erikschouten.com", listOf(Authority.USER)))

    fun checkPassword(email: String, password: String) = BCrypt.checkpw(password, BCrypt.hashpw(password, BCrypt.gensalt(12)))
}
