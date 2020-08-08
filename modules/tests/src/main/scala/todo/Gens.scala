package todo

import com.github.sagifogel.todo.domain.todo.Todo
import org.scalacheck.{Arbitrary, Gen}

object Gens {
  val genNonEmptyString: Gen[String] =
    Gen
      .chooseNum(21, 40)
      .flatMap { n =>
        Gen.buildableOfN[String, Char](n, Gen.alphaChar)
      }

  val genTodo: Gen[Todo] = {
    for {
      uuid <- Gen.some(Gen.uuid)
      description <- genNonEmptyString
      completed <- Arbitrary.arbBool.arbitrary
    } yield Todo(uuid, description, completed)
  }
}
