import scala.io.Source

name         := "eventworld-ticket-service"
organization := "eventworld"
version      := "1.0.0"
scalaVersion := "2.13.2"

resolvers ++= Seq(
  Resolver.typesafeRepo("releases")
)

libraryDependencies ++= Dependencies.allDependencies

// Migrations
enablePlugins(FlywayPlugin)
PostgresMigrations.settings

// SBT Native packager
enablePlugins(JavaAppPackaging)
