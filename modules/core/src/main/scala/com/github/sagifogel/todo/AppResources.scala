package com.github.sagifogel.todo

import cats.effect._
import cats.implicits._
import com.github.sagifogel.todo.config.data.{AppSettings, DatabaseSettings, HttpClientSettings}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import io.chrisdavenport.log4cats.Logger
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext

final case class AppResources[F[_]](client: Client[F], database: HikariTransactor[F])

object AppResources {
  def make[F[_]: ConcurrentEffect: ContextShift: Logger](cfg: AppSettings): Resource[F, AppResources[F]] = {
    def mkDatabaseResource(config: DatabaseSettings): Resource[F, HikariTransactor[F]] =
      for {
        ec <- ExecutionContexts.fixedThreadPool[F](config.threadPoolSize)
        blocker <- Blocker[F]
        transactor <- HikariTransactor.newHikariTransactor[F](
          config.driver,
          config.url,
          config.user,
          config.password,
          ec,
          blocker
        )
      } yield transactor

    def mkHttpClient(config: HttpClientSettings): Resource[F, Client[F]] =
      BlazeClientBuilder[F](ExecutionContext.global)
        .withConnectTimeout(config.connectTimeout)
        .withRequestTimeout(config.requestTimeout)
        .resource

    (mkHttpClient(cfg.client), mkDatabaseResource(cfg.database)).mapN(AppResources.apply[F])
  }
}
