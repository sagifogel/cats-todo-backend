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

final case class AppResources[F[_]](database: HikariTransactor[F])

object AppResources {
  def make[F[_]: ConcurrentEffect: ContextShift: Logger](cfg: AppSettings): Resource[F, AppResources[F]] =
    mkDatabaseResource(cfg.database).map(AppResources.apply[F])

  def mkDatabaseResource[F[_]: ConcurrentEffect: ContextShift](config: DatabaseSettings): Resource[F, HikariTransactor[F]] =
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
}
