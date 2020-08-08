package todo.http

import java.util.UUID

import cats.effect.IO
import cats.implicits._
import com.github.sagifogel.todo.config.Config
import com.github.sagifogel.todo.domain.todo.{Todo, TodoNotFoundError}
import com.github.sagifogel.todo.http.TodoRoutes
import com.github.sagifogel.todo.repository.TodoRepository
import fs2.Stream
import io.circe.generic.auto._
import io.circe.literal._
import org.http4s.circe._
import org.http4s.{Method, Request, Status, Uri}
import suite.{HttpTestSuite, IOAssertion}
import todo.Arbitraries._

class RoutesSpec extends HttpTestSuite {
  private val configFile = "test.conf"
  private lazy val config = Config.load[IO](configFile).unsafeRunSync()
  private lazy val urlStart = s"http://${config.server.host}:${config.server.port}"

  test("GET todos [OK]") {
    forAll { (ls: List[Todo]) =>
      IOAssertion {
        val req = Request[IO](method = Method.GET, uri = Uri.unsafeFromString(s"$urlStart/todos"))
        val routes = new TodoRoutes[IO](new InMemoryTodoRepository(ls)).routes
        assertHttpStatus(routes, req)(Status.Ok)
      }
    }
  }

  test("POST todo [OK]") {
    forAll { (todo: Todo, id: UUID) =>
      IOAssertion {
        val opId = id.some
        val payload = json"""
        {
          "description": ${todo.description},
          "completed": ${todo.completed}
        }"""

        val req = Request[IO](method = Method.POST, uri = Uri.unsafeFromString(s"$urlStart/todos")).withEntity(payload)
        val routes = new TodoRoutes[IO](new InMemoryTodoRepository(opId = opId)).routes
        assertHttpPredicate[Todo](routes, req, Status.Created) { res =>
          res.description == todo.description && res.completed == todo.completed && res.id == opId
        }
      }
    }
  }

  test("PUT todo ~> done [OK]") {
    forAll { id: UUID =>
      IOAssertion {
        val opId = id.some
        val req = Request[IO](method = Method.PUT, uri = Uri.unsafeFromString(s"$urlStart/todos/${id.toString}/done"))
        val routes = new TodoRoutes[IO](new InMemoryTodoRepository(opId = opId)).routes
        assertHttpStatus(routes, req)(Status.NoContent)
      }
    }
  }

  test("DELETE todos [OK]") {
    IOAssertion {
      val req = Request[IO](method = Method.DELETE, uri = Uri.unsafeFromString(s"$urlStart/todos/"))
      val routes = new TodoRoutes[IO](new InMemoryTodoRepository()).routes
      assertHttpStatus(routes, req)(Status.NoContent)
    }
  }

  test("DELETE todo [OK]") {
    forAll { id: UUID =>
      IOAssertion {
        val req = Request[IO](method = Method.DELETE, uri = Uri.unsafeFromString(s"$urlStart/todos/${id.toString}"))
        val routes = new TodoRoutes[IO](new InMemoryTodoRepository()).routes
        assertHttpStatus(routes, req)(Status.NoContent)
      }
    }
  }

  protected class InMemoryTodoRepository(ls: List[Todo] = List.empty, opId: Option[UUID] = None) extends TodoRepository[IO] {
    override def list: Stream[IO, Todo] = Stream[IO, Todo](ls: _*)

    override def create(todo: Todo): IO[Todo] = IO.pure(todo.copy(id = opId))

    override def update(id: UUID, todo: Todo): IO[Either[TodoNotFoundError.type, Todo]] = IO.pure(Right(todo.copy(id = opId)))

    override def done(id: UUID): IO[Either[TodoNotFoundError.type, Unit]] = IO.pure(Right(()))

    override def delete(id: UUID): IO[Either[TodoNotFoundError.type, Unit]] = IO.pure(Right(()))

    override def deleteAll: IO[Unit] = IO.unit
  }
}
