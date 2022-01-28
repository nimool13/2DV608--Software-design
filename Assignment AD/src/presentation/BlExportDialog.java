package presentation;/*
	Copyright 2013-2014 Mario Pascucci <mpascucci@gmail.com>
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




import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class BlExportDialog extends JDialog implements ActionListener {

	
	private static final long serialVersionUID = 6241796433157781436L;
	private Container mainPane;
	private JButton okButton;
	private JButton cancelButton;
	private JTextField blList;
	private JCheckBox blQuery;
	private JCheckBox blNotify;
	private JCheckBox blQty;
	private JComboBox blCondition;
	private int userChoice = JOptionPane.CANCEL_OPTION;

	
	public BlExportDialog(Frame owner) {
		
		super(owner,"Bricklink export options",true);
		createDialog();
	}
	
	
	private void createDialog() {
		
		// really create the dialog
//		setPreferredSize(new Dimension(800,500));
		setLocationByPlatform(true);
		mainPane = getContentPane();
		mainPane.setLayout(new BorderLayout(2,2));
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		mainPane.add(buttonPane, BorderLayout.SOUTH);

		blQty = new JCheckBox("Include quantity");
		blQty.setSelected(true);
		blNotify = new JCheckBox("Enable notification");
		blNotify.setSelected(false);
		blQuery = new JCheckBox("Include in queries");
		blQuery.setSelected(true);
		blList = new JTextField();
		blCondition = new JComboBox();
		blCondition.addItem("Don't care");
		blCondition.addItem("New");
		blCondition.addItem("Used");
		blCondition.setSelectedIndex(0);

		JPanel pane = new JPanel();
		pane.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.ipady = 2;
		gbc.ipadx = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;

		gbc.gridx = 0;
		gbc.gridy = 0;
		
		// list id
		pane.add(new JLabel("List ID (empty for main):",SwingConstants.RIGHT),gbc);
		gbc.gridx = 1;
		pane.add(blList,gbc);

		// status
		gbc.gridx = 0;
		gbc.gridy += 1;
		pane.add(new JLabel("Part condition:",SwingConstants.RIGHT),gbc);
		
		gbc.gridx = 1;
		pane.add(blCondition,gbc);
		
		// quantity
		gbc.gridy += 1;
		gbc.gridx = 0;
		pane.add(blQty,gbc);
		
		// notify
		gbc.gridx = 1;
		pane.add(blNotify,gbc);
		
		// includes in queries
		gbc.gridx = 0;
		gbc.gridy += 1;
		pane.add(blQuery,gbc);
		
		mainPane.add(pane, BorderLayout.CENTER);
		
		okButton = new JButton("OK");
		buttonPane.add(okButton);
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);

		cancelButton = new JButton("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(this);
		
		pack();

	}
	
	
	
	public int getResponse() {
		return userChoice;
	}

	
	public boolean getQty() {
		return blQty.isSelected();
	}
	
	
	public boolean getNotify() {
		return blNotify.isSelected();
	}
	
	
	public boolean getQuery() {
		return blQuery.isSelected();
	}
	
	
	public String getListId() {
		return blList.getText();
	}
	
	
	
	public String getCondition() {

		switch (blCondition.getSelectedIndex()) {
        case 1:
    		return "N";
        case 2:
        	return "U";
        }
		return "";

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
	}
	
	
	
}
