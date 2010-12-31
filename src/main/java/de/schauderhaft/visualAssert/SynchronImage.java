package de.schauderhaft.visualAssert;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.util.concurrent.CountDownLatch;

public class SynchronImage {
	private final Image image;

	private final CountDownLatch widthLatch = new CountDownLatch(1);
	private int width;

	private final CountDownLatch heightLatch = new CountDownLatch(1);
	private int height;

	private int[] pixelCache = null;

	SynchronImage(Image anImage) {
		image = anImage;
		int someWidth = image.getWidth(new ImageObserver() {
			@Override
			public boolean imageUpdate(Image img, int infoflags, int x, int y,
					int width, int height) {
				if ((infoflags & ImageObserver.WIDTH) > 0) {
					setWidth(width);
					return false;
				}
				return true;
			}
		});
		if (someWidth >= 0)
			setWidth(someWidth);

		int someHeight = image.getHeight(new ImageObserver() {
			@Override
			public boolean imageUpdate(Image img, int infoflags, int x, int y,
					int width, int height) {
				if ((infoflags & ImageObserver.HEIGHT) > 0) {
					setHeight(height);
					return false;
				}
				return true;
			}
		});
		if (someHeight >= 0)
			setHeight(someHeight);
	}

	public int getWidth() {
		boolean doneWaiting = false;
		while (!doneWaiting) {
			try {
				widthLatch.await();
				doneWaiting = true;
			} catch (InterruptedException e) {
				// we just start over waiting
			}
		}
		return width;
	}

	public int[] getPixels() {
		if (pixelCache == null) {
			pixelCache = grabPixels();
		}
		return pixelCache;
	}

	private int[] grabPixels() {
		int width = getWidth();
		int height = getHeight();

		int[] pixels = new int[width * height];
		PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, pixels,
				0, width);
		boolean done = false;
		while (!done) {
			try {
				pg.grabPixels();
				done = true;
			} catch (InterruptedException e) {
				// we just continue trying;s
			}
		}
		return pixels;
	}

	public int getRGB(int x, int y) {
		int[] pixels = getPixels();
		int width = getWidth();
		int index = x * width + y;
		return pixels[index]; // or pixels[x * width + y]
	}

	private synchronized void setWidth(int aWidth) {
		width = aWidth;
		widthLatch.countDown();
	}

	public int getHeight() {
		boolean doneWaiting = false;
		while (!doneWaiting) {
			try {
				heightLatch.await();
				doneWaiting = true;
			} catch (InterruptedException e) {
				// we just start over waiting
			}
		}
		return height;
	}

	private synchronized void setHeight(int aHeight) {
		height = aHeight;
		heightLatch.countDown();
	}

}
