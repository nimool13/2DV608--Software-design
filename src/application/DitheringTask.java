package application;

import presentation.BrickColors;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.SwingWorker;



public class DitheringTask extends SwingWorker<BufferedImage, Void> {

	
	private HashMap<Integer, BrickColors> plt;
	private BufferedImage image;
	private int ditherMode;


	// some code from http://commons.apache.org/proper/commons-imaging/
	// from class org.apache.commons.imaging.palette.Dithering

	
	public DitheringTask(BufferedImage img, HashMap<Integer, BrickColors> plt, int dither) {
		
		image = img;
		this.ditherMode = dither;
		this.plt = plt;
	}
	
    private static int errorFloydSteinberg(final int argb, final int errA, final int errR, final int errG, final int errB, final int mul) {
        int a = (argb >> 24) & 0xff;
        int r = (argb >> 16) & 0xff;
        int g = (argb >> 8) & 0xff;
        int b = argb & 0xff;

        a += errA * mul / 16;
        r += errR * mul / 16;
        g += errG * mul / 16;
        b += errB * mul / 16;

        if (a < 0) {
            a = 0;
        } else if (a > 0xff) {
            a = 0xff;
        }
        if (r < 0) {
            r = 0;
        } else if (r > 0xff) {
            r = 0xff;
        }
        if (g < 0) {
            g = 0;
        } else if (g > 0xff) {
            g = 0xff;
        }
        if (b < 0) {
            b = 0;
        } else if (b > 0xff) {
            b = 0xff;
        }

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

	
    private static int errorDiffusion(final int argb, final int errA, final int errR, final int errG, final int errB, final int mul) {
        int a = (argb >> 24) & 0xff;
        int r = (argb >> 16) & 0xff;
        int g = (argb >> 8) & 0xff;
        int b = argb & 0xff;

        a += errA * mul / 4;
        r += errR * mul / 4;
        g += errG * mul / 4;
        b += errB * mul / 4;

        if (a < 0) {
            a = 0;
        } else if (a > 0xff) {
            a = 0xff;
        }
        if (r < 0) {
            r = 0;
        } else if (r > 0xff) {
            r = 0xff;
        }
        if (g < 0) {
            g = 0;
        } else if (g > 0xff) {
            g = 0xff;
        }
        if (b < 0) {
            b = 0;
        } else if (b > 0xff) {
            b = 0xff;
        }

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

	


	
	
	@Override
	protected BufferedImage doInBackground() {
		
    	for (int y = 0; y < image.getHeight(); y++) {
    		setProgress(y*100/image.getHeight());
            for (int x = 0; x < image.getWidth(); x++) {
                final int argb = image.getRGB(x, y);
                final int nextArgb = BrickColors.getNearestColorFromPalette(new Color(argb), plt).color.getRGB();
                image.setRGB(x, y, nextArgb);
                if (ditherMode == 0)
                	continue;
                final int a = (argb >> 24) & 0xff;
                final int r = (argb >> 16) & 0xff;
                final int g = (argb >> 8) & 0xff;
                final int b = argb & 0xff;
                
                final int na = (nextArgb >> 24) & 0xff;
                final int nr = (nextArgb >> 16) & 0xff;
                final int ng = (nextArgb >> 8) & 0xff;
                final int nb = nextArgb & 0xff;

                final int errA = a - na;
                final int errR = r - nr;
                final int errG = g - ng;
                final int errB = b - nb;

                switch (ditherMode) {
                case 1:
                	// error diffusion
                	//  *  2
                	//  1  1
                	if (x + 1 < image.getWidth()) {
                        int update = errorDiffusion(image.getRGB(x + 1, y), errA, errR, errG, errB, 2);
                        image.setRGB(x + 1, y, update);
                        if (y + 1 < image.getHeight()) {
                            update = errorDiffusion(image.getRGB(x + 1, y + 1), errA, errR, errG, errB, 1);
                            image.setRGB(x + 1, y + 1, update);
                        }
                    }
                    if (y + 1 < image.getHeight()) {
                        int update = errorDiffusion(image.getRGB(x, y + 1), errA, errR, errG, errB, 1);
                        image.setRGB(x, y + 1, update);
                	}
                	break;
                case 2:
                	// Floyd-Steinberg
                	//  -  *  7
                	//  3  5  1
                    if (x + 1 < image.getWidth()) {
                        int update = errorFloydSteinberg(image.getRGB(x + 1, y), errA, errR, errG, errB, 7);
                        image.setRGB(x + 1, y, update);
                        if (y + 1 < image.getHeight()) {
                            update = errorFloydSteinberg(image.getRGB(x + 1, y + 1), errA, errR, errG, errB, 1);
                            image.setRGB(x + 1, y + 1, update);
                        }
                    }
                    if (y + 1 < image.getHeight()) {
                        int update = errorFloydSteinberg(image.getRGB(x, y + 1), errA, errR, errG, errB, 5);
                        image.setRGB(x, y + 1, update);
                        if (x - 1 >= 0) {
                            update = errorFloydSteinberg(image.getRGB(x - 1, y + 1), errA, errR, errG, errB, 3);
                            image.setRGB(x - 1, y + 1, update);
                        }

                    }
                    break;
                }
            }
        }
    	setProgress(100);
		return image;
	}

}
