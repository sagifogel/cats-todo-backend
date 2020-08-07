package com.github.sagifogel.todo.algebras


import cats.effect.{Resource, Sync}
import com.github.sagifogel.todo.domain.todo.{Todo, TodoItemId, TodoNotFoundError}
import com.github.sagifogel.todo.effects.{GenUUID, ThrowableBracket}
import skunk.Session

trait TodoRepository[F[_]] {
  def list: F[List[Todo]]

  def create(todo: Todo): F[Todo]

  def update(todo: Todo): F[Either[TodoNotFoundError.type, Todo]]

  def done(itemId: TodoItemId): F[Either[TodoNotFoundError.type, Todo]]

  def delete(itemId: TodoItemId): F[Either[TodoNotFoundError.type, Todo]]
}

object LiveTodoRepository {
  def make[F[_] : Sync](sessionPool: Resource[F, Session[F]]): F[TodoRepository[F]] =
    Sync[F].delay(new LiveTodoRepository[F](sessionPool))
}

final class LiveTodoRepository[F[_] : ThrowableBracket : GenUUID](sessionPool: Resource[F, Session[F]]) extends TodoRepository[F] {
  override def list: F[List[Todo]] = ???

  override def create(todo: Todo): F[Todo] = ???

  override def update(todo: Todo): F[Either[TodoNotFoundError.type, Todo]] = ???

  override def done(itemId: TodoItemId): F[Either[TodoNotFoundError.type, Todo]] = ???

  override def delete(itemId: TodoItemId): F[Either[TodoNotFoundError.type, Todo]] = ???
}

