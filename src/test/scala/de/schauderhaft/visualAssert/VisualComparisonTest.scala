package de.schauderhaft.visualAssert

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

/**
 * For manual testing
 */
object VisualComparisonStarter {
    import javax.imageio.ImageIO
    def anImage = ImageIO.read(getClass.getClassLoader.getResourceAsStream("anImage.gif"))

    def main(args: Array[String]) {
        println(VisualComparison.visualAssert(anImage, "Please verify that everything is ok in the image"))
    }
}

class VisualComparionTest extends FunSuite with VisualAssert with ShouldMatchers {

    override protected def test(testName: String, testTags: Tag*)(f: ⇒ Unit) {
        super.test(testName, testTags :_*) {
            javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                def run(): Unit = {
                    f
                }
            })
        }
    }

    /** this is completely meta, testing the visuals of visualAssert with visualAssert */
    test("Dialog has correct looks") {
        import javax.imageio.ImageIO
        def anImage = ImageIO.read(getClass.getClassLoader.getResourceAsStream("anImage.gif"))

        val mainFrame = VisualComparison.mainFrame(anImage, "somedecription", null)

        assertEquals("contentPaneForVisualComparison", mainFrame.contents.head.peer)
    }
    test("Pressing The Ok Button results in a positive result") {
        import javax.imageio.ImageIO
        def anImage = ImageIO.read(getClass.getClassLoader.getResourceAsStream("anImage.gif"))
        val result = new MockResult

        val mainFrame = VisualComparison.mainFrame(anImage, "somedecription", result)

        mainFrame.okButton.doClick()

        result.getValue should be(true)
    }

    test("Pressing The No Button results in a negativ result") {
        import javax.imageio.ImageIO
        def anImage = ImageIO.read(getClass.getClassLoader.getResourceAsStream("anImage.gif"))
        val result = new MockResult

        val mainFrame = VisualComparison.mainFrame(anImage, "somedecription", result)

        mainFrame.notOkButton.doClick()

        result.getValue should be(false)
    }

    class MockResult extends VisualComparison.ReturnValue {
        var value = false

        override def setValue(aValue: Boolean) {
            value = aValue
        }

        override def getValue() = {
            value
        }
    }
}