import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Webcam-based drawing 
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Spring 2015 (based on a different webcam app from previous terms)
 *
 * @author Sajjad
 * @author Josue
 * We completed all the to do code for the PS1 assignment.
 */
public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private final Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece

	private BufferedImage recoloredImage;         // recolored painting

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting, 
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) {
		if (displayMode == 'w') //if in webcam mode
		{
			g.drawImage(image, 0, 0, null); //just display the webcam
		} else if (displayMode == 'r') { //else if in recolored image mode
			finder.setImage(image); //set the image
			finder.findRegions(targetColor); //find the regions of the target color
			finder.recolorImage(); //recolor those regions
			recoloredImage = finder.getRecoloredImage(); //get that recolored image
			g.drawImage(recoloredImage, 0, 0, null); //draw the recolored image
		} else if (displayMode == 'p') { //if in painting mode
			g.drawImage(painting, 0, 0, null); //draw the painting that has been created
		}
	}

	/**
	 * Webcam method, here finding regions and updating the painting.
	 */
	@Override
		public void processImage()
		{
			if (image != null & targetColor != null) //if the targetColor & image is not null
			{
				finder.setImage(image); //set the image
				finder.findRegions(targetColor); //find the regions of target color
				ArrayList<Point> largestRegion = finder.largestRegion(); //get the largest region

				for (int i = 0; i < largestRegion.size(); i++) //for every point in the largest region
				{
					Point point  = largestRegion.get(i); //get the point
					int px = (int)point.getX(); //get the x of the point
					int py = (int)point.getY(); //get the y of the point
					painting.setRGB(px, py, paintColor.getRGB()); //at that px and py, set the painting rgb to that of paint color
				}

			}
		}

	/**
	 * Overrides the DrawingGUI method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y)
	{
		if(image != null) //if the image is not null
		{
			targetColor = new Color(image.getRGB(x, y)); //get the target color where mouse is clicked
		}
	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			saveImage(painting, "pictures/painting.png", "png");
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}
