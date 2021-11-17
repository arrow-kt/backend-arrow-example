package io.arrow.example.external

import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.validNel
import io.arrow.example.Entry

interface Warehouse {
  suspend fun checkAvailability(productId: String, amount: Int): Boolean
}

data class AvailabilityProblem(val productId: String)

suspend fun Warehouse.validateAvailability(productId: String, amount: Int)
  : ValidatedNel<AvailabilityProblem, Entry> =
  if (checkAvailability(productId, amount))
    Entry(productId, amount).validNel()
  else
    AvailabilityProblem(productId).invalidNel()