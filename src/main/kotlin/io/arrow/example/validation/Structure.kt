package io.arrow.example.validation

import arrow.core.ValidatedNel
import arrow.core.andThen
import arrow.core.invalidNel
import arrow.core.sequenceValidated
import arrow.core.traverseValidated
import arrow.core.validNel
import io.arrow.example.Entry
import io.arrow.example.Order

enum class ValidateStructureProblem {
  EMPTY_ORDER,
  EMPTY_ID,
  INCORRECT_ID,
  NON_POSITIVE_AMOUNT
}

fun Order.validateStructure(): ValidatedNel<ValidateStructureProblem, Order> =
  validateAll(
    entries.check(ValidateStructureProblem.EMPTY_ORDER, List<Entry>::isNotEmpty),
    entries.traverseValidated(Entry::validateStructure)
  )

fun Entry.validateStructure(): ValidatedNel<ValidateStructureProblem, Entry> =
  validateAll(
    id.check(ValidateStructureProblem.EMPTY_ID, String::isNotEmpty).andThen {
      id.check(ValidateStructureProblem.INCORRECT_ID) {
        Regex("^ID-(\\d){4}\$").matches(it)
      }
    },
    amount.check(ValidateStructureProblem.NON_POSITIVE_AMOUNT) { it > 0 }
  )

fun <E, A> A.check(problem: E, predicate: (A) -> Boolean): ValidatedNel<E, A> =
  if (predicate(this)) this.validNel() else problem.invalidNel()

fun <E, A> A.validateAll(vararg validations: ValidatedNel<E, *>): ValidatedNel<E, A> =
  validations.toList().sequenceValidated().map { this }