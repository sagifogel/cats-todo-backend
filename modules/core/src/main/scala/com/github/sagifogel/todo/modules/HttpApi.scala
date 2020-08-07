package com.github.sagifogel.todo.modules

import cats.effect.{Concurrent, Sync, Timer}
import com.github.sagifogel.todo.algebras.TodoRepository
import com.github.sagifogel.todo.http.{TodoRoutes, version}
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.server.Router
import org.http4s.implicits._
import org.http4s.server.middleware.{AutoSlash, CORS, RequestLogger, ResponseLogger, Timeout}

import concurrent.duration._

object HttpApi {
  def make[F[_] : Concurrent : Timer](todoRepo: TodoRepository[F]): F[HttpApi[F]] =
    Sync[F].delay(new HttpApi[F](todoRepo))
}

final class HttpApi[F[_] : Concurrent : Timer] private(todoRepo: TodoRepository[F]) {
  private val todoRoutes = new TodoRoutes[F](todoRepo).routes

  private val middleware: HttpRoutes[F] => HttpRoutes[F] = {
    { http: HttpRoutes[F] =>
      AutoSlash(http)
    } andThen { http: HttpRoutes[F] =>
      CORS(http, CORS.DefaultCORSConfig)
    } andThen { http: HttpRoutes[F] =>
      Timeout(60 seconds)(http)
    }
  }

  private val routes: HttpRoutes[F] = Router(version.v1 -> todoRoutes)

  private val loggers: HttpApp[F] => HttpApp[F] = {
    { http: HttpApp[F] =>
      RequestLogger.httpApp(true, true)(http)
    } andThen { http: HttpApp[F] =>
      ResponseLogger.httpApp(true, true)(http)
    }
  }

  val httpApp: HttpApp[F] = loggers(middleware(routes).orNotFound)
}
