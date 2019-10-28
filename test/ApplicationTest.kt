package com.erikschouten

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.auth.UserPasswordCredential
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.contentType
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun login() {
        withTestApplication({ module(testing = true) }) {

            var token = ""

            handleRequest(HttpMethod.Post, "/login") {
                addHeader("Content-Type", "application/json")
                setBody(Gson().toJson(UserPasswordCredential("erik@erikschouten.com", "secret123")))
            }.apply {
                token = response.content!!
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(JwtConfig.verifier.verify(token.removePrefix("Bearer ")).subject, "b4e468ba-2ce9-4089-b964-e0cc6709674d")
            }

            handleRequest(HttpMethod.Get, "/users") {
                addHeader(HttpHeaders.Authorization, token)
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(Gson().fromJson(response.content, Array<User>::class.java)[0].email, "erik@erikschouten.com")
            }
        }
    }

    @Test
    fun loginFail() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/login") {
                addHeader("Content-Type", "application/json")
                setBody(Gson().toJson(UserPasswordCredential("erik@erikschouten.com", "secret12")))
            }.apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }
}
