package torque.graphics;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

/**
 * A class to load and store images in. Provides helpful functions for modifying images as well.
 *
 * @author gagnoncl
 *
 */
public class ImageLibrary {

	private static HashMap<String,Image> mImageLibrary = new HashMap<String,Image>();

	private static final Logger mLog = Logger.getLogger(ImageLibrary.class);
	
	static
	{
		loadImage("default", "images/default.gif");
	}
	/**
	 * Loads an image into the library. This should only be called once per image.
	 *
	 * @param aName The name to associate this image with, used to fetch the image with the {@link ImageLibrary#getImage ImageLibrary.getImage} method
	 * @param aPathname The path used to load the image
	 * @return The image which was loaded
	 * @throws IOException If an error occurs during reading
	 */
	public static Image loadImage(final String aName, final String aPathname)
	{
		//TODO: Implement the Media TrackerMediaTracker l = new MediaTracker(null);

		mLog.debug("Loading image : " + aName);
		BufferedImage lRet = null;
		try {
			URL lURL = ImageLibrary.class.getClassLoader().getResource(aPathname);
			lRet = ImageIO.read(lURL);
		} catch (IOException e) {
			mLog.error("I/O exception loading \"" + aName + "\" image - " + e.getMessage());
			return null;
		}
		mLog.debug(aName + " successfully loaded");
		mImageLibrary.put(aName, lRet);
		return lRet;
	}

	/**
	 * Gets an image previously loaded into the {@link ImageLibrary}. If the specified image doesn't exist returns a default
	 * image in place of it.
	 *
	 * @param aName The name associated with the image to load
	 * @return The {@link Image} object if a mapping exists for the given name, otherwise a default image if not
	 */
	public static Image getImage(final String aName)
	{
		mLog.debug("Fetching image " + aName);
		Image lRet = mImageLibrary.get(aName);
		if(lRet == null)
		{
			System.out.println("wut");
			mLog.error("No image with name of " + aName + " found");
			lRet = mImageLibrary.get("default");
		}
		return lRet;
	}

	/**
	 * Gets a scaled version of an image previously loaded into the {@link ImageLibrary}. If the specified image doesn't exist returns a default
	 * image in place of it. This default image is NOT scaled.
	 *
	 * @param aName The name associated with the image to load
	 * @param aDimensions The dimensions to scale the image to
	 * @return The {@link Image} object if a mapping exists for the given name, otherwise a default image if not
	 */
	public static Image getScaledImage(final String aName, final Dimension aDimensions)
	{
		mLog.debug("Fetching image \"" + aName + "\" with dimensions " + aDimensions.toString());
		Image lOriginal = mImageLibrary.get(aName);
		if(lOriginal == null)
		{
			mLog.error("No image with name of " + aName + " found");
			lOriginal = mImageLibrary.get("default");
			return lOriginal;
		}
		BufferedImage lRet = new BufferedImage(aDimensions.width, aDimensions.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D lG = lRet.createGraphics();
		lG.drawImage(lOriginal, 0, 0, aDimensions.width, aDimensions.height, null);
		lG.dispose();
		return lRet;
		//return lRet.getScaledInstance(aDimensions.width, aDimensions.height, 0);
	}
}