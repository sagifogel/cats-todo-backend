package com.github.sagifogel.todo.tests.suite

import cats.effect.IO
import io.circe._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._

import scala.util.control.NoStackTrace

trait HttpTestSuite extends PureTestSuite {

  case object DummyError extends NoStackTrace

  def assertHttp[A: Encoder](routes: HttpRoutes[IO], req: Request[IO])(
      expectedStatus: Status,
      expectedBody: A
  ) =
    routes.run(req).value.flatMap {
      case Some(resp) =>
        resp.asJson.map { json =>
          assert(resp.status === expectedStatus && json.dropNullValues === expectedBody.asJson.dropNullValues)
        }
      case None => fail("route not found")
    }

  def assertHttpPredicate[A: Encoder: Decoder](routes: HttpRoutes[IO], req: Request[IO]#Self, expectedStatus: Status)(expectedBody: A => Boolean) =
    routes.run(req).value.flatMap {
      case Some(resp) =>
        resp.decodeJson[A].map { json =>
          assert(resp.status === expectedStatus && expectedBody(json))
        }
      case None => fail("route not found")
    }

  def assertHttpStatus(routes: HttpRoutes[IO], req: Request[IO])(expectedStatus: Status) =
    routes.run(req).value.map {
      case Some(resp) =>
        assert(resp.status === expectedStatus)
      case None => fail("route not found")
    }

  def assertHttpFailure(routes: HttpRoutes[IO], req: Request[IO]) =
    routes.run(req).value.attempt.map {
      case Left(_)  => assert(true)
      case Right(_) => fail("expected a failure")
    }

}
