package io.arrow.example.validation

import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.ValidatedNel
import arrow.core.andThen
import arrow.core.invalidNel
import arrow.core.traverseValidated
import arrow.core.validNel
import arrow.core.zip
import io.arrow.example.Entry
import io.arrow.example.Order

enum class ValidateStructureProblem {
  EMPTY_ORDER,
  EMPTY_ID,
  INCORRECT_ID,
  NON_POSITIVE_AMOUNT
}

fun Order.validateStructure(): ValidatedNel<ValidateStructureProblem, Order> =
either<Nel<ValidateStructureProblem>, Order> {
  ensure(entries.isNotEmpty()) { EMPTY_ORDER }
  entries.traverseValidated(Entry::validateEntry).bind()
  this
}.toValidated()

fun Entry.validateEntry(): ValidatedNel<ValidateStructureProblem, Entry> =
  id.validateEntryId()
    .zip(amount.validateEntryAmount(), ::Entry)

fun String.validateEntryId(): ValidatedNel<ValidateStructureProblem, String> =
  either<Nel<ValidateStructureProblem>, String> {
    ensure(isNotEmpty()) { EMPTY_ID }
    ensure(Regex("^ID-(\\d){4}\$").matches(this)) { INCORRECT_ID }
    this
  }

fun Int.validateEntryAmount(): Validated<NonEmptyList<ValidateStructureProblem>, Int> =
  check(ValidateStructureProblem.NON_POSITIVE_AMOUNT) { it > 0 }

fun <E, A> A.check(problem: E, predicate: (A) -> Boolean): ValidatedNel<E, A> =
  if (predicate(this)) this.validNel() else problem.invalidNel()