package com.erikschouten

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserPasswordCredential
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.netty.EngineMain
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import org.slf4j.event.Level
import java.time.Duration
import java.util.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module(testing: Boolean = false) {

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    install(Authentication) {
        jwt {
            verifier(JwtConfig.verifier)
            validate {
                UUID.fromString(it.payload.getClaim("id").asString())?.let(UserRepository::get)
            }
        }
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        post("login") {
            val credentials = call.receive<UserPasswordCredential>()

            val token = if (UserRepository.checkPassword(credentials.name, credentials.password)) {
                JwtConfig.makeToken(UserRepository.get(credentials.name))
            } else null

            if (token != null) call.respondText(token)
            else call.respond(HttpStatusCode.Unauthorized)
        }

        authenticate {
            get("/hi") {
                call.respond(call.authentication.principal!!)
            }
        }

        webSocket("/myws/echo") {
            send(Frame.Text("Hi from server"))
            while (true) {
                val frame = incoming.receive()
                if (frame is Frame.Text) {
                    send(Frame.Text("Client said: " + frame.readText()))
                }
            }
        }
    }
}

