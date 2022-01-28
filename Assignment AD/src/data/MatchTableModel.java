package data;

import presentation.BrickColors;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.table.AbstractTableModel;





public class MatchTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	protected HashMap<Color,Integer> colorMatch;
	private HashMap<Integer, BrickColors> allColor;
	private HashMap<Color,Integer> colorCount;
	private Object[] rowIndex;

	

	private static String[] columnNames = {
			"Original color",
			"Bricks",
			"LDD color",
			"LDD color name"
	};
	

	/* 
	 * sets whole data model for table
	 */
	public void setMatchList(HashMap<Color,Integer> cm, HashMap<Integer, BrickColors> ac,
			HashMap<Color,Integer> cc) {
		colorMatch = cm;
		allColor = ac;
		colorCount = cc;
		rowIndex = colorCount.keySet().toArray();
		fireTableDataChanged();
	}

	
	public HashMap<Color,Integer> getMatch() {
		return colorMatch;
	}
	
	
	@Override
	public int getColumnCount() {

		return columnNames.length;
	}
	

	@Override
	public int getRowCount() {
		if (colorCount != null)
			return colorCount.size();
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
		case 2:
			return Color.class;
		case 1:
			return Integer.class;
		case 3:
			return String.class;
		}
		return String.class;
    }

	
	@Override
	public String getColumnName(int col) {

        return columnNames[col];
    }

	
    public void setValueAt(Object value, int row, int col) {
    	
   		return;
     }

    
    public Color getColor(int index) {
    	
    	return (Color) rowIndex[index];
    }
    
    
    public int getLddColor(int index) {
    	
    	return allColor.get(colorMatch.get(rowIndex[index])).ldd;
    }
    
    
	@Override
	public Object getValueAt(int arg0, int arg1) {
		switch (arg1) {
		case 0:
			return rowIndex[arg0];
		case 1:
			return colorCount.get(rowIndex[arg0]);
		case 2:
			return allColor.get(colorMatch.get(rowIndex[arg0])).color;
		case 3:
			return allColor.get(colorMatch.get(rowIndex[arg0])).lddName;
		}
		return null;
	}



}

