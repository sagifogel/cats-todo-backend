package com.github.sagifogel.todo

import cats.effect._
import cats.implicits._
import com.github.sagifogel.todo.repository.LiveTodoRepository
import com.github.sagifogel.todo.config.Config
import com.github.sagifogel.todo.db.Database
import com.github.sagifogel.todo.modules.HttpApi
import io.chrisdavenport.log4cats.{Logger, SelfAwareStructuredLogger}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.server.blaze.BlazeServerBuilder

object Main extends IOApp {
  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  override def run(args: List[String]): IO[ExitCode] =
    Config.load[IO].flatMap { cfg =>
      Logger[IO].info(s"Loaded config $cfg") >>
        AppResources.make[IO](cfg).use {
          res =>
            for {
              _ <- Database.initialize(res.database)
              todoRepo <- LiveTodoRepository.make[IO](res.database)
              api <- HttpApi.make[IO](todoRepo)
              _ <- BlazeServerBuilder[IO]
                .bindHttp(cfg.server.port, cfg.server.host)
                .withHttpApp(api.httpApp)
                .serve
                .compile
                .drain
            } yield ExitCode.Success
        }
    }
}
