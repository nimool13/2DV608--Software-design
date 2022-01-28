package presentation;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.table.AbstractTableModel;





public class PaletteTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	private HashMap<Integer, BrickColors> allColor;
	private HashMap<Integer,Boolean> selected;
	private Object[] rowIndex;

	

	private static String[] columnNames = {
			"LDD Color",
			"LDD color name",
			"Select"
	};
	

	/* 
	 * sets whole data model for table
	 */
	@SuppressWarnings("boxing")
	public void setColorList(HashMap<Integer, BrickColors> ac) {
		allColor = ac;
		selected = new HashMap<Integer,Boolean>();
		for (int ldd : allColor.keySet()) {
			selected.put(ldd, false);
		}
		rowIndex = allColor.keySet().toArray();
		fireTableDataChanged();
	}

	
	public HashMap<Integer,Boolean> getSelected() {
		return selected;
	}
	
	
	@Override
	public int getColumnCount() {

		return columnNames.length;
	}
	

	@Override
	public int getRowCount() {
		if (allColor != null)
			return allColor.size();
		else
			return 0;
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {

		if (col == 2)
			return true;
		return false;
    }
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int c) {

		switch (c) {
		case 0:
			return Color.class;
		case 1:
			return String.class;
		case 2:
			return Boolean.class;
		}
		return String.class;
    }

	
	@Override
	public String getColumnName(int col) {

        return columnNames[col];
    }

	
    public void setValueAt(Object value, int row, int col) {
    	
    	if (col != 2)
    		return;
    	else {
    		selected.put((Integer)rowIndex[row], (Boolean) value);
    	}
     }

    
	@Override
	public Object getValueAt(int arg0, int arg1) {
		switch (arg1) {
		case 0:
			return allColor.get(rowIndex[arg0]).color;
		case 1:
			return allColor.get(rowIndex[arg0]).lddName;
		case 2:
			return selected.get(rowIndex[arg0]);
		}
		return null;
	}



}

