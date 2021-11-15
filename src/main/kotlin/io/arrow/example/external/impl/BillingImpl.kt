package io.arrow.example.external.impl

import io.arrow.example.external.Billing
import io.arrow.example.external.BillingResponse
import io.ktor.http.*

class BillingImpl(private val serviceUrl: Url): Billing {
  override suspend fun processBilling(order: Map<String, Int>): BillingResponse {
    TODO("Not yet implemented")
  }
}