package de.schauderhaft.visualAssert

import swing._
import javax.swing._
import swing.event._

object VisualComparisonTest {
    def mainFrame(image: Image, description: String) = new Dialog {
        title = "Visual Assert"
        val okButton = new Button("ok")
        val notOkButton = new Button("not ok")
        var result: Option[Boolean] = None
        contents = new BorderPanel {
            import BorderPanel.Position._
            add(new Label { icon = new ImageIcon(image) }, Center)
            add(new BorderPanel {
                add(new Label(description), Center)
                add(new BorderPanel {
                    add(okButton, West)
                    add(notOkButton, East)
                }, South)
            }, East)
        }

        listenTo(okButton, notOkButton)
        reactions += {
            case ButtonClicked(button) if button == okButton ⇒ ok
            case ButtonClicked(button) if button == notOkButton ⇒ notOk
        }

        def ok {
            result = Some(false)
            close
            dispose
        }
        def notOk {
        	result = Some(false)
        	close
        	dispose
        }
        
        closeOperation 
    }
}

object Starter {
    import javax.imageio.ImageIO
    def anImage = ImageIO.read(getClass.getClassLoader.getResourceAsStream("anImage.gif"))

    def main(args: Array[String]) {
        Swing.onEDT {
            val t = VisualComparisonTest.mainFrame(anImage, "Please verify that everything is ok in the image")
            t.pack
            t.visible = true
        }
    }
}
