package com.erikschouten

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JwtConfig {

    private const val issuer = "erikschouten.com"
    private const val validityInMs = 1000 * 60 * 30
    private val algorithm = Algorithm.HMAC512(UUID.randomUUID().toString())

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun generateToken(user: User): String = JWT.create()
        .withSubject(user.id.toString())
        .withIssuer(issuer)
        .withArrayClaim("roles", user.authorities.map { it.toString() }.toTypedArray())
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)

}
