package io.arrow.example

import kotlinx.serialization.Serializable

@Serializable
data class Order(val entries: List<Entry>)
@Serializable
data class Entry(val id: String, val amount: Int)

public fun Order.flatten(): Order = Order(
  entries
    .groupBy(Entry::id)
    .map { (id, entries) -> Entry(id, entries.sumOf(Entry::amount)) }
)