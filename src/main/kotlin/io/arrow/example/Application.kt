package io.arrow.example

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*

fun main() {
  embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
    configure()
  }.start(wait = true)
}

internal fun Application.configure() {
  install(ContentNegotiation) { json() }
  install(AutoHeadResponse)

  routing {
    get("/") {
      call.respondText("Hello World!")
    }
  }
}

