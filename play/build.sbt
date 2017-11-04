name := """try-gcp"""
organization := "com.example"

version := "0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala).enablePlugins(DockerPlugin, JavaAppPackaging)

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"

/*
 dockerfile in docker := {
        // sbt-native-packager の stage タスクによってアプリがステージングされたディレクトリ
        val stageDir: File = stage.value
        val targetDir = "/opt/docker"
 
        new Dockerfile {
          from("java:8-jdk-alpine")
          copy(stageDir, targetDir)
          //entryPoint(s"$targetDir/bin/${executableScriptName.value}")
        }
      }

*/      
/****
dockerfile in docker := {
  val appDir: File = stage.value
  val targetDir = "/app"

  new Dockerfile {
    from("java:8-jdk-alpine")
    entryPoint(s"$targetDir/bin/${executableScriptName.value}")
    copy(appDir, targetDir, chown = "daemon:daemon")
  }
}
*****/
import com.typesafe.sbt.packager.docker._

//dockerBaseImage := "ibmcom/ibmjava:8-sdk"

//dockerCommands ++= Seq(ExecCmd("RUN", "apk update && apk add bash"))

defaultLinuxInstallLocation in Docker := "/usr/local/app"

dockerCommands:= Seq(
  Cmd("FROM","gcr.io/spatial-framing-163309/ibmjdk-on-debian:latest"),
  Cmd("ADD","usr /usr"),
  //Cmd("RUN" ls -ltr /usr
  //Cmd("RUN ls -ltr /usr/local
  Cmd("RUN", "chown -R daemon:daemon /usr/local/app && chmod +x /usr/local/app/bin/try-gcp"),
  Cmd("USER","daemon"),
  Cmd("WORKDIR","/usr/local/app")
)







/*
windows固有の問題(環境変数に割り当てた文字数が長すぎるとエラー「入力文字数が長すぎる」)に対応
参考
http://qiita.com/qr_taka/items/bf3cbfbb70a7de968be9
*/
lazy val isDev = false

import com.typesafe.sbt.packager.Keys.scriptClasspath

scriptClasspath := {
  val originalClasspath = scriptClasspath.value
  val manifest = new java.util.jar.Manifest()
  manifest.getMainAttributes().putValue("Class-Path", originalClasspath.mkString(" "))
  val classpathJar = (target in Universal).value / "lib" / "classpath.jar"
  IO.jar(Seq.empty, classpathJar, manifest)
  //フォルダ「bin」(Scala IDEのclassファイル生成場所)の内容を丸ごとjar化したものをclasspathの先頭に持ってくる
  if(isDev)
  "bin.jar" +: List(classpathJar.getName)
  else
  Seq(classpathJar.getName)
}
mappings in Universal += (((target in Universal).value / "lib" / "classpath.jar") -> "lib/classpath.jar")

pipelineStages := Seq(digest, gzip)


      
      