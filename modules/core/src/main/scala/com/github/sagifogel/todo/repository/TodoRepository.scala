package com.github.sagifogel.todo.repository

import java.util.UUID

import cats.effect.Sync
import cats.syntax.either._
import cats.syntax.option._
import com.github.sagifogel.todo.db.Sql
import com.github.sagifogel.todo.domain.todo.{Todo, TodoNotFoundError}
import com.github.sagifogel.todo.effects.ThrowableBracket
import doobie.hikari._
import doobie.implicits._
import fs2.Stream

import scala.Function.const

trait TodoRepository[F[_]] {
  def list: Stream[F, Todo]

  def create(todo: Todo): F[Todo]

  def update(id: UUID, todo: Todo): F[Either[TodoNotFoundError.type, Todo]]

  def done(id: UUID): F[Either[TodoNotFoundError.type, Unit]]

  def delete(itemId: UUID): F[Either[TodoNotFoundError.type, Unit]]

  def deleteAll: F[Unit]
}

object LiveTodoRepository {
  def make[F[_] : Sync](transactor: HikariTransactor[F]): F[TodoRepository[F]] =
    Sync[F].delay(new LiveTodoRepository[F](transactor))
}

final class LiveTodoRepository[F[_] : ThrowableBracket](transactor: HikariTransactor[F]) extends TodoRepository[F] {
  override def list: Stream[F, Todo] =
    Sql.list.stream.transact(transactor)

  override def create(todo: Todo): F[Todo] = {
    val id = UUID.randomUUID()

    Sql.create(id, todo)
      .run
      .map(_ => todo.copy(id = id.some))
      .transact(transactor)
  }

  override def update(id: UUID, todo: Todo): F[Either[TodoNotFoundError.type, Todo]] =
    Sql.update(id, todo)
      .run
      .map { affectedRows =>
        if (affectedRows == 1) todo.copy(id = todo.id).asRight[TodoNotFoundError.type]
        else TodoNotFoundError.asLeft[Todo]
      }
      .transact(transactor)

  override def done(id: UUID): F[Either[TodoNotFoundError.type, Unit]] =
    Sql.done(id)
      .run
      .map { affectedRows =>
        if (affectedRows == 1) ().asRight[TodoNotFoundError.type]
        else TodoNotFoundError.asLeft[Unit]
      }
      .transact(transactor)

  override def delete(id: UUID): F[Either[TodoNotFoundError.type, Unit]] =
    Sql.delete(id)
      .run
      .map { affectedRows =>
        if (affectedRows == 1) ().asRight[TodoNotFoundError.type]
        else TodoNotFoundError.asLeft[Unit]
      }
      .transact(transactor)

  override def deleteAll: F[Unit] =
    Sql.deleteAll
      .run
      .map(const(()))
      .transact(transactor)
}

