package io.arrow.example.external

interface Warehouse {
  suspend fun checkAvailability(productId: String, amount: Int): Boolean
}