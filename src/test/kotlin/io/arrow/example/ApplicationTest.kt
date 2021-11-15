package io.arrow.example

import io.arrow.example.external.test.BillingTest
import io.arrow.example.external.test.WarehouseTest
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.features.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import kotlin.test.*
import io.ktor.server.testing.*

class ApplicationTest {

  // inject fake implementations
  private val app =
    ExampleApp(WarehouseTest(100), BillingTest(200))

  @Test
  fun testRoot() {
    withTestApplication({ app.configure(this) }) {
      handleRequest(HttpMethod.Get, "/").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        assertEquals("Hello World!", response.content)
      }
    }
  }
}