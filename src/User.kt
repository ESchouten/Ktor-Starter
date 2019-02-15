package com.erikschouten

import io.ktor.auth.Principal
import org.jetbrains.exposed.dao.UUIDTable
import java.util.*

object Users : UUIDTable() {
  val email = varchar("email", 255)
  val password = varchar("password", 255)
  val authorities = varchar("authorities", 255)
  val locked = bool("boolean")
}

data class User (
    val id: UUID,
    val email: String,
    val authorities: List<Authority>) : Principal

enum class Authority {
    USER
}

