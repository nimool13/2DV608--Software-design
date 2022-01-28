package application;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JLabel;


public class ImageView extends JComponent {

	private static final long serialVersionUID = 3559417615228891685L;
	private BufferedImage image;
	private JLabel scaleLabel;
	private int w;
	private int h;
	private double scale = 1.0;
	private int iw;
	private int ih;
	private Image imgScaled;

	
	
	public ImageView(BufferedImage img, int prefwidth, int prefheight) {
		
		image = img;
		w = prefwidth;
		h = prefheight;
		setPreferredSize(new Dimension(w, h));
		iw = image.getWidth();
		ih = image.getHeight();
	}

	
	public void setImage(BufferedImage img) {
		image = img;
		iw = image.getWidth();
		ih = image.getHeight();
		repaint();
	}
	
	
	public int getScale() {
		
		return (int) Math.round(100.0/scale);
	}
	
	
	public void setScaleLabel(JLabel sl) {
		
		scaleLabel = sl;
	}
	
	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		w = getVisibleRect().width - getInsets().left - getInsets().right;
		h = getVisibleRect().height - getInsets().top - getInsets().bottom;
		scale = Math.max((double)iw/w, (double)ih/h);
		if (scale > 1.0) {
			imgScaled = image.getScaledInstance((int) (iw / scale),(int) (ih / scale),Image.SCALE_SMOOTH);
		}
		else {
			scale = 1.0;
			imgScaled = image.getScaledInstance(iw,ih,Image.SCALE_FAST);
		}
		int offsX = getInsets().left,offsY = getInsets().top;
		if (imgScaled.getWidth(null) < w) {
			offsX = getInsets().left + (w - imgScaled.getWidth(null)) / 2;
		}
		if (imgScaled.getHeight(null) < h) {
			offsY = getInsets().top + (h - imgScaled.getHeight(null)) / 2;
		}
		g.drawImage(imgScaled, offsX, offsY, null);
		if (scaleLabel != null)
			scaleLabel.setText(getScale()+"%");
	}
}
