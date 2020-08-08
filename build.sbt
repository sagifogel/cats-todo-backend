import Dependencies._

ThisBuild / scalaVersion := "2.13.3"
ThisBuild / version := "0.1"
ThisBuild / organizationName := "com.github.sagifogel"

resolvers += Resolver.sonatypeRepo("snapshots")

lazy val root = (project in file("."))
  .settings(name := "cats-todo-backend")
  .aggregate(core)

lazy val core = (project in file("modules/core"))
  .settings(
    name := "cats-todo-backend-core",
    scalacOptions ++= Seq("-Ymacro-annotations", "-language:postfixOps"),
    scalafmtOnCompile := true,
    resolvers += Resolver.sonatypeRepo("snapshots"),
    libraryDependencies ++= Seq(
      compilerPlugin(Libraries.kindProjector cross CrossVersion.full),
      compilerPlugin(Libraries.betterMonadicFor),
      Libraries.cats,
      Libraries.catsEffect,
      Libraries.catsMeowMtl,
      Libraries.circeCore,
      Libraries.circeGeneric,
      Libraries.circeParser,
      Libraries.circeRefined,
      Libraries.circeConfigAll,
      Libraries.http4sDsl,
      Libraries.http4sServer,
      Libraries.http4sClient,
      Libraries.http4sCirce,
      Libraries.log4cats,
      Libraries.logback % Runtime,
      Libraries.refinedCore,
      Libraries.h2,
      Libraries.flyway,
      Libraries.doobieCore,
      Libraries.doobieRefined,
      Libraries.doobieHikari,
      Libraries.doobieH2,
      Libraries.doobiePostgres
    )
  )

