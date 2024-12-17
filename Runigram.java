import java.awt.Color;
import java.util.logging.Handler;

/** A library of image processing functions. */
public class Runigram {

	public static void main(String[] args) {
	    
		//// Hide / change / add to the testing code below, as needed.
		
		// Tests the reading and printing of an image:	
/*		Color[][] tinypic = read("tinypic.ppm");
		print(tinypic);

		// Creates an image which will be the result of various 
		// image processing operations:
		Color[][] image;
		image = scaled(tinypic, 3 ,5);
		System.out.println();
		print(image);
 */
		Color[][] image1 = read("tinypic.ppm");
        Color[][] image2 = flippedHorizontally(image1);

		System.out.println("=== Testing blend(Color, Color, double) ===");
        Color blendedColor = blend(new Color(255, 0, 0), new Color(0, 0, 255), 0.5);
        System.out.println("Blended Color (red + blue, alpha=0.5): " + blendedColor);

        System.out.println("\n=== Testing blend(Color[][], Color[][], double) ===");
        Color[][] blendedImage = blend(image1, image2, 0.5);
        print(blendedImage);

        System.out.println("\n=== Testing morph(Color[][], Color[][], n) ===");
        morph(image1, image2, 5);  // יציג מעבר מדורג בין התמונות (אנימציה)

        System.out.println("\nTests completed.");



	}

	/** Returns a 2D array of Color values, representing the image data
	 * stored in the given PPM file. */
	public static Color[][] read(String fileName) {
		In in = new In(fileName);
		// Reads the file header, ignoring the first and the third lines.
		in.readString();
		int numCols = in.readInt();
		int numRows = in.readInt();
		in.readInt();
		// Creates the image array
		Color[][] image = new Color[numRows][numCols];
		for (int i=0; i<numRows; i++)
		{
			for (int j=0; j<numCols; j++)
			{
				int red = in.readInt();
                int green = in.readInt();
                int blue = in.readInt();
				image [i][j] = new Color(red, green, blue);
			}
		}
		return image;
	}

    // Prints the RGB values of a given color.
	private static void print(Color c) {
	    System.out.print("(");
		System.out.printf("%3s,", c.getRed());   // Prints the red component
		System.out.printf("%3s,", c.getGreen()); // Prints the green component
        System.out.printf("%3s",  c.getBlue());  // Prints the blue component
        System.out.print(")  ");
	}

	// Prints the pixels of the given image.
	// Each pixel is printed as a triplet of (r,g,b) values.
	// This function is used for debugging purposes.
	// For example, to check that some image processing function works correctly,
	// we can apply the function and then use this function to print the resulting image.
	private static void print(Color[][] image) {
		for (int i=0; i<image.length; i++)
		{
			for (int j=0; j<image[0].length; j++)
			{
				print(image [i][j]);
			}
			System.out.println();
		}
	}
	
	/**
	 * Returns an image which is the horizontally flipped version of the given image. 
	 */
	public static Color[][] flippedHorizontally(Color[][] image) {
		int numRow = image.length;
		int numCol = image [0].length;
		Color [][] flip = new Color [numRow][numCol];
		int flipIndex = numCol-1;
		for (int i=0; i<numRow; i++)
		{
			for (int j=0; j<numCol; j++)
			{
				flip [i][flipIndex] = image [i][j];		
				flipIndex--;
			}
			flipIndex = numCol-1;
		}
		return flip;
	}
	
	/**
	 * Returns an image which is the vertically flipped version of the given image. 
	 */
	public static Color[][] flippedVertically(Color[][] image){
		int numRow = image.length;
		int numCol = image [0].length;
		Color [][] flip = new Color [numRow][numCol];
		int flipIndex = numRow-1;
		for (int i=0; i<numRow; i++)
		{
			for (int j=0; j<numCol; j++)
			{
				flip [flipIndex][j] = image [i][j];
			}
			flipIndex--;
		}
		return flip;
	}
	
	// Computes the luminance of the RGB values of the given pixel, using the formula 
	// lum = 0.299 * r + 0.587 * g + 0.114 * b, and returns a Color object consisting
	// the three values r = lum, g = lum, b = lum.
	private static Color luminance(Color pixel) {
		int red = pixel.getRed();
		int green = pixel.getGreen();
		int blue = pixel.getBlue();
		int grayValue = (int) ((0.299 * red) + (0.587 * green) + (0.114 * blue));
		Color lum = new Color (grayValue, grayValue, grayValue);
		return lum;
	}
	
	/**
	 * Returns an image which is the grayscaled version of the given image.
	 */
	public static Color[][] grayScaled(Color[][] image) {
		int numRow = image.length;
		int numCol = image [0].length;
		Color [][] lum = new Color [numRow][numCol];
		for (int i=0; i<numRow; i++)
		{
			for (int j=0; j<numCol; j++)
			{
				lum [i][j] = luminance(image [i][j]);
			}
		}
		return lum;
	}	
	
	/**
	 * Returns an image which is the scaled version of the given image. 
	 * The image is scaled (resized) to have the given width and height.
	 */
	public static Color[][] scaled(Color[][] image, int width, int height) {
		int numRow = image.length;
		int numCol = image [0].length;
		Color [][] scaled = new Color [height][width];
		int jScaleWidth = 0;
		int iScaleHeight = 0;
		for (int i=0; i<height; i++)
		{
			for (int j=0; j<width; j++)
			{
				jScaleWidth =(int) Math.round((j*numCol)/(double) width);
				iScaleHeight = (int) Math.round((i*numRow)/(double)height);
				scaled [i][j] = image [iScaleHeight][jScaleWidth];
			}
		}
		return scaled;
	}
	
	/**
	 * Computes and returns a blended color which is a linear combination of the two given
	 * colors. Each r, g, b, value v in the returned color is calculated using the formula 
	 * v = alpha * v1 + (1 - alpha) * v2, where v1 and v2 are the corresponding r, g, b
	 * values in the two input color.
	 */
	public static Color blend(Color c1, Color c2, double alpha) {
		if (alpha>1 || alpha<0)
		{
			return null;
		}
		int red = (int) (alpha*c1.getRed()+ (1-alpha)*c2.getRed());
		int green = (int) (alpha*c1.getGreen()+ (1-alpha)*c2.getGreen());
		int blue = (int) (alpha*c1.getBlue()+ (1-alpha)*c2.getBlue());
		Color blend = new Color(red, green, blue);
		return blend;
	}
	
	/**
	 * Cosntructs and returns an image which is the blending of the two given images.
	 * The blended image is the linear combination of (alpha) part of the first image
	 * and (1 - alpha) part the second image.
	 * The two images must have the same dimensions.
	 */
	public static Color[][] blend(Color[][] image1, Color[][] image2, double alpha) {
		int numRow = image1.length;
		int numCol = image1 [0].length;
		Color [][] blended = new Color [numRow][numCol];
		for (int i=0; i<numRow; i++)
		{
			for (int j=0; j<numCol; j++)
			{
				blended [i][j] = blend (image1[i][j], image2[i][j], alpha);
			}
		}
		return blended;
		}

	/**
	 * Morphs the source image into the target image, gradually, in n steps.
	 * Animates the morphing process by displaying the morphed image in each step.
	 * Before starting the process, scales the target image to the dimensions
	 * of the source image.
	 */
	public static void morph(Color[][] source, Color[][] target, int n) {
		int sourceRow = source.length;
		int sourceCol = source [0].length;
		int targetRow = target.length;
		int targetCol = target[0].length;
		if ((sourceRow != targetRow) || (sourceCol != targetCol))
		{
			target = scaled(target, sourceCol, sourceRow);
		}
		double alpha;
		for (int i=0; i<=n; i++)
		{
			alpha = ((n-i)/(double)(n));
			Color[][] blendedImage = blend(source, target, alpha);
        	display(blendedImage);
			StdDraw.pause(500); 
		}
	}
	
	/** Creates a canvas for the given image. */
	public static void setCanvas(Color[][] image) {
		StdDraw.setTitle("Runigram 2023");
		int height = image.length;
		int width = image[0].length;
		StdDraw.setCanvasSize(width, height);
		StdDraw.setXscale(0, width);
		StdDraw.setYscale(0, height);
        // Enables drawing graphics in memory and showing it on the screen only when
		// the StdDraw.show function is called.
		StdDraw.enableDoubleBuffering();
	}

	/** Displays the given image on the current canvas. */
	public static void display(Color[][] image) {
		int height = image.length;
		int width = image[0].length;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				// Sets the pen color to the pixel color
				StdDraw.setPenColor( image[i][j].getRed(),
					                 image[i][j].getGreen(),
					                 image[i][j].getBlue() );
				// Draws the pixel as a filled square of size 1
				StdDraw.filledSquare(j + 0.5, height - i - 0.5, 0.5);
			}
		}
		StdDraw.show();
	}
}

