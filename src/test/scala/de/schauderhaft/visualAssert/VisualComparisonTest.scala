package de.schauderhaft.visualAssert


object VisualComparisonStarter {
    import javax.imageio.ImageIO
    def anImage = ImageIO.read(getClass.getClassLoader.getResourceAsStream("anImage.gif"))

    def main(args: Array[String]) {
        println(VisualComparison.visualAssert(anImage, "Please verify that everything is ok in the image"))
    }

}
