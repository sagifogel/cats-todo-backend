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
    scalacOptions += "-Ymacro-annotations",
    scalafmtOnCompile := true,
    resolvers += Resolver.sonatypeRepo("snapshots"),
    libraryDependencies ++= Seq(
      compilerPlugin(Libraries.kindProjector cross CrossVersion.full),
      compilerPlugin(Libraries.betterMonadicFor),
      Libraries.cats,
      Libraries.catsEffect,
      Libraries.catsMeowMtl,
      Libraries.catsRetry,
      Libraries.circeCore,
      Libraries.circeGeneric,
      Libraries.circeParser,
      Libraries.circeRefined,
      Libraries.circeConfigAll,
      Libraries.fs2,
      Libraries.http4sDsl,
      Libraries.http4sServer,
      Libraries.http4sClient,
      Libraries.http4sCirce,
      Libraries.log4cats,
      Libraries.logback % Runtime,
      Libraries.newtype,
      Libraries.refinedCore,
      Libraries.refinedCats,
      Libraries.skunkCore,
      Libraries.skunkCirce,
      Libraries.squants
    )
  )

