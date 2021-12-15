package io.arrow.example

import arrow.optics.optics

@JvmInline
value class ProductId(val value: String)

@optics
data class Entry(val id: ProductId, val amount: Int) {
  companion object // required by @optics

  val asPair: Pair<String, Int>
    get() = Pair(id.value, amount)
}

@optics
data class Order(val entries: List<Entry>) {
  companion object // required by @optics
}

fun Order.flatten(): Order = Order(
  entries
    .groupBy(Entry::id)
    .map { (id, entries) -> Entry(id, entries.sumOf(Entry::amount)) }
)