package de.schauderhaft.visualAssert

class ComponentToImageService {
    import javax.swing.JComponent
    import java.awt.Component
    import java.awt.Container
    import java.awt.Rectangle
    import java.awt.image.BufferedImage

    /**
     * 
     * ported from http://www.camick.com/java/source/ScreenImage.java
     * 
     *  Create a BufferedImage for Swing components.
     *  The entire component will be captured to an image.
     *
     *  @param  component Swing component to create image from
     *  @return	image the image for the given region
     */

    def createImage(component: JComponent): BufferedImage = {
        var d = component.getSize()

        if (d.width == 0 || d.height == 0) {
            d = component.getPreferredSize()
            component.setSize(d)
        }

        val region = new Rectangle(0, 0, d.width, d.height)
        return createImage(component, region)
    }

    /**
     * ported from http://www.camick.com/java/source/ScreenImage.java
     * 
     *  Create a BufferedImage for Swing components.
     *  All or part of the component can be captured to an image.
     *
     *  @param  component Swing component to create image from
     *  @param  region The region of the component to be captured to an image
     *  @return	image the image for the given region
     */
    def createImage(component: JComponent, region: Rectangle): BufferedImage = {
        //  Make sure the component has a size and has been layed out.
        //  (necessary check for components not added to a realized frame)

        if (!component.isDisplayable()) {
            var d = component.getSize();

            if (d.width == 0 || d.height == 0) {
                d = component.getPreferredSize();
                component.setSize(d);
            }

            layoutComponent(component);
        }

        val image = new BufferedImage(region.width, region.height, BufferedImage.TYPE_INT_RGB);
        val g2d = image.createGraphics();

        //  Paint a background for non-opaque components,
        //  otherwise the background will be black

        if (!component.isOpaque()) {
            g2d.setColor(component.getBackground());
            g2d.fillRect(region.x, region.y, region.width, region.height);
        }

        g2d.translate(-region.x, -region.y);
        component.paint(g2d);
        g2d.dispose();
        return image;
    }

    def layoutComponent(component: Component) {
        component.getTreeLock().synchronized {
            component.doLayout()
            component match {
                case c: Container ⇒ c.getComponents.foreach(layoutComponent(_))
                case _ ⇒
            }
        }
    }
}