package data;

import presentation.BrickColors;
import presentation.ColorCountTableModel;
import presentation.Mosaic;
import presentation.PaletteTableModel;
import application.CellColorRenderer;
import application.DitheringTask;
import application.ImageView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;




public class ColorReduceDialog extends JDialog implements ActionListener {


	private static final long serialVersionUID = 8933288964831247818L;
	private BufferedImage image,convertedImage = null;
	private PaletteTableModel paletteTM;
	private JTable colorTable;
	private JButton okButton;
	private JButton cancelButton;
	private int userChoice = JOptionPane.CANCEL_OPTION;
	private ImageView imagebox;
	private ImageView convertedbox;
	private JButton convertButton;
	private ColorCountTableModel colorCountTM;
	private JTable colorCountTable;
	private JLabel totalCount;
	private JComboBox colorMode;
	private GridBagConstraints gbc;
	private JPanel controlsPane;


	public ColorReduceDialog(Frame owner, String title, boolean modal, BufferedImage img) {
		super(owner, title, modal);

		image = img;
		createDialog();
	}




	private void createDialog() {


		// really create the dialog
		setLocationByPlatform(true);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout(2,2));

		// center image
		JPanel imagePane = new JPanel();
		imagePane.setLayout(new GridBagLayout());
		contentPane.add(imagePane,BorderLayout.CENTER);

		// controls and buttons
		controlsPane = new JPanel();
		controlsPane.setLayout(new GridBagLayout());
		contentPane.add(controlsPane,BorderLayout.WEST);

		gbc = new GridBagConstraints();

		TableColumnModel tcl = colorTable.getColumnModel();

		JScrollPane scrollPane = new JScrollPane();

		colorSetting();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		controlsPane.add(scrollPane, gbc);


		gbc.gridwidth = 1;
		gbc.gridy += 1;
		gbc.gridx = 0;
		convertButton = new JButton("Convert!");
		convertButton.addActionListener(this);
		controlsPane.add(convertButton, gbc);

		gbc.gridx = 1;
		colorMode = new JComboBox();
		colorMode.addItem("Plain");
		colorMode.addItem("Dither");
		colorMode.addItem("Floyd-Steinberg");
		colorMode.setSelectedIndex(2);
		controlsPane.add(colorMode, gbc);

		gbc.gridwidth = 2;
		gbc.gridy += 1;
		gbc.gridx = 0;
		gbc.weightx = 0.1;
		gbc.weighty = 0.1;
		colorCountTM = new ColorCountTableModel();
		colorCountTable = new JTable(colorCountTM);
		colorCountTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		colorCountTable.setRowHeight(20);
		tcl = colorCountTable.getColumnModel();
		tcl.getColumn(0).setPreferredWidth(15);
		tcl.getColumn(1).setPreferredWidth(120);
		tcl.getColumn(2).setPreferredWidth(20);
		tcl.getColumn(0).setCellRenderer(new CellColorRenderer(false));
		JScrollPane scc = new JScrollPane();
		scc.setBorder(BorderFactory.createTitledBorder("Color Count"));
		scc.setPreferredSize(new Dimension(250,250));
		scc.setViewportView(colorCountTable);
		controlsPane.add(scc, gbc);

		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.gridy += 1;
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		controlsPane.add(new JLabel("Total:",SwingConstants.RIGHT), gbc);
		gbc.gridx = 1;
		totalCount = new JLabel("-");
		controlsPane.add(totalCount, gbc);

		//////////////////////////////
		// setup image preview pane
		gbc.weightx = 0.01;
		gbc.weighty = 0.01;
		gbc.fill = GridBagConstraints.BOTH;

		gbc.gridy = 0;
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		imagebox = new ImageView(image,350,250);
		imagebox.setBorder(BorderFactory.createTitledBorder("Original"));
		imagePane.add(imagebox, gbc);

		gbc.weightx = 0.01;
		gbc.weighty = 0.01;
		gbc.gridy = 1;
		imagePane.add(new JLabel(new ImageIcon(IBrick.class.getResource("images/convert.png"))), gbc);

		gbc.gridy = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		try {
			convertedbox = new ImageView(ImageIO.read(IBrick.class.getResource("images/empty-image.png")),350,250);
		} catch (IOException e) {
			e.printStackTrace();
		}
		convertedbox.setBorder(BorderFactory.createTitledBorder("Converted"));
		imagePane.add(convertedbox, gbc);


		// dialog standard buttons
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

		pack();
	}

	private void colorSetting() {
		gbc.weightx = 0.1;
		gbc.weighty = 0.1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.ipady = 2;
		gbc.ipadx = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;

		//////////////////////////
		// setup controls pane

		paletteTM = new PaletteTableModel();
		colorTable = new JTable(paletteTM);
		paletteTM.setColorList(BrickColors.getAllColors());
		colorTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		colorTable.setAutoCreateRowSorter(true);
		colorTable.setRowHeight(20);
		TableRowSorter<PaletteTableModel>sorterFilter = new TableRowSorter<PaletteTableModel>(paletteTM);
		colorTable.setRowSorter(sorterFilter);
		TableColumnModel tcl = colorTable.getColumnModel();
		tcl.getColumn(0).setPreferredWidth(15);
		tcl.getColumn(1).setPreferredWidth(120);
		tcl.getColumn(2).setPreferredWidth(15);
		tcl.getColumn(0).setCellRenderer(new CellColorRenderer(false));
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(250,250));
		scrollPane.setViewportView(colorTable);
		scrollPane.setBorder(BorderFactory.createTitledBorder("Select colors"));

	}


	public int getResponse() {
		return userChoice;
	}


	public BufferedImage getColorReducedImage() {

		return convertedImage;
	}


	@SuppressWarnings("boxing")
	public static BufferedImage doPaletteImage(HashMap<Integer,Boolean> sel) {

		int i;

		i = 0;
		for (int j : sel.keySet()) {
			if (sel.get(j)) {
				i++;
			}
		}
		BufferedImage img = new BufferedImage(20, 20*i, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		i = 0;
		for (int j : sel.keySet()) {
			if (sel.get(j)) {
				g.setColor(BrickColors.getLddColor(j).color);
				g.fillRect(0, i*20, 20, 20);
				i++;
			}
		}
		return img;
	}


//    public void applyFloydSteinbergDithering(final BufferedImage image,
//    		HashMap<Integer,BrickColors> plt) {
//
//    	for (int y = 0; y < image.getHeight(); y++) {
//            for (int x = 0; x < image.getWidth(); x++) {
//                final int argb = image.getRGB(x, y);
//                final int nextArgb = BrickColors.getNearestColorFromPalette(new Color(argb), plt).color.getRGB();
//                image.setRGB(x, y, nextArgb);
//
//                final int a = (argb >> 24) & 0xff;
//                final int r = (argb >> 16) & 0xff;
//                final int g = (argb >> 8) & 0xff;
//                final int b = argb & 0xff;
//
//                final int na = (nextArgb >> 24) & 0xff;
//                final int nr = (nextArgb >> 16) & 0xff;
//                final int ng = (nextArgb >> 8) & 0xff;
//                final int nb = nextArgb & 0xff;
//
//                final int errA = a - na;
//                final int errR = r - nr;
//                final int errG = g - ng;
//                final int errB = b - nb;
//
//                if (x + 1 < image.getWidth()) {
//                    int update = adjustPixel(image.getRGB(x + 1, y), errA, errR, errG, errB, 7);
//                    image.setRGB(x + 1, y, update);
//                    if (y + 1 < image.getHeight()) {
//                        update = adjustPixel(image.getRGB(x + 1, y + 1), errA, errR, errG, errB, 1);
//                        image.setRGB(x + 1, y + 1, update);
//                    }
//                }
//                if (y + 1 < image.getHeight()) {
//                    int update = adjustPixel(image.getRGB(x, y + 1), errA, errR, errG, errB, 5);
//                    image.setRGB(x, y + 1, update);
//                    if (x - 1 >= 0) {
//                        update = adjustPixel(image.getRGB(x - 1, y + 1), errA, errR, errG, errB, 3);
//                        image.setRGB(x - 1, y + 1, update);
//                    }
//
//                }
//            }
//        }
//    }
//
//
//    private int adjustPixel(final int argb, final int errA, final int errR, final int errG, final int errB, final int mul) {
//        int a = (argb >> 24) & 0xff;
//        int r = (argb >> 16) & 0xff;
//        int g = (argb >> 8) & 0xff;
//        int b = argb & 0xff;
//
//        a += errA * mul / 16;
//        r += errR * mul / 16;
//        g += errG * mul / 16;
//        b += errB * mul / 16;
//
//        if (a < 0) {
//            a = 0;
//        } else if (a > 0xff) {
//            a = 0xff;
//        }
//        if (r < 0) {
//            r = 0;
//        } else if (r > 0xff) {
//            r = 0xff;
//        }
//        if (g < 0) {
//            g = 0;
//        } else if (g > 0xff) {
//            g = 0xff;
//        }
//        if (b < 0) {
//            b = 0;
//        } else if (b > 0xff) {
//            b = 0xff;
//        }
//
//        return (a << 24) | (r << 16) | (g << 8) | b;
//    }
//
//


	@SuppressWarnings("boxing")
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
		else if (ev.getSource() == convertButton) {
			// do image conversion
			HashMap<Integer,BrickColors> palt = new HashMap<Integer,BrickColors>();
			HashMap<Integer,Boolean> sel = paletteTM.getSelected();
			int i = 0;
			for (int j : sel.keySet()) {
				if (sel.get(j)) {
					palt.put(j, BrickColors.getLddColor(j));
					i++;
				}
			}
			if (i == 0)
				return;
			convertedImage = new BufferedImage(image.getWidth(),image.getHeight(),image.getType());
			convertedImage.setData(image.getData());
			BusyDialog busyDialog = new BusyDialog((JDialog) this,"Working...",true,true,null);
			busyDialog.setLocationRelativeTo(this);
			DitheringTask task = new DitheringTask(convertedImage,palt,colorMode.getSelectedIndex());
			busyDialog.setTask(task);
			busyDialog.setMsg("Reducing colors...");
			Timer timer = new Timer(200, busyDialog);
			task.execute();
			timer.start();
			busyDialog.setVisible(true);
			// after completing task return here
			timer.stop();
			busyDialog.dispose();
			convertedbox.setImage(convertedImage);
			// count colors and bricks
			Mosaic m = new Mosaic(convertedImage);
			m.check();
			m.colorCount();
			// display data
			HashMap<Integer,Integer> count = new HashMap<Integer,Integer>();
			HashMap<Integer,BrickColors> colors = new HashMap<Integer,BrickColors>();
			int total = 0;
			for (Color c : m.getOriginalColors().keySet()) {
				BrickColors b = BrickColors.getNearestColor(c);
				colors.put(b.ldd,b);
				count.put(b.ldd, m.getOriginalColors().get(c));
				total += m.getOriginalColors().get(c);
			}
			colorCountTM.setColorList(colors, count);
			totalCount.setText(Integer.toString(total));
		}

	}


}
