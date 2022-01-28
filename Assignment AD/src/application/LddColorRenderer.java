package application;

import presentation.BrickColors;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;


public class LddColorRenderer extends JLabel implements ListCellRenderer {

	class LddIcon implements Icon {
		
		private Color c;

		  public LddIcon(Color c) {
			  this.c = c;
		  }

		  public int getIconHeight() {
			  return 26;
		  }

		  public int getIconWidth() {
			  return 26;
		  }

		  public void paintIcon(Component cp, Graphics g, int x, int y) {
			  
			  g.setColor(Color.BLACK);
			  g.drawRect(0, 0, 25, 25);
			  g.setColor(c);
			  g.fillRect(1, 1, 24, 24);
		  }
		}
	
	
	private static final long serialVersionUID = 6307685763094812103L;

    public LddColorRenderer() {

    	setOpaque(true); //MUST do this for background to show up.
    }

    
    @SuppressWarnings("boxing")
	public Component getListCellRendererComponent(
                            JList list, Object index,
                            int idx,
                            boolean isSelected, boolean hasFocus) {

        setText(BrickColors.getLddColor((Integer)index).lddName);
        setIcon(new LddIcon(BrickColors.getLddColor((Integer)index).color));
        if (isSelected) {
        	setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } 
        else {
        	setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        
        return this;
    }

}
