package presentation;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.table.AbstractTableModel;





public class ColorCountTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	private HashMap<Integer, BrickColors> allColor;
	private HashMap<Integer,Integer> count;
	private Object[] rowIndex;

	

	private static String[] columnNames = {
			"LDD Color",
			"LDD color name",
			"Count"
	};
	

	/* 
	 * sets whole data model for table
	 */
	public void setColorList(HashMap<Integer, BrickColors> ac, HashMap<Integer,Integer> count) {
		allColor = ac;
		this.count = count;
		rowIndex = allColor.keySet().toArray();
		fireTableDataChanged();
	}

	
	public HashMap<Integer,Integer> getSelected() {
		return count;
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
			return Integer.class;
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
    		count.put((Integer)rowIndex[row], (Integer) value);
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
			return count.get(rowIndex[arg0]);
		}
		return null;
	}



}

