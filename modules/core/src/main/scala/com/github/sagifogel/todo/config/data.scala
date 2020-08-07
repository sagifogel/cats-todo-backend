package com.github.sagifogel.todo.config

import eu.timepit.refined.types.net.UserPortNumber
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString

import scala.concurrent.duration.FiniteDuration

object data {

  case class AppConfig(httpClientConfig: HttpClientConfig, postgreSQL: PostgreSQLConfig, httpServerConfig: HttpServerConfig)

  case class PostgreSQLConfig(host: NonEmptyString, port: UserPortNumber, user: NonEmptyString, database: NonEmptyString, max: PosInt)

  case class HttpServerConfig(host: NonEmptyString, port: UserPortNumber)

  case class HttpClientConfig(connectTimeout: FiniteDuration, requestTimeout: FiniteDuration)

}
