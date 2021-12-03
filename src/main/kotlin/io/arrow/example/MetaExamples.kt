@file:Suppress("unused")
package io.arrow.example

import arrow.optics.Every

// WRONG
fun Order.containsSingleProduct() =
  entries.all { entry -> entry.id == entries[0].id }

/* // RIGHT
fun Order.containsSingleProduct() =
  if (entries.isEmpty()) false
  else entries.all { entry -> entry.id == entries[0].id }
*/

fun Order.addOneFreeNoOptics(): Order =
  Order(entries.map { it.copy(amount = it.amount + 1) })

fun Order.addOneFreeOptics(): Order {
  // an optic focuses on a set of elements
  // in this case we focus:
  //   1. on the 'entries' field
  //   2. on each element on the list
  //   3. on the 'amount' field
  // RESULT: we focus on each 'amount' field in each 'Entry'
  val optic = Order.entries compose Every.list() compose Entry.amount
  // now we can perform a single modification step
  return optic.modify(this) { it + 1 }
}
