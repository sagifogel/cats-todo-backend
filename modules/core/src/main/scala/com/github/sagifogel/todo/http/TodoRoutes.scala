package com.github.sagifogel.todo.http

import cats.Defer
import cats.implicits._
import cats.syntax.option._
import com.github.sagifogel.todo.algebras.TodoRepository
import com.github.sagifogel.todo.domain.todo.{Todo, TodoItemId, TodoNotFoundError}
import com.github.sagifogel.todo.effects.ThrowableMonad
import com.github.sagifogel.todo.http.Json._
import com.github.sagifogel.todo.http.RefinedDecoder._
import io.circe.syntax._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.{HttpRoutes, Response}

final class TodoRoutes[F[_] : Defer : ThrowableMonad : JsonDecoder](repository: TodoRepository[F]) extends Http4sDsl[F] {
  private[http] val prefixPath = "/todos"

  val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root => Ok(repository.list)

    case req@POST -> Root =>
      req.decodeR[Todo] { todo =>
        repository.create(todo).flatMap(Ok(_))
      }

    case req@PUT -> Root =>
      req.decodeR[Todo] { todo =>
        for {
          res <- repository.update(todo)
          response <- todoResult(res)
        } yield response
      }

    case DELETE -> Root / UUIDVar(id) =>
      repository.delete(TodoItemId(Some(id))).flatMap(either => todoResult(either))
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )

  private def todoResult(result: Either[TodoNotFoundError.type, Todo]): F[Response[F]] = {
    result match {
      case Right(todo) => Ok(todo.asJson)
      case Left(TodoNotFoundError) => NotFound()
    }
  }
}

