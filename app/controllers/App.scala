package controllers

import com.gu.pandomainauth.PanDomainAuthSettingsRefresher
import config.Config
import models._
import play.api.Logger
import play.api.libs.json.{Json, OWrites}
import play.api.libs.ws.WSClient
import play.api.mvc._
import services.S3

case class TranscriptFiles(name: String, media: String, transcript: String)
object TranscriptFiles {
  implicit val jsonWrites: OWrites[TranscriptFiles] = Json.writes[TranscriptFiles]
}

case class UploadResult(dataUrl: String, textUrl: String)
object UploadResult {
  implicit val jsonWrites = Json.writes[UploadResult]
}

case class TextBlock(speaker: String, text: String, startTime: Option[Float])
object TextBlock {
  implicit val textBlockFormats = Json.format[TextBlock]
}

class App(val wsClient: WSClient,
  val controllerComponents: ControllerComponents,
  val panDomainSettings: PanDomainAuthSettingsRefresher,
  val config: Config,
  val assets: Assets) extends BaseController with PanDomainAuthActions {

  def index(transcriptName: Option[String]) = AuthAction {
    Logger.info(s"I am the ${config.appName}")

    transcriptName.map {name =>
      val audioUrl = S3.getObjectUrl(config.s3Client, config.audioSourceBucket, s"$name.mp3")
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

  def getTextVersion(textBlock: List[TextBlock]): String = {
    textBlock.map{ block =>
      s"${block.speaker}: ${block.text}"
    }.mkString("\n\n")
  }

  def saveAndGetS3(data: String, fileName: String): Either[TranscribeAPIError, String] = {
    try {
      S3.saveObject(config.s3Client, config.dataBucket, fileName, data)
      val url = S3.getObjectUrl(config.s3Client, config.dataBucket, fileName)
      Right(url)
    } catch {
      case e: Throwable =>
        Logger.error("Failed to upload to S3", e)
        Left(UploadFilesError)
    }
  }


  def saveTranscript(transcriptName: String): Action[AnyContent] = Action { request =>
    APIResponse {
      val transcript = request.body.asJson
      println(transcript)

      transcript.map {t =>
        t.validate[List[TextBlock]].map {textBlocks =>
          val textVersion = getTextVersion(textBlocks)
          for {
            uploadTextResult <- saveAndGetS3(textVersion, s"$transcriptName.txt")
            uploadDataResult <- saveAndGetS3(Json.stringify(t), s"${transcriptName}Out.json")
          } yield {
            UploadResult(uploadDataResult, uploadTextResult)
          }
        }.getOrElse(Left(InvalidUploadJsonFields))
      }.getOrElse(Left(InvalidUploadJson))
    }
  }

}
