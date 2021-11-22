package io.arrow.example.external

import arrow.fx.coroutines.CircuitBreaker
import arrow.fx.coroutines.Schedule

enum class BillingResponse {
  OK, USER_ERROR, SYSTEM_ERROR
}

interface Billing {
  suspend fun processBilling(order: Map<String, Int>): BillingResponse
}

fun Billing.withBreaker(circuitBreaker: CircuitBreaker, retries: Int): Billing = object : Billing {
  override suspend fun processBilling(order: Map<String, Int>): BillingResponse =
    (Schedule.recurs<BillingResponse>(retries) zipRight Schedule.doWhile { it == BillingResponse.SYSTEM_ERROR }).repeat {
      circuitBreaker.protectOrThrow {
        this@withBreaker.processBilling(order)
      }
    }
}