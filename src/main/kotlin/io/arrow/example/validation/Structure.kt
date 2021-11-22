package io.arrow.example.validation

import arrow.core.Nel
import arrow.core.ValidatedNel
import arrow.core.computations.either
import arrow.core.computations.either.eager
import arrow.core.nel
import arrow.core.traverseValidated
import arrow.core.zip
import io.arrow.example.Entry
import io.arrow.example.Order
import io.arrow.example.ensure
import io.arrow.example.flatten

enum class ValidateStructureProblem {
  EMPTY_ORDER,
  EMPTY_ID,
  INCORRECT_ID,
  NON_POSITIVE_AMOUNT
}

suspend fun validateStructure(order: Order): ValidatedNel<ValidateStructureProblem, Order> =
  either<Nel<ValidateStructureProblem>, Order> {
    ensure(order.entries.isNotEmpty()) { ValidateStructureProblem.EMPTY_ORDER.nel() }
    order.entries.traverseValidated(::validateEntry).bind()
    order.flatten()
  }.toValidated()

fun validateEntry(entry: Entry): ValidatedNel<ValidateStructureProblem, Entry> =
  validateEntryId(entry.id).zip(validateEntryAmount(entry.amount), ::Entry)

fun validateEntryId(id: String): ValidatedNel<ValidateStructureProblem, String> =
  eager<Nel<ValidateStructureProblem>, String> {
    ensure(id.isNotEmpty()) { ValidateStructureProblem.EMPTY_ID.nel() }
    ensure(Regex("^ID-(\\d){4}\$").matches(id)) { ValidateStructureProblem.INCORRECT_ID.nel() }
    id
  }.toValidated()

fun validateEntryAmount(amount: Int): ValidatedNel<ValidateStructureProblem, Int> =
  amount.ensure({ it > 0 }) { ValidateStructureProblem.NON_POSITIVE_AMOUNT }