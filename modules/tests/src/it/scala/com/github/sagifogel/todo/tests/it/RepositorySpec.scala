package com.github.sagifogel.todo.tests.it

import cats.effect.{IO, Resource}
import com.github.sagifogel.todo.AppResources
import com.github.sagifogel.todo.config.Config
import com.github.sagifogel.todo.db.Database
import com.github.sagifogel.todo.domain.todo.Todo
import com.github.sagifogel.todo.repository.LiveTodoRepository
import com.github.sagifogel.todo.tests.suite.{IOAssertion, ResourceSuite}
import doobie.hikari.HikariTransactor

import Function.const

class RepositorySpec extends ResourceSuite[HikariTransactor[IO]] {
  private val configFile = "test.conf"
  private val config = Config.load[IO](configFile).unsafeRunSync()

  override def resources: Resource[IO, HikariTransactor[IO]] = AppResources.mkDatabaseResource[IO](config.database)

  withResources { transactor =>
    val databaseResource = LiveTodoRepository.make[IO](transactor)
    resources.map(t => Database.initialize(t))

    test("repository create") {
      IOAssertion {
        for {
          todoRepo <- databaseResource
          description = "todo1"
          completed = false
          todo <- todoRepo.create(Todo(None, description, completed))
        } yield assert(todo.id.nonEmpty && todo.description == description && todo.completed == completed)
      }
    }

    test("repository update") {
      IOAssertion {
        for {
          todoRepo <- databaseResource
          description = "todo2"
          completed = true
          todo1 <- todoRepo.create(Todo(None, description, completed))
          either <- todoRepo.update(todo1.id.get, Todo(None, description, completed))
          todo2 <- IO.pure[Todo](either.getOrElse(todo1))
        } yield assert(
          todo2.id == todo1.id &&
            todo1.description == todo2.description &&
            todo1.completed == todo2.completed)
      }
    }

    test("repository done") {
      IOAssertion {
        for {
          todoRepo <- databaseResource
          description = "todo3"
          completed = false
          todo1 <- todoRepo.create(Todo(None, description, completed))
          _ <- todoRepo.done(todo1.id.get)
          todo2 <- todoRepo.get(todo1.id.get)
        } yield assert(todo2.fold(const(false), _.completed))
      }
    }

    test("repository delete") {
      IOAssertion {
        for {
          todoRepo <- databaseResource
          description = "todo4"
          completed = false
          todo1 <- todoRepo.create(Todo(None, description, completed))
          _ <- todoRepo.delete(todo1.id.get)
          todo2 <- todoRepo.get(todo1.id.get)
        } yield assert(todo2.isLeft)
      }
    }
  }
}
