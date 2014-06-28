lazy val baseName = "Prefuse"

lazy val baseNameL = baseName.toLowerCase

// - generate debugging symbols
// - compile to 1.6 compatible class files
// - source adheres to Java 1.6 API
lazy val commonJavaOptions = Seq("-source", "1.6")

lazy val fullDescr = "A toolkit for building interactive information visualization applications"

lazy val commonSettings = Project.defaultSettings ++ Seq(
  version           := "1.0.0-SNAPSHOT",
  organization      := "de.sciss",
  scalaVersion      := "2.11.1",  // not used
  homepage          := Some(url(s"https://github.com/Sciss/$baseName")),
  licenses          := Seq("BSD License" -> url("http://prefuse.org/license-prefuse.txt")),
  crossPaths        := false,   // this is just a Java project
  autoScalaLibrary  := false,   // this is just a Java project
  javacOptions      := commonJavaOptions ++ Seq("-target", "1.6", "-g", "-Xlint:deprecation" /*, "-Xlint:unchecked" */),
  javacOptions in doc := commonJavaOptions,  // cf. sbt issue #355
  // ---- publishing to Maven Central ----
  publishMavenStyle := true,
  publishTo := {
    Some(if (version.value endsWith "-SNAPSHOT")
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    else
      "Sonatype Releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
    )
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  pomExtra := {
  <scm>
    <url>git@github.com:Sciss/{baseName}.git</url>
    <connection>scm:git:git@github.com:Sciss/{baseName}.git</connection>
  </scm>
    <developers>
      <developer>
        <id>jheer</id>
        <name>Jeffrey Heer</name>
        <url>http://homes.cs.washington.edu/~jheer/</url>
      </developer>
      <developer>
        <id>sciss</id>
        <name>Hanns Holger Rutz</name>
        <url>http://www.sciss.de</url>
      </developer>
    </developers>
  }
)

// ---- projects ----

lazy val full = Project(
  id            = baseNameL,
  base          = file("."),
  aggregate     = Seq(core, demos),
  dependencies  = Seq(core, demos),
  settings      = commonSettings ++ Seq(
    name := baseName,
    description := fullDescr,
    publishArtifact in (Compile, packageBin) := false, // there are no binaries
    publishArtifact in (Compile, packageDoc) := false, // there are no javadocs
    publishArtifact in (Compile, packageSrc) := false  // there are no sources
  )
)

lazy val core: Project = Project(
  id        = s"$baseNameL-core",
  base      = file("core"),
  settings  = commonSettings ++ Seq(
    name        := s"$baseName-core",
    description := fullDescr,
    libraryDependencies ++= Seq(
      // "junit" % "junit" % "3.8.1" % "test"
      "com.novocode" % "junit-interface"      % "0.10"  % "test",
      "mysql"        % "mysql-connector-java" % "5.1.6" % "test"
    )
  )
)

lazy val demos = Project(
  id        = s"$baseNameL-demos",
  base      = file("demos"),
  dependencies = Seq(core),
  settings  = commonSettings ++ Seq(
    name        := s"$baseName-demos",
    description := "Demo applications for the Prefuse graph visualization toolkit",
    unmanagedResourceDirectories in Compile += baseDirectory.value / ".." / "sample_data",
    mainClass in (Compile,run) := Some("prefuse.demos.Demos")
  )
)
