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
  entries.check(ValidateStructureProblem.EMPTY_ORDER, List<Entry>::isNotEmpty).andThen {
    entries.traverseValidated(Entry::validateEntry)
  }.map(::Order)

fun Entry.validateEntry(): ValidatedNel<ValidateStructureProblem, Entry> =
  id.validateEntryId()
    .zip(amount.validateEntryAmount(), ::Entry)

fun String.validateEntryId(): ValidatedNel<ValidateStructureProblem, String> =
  check(ValidateStructureProblem.EMPTY_ID, String::isNotEmpty).andThen {
    check(ValidateStructureProblem.INCORRECT_ID) { Regex("^ID-(\\d){4}\$").matches(it) }
  }

fun Int.validateEntryAmount(): Validated<NonEmptyList<ValidateStructureProblem>, Int> =
  check(ValidateStructureProblem.NON_POSITIVE_AMOUNT) { it > 0 }

fun <E, A> A.check(problem: E, predicate: (A) -> Boolean): ValidatedNel<E, A> =
  if (predicate(this)) this.validNel() else problem.invalidNel()