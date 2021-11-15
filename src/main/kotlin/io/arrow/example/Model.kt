package io.arrow.example

import arrow.optics.optics
import kotlinx.serialization.Serializable

@Serializable @optics
data class Order(val entries: List<Entry>)
@Serializable @optics
data class Entry(val id: String, val amount: Int)

fun Order.flatten(): Order = Order(
  entries
    .groupBy(Entry::id)
    .map { (id, entries) -> Entry(id, entries.sumOf(Entry::amount)) }
)