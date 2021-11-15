package io.arrow.example.plugins

import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.features.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureRouting() {
    install(AutoHeadResponse)

    routing {
        get("/") {
                call.respondText("Hello World!")
            }
    }
}
