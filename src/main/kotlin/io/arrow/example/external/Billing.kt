package io.arrow.example.external

import arrow.fx.coroutines.CircuitBreaker
import arrow.fx.coroutines.Schedule
import arrow.fx.coroutines.retry
import java.util.concurrent.ScheduledExecutorService
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

enum class BillingResponse {
  OK, USER_ERROR, SYSTEM_ERROR
}

interface Billing {
  suspend fun processBilling(order: Map<String, Int>): BillingResponse
}

fun Billing.withBreaker(circuitBreaker: CircuitBreaker, retries: Int): Billing =
  BillingWithBreaker(this, circuitBreaker, retries)

@OptIn(ExperimentalTime::class)
private class BillingWithBreaker(
  private val underlying: Billing,
  private val circuitBreaker: CircuitBreaker,
  private val retries: Int
) : Billing {
  override suspend fun processBilling(order: Map<String, Int>): BillingResponse =
    Schedule.recurs<BillingResponse>(retries)
      .zipRight(Schedule.doWhile { it == BillingResponse.SYSTEM_ERROR })
      .repeat {
        Schedule.recurs<Throwable>(retries)
          .and(Schedule.exponential(20.milliseconds))
          .retry {
          circuitBreaker.protectOrThrow {
            underlying.processBilling(order)
          }
        }
      }
}
