package com.github.sagifogel.todo

import cats.effect._
import cats.implicits._
import com.github.sagifogel.todo.algebras.LiveTodoRepository
import com.github.sagifogel.todo.config.Config
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
              todoRepo <- LiveTodoRepository.make[IO](res.psql)
              api <- HttpApi.make[IO](todoRepo)
              _ <- BlazeServerBuilder[IO]
                .bindHttp(cfg.httpServerConfig.port.value, cfg.httpServerConfig.host.value)
                .withHttpApp(api.httpApp)
                .serve
                .compile
                .drain
            } yield ExitCode.Success
        }
    }
}
