package com.github.sagifogel.todo.db

import java.util.UUID

import com.github.sagifogel.todo.domain.todo.Todo
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._

object Sql {
  def get(id: UUID): Query0[Todo] =
    sql"""
      SELECT * FROM TODOS WHERE id = $id
      """.query[Todo]

  val list: Query0[Todo] =
    sql"""
      SELECT * FROM TODOS
      """.query[Todo]

  def create(id: UUID, todo: Todo): Update0 =
    sql"""
      INSERT INTO TODOS (id, description, completed)
      VALUES ($id, ${todo.description}, ${todo.completed})
      """.update

  def update(id: UUID, todo: Todo): Update0 =
    sql"""
      UPDATE TODOS SET description = ${todo.description}, completed = ${todo.completed} WHERE id = $id
      """.update

  def done(id: UUID): Update0 =
    sql"""
      UPDATE TODOS SET completed = ${true} WHERE id = $id
      """.update

  def delete(id: UUID): Update0 =
    sql"""
      DELETE from TODOS WHERE id = $id
      """.update

  def deleteAll: Update0 =
    sql"""
      DELETE from TODOS
      """.update
}
