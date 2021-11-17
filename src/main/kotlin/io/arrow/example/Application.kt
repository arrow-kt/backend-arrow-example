package io.arrow.example

import arrow.core.Validated
import io.arrow.example.external.Billing
import io.arrow.example.external.Warehouse
import io.arrow.example.external.impl.BillingImpl
import io.arrow.example.external.impl.WarehouseImpl
import io.arrow.example.validation.validateStructure
import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*

// dependencies are declared as interfaces
// where we mark everything as suspend
class ExampleApp(val warehouse: Warehouse, val billing: Billing) {
  fun configure(app: Application) = app.run {
    install(ContentNegotiation) { json() }
    install(AutoHeadResponse)

    routing {
      get("/hello") {
        call.respondText("Hello World!")
      }
      get("/process") {
        val order = call.receive<Order>()
        when (val result = validateStructure(order)) {
          is Validated.Invalid ->
            call.respond(status = HttpStatusCode.BadRequest, message = result.value)
          is Validated.Valid ->
            call.respondText("all good")
        }
      }
    }
  }
}

fun main() {
  // inject implementation as parameters
  val app = ExampleApp(
    WarehouseImpl(Url("my.internal.warehouse.service")),
    BillingImpl(Url("my.external.billing.service"))
  )
  // start the app
  embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
    app.configure(this)
  }.start(wait = true)
}

