package io.arrow.example

import arrow.core.Either
import arrow.core.computations.either
import arrow.fx.coroutines.CircuitBreaker
import io.arrow.example.external.Billing
import io.arrow.example.external.BillingResponse
import io.arrow.example.external.Warehouse
import io.arrow.example.external.impl.BillingImpl
import io.arrow.example.external.impl.WarehouseImpl
import io.arrow.example.external.validateAvailability
import io.arrow.example.external.withBreaker
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
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

// dependencies are declared as interfaces
// where we mark everything as suspend
class ExampleApp(
  private val warehouse: Warehouse,
  private val billing: Billing
) {
  fun configure(app: Application) = app.run {
    install(ContentNegotiation) { json() }
    install(AutoHeadResponse)

    routing {
      get("/hello") {
        call.respondText("Hello World!")
      }
      get("/process") {
        val order = call.receive<Order>()
        when (val result = either<Any, List<Entry>> {
          validateStructure(order).bind()
          order.entries.parTraverseValidated {
            warehouse.validateAvailability(it.id, it.amount)
          }.bind()
        }) {
          is Either.Left<Any> ->
            call.respond(status = HttpStatusCode.BadRequest, message = result.value)
          is Either.Right<List<Entry>> -> when (billing.processBilling(mapOf())) {
            BillingResponse.OK ->
              call.respondText("ok")
            BillingResponse.USER_ERROR ->
              call.respondText(status = HttpStatusCode.BadRequest) { "not enough items" }
            BillingResponse.SYSTEM_ERROR ->
              call.respondText(status = HttpStatusCode.InternalServerError) { "server error" }
          }
        }
      }
    }
  }
}

@ExperimentalTime
suspend fun main() {
  // create the policy for talking to the billing service
  val circuitBreaker = CircuitBreaker.of(
    maxFailures = 2,
    resetTimeout = 2.seconds,
    exponentialBackoffFactor = 2.0, // enable exponentialBackoffFactor
    maxResetTimeout = 60.seconds,   // limit exponential back-off time
  )
  val retries = 5
  // inject implementation as parameters
  val app = ExampleApp(
    WarehouseImpl(Url("my.internal.warehouse.service")),
    BillingImpl(Url("my.external.billing.service")).withBreaker(circuitBreaker, retries)
  )
  // start the app
  embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
    app.configure(this)
  }.start(wait = true)
}

