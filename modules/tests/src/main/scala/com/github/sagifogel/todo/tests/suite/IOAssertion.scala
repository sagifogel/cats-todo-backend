package com.github.sagifogel.todo.tests.suite

import cats.effect.IO

object IOAssertion {
  def apply[A](ioa: IO[A]): IO[A] = {
    ioa.void.unsafeRunSync()
    ioa
  }
}
