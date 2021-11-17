package io.arrow.example.validation

import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.ValidatedNel
import arrow.core.computations.either
import arrow.core.computations.either.eager
import arrow.core.invalidNel
import arrow.core.nel
import arrow.core.traverseValidated
import arrow.core.validNel
import arrow.core.zip
import io.arrow.example.Entry
import io.arrow.example.Order
import io.arrow.example.flatten
import kotlinx.serialization.Serializable

@Serializable
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

fun validateEntryAmount(amount: Int): Validated<NonEmptyList<ValidateStructureProblem>, Int> =
  if (amount > 0) amount.validNel() else ValidateStructureProblem.NON_POSITIVE_AMOUNT.invalidNel()