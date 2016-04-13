package jTableFunctions;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;

public class MouseRightClick extends MouseAdapter
{
	private JTable table;
	public MouseRightClick(JTable table)
	{
		super();
		this.table = table;
	}
	public void mousePressed(MouseEvent e)
	{
		int r = table.rowAtPoint(e.getPoint());
		if (r>=0 && r<table.getRowCount())
		{
			int x = table.columnAtPoint(e.getPoint());
			int y = table.rowAtPoint(e.getPoint());
			table.getSelectionModel().setSelectionInterval(y, y);
			table.getColumnModel().getSelectionModel().setSelectionInterval(x, x);
		}
		else table.clearSelection();
	}
}