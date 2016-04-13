package jTableFunctions;

import java.awt.Component;
import java.util.Arrays;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class TableResize
{
	public static void resizeColumnWidth(JTable table, int[] a, int b)
	{
		int width;
		final TableColumnModel colModel = table.getColumnModel();
		for (int x=0; x<table.getColumnCount(); x++)
		{
			width = 30;
			for (int y=0; y<table.getRowCount(); y++)
			{
				TableCellRenderer renderer = table.getCellRenderer(y, x);
				Component component = table.prepareRenderer(renderer, y, x);
				width=Math.max(component.getPreferredSize().width+6, width);
			}
			colModel.getColumn(x).setPreferredWidth(Math.min(width, b));
			a[0] += Math.min(width,250);
		}
	}
	public static void complexResize(JTable table, int[] a, int width)
	{
		TableColumnModel colModel = table.getColumnModel();
		TableCellRenderer renderer;
		Component component;
		int columns = table.getColumnCount();
		int rows = table.getRowCount();
		int[] columnsWidth = new int[columns];
		for (int x=0; x<columns; x++)
		{
			columnsWidth[x] = 20;
			for (int y=0; y<rows; y++)
			{
				renderer = table.getCellRenderer(y, x);
				component = table.prepareRenderer(renderer, y, x);
				columnsWidth[x] = Math.max(columnsWidth[x], component.getPreferredSize().width+6);
			}
		}
		int sum = 0; int average;
		for (int x=0; x<columns; x++)
		{
			sum = sum + columnsWidth[x];
		}
		average = sum/columns;
		int [] sorted = columnsWidth.clone();
		Arrays.sort(sorted);
		int median = sorted[sorted.length/2];
		for (int x=0; x<columns; x++)
		{
			if (columnsWidth[x]<median)
			{
				colModel.getColumn(x).setPreferredWidth(columnsWidth[x]);
				width -= columnsWidth[x];
				columnsWidth[x] = 0;
			}
		}
		sum = 0;
		for (int x=0; x<columns; x++)
		{
			if (columnsWidth[x]>0)
			{
				sum += 1;
			}
		}
		for (int x=0; x<columns; x++)
		{
			if (columnsWidth[x]>0)
			{
				colModel.getColumn(x).setPreferredWidth(width/sum);
			}
		}
	}
}