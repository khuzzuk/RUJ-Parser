package jTableFunctions;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

public class TableColorByRow extends JLabel implements TableCellRenderer
{
	boolean isBordered = true;
/*	public TableColorByRow(boolean isBordered)
	{
		this.isBordered = isBordered;
		setOpaque(true);
	}*/
	public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		setOpaque(true);
		if (row % 4 > 1)
		{
			setBackground(new Color(210, 210, 210));
		}
		else setBackground(Color.WHITE);
		if (hasFocus)
			{
				setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
				setBackground(new Color(110, 130, 163));
				//setBackground(table.getSelectionBackground());
			}
		else setBorder(null);
		setFont(table.getFont());
		setValue(value);
		return this;
	}
    protected void setValue(Object value)
    {
    	setText((value == null) ? "" : value.toString());
    }
}