package de.schauderhaft.visualAssert
import java.awt.Image

class ImageFileService {
    import java.awt.image.BufferedImage
    import javax.imageio.ImageIO
    import java.io.File

    val types = ImageIO.getWriterFileSuffixes()
    /**
     * 
     * ported from http://www.camick.com/java/source/ScreenImage.java
     * 
     *  Write a BufferedImage to a File.
     *
     *  @param	 image image to be written
     *  @param	 fileName name of file to be created
     *   IOException if an error occurs during writing
     */
    def writeImage(image: BufferedImage, fileName: String) {
        import java.io.IOException

        if (fileName == null) return
        val offset = fileName.lastIndexOf(".")
        if (offset == -1) {
            val message = "file suffix was not specified"
            throw new IOException(message)
        }

        val fileType = fileName.substring(offset + 1)

        if (types.contains(fileType))
            ImageIO.write(image, fileType, new File(fileName))
        else {
            val message = "unknown writer file suffix (" + fileType + ")"
            throw new IOException(message);
        }
    }

    def findImage(imageName: String): Image = {
        try {
            val possibleFileNames = Stream("png", "gif").map(imageName + "." + _)
            possibleFileNames.map(loadImage(_)).find(_ != null).get
        } catch {
            case e ⇒ {
                println("couldn't find an image with base name: '" + imageName + "' " + e)
                throw new ImageNotFoundException("couldn't find an image with base name: '" + imageName + "'")
            }
        }
    }

    /**
     * loads an image identified by its name 
     * 
     * @param name
     * 
     * @return
     */
    private def loadImage(imageName: String): Image = {
        import javax.imageio.ImageIO

        def loadFromClasspath(name: String) = ImageIO.read(getClass().getClassLoader().getResourceAsStream(name))

        def loadFromWorkingDir(name: String) = ImageIO.read(new File(name))

        def failSafe(f: String ⇒ Image)(s: String) = {
            try {
                f(s)
            } catch {
                case e ⇒ null
            }
        }

        val loadStrategies = Seq(
            failSafe(loadFromClasspath)_,
            failSafe(loadFromWorkingDir)_)
        try {
            val images = loadStrategies map { _(imageName) }
            
            images.find(_ != null) get
        } catch {
            case e ⇒ null;
        }
    }

    class ImageNotFoundException(message: String) extends RuntimeException(message)
}