package com.erikschouten.user

import com.erikschouten.JwtConfig
import io.ktor.application.call
import io.ktor.auth.UserPasswordCredential
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import java.util.*

fun Route.user() {

    authenticate {
        route("/users") {

            get("/") {
                call.respond(UserRepository.getAll())
            }

            get("/{id}") {
                val user = UserRepository.get(UUID.fromString(call.parameters["id"]!!))
                if (user != null) {
                    call.respond(user)
                } else call.respond(HttpStatusCode.NotFound)
            }

        }
    }

    post("/login") {
        val credentials = call.receive<UserPasswordCredential>()
        val user = UserRepository.get(credentials.name)

        val token = if (user != null && UserRepository.checkPassword(credentials.name, credentials.password)) {
            "Bearer " + JwtConfig.generateToken(user)
        } else null

        if (token != null) call.respondText(token)
        else call.respond(HttpStatusCode.Unauthorized)
    }
}
