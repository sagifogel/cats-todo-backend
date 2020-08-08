package com.github.sagifogel.todo.domain

import java.util.UUID

import scala.util.control.NoStackTrace

object todo {
  case class Todo(id: Option[UUID], description: String, completed: Boolean)

  case object TodoNotFoundError extends NoStackTrace
}
