package models


sealed abstract class TranscribeAPIError(val msg: String)

case object TranscribeFilesError extends TranscribeAPIError("Failed to fetch presigned urls for transcript")