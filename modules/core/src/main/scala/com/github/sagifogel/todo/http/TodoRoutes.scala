package com.github.sagifogel.todo.http

import cats.Defer
import cats.implicits._
import com.github.sagifogel.todo.domain.todo.{Todo, TodoNotFoundError}
import com.github.sagifogel.todo.effects.ThrowableMonad
import com.github.sagifogel.todo.repository.TodoRepository
import fs2.Stream
import io.circe.syntax._
import io.circe.generic.auto._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.{`Content-Type`, Location}
import org.http4s.server.Router
import org.http4s.{HttpRoutes, MediaType, Response, Uri}
import Function.const

final class TodoRoutes[F[_]: Defer: ThrowableMonad: JsonDecoder](repository: TodoRepository[F]) extends Http4sDsl[F] {
  private[http] val prefixPath = "/todos"

  val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root =>
      Ok(Stream("[") ++ repository.list.map(_.asJson.noSpaces).intersperse(",") ++ Stream("]"), `Content-Type`(MediaType.application.json))

    case req @ POST -> Root =>
      req.asJsonDecode[Todo].flatMap { todo =>
        for {
          createdTodo <- repository.create(todo)
          response <- Created(createdTodo.asJson, Location(Uri.unsafeFromString(s"/todos/${createdTodo.id.get}")))
        } yield response
      }

    case req @ PUT -> Root / UUIDVar(id) =>
      req.asJsonDecode[Todo].flatMap { todo =>
        for {
          result <- repository.update(id, todo)
          res <- todoResult(result)
        } yield res
      }

    case PUT -> Root / UUIDVar(id) / "done" =>
      repository.done(id).flatMap {
        case Right(_)                => NoContent()
        case Left(TodoNotFoundError) => NotFound()
      }

    case DELETE -> Root / UUIDVar(id) =>
      repository.delete(id).flatMap {
        case Right(_)                => NoContent()
        case Left(TodoNotFoundError) => NotFound()
      }

    case DELETE -> Root =>
      repository.deleteAll.flatMap(const(NoContent()))
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )

  private def todoResult(result: Either[TodoNotFoundError.type, Todo]): F[Response[F]] =
    result match {
      case Right(todo)             => Ok(todo.asJson)
      case Left(TodoNotFoundError) => NotFound()
    }
}
