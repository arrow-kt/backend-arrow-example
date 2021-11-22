package io.arrow.example

import arrow.optics.optics

@optics
data class Order(val entries: List<Entry>) {
  companion object { }
}
@optics
data class Entry(val id: String, val amount: Int) {
  companion object { }
}

fun Order.flatten(): Order = Order(
  entries
    .groupBy(Entry::id)
    .map { (id, entries) -> Entry(id, entries.sumOf(Entry::amount)) }
)