package com.erikschouten.user

import com.erikschouten.Authority
import com.erikschouten.User
import org.mindrot.jbcrypt.BCrypt
import java.util.*

object UserRepository {

    fun get(id: UUID) : User? = User(id, "erik@erikschouten.com", listOf(Authority.USER))

    fun get(email: String) : User? = User(UUID.fromString("b4e468ba-2ce9-4089-b964-e0cc6709674d"), email, listOf(Authority.USER))

    fun getAll() = listOf(User(UUID.fromString("b4e468ba-2ce9-4089-b964-e0cc6709674d"), "erik@erikschouten.com", listOf(Authority.USER)))

    fun checkPassword(email: String, password: String) = BCrypt.checkpw(password, BCrypt.hashpw("secret123", BCrypt.gensalt(12)))
}
