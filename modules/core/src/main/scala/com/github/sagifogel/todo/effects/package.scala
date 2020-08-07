package com.github.sagifogel.todo

import cats.MonadError
import cats.effect.Bracket

package object effects {
  type ThrowableBracket[F[_]] = Bracket[F, Throwable]

  object ThrowableBracket {
    def apply[F[_]](implicit ev: Bracket[F, Throwable]): ThrowableBracket[F] = ev
  }

  type ThrowableMonad[F[_]] = MonadError[F, Throwable]

  object ThrowableMonad {
    def apply[F[_]](implicit ev: MonadError[F, Throwable]): ThrowableMonad[F] = ev
  }

}
