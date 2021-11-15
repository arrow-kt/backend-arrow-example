# Ktor 💜 Arrow

> This example is going to be presented as part of the Kotlin webinar series.

This repo showcases how [Arrow](https://arrow-kt.io) helps with frequent pain points in backend development. Needless to say, the implementation is very simplistic in many other aspects we do not cover in the webinar; in particular we try to keep the number of files as a minimum.

## The example

The repo contains a small HTTP service built with [Ktor](https://ktor.io) responsible for handling _orders_. The behavior comprises the following steps:

1. The order comes in as JSON, and we perform some simple validation (like the amount in the order is positive),
2. We then validate against the _warehouse_ service that enough of each product still exist,
3. Finally, we send a request to the _billing_ microservice, which is responsible for handling the payments.

The order is represented as a [simple set of `data` classes](tree/main/src/main/kotlin/io/arrow/examaple/Model.kt) marked as [`@Serializable`](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md).

### Validation

Steps (1) and (2) involve validation logic, in both cases we take a functional approach to the problem. That is, instead of creating functions which directly check the values, we build a larger validator by combining small building blocks. More concretely, [`Validated`](https://arrow-kt.io/docs/apidocs/arrow-core/arrow.core/-validated/) is used.

Validation against the warehouse is a bit trickier, since it involves several calls to an external service. This example highlights how the tools provided by Arrow Fx, like [`parTraverse`](https://arrow-kt.io/docs/apidocs/arrow-fx-coroutines/arrow.fx.coroutines/par-traverse-validated.html), integrate very well with the `suspend` mechanism and take care of cancellation when required.

### Retries

A very common situation is for the billing microservice in step (3) to be completely external to our infrastructure. One fewer headache for us, except for those moments in which that service becomes unstable or unresponsive. Arrow Fx provides simple ways to [retry](https://arrow-kt.io/docs/apidocs/arrow-fx-coroutines/arrow.fx.coroutines/-schedule/) those calls, and even introduce a [circuit breaker](https://arrow-kt.io/docs/apidocs/arrow-fx-coroutines/arrow.fx.coroutines/-circuit-breaker/) to prevent saturation after some downtime.