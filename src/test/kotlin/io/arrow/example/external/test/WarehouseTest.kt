package io.arrow.example.external.test

import io.arrow.example.external.Warehouse

class WarehouseTest(private val maxAmount: Int): Warehouse {
  override suspend fun checkAvailability(productId: String, amount: Int): Boolean =
    amount < maxAmount
}