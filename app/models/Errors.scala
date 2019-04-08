package models


sealed abstract class TranscribeAPIError(val msg: String)

case object TranscribeFilesError extends TranscribeAPIError("Failed to fetch presigned urls for transcript")
case object UploadFilesError extends TranscribeAPIError("Failed to upload file to s3")
case object InvalidUploadJsonFields extends TranscribeAPIError("Failed to upload file to s3")
case object InvalidUploadJson extends TranscribeAPIError("Failed to upload file to s3")
