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
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.content.TextContent
import io.ktor.features.AutoHeadResponse
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.content.OutgoingContent
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

// dependencies are declared as interfaces
// where we mark everything as suspend
class ExampleApp(
  private val warehouse: Warehouse,
  private val billing: Billing
) {
  fun configure(app: Application) = app.run {
    install(ContentNegotiation) { gson() }
    install(AutoHeadResponse)

    routing {
      get("/hello") {
        call.respondText("Hello World!")
      }
      get("/process") {
        val result = either<BadRequest, List<Entry>> {
          val order = Either.catch { call.receive<Order>() }
            .mapLeft { badRequest(it.message ?: "Received an invalid order") }
            .bind()

          validateStructure(order).mapLeft { problems ->
            badRequest(problems.joinToString { it.name })
          }.bind()

          order.entries.parTraverseValidated {
            warehouse.validateAvailability(it.id, it.amount)
          }.mapLeft { availability ->
            badRequest("Following productIds weren't available: ${availability.joinToString { it.productId }}")
          }.bind()
        }
        when (result) {
          is Either.Left<BadRequest> ->
            call.respond(result.value)
          is Either.Right<List<Entry>> ->
            when (billing.processBilling(result.value.associate(Entry::asPair))) {
              BillingResponse.OK ->
                call.respondText("ok")
              BillingResponse.USER_ERROR ->
                call.respond(badRequest("not enough items"))
              BillingResponse.SYSTEM_ERROR ->
                call.respondText(status = HttpStatusCode.InternalServerError) { "server error" }
            }
        }
      }
    }
  }
}

typealias BadRequest = TextContent

private fun badRequest(message: String): TextContent =
  TextContent(message, ContentType.Text.Plain, HttpStatusCode.BadRequest)

@ExperimentalTime
suspend fun main() {
  // create the policy for talking to the billing service
  val circuitBreaker = CircuitBreaker.of(
    maxFailures = 2,
    resetTimeout = 2.seconds,
    exponentialBackoffFactor = 2.0, // enable exponentialBackoffFactor
    maxResetTimeout = 60.seconds,  // limit exponential back-off time
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

