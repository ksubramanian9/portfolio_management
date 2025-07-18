name := "investment-portfolio-manager"
version := "1.0"
scalaVersion := "2.13.12"

lazy val portfolioManagement = project.in(file("src/portfolio-management-service"))
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.5.3",
      "com.typesafe.akka" %% "akka-stream-kafka" % "4.0.2",
      "com.lightbend.akka" %% "akka-persistence" % "2.6.20",
      "com.typesafe.slick" %% "slick" % "3.5.1"
    )
  )
