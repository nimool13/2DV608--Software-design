package data;

import presentation.Mosaic;
import application.ImageView;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;



public class ImageRescaleDialog extends JDialog implements ActionListener {


	private static final long serialVersionUID = 7189193964441370509L;
	private JButton okButton;
	private JButton cancelButton;
	private BufferedImage image,workImage;
	private JComboBox tileCombo;
	private JComboBox brickCombo;
	private JTextField sizex;
	private JTextField sizey;
	private int userChoice = JOptionPane.CANCEL_OPTION;
	private JButton topButton;
	private ImageView imagebox;
	private JButton leftButton;
	private JButton rightButton;
	private JButton bottomButton;
	private JLabel scaleLabel;
	private JLabel tilesLab;
	private JLabel bricksLab;
	private JLabel sizeLab;
	private JLabel labOverX;
	private JLabel labOverY;
	private JButton hButton;
	private JButton wButton;
	private int overX;
	private int overY;
	private JButton resetButton;
	private JLabel metersLab;


	
	public ImageRescaleDialog(Frame owner, String title, boolean modal, BufferedImage img) {
		super(owner, title, modal);
		
		image = img;
		createDialog();
	}


	
	
	private void createDialog() {
		
		// really create the dialog
		//setPreferredSize(new Dimension(800,500));
		setLocationByPlatform(true);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout(2,2));
		
		// center image
		JPanel imagePane = new JPanel();
		imagePane.setLayout(new GridBagLayout());
		contentPane.add(imagePane,BorderLayout.CENTER);
		
		// controls and buttons
		JPanel controlsPane = new JPanel();
		controlsPane.setLayout(new GridBagLayout());
		contentPane.add(controlsPane,BorderLayout.WEST);
		controlsPane.setBorder(BorderFactory.createTitledBorder("Scale controls"));
		
		ButtonAndGbc();

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		contentPane.add(buttonPane, BorderLayout.SOUTH);

		okButton = new JButton("OK");
		buttonPane.add(okButton);
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);

		cancelButton = new JButton("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(this);

		workImage = new BufferedImage(image.getWidth(),image.getHeight(),image.getType()); 
		workImage.setData(image.getData());
		ButtonAndGbc();
		recalc();
		pack();
	}
	public void ButtonAndGbc(){
		JPanel controlsPane = new JPanel();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.ipady = 2;
		gbc.ipadx = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;


		// setup controls pane

		// X size
		gbc.gridx = 0;
		gbc.gridy = 0;
		controlsPane.add(new JLabel("Size X: ",SwingConstants.RIGHT), gbc);
		gbc.gridx = 1;
		sizex = new JTextField(Integer.toString(image.getWidth()));
		sizex.addActionListener(this);
		controlsPane.add(sizex,gbc);

		// Y size
		gbc.gridx = 0;
		gbc.gridy += 1;
		controlsPane.add(new JLabel("Size Y: ",SwingConstants.RIGHT), gbc);
		gbc.gridx = 1;
		sizey = new JTextField(Integer.toString(image.getHeight()));
		sizey.addActionListener(this);
		controlsPane.add(sizey,gbc);

		// tile settings
		gbc.gridx = 0;
		gbc.gridy += 1;
		controlsPane.add(new JLabel("Tile size (stud): ",SwingConstants.RIGHT), gbc);
		gbc.gridx = 1;
		tileCombo = new JComboBox();
		tileCombo.addItem("8x8");
		tileCombo.addItem("16x16");
		tileCombo.addItem("32x32");
		tileCombo.addItem("48x48");
		tileCombo.setSelectedIndex(2);
		controlsPane.add(tileCombo,gbc);
		tileCombo.addActionListener(this);

		// brick settings
		gbc.gridx = 0;
		gbc.gridy += 1;
		controlsPane.add(new JLabel("Brick size (stud): ",SwingConstants.RIGHT), gbc);
		gbc.gridx = 1;
		brickCombo = new JComboBox();
		brickCombo.addItem("1x1 (3005)");
		brickCombo.addItem("2x2 (3003)");
		brickCombo.setSelectedIndex(1);
		brickCombo.addActionListener(this);
		controlsPane.add(brickCombo,gbc);

		gbc.gridy += 1;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		controlsPane.add(new JSeparator(),gbc);

		gbc.gridwidth = 1;
		gbc.gridy += 1;
		gbc.gridx = 0;
		controlsPane.add(new JLabel("Total tiles: ",SwingConstants.RIGHT), gbc);
		gbc.gridx = 1;
		tilesLab = new JLabel("-");
		controlsPane.add(tilesLab,gbc);

		gbc.gridy += 1;
		gbc.gridx = 0;
		controlsPane.add(new JLabel("Total bricks:",SwingConstants.RIGHT), gbc);
		gbc.gridx = 1;
		bricksLab = new JLabel("-");
		controlsPane.add(bricksLab,gbc);

		gbc.gridy += 1;
		gbc.gridx = 0;
		controlsPane.add(new JLabel("Approx. size (m):",SwingConstants.RIGHT), gbc);
		gbc.gridx = 1;
		metersLab = new JLabel("-");
		controlsPane.add(metersLab,gbc);


		gbc.gridx = 0;
		gbc.gridy += 1;
		gbc.gridwidth = 2;
		sizeLab = new JLabel("Size: -",SwingConstants.CENTER);
		controlsPane.add(sizeLab,gbc);
		gbc.gridwidth = 1;

		gbc.gridx = 0;
		gbc.gridy += 1;
		controlsPane.add(new JLabel("Oversize X: ",SwingConstants.RIGHT), gbc);
		labOverX = new JLabel("0",SwingConstants.CENTER);
		gbc.gridx = 1;
		controlsPane.add(labOverX,gbc);

		gbc.gridx = 0;
		gbc.gridy += 1;
		controlsPane.add(new JLabel("Oversize Y: ",SwingConstants.RIGHT), gbc);
		labOverY = new JLabel("0",SwingConstants.CENTER);
		gbc.gridx = 1;
		controlsPane.add(labOverY,gbc);

		gbc.gridy += 1;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		controlsPane.add(new JSeparator(),gbc);

		gbc.gridx = 0;
		gbc.gridy += 1;
		resetButton = new JButton("Reset changes");
		resetButton.addActionListener(this);
		controlsPane.add(resetButton, gbc);
		gbc.gridwidth = 1;

		// setup image pane

	imageSetter();
	}

	public void imageSetter() {
		JPanel imagePane = new JPanel();
		imagePane.setBorder(BorderFactory.createTitledBorder("Image preview/cut"));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0.01;
		gbc.weighty = 0.01;
		gbc.fill = GridBagConstraints.BOTH;

		gbc.gridy = 0;
		gbc.gridx = 0;
		gbc.gridwidth = 3;
		gbc.gridheight = 3;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		imagebox = new ImageView(image,500,400);
		imagePane.add(imagebox, gbc);

		gbc.weightx = 0.01;
		gbc.weighty = 0.01;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;

		gbc.gridx = 3;
		gbc.gridy = 0;
		topButton = new JButton(new ImageIcon(IBrick.class.getResource("images/cut-top.png")));
		topButton.setToolTipText("Cut top");
		topButton.addActionListener(this);
		imagePane.add(topButton,gbc);

		gbc.gridy = 1;
		hButton = new JButton(new ImageIcon(IBrick.class.getResource("images/cut-height.png")));
		hButton.setToolTipText("Cut top+bottom");
		hButton.addActionListener(this);
		imagePane.add(hButton,gbc);

		gbc.gridy = 2;
		bottomButton = new JButton(new ImageIcon(IBrick.class.getResource("images/cut-bottom.png")));
		bottomButton.setToolTipText("Cut bottom");
		bottomButton.addActionListener(this);
		imagePane.add(bottomButton,gbc);

		gbc.gridy = 3;
		gbc.gridx = 0;
		leftButton = new JButton(new ImageIcon(IBrick.class.getResource("images/cut-left.png")));
		leftButton.setToolTipText("Cut left");
		leftButton.addActionListener(this);
		imagePane.add(leftButton,gbc);

		gbc.gridx = 1;
		wButton = new JButton(new ImageIcon(IBrick.class.getResource("images/cut-width.png")));
		wButton.setToolTipText("Cut left+right");
		wButton.addActionListener(this);
		imagePane.add(wButton,gbc);

		gbc.gridx = 2;
		rightButton = new JButton(new ImageIcon(IBrick.class.getResource("images/cut-right.png")));
		rightButton.setToolTipText("Cut right");
		rightButton.addActionListener(this);
		imagePane.add(rightButton,gbc);

		gbc.gridy = 3;
		gbc.gridx = 3;
		scaleLabel = new JLabel();
		imagePane.add(scaleLabel,gbc);
		imagebox.setScaleLabel(scaleLabel);

	}


	@SuppressWarnings("boxing")
	private void recalc() {
		
		int x,y;
		
		x = workImage.getWidth();
		y = workImage.getHeight();
		Mosaic m = new Mosaic(x,y);
		sizex.setText(Integer.toString(x));
		sizey.setText(Integer.toString(y));
		switch (tileCombo.getSelectedIndex()) {
		case 0:
			m.setTileSize(8, 8);
			break;
		case 1:
			m.setTileSize(16, 16);
			break;
		case 2:
			m.setTileSize(32, 32);
			break;
		case 3:
			m.setTileSize(48, 48);
			break;
		}
		// for mosaic real size
		float mm = 0f;
		switch (brickCombo.getSelectedIndex()) {
		case 0:
			mm = 0.008f;
			m.setBrickSize(1);
			break;
		case 1:
			mm = 0.016f;
			m.setBrickSize(2);
			break;
		}
		m.check();
		if (m.isSizeOk()) {
			sizeLab.setText("<html>Size: <span style='color:green;font-weight:bold'>OK</span>");
		}
		else {
			sizeLab.setText("<html>Size: <span style='color:red;font-weight:bold'>Mismatch</span>");
		}
		tilesLab.setText(Integer.toString(m.getNumTiles()));
		bricksLab.setText(Integer.toString(m.getNumBricks()));
		metersLab.setText(String.format("%4.1fx%4.1f",x*mm,mm*y));
		overX = m.getOverX();
		overY = m.getOverY();
		labOverX.setText(Integer.toString(m.getOverX()));
		labOverY.setText(Integer.toString(m.getOverY()));
		if (m.getOverX() != 0) {
			leftButton.setEnabled(true);
			rightButton.setEnabled(true);
			wButton.setEnabled(true);
		}
		else {
			leftButton.setEnabled(false);
			rightButton.setEnabled(false);
			wButton.setEnabled(false);
		}
		if (m.getOverY() != 0) {
			topButton.setEnabled(true);
			bottomButton.setEnabled(true);
			hButton.setEnabled(true);
		}
		else {
			topButton.setEnabled(false);
			bottomButton.setEnabled(false);
			hButton.setEnabled(false);
		}
	}
	
	
	public int getBrickSize() {
		return brickCombo.getSelectedIndex();
	}
	
	
	public int getTileSize() {
		return tileCombo.getSelectedIndex();
	}
	

	public int getResponse() {
		return userChoice;
	}

	
	public BufferedImage getImage() {
		return workImage;
	}
	

	@Override
	public void actionPerformed(ActionEvent ev) {
		
		if (ev.getSource() == okButton) {
			setVisible(false);
			userChoice = JOptionPane.OK_OPTION;
		}
		else if (ev.getSource() == cancelButton) {
			setVisible(false);
			userChoice = JOptionPane.CANCEL_OPTION;
		}
		else if (ev.getSource() == brickCombo ||
				ev.getSource() == tileCombo) {
			recalc();
		}
		else if (ev.getSource() == sizex) {
			int x = 0, y = 0;
			try {
				x = Integer.parseInt(sizex.getText());
			}
			catch (NumberFormatException e) {
				x = 0;
			}
			if (x <= 0) 
				return;
			double scale = (double) x / image.getWidth();
			y = (int) (image.getHeight() * scale);
			workImage = new BufferedImage(x,y,image.getType()); 
//			BufferedImage tmpImg = new BufferedImage(x,y,BufferedImage.TYPE_INT_ARGB);
//			AffineTransformOp at = new AffineTransformOp(AffineTransform.getScaleInstance(scale, scale),
//					AffineTransformOp.TYPE_BICUBIC);
			// no-> there is a bug in Java... bah... 
			// http://stackoverflow.com/questions/17343518/batik-fails-on-transformations-since-jre-1-7-0-25
			// this gives an exception...
			// tmpImg = at.createCompatibleDestImage(workImage, workImage.getColorModel());
//			workImage = at.filter(workImage, tmpImg);
			// getScaledInstance gives a better result of AffineTransformOp...
			workImage.getGraphics().drawImage(image.getScaledInstance(x, y, Image.SCALE_SMOOTH),0,0,null);
			imagebox.setImage(workImage);
			recalc();
		}
		else if (ev.getSource() == sizey) {
			int x = 0, y = 0;
			try {
				y = Integer.parseInt(sizey.getText());
			}
			catch (NumberFormatException e) {
				y = 0;
			}
			if (y <= 0) 
				return;
			double scale = (double) y / image.getHeight();
			x = (int) (image.getWidth() * scale);
			workImage = new BufferedImage(x,y,image.getType()); 
			workImage.getGraphics().drawImage(image.getScaledInstance(x, y, Image.SCALE_SMOOTH),0,0,null);
			imagebox.setImage(workImage);
			recalc();
		}
		else if (ev.getSource() == topButton) {
			workImage = workImage.getSubimage(0, overY, workImage.getWidth(), workImage.getHeight()-overY);
			imagebox.setImage(workImage);
			recalc();
		}
		else if (ev.getSource() == bottomButton) {
			workImage = workImage.getSubimage(0, 0, workImage.getWidth(), workImage.getHeight()-overY);
			imagebox.setImage(workImage);
			recalc();
		}
		else if (ev.getSource() == hButton) {
			int h = overY / 2;
			workImage = workImage.getSubimage(0, h, workImage.getWidth(), workImage.getHeight()-overY);
			imagebox.setImage(workImage);
			recalc();
		}
		else if (ev.getSource() == leftButton) {
			workImage = workImage.getSubimage(overX, 0, workImage.getWidth()-overX, workImage.getHeight());
			imagebox.setImage(workImage);
			recalc();
		}
		else if (ev.getSource() == rightButton) {
			workImage = workImage.getSubimage(0, 0, workImage.getWidth()-overX, workImage.getHeight());
			imagebox.setImage(workImage);
			recalc();
		}
		else if (ev.getSource() == wButton) {
			int w = overX / 2;
			workImage = workImage.getSubimage(w, 0, workImage.getWidth()-overX, workImage.getHeight());
			imagebox.setImage(workImage);
			recalc();
		}
		else if (ev.getSource() == resetButton) {
			workImage = image.getSubimage(0, 0, image.getWidth(), image.getHeight());
			imagebox.setImage(workImage);
			recalc();
		}
	}
	
}
