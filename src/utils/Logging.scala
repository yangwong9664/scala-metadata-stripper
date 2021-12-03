package utils

import com.typesafe.scalalogging.Logger
import sourcecode.{File, Line}

import scala.util.{Failure, Success, Try}

object Logging {
  private val logger: Logger = Logger("scala-base-template")

  def logInfo(arg: String)(implicit line: Line, file: File): Unit = {
    logger.info(s"message: $arg, file: ${fileName(file.value)}, line: ${line.value}")
  }

  def logError(arg: String)(implicit line: Line, file: File): Unit = {
    logger.error(s"message: $arg, file: ${fileName(file.value)}, line: ${line.value}")
  }

  def logWarn(arg: String)(implicit line: Line, file: File): Unit = {
    logger.warn(s"message: $arg, file: ${fileName(file.value)}, line: ${line.value}")
  }

  private def fileName(path: String) = Try {
    path.split("src/").last
  } match {
    case Success(value) => value
    case Failure(_) => "unknown"
  }
}
