package com.github.sagifogel.todo

import cats.effect._
import cats.implicits._
import com.github.sagifogel.todo.config.data.{AppConfig, HttpClientConfig, PostgreSQLConfig}
import io.chrisdavenport.log4cats.Logger
import natchez.Trace.Implicits.noop
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import skunk._

import scala.concurrent.ExecutionContext

final case class AppResources[F[_]](client: Client[F], psql: Resource[F, Session[F]])

object AppResources {

  def make[F[_] : ConcurrentEffect : ContextShift : Logger](cfg: AppConfig): Resource[F, AppResources[F]] = {
    def mkPostgreSqlResource(config: PostgreSQLConfig): SessionPool[F] =
      Session
        .pooled[F](
          host = config.host.value,
          port = config.port.value,
          user = config.user.value,
          database = config.database.value,
          max = config.max.value
        )

    def mkHttpClient(config: HttpClientConfig): Resource[F, Client[F]] =
      BlazeClientBuilder[F](ExecutionContext.global)
        .withConnectTimeout(config.connectTimeout)
        .withRequestTimeout(config.requestTimeout)
        .resource

    (mkHttpClient(cfg.httpClientConfig), mkPostgreSqlResource(cfg.postgreSQL)).mapN(AppResources.apply[F])
  }
}
