package controllers

import com.gu.pandomainauth.PanDomainAuthSettingsRefresher
import config.Config
import models.{APIResponse, TranscribeFilesError}
import play.api.Logger
import play.api.libs.json.{Json, OWrites}
import play.api.libs.ws.WSClient
import play.api.mvc._
import services.S3

import scala.util.Try

case class TranscriptFiles(name: String, media: String, transcript: String)
object TranscriptFiles {
  implicit val jsonWrites: OWrites[TranscriptFiles] = Json.writes[TranscriptFiles]
}

case class UploadResult(fileUrl: String)
object UploadResult {
  implicit val jsonWrites = Json.writes[UploadResult]
}

class App(val wsClient: WSClient,
  val controllerComponents: ControllerComponents,
  val panDomainSettings: PanDomainAuthSettingsRefresher,
  val config: Config,
  val assets: Assets) extends BaseController with PanDomainAuthActions {

  def index(transcriptName: Option[String]) = AuthAction {
    Logger.info(s"I am the ${config.appName}")

    transcriptName.map {name =>
      val audioUrl = S3.getObjectUrl(config.s3Client, config.dataBucket, s"$name.mp3")
      val transcriptUrl = S3.getObjectUrl(config.s3Client, config.dataBucket, s"$name.json")

      Ok(views.html.index(name, audioUrl, transcriptUrl))
    }.getOrElse(Ok("please provide transcriptName query parameter"))

  }


  def transcriptFiles(transcriptName: String): Action[AnyContent] = AuthAction {
    APIResponse {
      val files = try {
        val audioUrl = S3.getObjectUrl(config.s3Client, config.dataBucket, s"$transcriptName.mp3")
        val transcriptUrl = S3.getObjectUrl(config.s3Client, config.dataBucket, s"$transcriptName.json")
        Right(TranscriptFiles(transcriptName, audioUrl, transcriptUrl))
      } catch {
        case e: Throwable =>
          Logger.error("Failed to get transcript files", e)
          Left(TranscribeFilesError)
      }
      files
    }
  }

  def saveTranscript(transcriptName: String): Action[AnyContent] = Action { request =>
    APIResponse {
      val name = s"$transcriptName.txt"
      val transcript = request.body.asJson
      println(transcript)
      val s3Result = try {
        val downloadUrl = transcript.map(t => {
          S3.saveObject(config.s3Client, config.dataBucket, name, Json.stringify(t))
          S3.getObjectUrl(config.s3Client, config.dataBucket, name)
        }).getOrElse("failed to upload file and obtain download url")

        Right(UploadResult(downloadUrl))
      } catch {
        case e: Throwable =>
          Logger.error("Failed to upload transcript data", e)
          Left(TranscribeFilesError)
      }
      s3Result
    }
  }


//  def index: Action[AnyContent] = {
//    println("HELLO MY NAME IS JOE")
//    assets.at("index.html")
//  }
//
//  def assetOrDefault(resource: String): Action[AnyContent] = if (resource.startsWith("api")){
//    Action(_ => Ok("ok"))
//  } else {
//    if (resource.contains(".")) assets.at(resource) else index
//  }

}
