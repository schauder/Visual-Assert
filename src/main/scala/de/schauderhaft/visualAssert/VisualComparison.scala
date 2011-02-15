package de.schauderhaft.visualAssert

import swing._
import javax.swing._
import swing.event._

object VisualComparison {


    def visualAssert(anImage: Image, aMessage: String) = {
        val returnValue = new ReturnValue
        Swing.onEDT {
            val t = VisualComparison.mainFrame(anImage, aMessage, returnValue)
            t.pack
            t.visible = true
        }
        returnValue.getValue
    }

    private [visualAssert] class ReturnValue {
        private var value = false

        private val latch = new java.util.concurrent.CountDownLatch(1)

        def setValue(aValue: Boolean) {
            value = aValue
            latch.countDown()
        }
        def getValue() = {
            latch.await
            value
        }
    }

    private [visualAssert] def mainFrame(image: Image, description: String, result: ReturnValue) = new Frame {
        title = "Visual Assert"
        val okButton = new Button("ok")
        val notOkButton = new Button("not ok")

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
            result.setValue(true)
            dispose
        }
        def notOk {
            result.setValue(false)
            dispose
        }
        override def closeOperation() {
            result.setValue(false)
            super.closeOperation()
            dispose
        }
    }
}
