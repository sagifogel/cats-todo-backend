package com.github.sagifogel.todo.config

import cats.effect.{Async, ContextShift}
import com.github.sagifogel.todo.config.data.AppSettings
import io.circe.config.parser
import io.circe.Decoder._
import io.circe.config.syntax._
import io.circe.generic.auto._

object Config {
  def load[F[_] : Async : ContextShift]: F[AppSettings] = parser.decodeF[F, AppSettings]
}
