package io.arrow.example.external

enum class BillingResponse {
  OK, USER_ERROR, SYSTEM_ERROR
}

interface Billing {
  suspend fun processBilling(order: Map<String, Int>): BillingResponse
}