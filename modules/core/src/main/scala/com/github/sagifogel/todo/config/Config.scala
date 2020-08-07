package com.github.sagifogel.todo.config

import java.util.concurrent.TimeUnit

import cats.effect.{Async, ContextShift}
import com.github.sagifogel.todo.config.data.{AppConfig, HttpClientConfig, HttpServerConfig, PostgreSQLConfig}
import io.circe.config.parser
import io.circe.{Decoder, DecodingFailure, HCursor}
import io.circe.refined._
import io.circe.Decoder._
import io.circe.generic.semiauto.deriveDecoder

import scala.concurrent.duration.FiniteDuration

object Config {
  implicit final val finiteDurationDecoder: Decoder[FiniteDuration] =
    new Decoder[FiniteDuration] {
      def apply(c: HCursor): Result[FiniteDuration] = for {
        length <- c.downField("length").as[Long].right
        unitString <- c.downField("unit").as[String].right
        unit <- (try {
          Right(TimeUnit.valueOf(unitString))
        } catch {
          case _: IllegalArgumentException => Left(DecodingFailure("FiniteDuration", c.history))
        }).right
      } yield FiniteDuration(length, unit)
    }

  implicit val appConfigDecoder: Decoder[AppConfig] = deriveDecoder[AppConfig]
  implicit val httpClientDecoder: Decoder[HttpClientConfig] = deriveDecoder[HttpClientConfig]
  implicit val postgresDecoder: Decoder[PostgreSQLConfig] = deriveDecoder[PostgreSQLConfig]
  implicit val httpServerDecoder: Decoder[HttpServerConfig] = deriveDecoder[HttpServerConfig]

  def load[F[_] : Async : ContextShift]: F[AppConfig] = parser.decodeF[F, AppConfig]
}
