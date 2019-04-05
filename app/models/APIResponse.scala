package models
import play.api.libs.json.{Json, Writes}
import play.api.mvc._
import play.api.Logger

case class TranscribeAPIResponse(message: String)
object TranscribeAPIResponse{
  implicit val atomWorkshopApiResponseEncoder= Json.writes[TranscribeAPIResponse]
}

object APIResponse extends Results {
  def apiErrorToResult(e: TranscribeAPIError): Result = {
    Logger.error(e.msg)
    InternalServerError(Json.prettyPrint(Json.toJson(TranscribeAPIResponse(e.msg))))
  }

  def apply[T](result: Either[TranscribeAPIError, T])(implicit writes: Writes[T]): Result = {
    val res = result.fold(apiErrorToResult, r => {
      println(Json.stringify(Json.toJson(r)))
      Ok(Json.stringify(Json.toJson(r)))
    })

    res.as("text/json")
  }
}