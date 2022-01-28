package presentation;/*
	Copyright 2013-2014 Mario Pascucci <mpascucci@gmail.com>
	This file is part of BrickMosaic.

	BrickMosaic is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	BrickMosaic is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with BrickMosaic.  If not, see <http://www.gnu.org/licenses/>.

*/





import java.awt.Color;
import java.awt.color.ColorSpace;
import java.util.HashMap;



/* 
 * A brick "color" in LDD, BL and LDraw catalogs, with helpers for display 
 * color sample and conversions between catalogs 
 */



public class BrickColors {
	public int ldd;		// master ID for color
	public int bl;		// BrickLink color id
	public Color color;
	public boolean inProduction;
	public String lddName;
	private static ColorSpace cs_xyz = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);


	private static HashMap<Integer,BrickColors> allColor = new HashMap<Integer,BrickColors>();
	
	@Override
	public String toString() {
		return "BrickColor [ldd=" + ldd + ", color=" + color
				+ ", inProduction=" + inProduction
				+ ", lddName=" + lddName
				+ "]";
	}

	
	public BrickColors() {

		color = Color.BLACK;
		inProduction = false;
		lddName = "";
	}
	
	public BrickColors(BrickColors bc) {
		
		ldd = bc.ldd;
		color = new Color(bc.color.getRed(),
				bc.color.getGreen(),
				bc.color.getBlue(),
				bc.color.getAlpha());
		inProduction = bc.inProduction;
		lddName = bc.lddName;
	}

	
	@SuppressWarnings("boxing")
	public static void addLddColor(int ldd, BrickColors bc) {
		
		allColor.put(ldd, bc);
	}
	
	
	@SuppressWarnings("boxing")
	public static BrickColors getLddColor(int idx) {
		
		return allColor.get(idx);
	}

	
	public static HashMap<Integer,BrickColors> getAllColors() {
		
		return allColor;
	}
	
	
	
	/*
	 * from http://www.easyrgb.com/index.php?X=MATH&H=02#text2
	 */
//	private static float[] RGB2XYZ(Color c) {
		
//		float r,g,b;
//		
//		r = (c.getRed()/256.0f);
//		g = (float) (c.getGreen()/256.0);
//		b = (float) (c.getBlue()/256.0);
//		return cs_xyz.fromRGB(new float[] {c.getRed()/256.0f,c.getGreen()/256.0f,c.getBlue()/256.0f});
//		if (r > 0.04045) {
//			r = Math.pow((r + 0.055) / 1.055 , 2.4);
//		}
//		else {
//			r = r / 12.92;
//		}
//		if (g > 0.04045) {
//			g = Math.pow((g + 0.055) / 1.055, 2.4);
//		}
//		else {
//			g = g / 12.92;
//		}
//		if (b > 0.04045) {
//			b = Math.pow((b + 0.055) / 1.055, 2.4);
//		}
//		else {
//			b = b / 12.92;
//		}
//		r = r * 100;
//		g = g * 100;
//		b = b * 100;
//		
//		//Observer. = 2°, Illuminant = D65
//		X = r * 0.4124 + g * 0.3576 + b * 0.1805;
//		Y = r * 0.2126 + g * 0.7152 + b * 0.0722;
//		Z = r * 0.0193 + g * 0.1192 + b * 0.9505;
//		return new Double[] {X,Y,Z};
//	}
	

	/* 
	 * to transform RGB color in CIE/Lab color 
	 * uses RGB2XYZ
	 * from:
	 * http://www.easyrgb.com/index.php?X=MATH&H=07#text7
	 * http://www.emanueleferonato.com/2009/08/28/color-differences-algorithm/
	 */
	private static double[] RGB2Lab(Color c) {
		
		float X[];
		double x,y,z,l,a,b;
		
		X = cs_xyz.fromRGB(new float[] {c.getRed()/256.0f,c.getGreen()/256.0f,c.getBlue()/256.0f});
		//X = RGB2XYZ(c);
		x = X[0]/95.047;
		y = X[1]/100.0;
		z = X[2]/108.883;
		 
		// adjusting the values
		if (x > 0.008856) {
		     x = Math.pow(x,1.0/3.0);
		}
		else {
		     x = 7.787 * x + 16.0/116.0;
		}
		if (y > 0.008856) {
		     y = Math.pow(y,1.0/3.0);
		}
		else {
		     y = (7.787 * y) + (16.0/116.0);
		}
		if (z > 0.008856) {
		     z = Math.pow(z,1.0/3.0);
		}
		else {
		     z = 7.787 * z + 16.0/116.0;
		}
		
		l = 116 * y -16;
		a = 500 * (x-y);
		b = 200 * (y-z);
		
		return new double[] {l,a,b};
	}
	
	
	/*
	 * to compute "color distance" between two rgb sample
	 * uses RGB2Lab
	 * from: 
	 * http://www.easyrgb.com/index.php?X=DELT&H=04#text4
	 * http://www.emanueleferonato.com/2009/09/08/color-difference-algorithm-part-2/
	 */
	private static double colorDiff_DE1994(Color uno, Color due) {
		
		double c1,c2,dc,dl,da,db,dh,first,second,third,c[],d[];
		
		c = RGB2Lab(uno);
		d = RGB2Lab(due);
	    c1 = Math.sqrt(c[1]*c[1]+c[2]*c[2]);
	    c2 = Math.sqrt(d[1]*d[1]+d[2]*d[2]);
	    dc = c1-c2;
	    dl = c[0]-d[0];
	    da = c[1]-d[1];
	    db = c[2]-d[2];
	    dh = (da*da)+(db*db)-(dc*dc);
	    if (dh > 0) {
	    	dh = Math.sqrt(dh);
	    }
	    else {
	    	dh = 0.0;
	    }
	    first = dl;
	    second = dc/(1+0.045*c1);
	    third = dh/(1+0.015*c1);
	    return(Math.sqrt(first*first+second*second+third*third));
	}
    

		
	// RGB colorspace, even with linear color sensitivity correction is really different from human eye
    // color perception. See http://en.wikipedia.org/wiki/List_of_color_spaces_and_their_uses

	@SuppressWarnings("boxing")
	public static BrickColors getNearestColor(Color color) {
        
    	double minDist,dist;
    	BrickColors bc,nc=null;
    	
    	
        minDist = 1.0e9;
        for (int b : allColor.keySet()) {
        	bc = allColor.get(b);
        	if (!bc.inProduction)
        		continue;
        	dist = colorDiff_DE1994(color, bc.color);
//            if (bc.ldd == 5 || bc.ldd == 208) { 
//            	System.out.println("Name: "+bc.lddName+" "+dist+" "+bc.color);
//            }
            if (minDist > dist) {
                nc = bc;
                minDist = dist;
            }
        }
        return nc;        
    }

	
	
    @SuppressWarnings("boxing")
	public static BrickColors getNearestColorFromPalette(Color color, HashMap<Integer,BrickColors> plt) {
        
    	double minDist,dist;
    	BrickColors bc,nc=null;
    	
        minDist = 1.0e9;
        for (int b : plt.keySet()) {
        	bc = plt.get(b);
        	if (!bc.inProduction)
        		continue;
        	dist = colorDiff_DE1994(color, bc.color);
            if (minDist > dist) {
                nc = bc;
                minDist = dist;
            }
        }
        return nc;
    }

	

	
	
}
