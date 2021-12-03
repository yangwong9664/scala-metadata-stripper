package services

import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.{Document, Image, PageSize}
import org.apache.commons.imaging.Imaging
import org.apache.commons.io.FileUtils
import org.ghost4j.document.PDFDocument
import org.ghost4j.renderer.SimpleRenderer
import utils.Logging._

import java.awt.Color
import java.awt.image.{BufferedImage, RenderedImage}
import java.io.{File, FileOutputStream}
import javax.imageio.ImageIO
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.sys.process._
import scala.util.{Failure, Success, Try}

class Stripper {

  case class FileInfo(directory: String, name: String, extension: String, originalFile: File)

  def reset: Future[Unit] = Future {
      FileUtils.deleteDirectory(new File("output"))
      FileUtils.deleteDirectory(new File("images"))
      FileUtils.deleteDirectory(new File("pdf"))
    }

  def printMetadata(file: File): Unit = Try {
    Imaging.getMetadata(file).toString(".")
  } match {
    case Success(value) =>
      logInfo("************************************")
      logInfo(s"image exif metadata: $value")
      logInfo("************************************")
    case Failure(_) =>
      logInfo("************************************")
      logInfo("no exif metadata.")
      logInfo("************************************")
  }

  def cleanseImage(fileInfo: FileInfo): Future[Unit] = Future {
    logInfo("cleansing image...")
    val outputFile = new File(s"output/strippedImage.png")
    printMetadata(fileInfo.originalFile)
    val stream = ImageIO.createImageInputStream(fileInfo.originalFile)
    val file = ImageIO.read(stream)
    val image = new BufferedImage(file.getWidth, file.getHeight, BufferedImage.TYPE_INT_RGB)
    image.createGraphics.drawImage(file, 0, 0, Color.WHITE, null)
    ImageIO.write(image, "png", outputFile)
    logInfo("image cleansed.")
    printMetadata(outputFile)
    logInfo("done")
  }

  def cleanseDoc(fileInfo: FileInfo): Future[Unit] = {
    FileUtils.forceMkdir(new File("images"))
    FileUtils.forceMkdir(new File("pdf"))
    Future { if(fileInfo.extension != "pdf") {
      s"libreoffice --headless --convert-to pdf ${fileInfo.directory} --outdir pdf".!!
    } else ""}.map { _ =>
      val pdf = new PDFDocument()
      pdf.load(new File(s"pdf/${fileInfo.name}.pdf"))
      val renderer = new SimpleRenderer()
      renderer.setResolution(70)

      val images = renderer.render(pdf).asScala.toList
      images.zipWithIndex.map { image =>
        ImageIO.write(image._1.asInstanceOf[RenderedImage], "png", new File(s"images/${image._2}.png"))
      }

      val document = new Document(PageSize.A4, 1, 1, 1, 1)
      PdfWriter.getInstance(document, new FileOutputStream("output/reconstructed.pdf"))
      document.open()
      val outputImages = new File("images").list().map { i =>
        val x = new File(s"images/$i")
        Image.getInstance(FileUtils.readFileToByteArray(x))
      }
      outputImages.map(document.add(_))
      document.close()
      logInfo("done")
    }
  }

  def clean(fileName: String): Future[String] = {
    logInfo("starting...")
  if (!fileName.contains(".")) Future.successful("bad request") else {
      val split = fileName.toLowerCase.split('.')
      val info = FileInfo(s"resources/${fileName}", split.head, split.last, new File(s"resources/${fileName}"))
      FileUtils.forceMkdir(new File("output"))
      info.extension match {
        case "png" | "jpg" | "jpeg" => cleanseImage(info).map(_ => ("done"))
        case _ => cleanseDoc(info).map(_ => ("done"))
      }
    }
  }

}
