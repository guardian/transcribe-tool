package services
import java.io.{BufferedReader, File, FileWriter, InputStreamReader}

import com.amazonaws.HttpMethod
import com.amazonaws.auth.AWSCredentialsProviderChain
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{GeneratePresignedUrlRequest, PutObjectRequest, PutObjectResult, S3Object}
import config.Config
import org.joda.time.DateTime
import sun.misc.IOUtils


object S3 {
  def getS3Client(credentialsProvider: AWSCredentialsProviderChain) = new AmazonS3Client(credentialsProvider)

  def getObjectUrl(s3Client: AmazonS3Client, bucketName: String, objectPath: String): String = {
    val expireTime = DateTime.now.plusHours(1)

    val presigningRequest = new GeneratePresignedUrlRequest(bucketName, objectPath)
      .withMethod(HttpMethod.GET)
      .withExpiration(expireTime.toDate)

    val preSignedUrl = s3Client.generatePresignedUrl(presigningRequest)

    preSignedUrl.toString
  }

  def saveObject(s3Client: AmazonS3Client, bucketName: String, objectPath: String, objectData: String): PutObjectResult = {

    val file = new File(s"/tmp/$objectPath")
    val fileWriter = new FileWriter(file)
    fileWriter.write(objectData)
    fileWriter.flush()
    fileWriter.close()

    s3Client.putObject(bucketName, objectPath, file)
  }

//  def getFile(s3Client: AmazonS3Client, bucketName: String, objectPath: String): String = {
//    val file = s3Client.getObject(bucketName, objectPath)
//
//
//    val stream = file.getObjectContent
//
//    val bufferedReader = new BufferedReader(new InputStreamReader(stream))
//  }

}