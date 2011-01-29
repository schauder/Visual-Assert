package de.schauderhaft.visualAssert
import java.awt.Image
import java.awt.image.BufferedImage
import org.scalatest.Assertions._
import org.scalatest._
import javax.swing.JComponent

trait VisualAssert {
	
	val component2ImageService = new ComponentToImageService()
	val imageFileService = new ImageFileService();

    def assertEquals(imageOne: Image, imageTwo: Image) {
        if (imageOne == imageTwo) return 
        val sImageOne = new SynchronImage(imageOne)
        val sImageTwo = new SynchronImage(imageTwo)
        val dimension =  compareDimension(sImageOne, sImageTwo)
        val totalNumberOfPixel = dimension._1 * dimension._2
        var wrongPixelCount = 0
        for (x ← 0 until dimension._2)
            for (y ← 0 until dimension._1)
                if (sImageOne.getRGB(x, y) != sImageTwo.getRGB(x, y)) wrongPixelCount += 1
        if (0 < wrongPixelCount) throw new TestFailedException("The compared images differ in " + wrongPixelCount + " pixel out of " + totalNumberOfPixel, 0)
    }

    def assertEquals(imageName: String, image: BufferedImage) {
    	try {
        val imageFromClassPath = imageFileService.findImage(imageName)
        assertEquals(imageFromClassPath, image)
    	} catch {
    		case e : Exception => {
    			imageFileService.writeImage(image, imageName + "_actual.png")
    			throw new TestFailedException(e.getMessage(), 0)
    		}
    	}
    }

    def assertEquals(imageName: String, component: JComponent) {
        assertEquals(imageName, component2ImageService.createImage(component))
    }

    private def compareDimension(sImageOne: SynchronImage, sImageTwo: de.schauderhaft.visualAssert.SynchronImage) = {
        val dimensionOne = (sImageOne.getWidth(), sImageOne.getHeight())
        val dimensionTwo = (sImageTwo.getWidth(), sImageTwo.getHeight())
        if (dimensionOne != dimensionTwo) throw new TestFailedException("expected an image with dimensions " + dimensionOne + ". Image has dimensions " + dimensionTwo + ".", 0)

        dimensionOne
    }
}

object VisualAssert{
	val INTERACTIVE_PROPERTY = "de.schauderhaft.visualAssert.interactive"
}