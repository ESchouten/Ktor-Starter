package com.erikschouten

import org.mindrot.jbcrypt.BCrypt
import java.util.*

object UserRepository {

    fun get(id: UUID) = User(id, "erik@erikschouten.com", listOf(Authority.USER))

    fun get(email: String) = User(UUID.randomUUID(), email, listOf(Authority.USER))

    fun checkPassword(email: String, password: String) = BCrypt.checkpw(password, BCrypt.hashpw(password, BCrypt.gensalt(12)))
}
