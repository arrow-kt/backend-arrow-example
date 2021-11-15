package io.arrow.example.external.impl

import io.arrow.example.external.Warehouse
import io.ktor.http.*

class WarehouseImpl(private val serviceUrl: Url): Warehouse {
  override suspend fun checkAvailability(productId: String, amount: Int): Boolean {
    TODO("Not yet implemented")
  }
}