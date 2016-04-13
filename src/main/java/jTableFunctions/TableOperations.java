package jTableFunctions;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JTable;

public class TableOperations
{
	public static void pasteCell(JTable table) throws UnsupportedFlavorException, IOException
	{
		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		final Transferable input = clipboard.getContents(clipboard);
		if (input!=null)
		{
			String text = input.getTransferData(DataFlavor.stringFlavor).toString();
			int x = table.getSelectedRow();
			int y = table.getSelectedColumn();
			if (table.isCellEditable(x, y))
			{
				table.setValueAt(text, x, y);
			}
			table.repaint();
		}
	}
}