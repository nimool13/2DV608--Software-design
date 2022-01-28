package data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;




public class AboutDialog extends JDialog implements ActionListener {

	
	
	private static final long serialVersionUID = -4693987158080226643L;
	private JButton okButton;
	private URI uri;
	
	
	public AboutDialog(JFrame owner, String title, ImageIcon icn) {
		
		super (owner, title, true);
		setLocationByPlatform(true);
		//setPreferredSize(new Dimension(700,300));
		Container pane = getContentPane();

		JPanel body = new JPanel();
		body.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		body.setLayout(new BoxLayout(body, BoxLayout.PAGE_AXIS));
		JLabel prog = new JLabel("BrickMosaic",SwingConstants.CENTER);
		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
		Font font = new Font(Font.SANS_SERIF, Font.BOLD, 24);
		prog.setFont(font);
		body.add(prog);
		JLabel img = new JLabel(icn,SwingConstants.CENTER);
		img.setBorder(BorderFactory.createEtchedBorder());
		img.setAlignmentX(Component.CENTER_ALIGNMENT);
//		body.add(img);
		prog = new JLabel("Version: 0.1.0.0-rc4",SwingConstants.CENTER);
		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
		body.add(prog);
		
		body.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		prog = new JLabel(new ImageIcon(IBrick.class.getResource("images/BrickMosaic.jpg")));
		prog.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
		body.add(prog);
		prog = new JLabel("Original photo by Domenico Franco",SwingConstants.CENTER);
		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
		body.add(prog);
		body.add(new JSeparator(SwingConstants.HORIZONTAL));


		prog = new JLabel(new ImageIcon(IBrick.class.getResource("images/rb-icon.png")));
		prog.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
		body.add(prog);
		prog = new JLabel("<html><center>BrickMosaic is offered to the AFOL community<br/>by Romabrick people</center></html>",
				SwingConstants.CENTER);
		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		body.add(prog);
		

		body.add(new JSeparator(SwingConstants.HORIZONTAL));

		prog = new JLabel("© 2013-2014 Mario Pascucci",SwingConstants.CENTER);
		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
		body.add(prog);
		
		JButton urlButton = new JButton();
	    urlButton.setText("<HTML><FONT color=\"#000099\"><U>http://www.romabrick.it/brickmosaic/</U></FONT></HTML>");
	    urlButton.setHorizontalAlignment(SwingConstants.CENTER);
	    urlButton.setBorderPainted(false);
	    urlButton.setOpaque(false);
	    urlButton.setBackground(Color.WHITE);
	    try {
			uri = new URI("http://www.romabrick.it/brickmosaic/");
		} catch (URISyntaxException e1) {
			uri = null;
		}
	    urlButton.setToolTipText(uri.toString());
	    urlButton.addActionListener(new ActionListener() {
			
	    	// from: http://stackoverflow.com/questions/527719/how-to-add-hyperlink-in-jlabel
	    	
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (Desktop.isDesktopSupported()) {
					try {
				        Desktop.getDesktop().browse(uri);
				    } catch (IOException ex) {}
				}
			}
		});
		
	    urlButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	    body.add(urlButton);
	    
		body.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		prog = new JLabel("<html><center><small>BrickMosaic is NOT related, linked,<br/>sponsored or supported by LEGO® Group</small></center></html>",
				SwingConstants.CENTER);
		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		body.add(prog);
		
		pane.add(body,BorderLayout.CENTER);
		
		// ok button
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pane.add(buttonPane, BorderLayout.SOUTH);

		okButton = new JButton("OK");
		buttonPane.add(okButton);
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);
		
		pack();
	}
	
	
	@Override
	public void actionPerformed(ActionEvent ev) {

		if (ev.getSource() == okButton) {
			setVisible(false);
		}
	}


	
}
