package config

import com.amazonaws.auth.{AWSCredentialsProviderChain, InstanceProfileCredentialsProvider}
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Region
import play.api.Configuration
import services.{AwsInstanceTags, S3}

class Config(conf: Configuration) extends AwsInstanceTags {

  val stage: String = readTag("Stage") getOrElse "DEV"
  val appName: String = readTag("App") getOrElse "transcribe"
  val stack: String = readTag("Stack") getOrElse "flexible"
  val region: Region = services.EC2Client.region

  val awsCredentialsProvider = new AWSCredentialsProviderChain(
    new ProfileCredentialsProvider("composer"),
    new InstanceProfileCredentialsProvider(false)
  )
  val s3Client = S3.getS3Client(awsCredentialsProvider)

  val dataBucket: String = conf.get[String]("data.bucket")

  val audioSourceBucket: String = conf.get[String]("s3.audioSourceBucket")
  val transcriptOutputBucket: String = conf.get[String]("s3.transcriptOutputBucket")

  val pandaDomain: String = conf.get[String]("panda.domain")
  val pandaSystem: String = conf.get[String]("panda.system")
  val pandaAuthCallback: String = conf.get[String]("panda.authCallback")

  val elkKinesisStream: String = conf.getOptional[String]("elk.kinesis.stream").getOrElse("")
  val elkLoggingEnabled: Boolean = conf.getOptional[Boolean]("elk.logging.enabled").getOrElse(false)

}
