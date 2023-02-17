import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 *
 * @author Sajjad
 * @author Josue
 * We implemented all the code for findRegions, colorMatch, largestRegion, and recolorImage.
 * Completed all the to do code for the PS1 assignment.
 */
public class RegionFinder {
	private static final int maxColorDiff = 20;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50; 				// how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions;			// a region is a list of points
															// so the identified regions are in a list of lists of points
	private ArrayList<Point> newRegion; //keep track of new region each time looping
															// if big enough, then we add it to the regions.
	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;		
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 * @param targetColor
	 */
	public void findRegions(Color targetColor)
	{
		regions = new ArrayList<>(); //instantiate regions and allocate heap memory for it
		BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB); //image that starts of all black
																													 //keeps track of visited pixels
		ArrayList<Point> toVisit = new ArrayList<>(); //keeps track of pixels we need to visit

		for (int y = 0; y < image.getHeight(); y++) //loop through every y pixel
		{
			for (int x = 0; x < image.getWidth(); x++) //loop through every x pixel

			{
			
					if (visited.getRGB(x, y) == 0 && colorMatch(new Color(image.getRGB(x, y)), targetColor)) //if not visited and of the correct color
					{
						newRegion = new ArrayList<>(); //start a new region
						newRegion.add(new Point(x, y)); //add the point to that region

						toVisit.add(new Point(x, y)); //add the point to the toVisit list.
						visited.setRGB(x, y, 1); //set it to visited

						while (!toVisit.isEmpty()) //while the list of pixels needed to visit is not empty
						{
							Point point = toVisit.remove(toVisit.size() - 1); //get the last point(x,y) in the toVisit list.
							int px = (int) point.getX(); //get the x of the point
							int py = (int) point.getY(); //get the y of the point

							for (int i = -1; i <= 1; i++) //loop through every neighbor in the x direction (-1, 0 ,1)
							{
								for (int j = -1; j <= 1; j++) //loop through every neighbor in the y direction (-1, 0, 1)
								{
									//nested for loop will cover (NE, N, NW, W, E, SE, S, SW)
									int xn = px + i; //get the X neighbor
									int yn = py + j; //get the Y neighbor

									if (xn >= 0 && xn < image.getWidth() && yn >= 0 && yn < image.getHeight() && colorMatch(new Color(image.getRGB(xn, yn)), targetColor) && visited.getRGB(xn, yn) == 0)
									//if the x and y coordinates are not out of bonds, and the neighbor has not been visited, and it is of correct color
									{
										toVisit.add(new Point(xn, yn)); //add it to the list of points we need to visit
										visited.setRGB(xn, yn, 1); //set it as visited
										newRegion.add(new Point(xn, yn)); //add it to the region we are keeping track of
									}
								}
							}
						}
						if (newRegion.size() >= minRegion) //if the region is big enough to be worth keeping
						{
							regions.add(newRegion); //then keep it by adding to list of regions
						}
					}
			}
		}
	}

	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 * @param c1
	 * @param c2
	 * compares the absolute value of each channel (red, green, blue), and makes sure each channel is less than a threshold
	 */
	private static boolean colorMatch(Color c1, Color c2) {

		int redD = Math.abs(c1.getRed() - c2.getRed()); //calculates red difference
		int greenD = Math.abs(c1.getGreen() - c2.getGreen()); //calculates green difference
		int blueD = Math.abs(c1.getBlue() - c2.getBlue()); //calculates blue difference

		if(redD > maxColorDiff || greenD > maxColorDiff || blueD > maxColorDiff) //if r/g/b > threshold
		{
			return false; //then it is not a color match
		}

		return true; //else it is a color match

	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion()
	{
		ArrayList<Point> largestRegion = new ArrayList<>(); //initialize a largest region of points --> currently empty
		int maxSize = 0; //start max size as 0

		for (ArrayList<Point> region : regions) //loop through every region of points in the list of regions
		{
			if (region.size() > maxSize) //if the size of that region is greater than the current max size
			{
				largestRegion = region; //set the largest region to that region
				maxSize = largestRegion.size(); //set the current max size to that regions' size
			}
		}
		return largestRegion; //return the largest region
	}

	/**
	 * Sets recoloredImage to be a copy of image,
	 * but with each region a uniform random color,
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		for (ArrayList<Point> region : regions)
		{

			final int min = 0;
			final int max = 16777216;
			int v = (int) ((Math.random() * (max - min)) + min);
			Color color = new Color(v); //gets a random color
			for (Point point : region) //for every point in the region
			{
				recoloredImage.setRGB((int) point.getX(), (int) point.getY(), color.getRGB()); //recolor that point with the random color
			}
		}
	}
}
