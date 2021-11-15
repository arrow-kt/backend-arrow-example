package io.arrow.example.external.test

import io.arrow.example.external.Billing
import io.arrow.example.external.BillingResponse
import kotlin.random.Random

class BillingTest(private val failureFrequency: Int) : Billing {
  override suspend fun processBilling(order: Map<String, Int>): BillingResponse =
    when (Random.nextInt(failureFrequency)) {
      0 -> BillingResponse.USER_ERROR
      1 -> BillingResponse.SYSTEM_ERROR
      else -> BillingResponse.OK
    }
}