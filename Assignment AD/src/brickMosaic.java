/*
	Copyright 2014 Mario Pascucci <mpascucci@gmail.com>
	This file is part of BrickMosaic

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




import presentation.BlExportDialog;
import presentation.BrickColors;
import data.IBrick;
import presentation.Mosaic;
import presentation.SmartFileChooser;
import data.*;
import data.ColorReduceDialog;
import application.PaletteDialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;





/*
 * 
 * done: About dialog
 * done: original image info
 * done: color names
 * done: put only square baseplates (no rectangular ever in use)
 * done: checks for size and offers the ability to "cut" top, bottom, left or right (or half and half)
 * done: use SwingWorker to do a progress bar
 * done: manually choose color pairing
 * done: LDD->Bricklink color codes
 * done: generates a Bricklink mass upload
 * done: generates an image for color reduction palette sample
 * 
 */



/**
 * @author mario
 *
 * Jar dependency: 
 * - xerces-j2 1.4.x
 * - xml-commons-apis
 * from odftoolkit project (http://incubator.apache.org/odftoolkit/)
 * - xslt-runner
 * - xslt-runner-task
 * - simple-odf 0.8
 * - odfdom-java 0.8.7 (do NOT use 0.8.9, it carries a useless dependency from Apache Clerezza)
 * 
 */
public class brickMosaic implements ActionListener, IBrick {

	
	private JFileChooser fileImg;
	private SmartFileChooser fileOdt;
	private SmartFileChooser fileXml;
	private JFrame frame;
	private JPanel brickToolPane;
	private JToolBar brickListTool;
	private JButton btnLoadImg;
	private JButton btnWriteInstr;
	private JMenu mnProgram;
	private JMenuItem mntmAbout;
	private JMenuItem mntmExit;
	private JComboBox moduleSizeCombo;
	private JComboBox brickSizeCombo;
	private JLabel moduleCheck;
	private Mosaic mosaic = null;
	private JLabel imgPreview;
	private double gamma = 1/1.1;
	private JLabel imgSizeX;
	private JLabel imgSizeY;
	private JLabel imgColors;
	private JLabel imgTiles;
	private JLabel imgBricks;
	private JButton btnColorMatch;
	private JButton btnWriteBlXml;
	private ImageIcon[] icnImg = new ImageIcon[4];
	private JButton btnPalette;
	private SmartFileChooser filePalette;
	private JButton btnPrepImg;
	private SmartFileChooser fileMosaicImage;
	private JLabel metersLab;
	private float brickSizemm = 0.016f;
	private JPanel workingPane ;
	private JPanel workingDetails;
	private GridBagConstraints gbc;
	private JPanel mosaicSettings;

	public brickMosaic() {
		
		initialize();
		
	}

	
	/**
	 * 
	 */
	public void initialize() {
		fileSetter();
		frameSetting ();
		workingPane = new JPanel();
		Container masterPane = frame.getContentPane();

		masterPane.add(workingPane);


 		/* *******************************************
 		 * pane setup
 		 *********************************************/

		workingPane.setLayout(new BorderLayout(0, 0));
		workingDetails = new JPanel();
		workingPane.add(workingDetails,BorderLayout.SOUTH);
		imgPreview = new JLabel(new ImageIcon(brickMosaic.class.getResource("images/empty-image.png")));
		imgPreview.setPreferredSize(new Dimension(450, 300));
		workingPane.add(imgPreview,BorderLayout.CENTER);

		brickToolPane = new JPanel();
		brickToolPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		workingPane.add(brickToolPane, BorderLayout.NORTH);

		// Brick list import tools
		brickToolPanel();

		mosaicTool();

		gbc.gridx = 0;
		gbc.gridwidth = 4;
		gbc.gridy = 2;
		JSeparator js = new JSeparator();
		mosaicSettings.add(js, gbc);
		gbc.gridwidth = 1;

		// module status and warnings
		gbc.gridy = 3;
		gbc.gridx = 0;
		JLabel labModuleCk = new JLabel("Module check: ", SwingConstants.RIGHT);
		mosaicSettings.add(labModuleCk, gbc);
		gbc.gridx = 1;
		gbc.gridwidth = 4;
		moduleCheck = new JLabel("-");
		mosaicSettings.add(moduleCheck, gbc);
		gbc.gridwidth = 1;

		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		workingDetails.add(mosaicSettings,gbc);

		// image info
		JPanel imageInfo = new JPanel();
		imageInfo.setBorder(BorderFactory.createTitledBorder("Image info"));
		imageInfo.setLayout(new GridBagLayout());

		// image size
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		JLabel imgLab = new JLabel("Image size X: ", SwingConstants.RIGHT);
		imageInfo.add(imgLab, gbc);
		imgSizeX = new JLabel("0");
		gbc.gridx = 1;
		imageInfo.add(imgSizeX,gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		imgLab = new JLabel("Image size Y: ", SwingConstants.RIGHT);
		imageInfo.add(imgLab, gbc);
		imgSizeY = new JLabel("0");
		gbc.gridx = 1;
		imageInfo.add(imgSizeY,gbc);

		// image colors
		gbc.gridx = 0;
		gbc.gridy = 2;
		imgLab = new JLabel("Colors: ", SwingConstants.RIGHT);
		imageInfo.add(imgLab, gbc);
		imgColors = new JLabel("0");
		gbc.gridx = 1;
		imageInfo.add(imgColors,gbc);

		// total tiles
		gbc.gridx = 0;
		gbc.gridy = 3;
		imgLab = new JLabel("Tiles: ", SwingConstants.RIGHT);
		imageInfo.add(imgLab, gbc);
		imgTiles = new JLabel("0");
		gbc.gridx = 1;
		imageInfo.add(imgTiles,gbc);

		// total bricks
		gbc.gridx = 0;
		gbc.gridy = 4;
		imgLab = new JLabel("Bricks: ", SwingConstants.RIGHT);
		imageInfo.add(imgLab, gbc);
		imgBricks = new JLabel("0");
		gbc.gridx = 1;
		imageInfo.add(imgBricks,gbc);

		gbc.gridy += 1;
		gbc.gridx = 0;
		imageInfo.add(new JLabel("Approx. size (m):",SwingConstants.RIGHT), gbc);
		gbc.gridx = 1;
		metersLab = new JLabel("-");
		imageInfo.add(metersLab,gbc);


		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		gbc.gridx = 1;
		gbc.gridy = 0;
		workingDetails.add(imageInfo,gbc);
		
		icnImg[0] = new ImageIcon(brickMosaic.class.getResource("images/f0.png"));
		icnImg[1] = new ImageIcon(brickMosaic.class.getResource("images/f1.png"));
		icnImg[2] = new ImageIcon(brickMosaic.class.getResource("images/f2.png"));
		icnImg[3] = new ImageIcon(brickMosaic.class.getResource("images/f3.png"));


		btnColorMatch.setEnabled(false);
		btnWriteInstr.setEnabled(false);
		btnWriteBlXml.setEnabled(false);

		frame.pack();
		readColors();
	}

	private void mosaicTool() {

		workingDetails.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		mosaicSettings = new JPanel();
		mosaicSettings.setBorder(BorderFactory.createTitledBorder("Mosaic setup"));
		mosaicSettings.setLayout(new GridBagLayout());
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.ipady = 2;
		gbc.ipadx = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;

		// module settings
		gbc.gridx = 0;
		gbc.gridy = 0;
		JLabel labModuleX = new JLabel("Tile size (stud): ",SwingConstants.RIGHT);
		mosaicSettings.add(labModuleX, gbc);
		gbc.gridx = 1;
		moduleSizeCombo = new JComboBox();
		moduleSizeCombo.addItem("8x8");
		moduleSizeCombo.addItem("16x16");
		moduleSizeCombo.addItem("32x32");
		moduleSizeCombo.addItem("48x48");
		moduleSizeCombo.setSelectedIndex(2);
		mosaicSettings.add(moduleSizeCombo,gbc);
		moduleSizeCombo.addActionListener(this);

		// brick settings
		gbc.gridx = 0;
		gbc.gridy = 1;
		JLabel labBrickSize = new JLabel("Brick size (stud): ",SwingConstants.RIGHT);
		mosaicSettings.add(labBrickSize, gbc);
		gbc.gridx = 1;
		brickSizeCombo = new JComboBox();
		brickSizeCombo.addItem("1x1 (3005)");
		brickSizeCombo.addItem("2x2 (3003)");
		brickSizeCombo.setSelectedIndex(1);
		brickSizeCombo.addActionListener(this);
		mosaicSettings.add(brickSizeCombo,gbc);

	}

	private void frameSetting() {
		frame = new JFrame();
		JMenuBar menuBar;


		frame.setTitle("BrickMosaic - Brick Mosaic Generator");
		frame.setLocationByPlatform(true);
		frame.setIconImage(new ImageIcon(brickMosaic.class.getResource("./images/BrickMosaic.png")).getImage());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// main menu
		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);


		// last menu in top right
		mnProgram = new JMenu("Program");
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(mnProgram);

		mntmAbout = new JMenuItem("About...");
		mntmAbout.addActionListener(this);
		mnProgram.add(mntmAbout);
		mnProgram.add(new JSeparator());


		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(this);
		mnProgram.add(mntmExit);
	}

	private void fileSetter() {
		fileImg = new JFileChooser(".");
		FileNameExtensionFilter ff = new FileNameExtensionFilter("All image type", "jpg","jpeg","gif","png","bmp");
		fileImg.addChoosableFileFilter(ff);
		fileOdt = new SmartFileChooser(".",".odt");
		fileOdt.setDialogType(JFileChooser.SAVE_DIALOG);
		fileXml = new SmartFileChooser(".",".xml");
		fileXml.setDialogType(JFileChooser.SAVE_DIALOG);
		filePalette = new SmartFileChooser(".",".png");
		filePalette.setDialogType(JFileChooser.SAVE_DIALOG);
		fileMosaicImage = new SmartFileChooser(".",".png");
		fileMosaicImage.setDialogType(JFileChooser.SAVE_DIALOG);
		fileMosaicImage.setDialogTitle("Save mosaic image");
	}

	private void brickToolPanel() {
		brickListTool = new JToolBar();
		brickToolPane.add(brickListTool);
		brickListTool.setToolTipText("Mosaic tools");

		ImageIcon prepImageIcon = new ImageIcon(brickMosaic.class.getResource("images/image-open.png"));
		btnPrepImg = new JButton(prepImageIcon);
		btnPrepImg.setToolTipText("Prepare an Image");
		btnPrepImg.addActionListener(this);
		brickListTool.add(btnPrepImg);

		ImageIcon newImageIcon = new ImageIcon(brickMosaic.class.getResource("images/document-open.png"));
		btnLoadImg = new JButton(newImageIcon);
		btnLoadImg.setToolTipText("Open Image");
		btnLoadImg.addActionListener(this);
		brickListTool.add(btnLoadImg);

		ImageIcon colorMatchIcon = new ImageIcon(brickMosaic.class.getResource("images/color-match.png"));
		btnColorMatch = new JButton(colorMatchIcon);
		btnColorMatch.setToolTipText("Match colors");
		btnColorMatch.addActionListener(this);
		brickListTool.add(btnColorMatch);

		ImageIcon writeInstrIcon = new ImageIcon(brickMosaic.class.getResource("images/export-icon.png"));
		btnWriteInstr = new JButton(writeInstrIcon);
		btnWriteInstr.setToolTipText("Write instruction document");
		btnWriteInstr.addActionListener(this);
		brickListTool.add(btnWriteInstr);

		ImageIcon blXmlIcon = new ImageIcon(brickMosaic.class.getResource("images/export-blxml.png"));
		btnWriteBlXml = new JButton(blXmlIcon);
		btnWriteBlXml.setToolTipText("Write Bricklink mass upload XML");
		btnWriteBlXml.addActionListener(this);
		brickListTool.add(btnWriteBlXml);

		ImageIcon paletteIcon = new ImageIcon(brickMosaic.class.getResource("images/palette.png"));
		btnPalette = new JButton(paletteIcon);
		btnPalette.setToolTipText("Write image for custom palette");
		btnPalette.addActionListener(this);
		brickListTool.add(btnPalette);

	}


	public void actionPerformed(ActionEvent e) {
		checker(e);

		 if (e.getSource() == brickSizeCombo) {
			if (mosaic == null)
				return;
			setMosaicSizes();
			mosaic.check();
			updateInfo();
		}
		else if (e.getSource() == btnPrepImg) {

			 imgOrganizer();
		}
		else if (e.getSource() == moduleSizeCombo) {
			if (mosaic == null)
				return;
			setMosaicSizes();
			mosaic.check();
			updateInfo();
		}
		else if (e.getSource() == mntmAbout) {
			AboutDialog dlg = new AboutDialog(frame, "About",
					new ImageIcon(brickMosaic.class.getResource("images/BrickMosaic.png")));
			dlg.setVisible(true);
		}
		else if (e.getSource() == btnWriteInstr) {
			generateDocs();
			btnWriteBlXml.setEnabled(true);
		}
		else if (e.getSource() == btnWriteBlXml) {
			exportBlXml();
		}
		else if (e.getSource() == btnPalette) {
			PaletteDialog dlg = new PaletteDialog(frame, "Select colors", true);
			dlg.setVisible(true);
			if (dlg.getResponse() == JOptionPane.OK_OPTION) {
				if (filePalette.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
					HashMap<Integer,Boolean> selected = dlg.getSelected();
					try {
						ImageIO.write(ColorReduceDialog.doPaletteImage(selected),"png",filePalette.getSelectedFile());
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(frame,
							    "Unable to write color sample image\n"+e1.getLocalizedMessage(),
							    "I/O Error",
							    JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}
				}
			}

		}
		else if (e.getSource() == mntmExit) {
			frame.dispose();
			System.exit(0);
		}
		else if (e.getSource() == btnColorMatch) {
			doColorMatch();
			btnWriteInstr.setEnabled(true);
		}
	}

	public void checker(ActionEvent e){

		if (e.getSource() == btnLoadImg) {
			fileImg.setDialogTitle("Select an image");
			int retval = fileImg.showOpenDialog(frame);
			if (retval != JFileChooser.APPROVE_OPTION) {
				return;
			}
			File f = fileImg.getSelectedFile();
			btnWriteInstr.setEnabled(false);
			btnWriteBlXml.setEnabled(false);
			btnColorMatch.setEnabled(false);
			try {
				mosaic = new Mosaic(f);
				setMosaicSizes();
				mosaic.check();
				mosaic.colorCount();
				imgPreview.setIcon(new ImageIcon(mosaic.getImage()));
				if (mosaic.isColorOk()) {
					btnColorMatch.setEnabled(true);
				}
				else {
					JOptionPane.showMessageDialog(frame,
							"Image contains too many different colors\n"+
									"It needs a color palette reduction",
							"Image Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				updateInfo();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(frame,
						"Error reading selected image"+
								"\n"+e1.getLocalizedMessage(),
						"Image Error",
						JOptionPane.ERROR_MESSAGE);
			}

		}

	}
	public void imgOrganizer(){
		fileImg.setDialogTitle("Select an image to edit");
		int retval = fileImg.showOpenDialog(frame);
		if (retval != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File f = fileImg.getSelectedFile();
		BufferedImage img = null;
		try {
			img = ImageIO.read(f);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(frame,
					"Error reading selected image"+
							"\n"+e1.getLocalizedMessage(),
					"Image Error",
					JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}
		// work on image size
		btnWriteInstr.setEnabled(false);
		btnWriteBlXml.setEnabled(false);
		btnColorMatch.setEnabled(false);
		ImageRescaleDialog dlg = new ImageRescaleDialog(frame, "Rescale/cut image for mosaic", true, img);
		dlg.setVisible(true);
		if (dlg.getResponse() != JOptionPane.OK_OPTION)
			return;
		// saves bricks and tile size
		brickSizeCombo.setSelectedIndex(dlg.getBrickSize());
		moduleSizeCombo.setSelectedIndex(dlg.getTileSize());
		// work on image colors
		// create a dialog for color editing
		ColorReduceDialog cdlg = new ColorReduceDialog(frame, "Convert to LEGO colors", true, dlg.getImage());
		cdlg.setVisible(true);
		if (cdlg.getResponse() == JOptionPane.OK_OPTION) {
			if (cdlg.getColorReducedImage() == null)
				return;
			int res = JOptionPane.showConfirmDialog(frame, "Do you want to save image to a file?",
					"Save image", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (res == JOptionPane.YES_OPTION) {
				// save image file
				if (fileMosaicImage.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
					try {
						ImageIO.write(cdlg.getColorReducedImage(), "png", fileMosaicImage.getSelectedFile());
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(frame,
								"Error writing mosaic image"+
										"\n"+e1.getLocalizedMessage(),
								"write Error",
								JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}
				}
			}
			mosaic = new Mosaic(cdlg.getColorReducedImage());
			setMosaicSizes();
			mosaic.check();
			mosaic.colorCount();
			imgPreview.setIcon(new ImageIcon(mosaic.getImage()));
			if (mosaic.isColorOk()) {
				btnColorMatch.setEnabled(true);
			}
			else {
				JOptionPane.showMessageDialog(frame,
						"Image contains too many different colors\n"+
								"It needs a color palette reduction",
						"Image Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			updateInfo();
		}
	}




	public void setMosaicSizes() {
		
		switch (brickSizeCombo.getSelectedIndex()) {
		case 0:
			brickSizemm = 0.008f;
			mosaic.setBrickSize(1);
			break;
		case 1:
			brickSizemm = 0.016f;
			mosaic.setBrickSize(2);
			break;
		}
		switch (moduleSizeCombo.getSelectedIndex()) {
		case 0:
			mosaic.setTileSize(8, 8);
			break;
		case 1:
			mosaic.setTileSize(16, 16);
			break;
		case 2:
			mosaic.setTileSize(32, 32);
			break;
		case 3:
			mosaic.setTileSize(48, 48);
			break;
		}

	}
//	workImage.setData(image.getData());

	
	@SuppressWarnings("boxing")
	public void updateInfo() {
		
		if (mosaic.isSizeOk()) {
			moduleCheck.setText("Ok");
		}
		else { 
			moduleCheck.setText("<html><span style='color:red'>Wrong size</span></html>");
		}
		imgTiles.setText(Integer.toString(mosaic.getNumTiles()));
		imgSizeX.setText(Integer.toString(mosaic.getSizeX()));
		imgSizeY.setText(Integer.toString(mosaic.getSizeY()));
		imgBricks.setText(Integer.toString(mosaic.getNumBricks()));
		imgColors.setText(Integer.toString(mosaic.getNumColor()));
		metersLab.setText(String.format("%4.1fx%4.1f",mosaic.getSizeX()*brickSizemm,
				mosaic.getSizeY()*brickSizemm));
	}
	
	
	public void doColorMatch() {

		ColorMatchDialog dlg = new ColorMatchDialog(frame,"Color matching check",true,mosaic);
		dlg.setVisible(true);
	}


	/* 
	 * reads colors from xml file with needed language, derived from 
	 * "user.language" system property
	 */
	@SuppressWarnings("boxing")
	public void readColors() {
		
		XMLInputFactory xmlFact;
		XMLEventReader xer;
		XMLEvent e;
		boolean isMaterials, isDoc;
		String tag;
		BrickColors bc = new BrickColors();
		
		try {
			xmlFact = XMLInputFactory.newInstance();
			xer = xmlFact.createXMLEventReader(brickMosaic.class.getResourceAsStream("data/colors-"+System.getProperty("user.language","en")+".xml"));
		} catch (XMLStreamException ex) {
			try {
				xmlFact = XMLInputFactory.newInstance();
				xer = xmlFact.createXMLEventReader(brickMosaic.class.getResourceAsStream("data/colors-en.xml"));
			}
			catch (XMLStreamException e1) {
				JOptionPane.showMessageDialog(frame,
					    "Unable to read colors 'colors-en.xml'\n"+e1.getLocalizedMessage(),
					    "XML Error",
					    JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		isMaterials = false;
		isDoc = false;
		while (xer.hasNext()) {
			try {
				e = xer.nextEvent();
			} catch (XMLStreamException e1) {
				JOptionPane.showMessageDialog(frame,
					    "Error reading color file"+
					    "\n"+e1.getLocalizedMessage(),
					    "XML Error",
					    JOptionPane.ERROR_MESSAGE);
				return;
			}
			switch (e.getEventType()) {
			case XMLEvent.START_DOCUMENT:
				isDoc = true;
				break;
			case XMLEvent.START_ELEMENT:
				tag = e.asStartElement().getName().getLocalPart();
				if (tag == "Materials" && isDoc) {
					isMaterials = true;
				}
				else if (tag.equals("Material")) {
					double r,g,b;
					float a;
					bc.inProduction = e.asStartElement().getAttributeByName(new QName("InUse")).getValue().equals("1");
					if (!bc.inProduction)
						continue;
					bc.ldd = Integer.parseInt(e.asStartElement().getAttributeByName(new QName("MatID")).getValue());
					// uses a slight gamma correction for LDD colors, that are a bit darker than real color.
					r = Integer.parseInt(e.asStartElement().getAttributeByName(new QName("Red")).getValue())/256.0;
					g = Integer.parseInt(e.asStartElement().getAttributeByName(new QName("Green")).getValue())/256.0;
					b = Integer.parseInt(e.asStartElement().getAttributeByName(new QName("Blue")).getValue())/256.0;
					a = (float) Integer.parseInt(e.asStartElement().getAttributeByName(new QName("Alpha")).getValue())/256.0f;
					bc.color = new Color((float)Math.pow(r, gamma),
							(float)Math.pow(g,gamma),
							(float)Math.pow(b,gamma),
							a);
//					bc.color = new Color((float)r,(float)g,(float)b,a);
					bc.lddName = e.asStartElement().getAttributeByName(new QName("Name")).getValue();
					BrickColors.addLddColor(bc.ldd, new BrickColors(bc));
					//System.out.println(bc.toString());
				}
				break;
			case XMLEvent.END_ELEMENT:
				tag = e.asEndElement().getName().getLocalPart();
				if (tag == "Materials" && isMaterials) {
					isMaterials = false;
				}
				break;
			}
		}
		try {
			xer.close();
		} catch (XMLStreamException e1) {
			;
		}
		// reads color mapping LDD<->BrickLink
		try {
			xmlFact = XMLInputFactory.newInstance();
			xmlFact.setProperty(XMLInputFactory.IS_COALESCING,true);
			xer = xmlFact.createXMLEventReader(brickMosaic.class.getResourceAsStream("data/colormap.xml"));
			isDoc = false;
			boolean isUpdate = false;
			boolean isColor = false;
			int ldd,bl;
			tag = "";
			while (xer.hasNext()) {
				e = xer.nextEvent();
				switch (e.getEventType()) {
				case XMLEvent.START_DOCUMENT:
					isDoc = true;
					break;
				case XMLEvent.START_ELEMENT:
					tag = e.asStartElement().getName().getLocalPart();
					if (tag == "update" && isDoc) {
						isUpdate = true;
					}
					else if (tag == "colors" && isUpdate) {
						isColor = true;
					}
					else if (tag == "colormap" && isColor) {
						ldd = Integer.parseInt(e.asStartElement().getAttributeByName(new QName("ldd")).getValue());
						if (ldd > 0) {
							bl = Integer.parseInt(e.asStartElement().getAttributeByName(new QName("bl")).getValue());
							BrickColors bb = BrickColors.getLddColor(ldd);
							if (bb != null) {
								bb.bl = bl;
							}
						}
					}
					break;
				}
			}
			xer.close();
		}
		catch (XMLStreamException e1) {
			JOptionPane.showMessageDialog(frame,
				    "Unable to read color mapping for BrickLink\n"+e1.getLocalizedMessage(),
				    "XML Error",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}

	}
	
	
	
	
	public void exportBlXml() {
		
		XMLOutputFactory output = XMLOutputFactory.newInstance();

		BlExportDialog dlg = new BlExportDialog(frame);
		dlg.setVisible(true);
		int ret = dlg.getResponse();
		if (ret != JOptionPane.OK_OPTION)
			return;
		fileXml.setDialogTitle("Bricklink XML mass upload");
		ret = fileXml.showSaveDialog(frame);
		if (ret != JFileChooser.APPROVE_OPTION)
			return;
		
		File f = fileXml.getSelectedFile();
		XMLStreamWriter xsw;
		try {
			xsw = output.createXMLStreamWriter(new FileOutputStream(f),"UTF-8");
			xsw.writeStartDocument("utf-8", "1.0");
			xsw.writeCharacters("\n");
			// a comment for exported list
			xsw.writeComment("Exported: "+f.getName()+
					" Date: "+DateFormat.getInstance().format(Calendar.getInstance().getTime()));
			xsw.writeCharacters("\n");
			// global start tag
			xsw.writeStartElement("INVENTORY");
			xsw.writeCharacters("\n");
			for (BrickColors c : mosaic.getMappedColors().keySet()) {
		        xsw.writeStartElement("ITEM");
				xsw.writeCharacters("\n");
				xsw.writeStartElement("ITEMTYPE");
				xsw.writeCharacters("P");
				xsw.writeEndElement();
				xsw.writeCharacters("\n");
				xsw.writeStartElement("ITEMID");
				xsw.writeCharacters(mosaic.getBrickSize()==2?"3003":"3005");
				xsw.writeEndElement();
				xsw.writeCharacters("\n");
				xsw.writeStartElement("COLOR");
				xsw.writeCharacters(Integer.toString(c.bl));
				xsw.writeEndElement();
				xsw.writeCharacters("\n");
		        if (dlg.getQty()) {
		    		xsw.writeStartElement("MINQTY");
		    		xsw.writeCharacters(mosaic.getMappedColors().get(c).toString());
		    		xsw.writeEndElement();
		    		xsw.writeCharacters("\n");
		        }
				xsw.writeStartElement("NOTIFY");
		        if (dlg.getNotify()) {
		    		xsw.writeCharacters("Y");
		        }
		        else {
		        	xsw.writeCharacters("N");
		        }
				xsw.writeEndElement();
				xsw.writeCharacters("\n");
				xsw.writeStartElement("WANTEDSHOW");
		        if (dlg.getQuery()) {
		    		xsw.writeCharacters("Y");
		        }
		        else {
		        	xsw.writeCharacters("N");
		        }
				xsw.writeEndElement();
				xsw.writeCharacters("\n");
				xsw.writeStartElement("CONDITION");
	    		xsw.writeCharacters(dlg.getCondition());
				xsw.writeEndElement();
				xsw.writeCharacters("\n");
		        if (dlg.getListId().length() > 0) {
		    		xsw.writeStartElement("WANTEDLISTID");
		    		xsw.writeCharacters(dlg.getListId());
		    		xsw.writeEndElement();
		    		xsw.writeCharacters("\n");
		        }
		        xsw.writeEndElement();
				xsw.writeCharacters("\n");
			}
			xsw.writeEndElement();
			xsw.writeCharacters("\n");
			xsw.writeEndDocument();
			xsw.flush();
			xsw.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(frame,
				    "Error writing Bricklink XML file"+
				    "\n"+e.getLocalizedMessage(),
				    "File Error",
				    JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (XMLStreamException e) {
			JOptionPane.showMessageDialog(frame,
				    "Error generating Bricklink XML file"+
				    "\n"+e.getLocalizedMessage(),
				    "XML Error",
				    JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return;
	}
	
	/* 
	 * generates instruction sheets (ODF/ODT)
	 * with total count
	 */
	public void generateDocs() {
		
		
		if (mosaic == null)
			return;
		
		if (!mosaic.isSizeOk()) {
			JOptionPane.showMessageDialog(frame,
				    "Image and tile size doesn't match\nImage will crop on bottom and left side",
				    "Tile/image mismatch",
				    JOptionPane.WARNING_MESSAGE);
		}
		
		fileOdt.setDialogTitle("Select mosaic instruction file");
		int ret = fileOdt.showSaveDialog(frame);
		if (ret != JFileChooser.APPROVE_OPTION)
			return;
		
		BusyDialog busyDialog = new BusyDialog(frame,"Writing instructions...",true,true,icnImg);
		busyDialog.setLocationRelativeTo(frame);
		DocWriterTask task = new DocWriterTask(fileOdt.getSelectedFile(),mosaic);
		busyDialog.setTask(task);
		busyDialog.setMsg("Writing mosaic instructions...");
		Timer timer = new Timer(200, busyDialog);
		task.execute();
		timer.start();
		busyDialog.setVisible(true);
		// after completing task return here
		timer.stop();
		busyDialog.dispose();
		try {
			@SuppressWarnings("boxing")
			int r = task.get(10, TimeUnit.MILLISECONDS);
			JOptionPane.showMessageDialog(frame, "Finished writing "+Integer.toString(r)+" instructions sheets\nHappy build!","Mosaic Instructions",JOptionPane.INFORMATION_MESSAGE);
		}
		catch (ExecutionException e) {
			JOptionPane.showMessageDialog(frame, "Unable to create instructions\nReason: "+e.getLocalizedMessage(), 
					"Instructions creation error",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(frame, "Task interrupted!\n Reason: "+e.getLocalizedMessage(), "Task interrupted",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (TimeoutException e) {
			JOptionPane.showMessageDialog(frame, "Timeout retrieving task output\nReason: "+e.getLocalizedMessage(), "Task timeout",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

	}

	
	


	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					brickMosaic window = new brickMosaic();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

}
