package com.github.sagifogel.todo.config

import scala.concurrent.duration.FiniteDuration

object data {

  case class AppSettings(client: HttpClientSettings, database: DatabaseSettings, server: HttpServerSettings)

  case class DatabaseSettings(driver: String, url: String, user: String, password: String, threadPoolSize: Int)

  case class HttpServerSettings(host: String, port: Int)

  case class HttpClientSettings(connectTimeout: FiniteDuration, requestTimeout: FiniteDuration)

}
