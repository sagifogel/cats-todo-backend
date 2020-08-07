package com.github.sagifogel.todo.domain

import java.util.UUID

import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype

import scala.util.control.NoStackTrace

object todo {

  @newtype case class TodoStatus(done: Boolean)

  @newtype case class TodoItemId(value: Option[UUID])

  @newtype case class TodoItemDescription(value: NonEmptyString)

  case class Todo(id: TodoItemId, description: TodoItemDescription, status: TodoStatus)

  case object TodoNotFoundError extends NoStackTrace
}
