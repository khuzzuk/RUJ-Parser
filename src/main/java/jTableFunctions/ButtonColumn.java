package jTableFunctions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class ButtonColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener, MouseListener
{
	private JTable table;
	private Action action;
	private JButton[] buttons = new JButton[2];
	
	private Border borderFocus;
	private Border borderOriginal;
	private int mnemonic;
	private Object value;
	private boolean isButtonColumnEditor;
	public ButtonColumn(JTable table, Action action, int column)
	{
		this.table = table;
		this.action = action;
		buttons[0] = new JButton("Edytuj");
		buttons[1] = new JButton("Edytuj");
		buttons[0].setFocusPainted(false);
		buttons[0].addActionListener(this);
		TableColumnModel model = table.getColumnModel();
		model.getColumn(column).setCellRenderer(this);
		model.getColumn(column).setCellEditor(this);
		table.addMouseListener(this);
	}
	public Border getFocusBorder()
	{
		return borderFocus;
	}
	public void setFocusBorder(Border borderFocus)
	{
		this.borderFocus = borderFocus;
		buttons[0].setBorder(borderFocus);
	}
	public int getMnemonic()
	{
		return mnemonic;
	}
	public void setMnemonic(int mnemonic)
	{
		this.mnemonic = mnemonic;
		buttons[1].setMnemonic(mnemonic);
		buttons[0].setMnemonic(mnemonic);
	}
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		if (value == null) buttons[0].setText("");
		else buttons[0].setText(value.toString());
		this.value = value;
		return buttons[0];
	}
	@Override
	public Object getCellEditorValue()
	{
		return value;
	}
	//implements TableCellRenderer
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		if (isSelected)
		{
			buttons[1].setForeground(table.getSelectionForeground());
			buttons[1].setBackground(table.getSelectionBackground());
		}
		else
		{
			buttons[1].setForeground(table.getForeground());
			buttons[1].setBackground(UIManager.getColor("Button.background"));
		}
		if (hasFocus)
		{
			buttons[1].setBorder(borderFocus);
		}
		else
		{
			buttons[1].setBorder(borderOriginal);
		}
		if (value==null)
		{
			buttons[1].setText("");
		}
		else
		{
			buttons[1].setText(value.toString());
		}
		return buttons[1];
	}
	//implements ActionListener
	public void actionPerformed(ActionEvent event)
	{
		int row = table.convertRowIndexToModel(table.getEditingRow());
		fireEditingStopped();
		ActionEvent event1 = new ActionEvent(table, ActionEvent.ACTION_PERFORMED, "" + row);
		action.actionPerformed(event1);
	}
	//implements MauseListener
	public void mousePressed(MouseEvent event)
	{
		if (table.isEditing() && table.getCellEditor() == this) isButtonColumnEditor = true;
	}
	public void mouseReleased(MouseEvent e)
	{
		if (isButtonColumnEditor && table.isEditing()) table.getCellEditor().stopCellEditing();
		isButtonColumnEditor = false;
	}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
