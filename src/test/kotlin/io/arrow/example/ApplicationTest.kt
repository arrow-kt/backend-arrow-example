package io.arrow.example

import com.google.gson.Gson
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
  // turn things into JSON
  private val json = Gson()

  @Test
  fun testRoot() {
    withTestApplication({ app.configure(this) }) {
      handleRequest(HttpMethod.Get, "/hello").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        assertEquals("Hello World!", response.content)
      }
    }
  }

  @Test
  fun `empty order gives error`() = testProcess(
    Order(emptyList())
  ) {
    assertEquals(HttpStatusCode.BadRequest, response.status())
    assertEquals("EMPTY_ORDER", response.content)
  }

  @Test
  fun `wrong id gives error`() = testProcess(
    Order(listOf(Entry("NOT-AN-ID", 2)))
  ) {
    assertEquals(HttpStatusCode.BadRequest, response.status())
    assertEquals("INCORRECT_ID", response.content)
  }

  @Test
  fun `reasonable order`() = testProcess(
    Order(listOf(Entry("ID-1234", 2)))
  ) {
    assertEquals(HttpStatusCode.OK, response.status())
  }

  private fun testProcess(o: Order, f: TestApplicationCall.() -> Unit) {
    withTestApplication({ app.configure(this) }) {
      handleRequest(HttpMethod.Get, "/process") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(json.toJson(o))
      }.apply(f)
    }
  }
}