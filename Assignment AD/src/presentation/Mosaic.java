package presentation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;




public class Mosaic {

	
	private BufferedImage mosaicImage = null;
	private int xSize = 0;
	private int ySize = 0;
	private int moduloX = 32;
	private int moduloY = 32;
	private int brickSize = 2;
	private int plateSizeX;
	private int plateSizeY;
	private int overSizeX;
	private int overSizeY;
	private HashMap<Color,Integer> totalColorCount;
	private HashMap<Color,Integer> colorMatch;
	private int numTiles;
	private int numBricks;
	private HashMap<BrickColors,Integer> totalColor;
	private boolean sizeOk;
	private boolean colorOk;

	
	
	public Mosaic(File f) throws IOException {

		mosaicImage = ImageIO.read(f);
		xSize = mosaicImage.getWidth();
		ySize = mosaicImage.getHeight();
	}


	public Mosaic(BufferedImage img) {
		
		mosaicImage = img;
		xSize = mosaicImage.getWidth();
		ySize = mosaicImage.getHeight();

	}

	
	// it is a "virtual" mosaic to do calculation on sizes
	public Mosaic(int w, int h) {
		
		mosaicImage = null;
		xSize = w;
		ySize = h;
	}
	

	public BufferedImage getImage() {
		return mosaicImage;
	}


	public int getNumTiles() {
		return numTiles;
	}


	public int getNumBricks() {
		return numBricks;
	}


	public int getBrickSize() {
		return brickSize;
	}


	public int getSizeX() {
		return xSize;
	}


	public int getSizeY() {
		return ySize;
	}


	public int getTileX() {
		return plateSizeX;
	}


	public int getTileY() {
		return plateSizeY;
	}
	
	
	public int getOverX() {
		return overSizeX;
	}
	
	
	public int getOverY() {
		return overSizeY;
	}


	public int getNumColor() {
		return totalColorCount.size();
	}
	
	
	public HashMap<Color,Integer> getColorMatch() {
		return colorMatch;
	}
	

	public HashMap<Color,Integer> getOriginalColors() {
		return totalColorCount;
	}
	

	public HashMap<BrickColors,Integer> getMappedColors() {
		return totalColor;
	}
	
	
	public void setMappedColors(HashMap<BrickColors,Integer> mp) {
		totalColor = mp;
	}
	

	public boolean isSizeOk() {
		return sizeOk;
	}
	
	
	public boolean isColorOk() {
		return colorOk;
	}
	
	
	public void setTileSize(int x, int y) {
		
		moduloX = x;
		moduloY = y;
	}
	
	
	public void setBrickSize(int x) {
		
		brickSize = x;
	}
	

	public boolean check() {
		
		sizeOk = false;
		
		plateSizeX = moduloX / brickSize;
		plateSizeY = moduloY / brickSize;
		sizeOk = true;
		if ((moduloX % brickSize) != 0) {
			// if plates cannot be filled with chosen brick
			// i.e. plate stud number isn't divisible by brick size
			sizeOk = false;
		}
		else if ((moduloY % brickSize) != 0) {
			sizeOk = false;
		}
		overSizeX = xSize % plateSizeX;
		overSizeY = ySize % plateSizeY;
		if ((overSizeX) != 0) {
			sizeOk = false;
		}
		else if ((overSizeY) != 0) {
			sizeOk = false;
		}
		numTiles = xSize / plateSizeX * ySize / plateSizeY;
		numBricks = xSize * ySize;
		return sizeOk;
	}


	/* 
	 * counts total colors, tiles, bricks.
	 * returns num of tiles
	 */
	@SuppressWarnings("boxing")
	public int colorCount() {
		
		int xi, yi, worked = 0;
		HashMap<Color,Integer> tileColorCount;
		
		if (mosaicImage == null) {
			return 0;
		}
		
		totalColorCount = new HashMap<Color,Integer>();
		colorMatch = new HashMap<Color, Integer>();

		xi = 0;

        while (xi <= xSize - plateSizeX) {
            yi = ySize;
            while (yi >= plateSizeY) {
                worked++;
                BufferedImage tile = mosaicImage.getSubimage(xi, yi - plateSizeY, plateSizeX, plateSizeY);
                // count colors for pixels
        		tileColorCount = new HashMap<Color,Integer>();

                for (int x=0;x<plateSizeX;x++) {
                	for (int y=0;y<plateSizeY;y++) {
                		Color c = new Color(tile.getRGB(x, y),true);
                		if (tileColorCount.containsKey(c)) {
                			// color already seen
                			int i = tileColorCount.get(c);
                			i++;
                			tileColorCount.put(c, i);
                		}
                		else {
                			// first seen
                			tileColorCount.put(c, 1);
                			// set automatically nearest LDD color
                			colorMatch.put(c, BrickColors.getNearestColor(c).ldd);
                		}
                	}
                	if (tileColorCount.size() > 100) {
                		colorOk = false;
                		return 0;
                	}
                }
                // updates total color counts
                for (Color c : tileColorCount.keySet()) {
        			int i = tileColorCount.get(c);
            		if (totalColorCount.containsKey(c)) {
            			// color already seen
            			i += totalColorCount.get(c);
            			totalColorCount.put(c, i);
            		}
            		else {
            			// first seen
            			totalColorCount.put(c, i);
            		}	
                }
                yi -= plateSizeY;
            }
            xi += plateSizeX;
        }
        colorOk = true;
        return worked;
	}
	
	

	
	
	
}
