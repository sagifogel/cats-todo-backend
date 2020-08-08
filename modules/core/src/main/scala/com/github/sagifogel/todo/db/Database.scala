package com.github.sagifogel.todo.db

import cats.Applicative
import cats.effect.Sync
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

object Database {
  def initialize[F[_]: Sync: Applicative](transactor: HikariTransactor[F]): F[Unit] =
    transactor.configure { dataSource =>
      Applicative[F].pure {
        val flyWay = Flyway.configure().dataSource(dataSource).load()
        flyWay.migrate()
        ()
      }
    }
}
