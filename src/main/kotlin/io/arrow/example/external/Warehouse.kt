package io.arrow.example.external

import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.validNel
import io.arrow.example.Entry
import io.arrow.example.ProductId

interface Warehouse {
  suspend fun checkAvailability(productId: ProductId, amount: Int): Boolean
}

data class AvailabilityProblem(val productId: ProductId)

suspend fun Warehouse.validateAvailability(productId: ProductId, amount: Int)
  : ValidatedNel<AvailabilityProblem, Entry> =
  if (checkAvailability(productId, amount))
    Entry(productId, amount).validNel()
  else
    AvailabilityProblem(productId).invalidNel()