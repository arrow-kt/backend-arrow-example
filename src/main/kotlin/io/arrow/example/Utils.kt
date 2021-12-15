package io.arrow.example

import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.validNel
import arrow.fx.coroutines.parTraverseValidated
import arrow.typeclasses.Semigroup
import kotlinx.coroutines.CoroutineScope

fun <A, E> A.ensure(predicate: (A) -> Boolean, problem: () -> E): ValidatedNel<E, A> =
  if (predicate(this)) this.validNel() else problem().invalidNel()

// TODO move to Arrow Fx Coroutines
suspend fun <E, A, B> Iterable<A>.parTraverseValidated(
  f: suspend CoroutineScope.(A) -> ValidatedNel<E, B>
): ValidatedNel<E, List<B>> =
  this.parTraverseValidated(Semigroup.nonEmptyList(), f)