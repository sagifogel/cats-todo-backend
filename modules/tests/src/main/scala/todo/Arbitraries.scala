package todo

import com.github.sagifogel.todo.domain.todo.Todo
import org.scalacheck.Arbitrary

object Arbitraries {
  implicit val arbTodo: Arbitrary[Todo] = Arbitrary[Todo](Gens.genTodo)
}
