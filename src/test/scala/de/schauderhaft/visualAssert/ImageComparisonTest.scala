package de.schauderhaft.visualAssert
import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

import java.awt.Image
import java.awt.Color._
import java.awt.image.BufferedImage

import javax.imageio.ImageIO

class ImageComparisonTest extends FeatureSpec with ShouldMatchers with GivenWhenThen with VisualAssert {
    def anImage = ImageIO.read(getClass.getClassLoader.getResourceAsStream("anImage.gif"))

    feature("A user can compare two images") {

        scenario("when a <null> image is compared to <null> nothing happens") {
            assertEquals(null.asInstanceOf[Image], null)
        }

        scenario("when an image is compared to itself nothing happens") {
            val image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)
            assertEquals(image, image)
        }

        scenario("when an image is compared to an identical image nothing happens") {
            val one = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)
            val two = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)
            assertEquals(one, two)
        }

        scenario("when an image is compared to an image of a different size the test failure contains information about the size of both images") {
            val one = new BufferedImage(90, 100, BufferedImage.TYPE_INT_ARGB)
            val two = new BufferedImage(80, 70, BufferedImage.TYPE_INT_ARGB)
            val exception = intercept[TestFailedException] { assertEquals(one, two) }
            assert(exception.getMessage.contains("90"))
            assert(exception.getMessage.contains("80"))
            assert(exception.getMessage.contains("100"))
            assert(exception.getMessage.contains("70"))
        }

        scenario("when an image is compared to an image that differs the number of differing pixels is returned") {
            val one = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)
            val two = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)
            val g = two.getGraphics()
            g.drawLine(20, 20, 20, 20)
            g.drawLine(30, 30, 30, 30)
            g.drawLine(40, 40, 40, 40)
            val exception = intercept[TestFailedException] { assertEquals(one, two) }
            assert(exception.getMessage.contains("3"), "expected a 3 in message, but found: " + exception.getMessage)
        }
    }

    feature("a user can compare an image stored in the classpath with an image in memory") {
        scenario("when no image is found a TestFailure is reported") {
            val image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)
            val exception = intercept[TestFailedException] {
                assertEquals("notexistingImage", image)
            }
            assert(exception.getMessage.contains("notexistingImage"))
        }

        scenario("when an image of that name is found it is loaded and compared to the provided image (png)") {
            given("an image")
            val image = ImageIO.read(getClass.getClassLoader.getResourceAsStream("anImage.png"))
            and("the same image in the class path")
            when("compared")
            assertEquals("anImage_png", image)
            then("nothing happens")

            given(" a different image in the class path")
            when("compared")
            then("a test failure results")
            intercept[TestFailedException] { assertEquals("otherImage_png", image) }
        }

        scenario("when an image of that name is found it is loaded and compared to the provided image (gif)") {
            given("an image")
            val image = anImage
            and("the same image in the class path")
            when("compared")
            assertEquals("anImage_gif", image)
            then("nothing happens")

            given(" a different image in the class path")
            when("compared")
            then("a test failure results")
            intercept[TestFailedException] { assertEquals("otherImage_gif", image) }
        }
    }

    feature("a user can compare the screenshot of a swing component with an image stored on disk") {
        import javax.swing.JLabel

        scenario("comparing a component to a matching image matching an image of that component in the classpath") {
            given("a swing component")
            val component = new JLabel("This is a test label")
            when ("comparing the component to the filename of a matching image")
            assertEquals("component", component)
            then ("nothing happens")
        }

        scenario("comparing a component to a NOT matching image") {
            given("a swing component")
            val component = new JLabel("This is another test label")
            when ("comparing the component to a filename of a NOT matching image")
            then ("the assertion fails")
            intercept[TestFailedException] { assertEquals("component", component) }
        }
        
        scenario("comparing a component to a NOT existing image in headless mode") {
            given("a swing component")
            val component = new JLabel("This is a test label")
            when ("comparing the component to a filename of a NOT matching image")
            then ("the assertion fails")
            intercept[TestFailedException] { assertEquals("no such component", component) }
        }
    }
    
    feature ("A reference image is stored on test failure"){
    	scenario("when a comparison fails the image that was to be tested gets stored") {
    		given("an image")
    		val image = anImage
    		when(" compared and the comparison fails")
    		val noImage = "noImage"
    		intercept[TestFailedException] { assertEquals(noImage, image) }
    		then("the image gets stored in the file systems")
    		assertEquals(noImage + "_actual", image)
    	}
    }    
}
