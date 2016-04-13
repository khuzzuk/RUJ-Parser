package PMainWindow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class SaveCSV
{
	public SaveCSV(String file, String[][] data, String[] columns) throws IOException
	{
		if (columns[columns.length-1].equals("Edytuj")) {data = Func.removeColumn(data, data[0].length-1); columns = Func.removeColumn(columns, columns.length-1);}
		Writer export = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
		int a = data.length;
		int b = data[0].length;
		String text = "";
		for (int x=0; x<b-1; x++)
		{
			text = text + columns[x] + ",";
		}
		text = text + columns[b-1];
		export.append(text).append("\r\n");
		for (int x=0; x<a; x++)
		{
			text="";
			for (int y=0; y<b; y++)
			{
				if (data[x][y] == null || data[x][y].equals("")) {if (y<b-1) text = text + ",";} 
				else {
					text = text + "\"" + data[x][y].replace("\"", "\"\"") + "\"";
					if (y<b-1)
					{
						text = text + ",";
					}
				}
			}
			export.append(text).append("\r\n");
		}
		export.close();
	}
	public SaveCSV(String file, String[] data, String[] columns) throws IOException
	{
		//PrintWriter export = new PrintWriter(file);
		Writer export = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
		int a = data.length;
		String text = "";
		for (int x=0; x<a-1; x++)
		{
			text = text + columns[x] + ",";
		}
		text = text + columns[a-1];
		export.append(text).append("\r\n");
		text="";
		for (int y=0; y<a; y++)
		{
			if (data[y].equals("")||data[y] == null) {if (y<a-1) text = text + ",";} 
			else {
				text = text + "\"" + data[y].replace("\"", "\"\"") + "\"";
				if (y<a-1)
				{
					text = text + ",";
				}
			}
		}
	export.append(text).append("\r\n");
	export.close();
	}
	public SaveCSV(File file, String[] data, String[] columns) throws IOException
	{
		//PrintWriter export = new PrintWriter(file);
		Writer export = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
		int a = data.length;
		String text = "";
		for (int x=0; x<a-1; x++)
		{
			text = text + columns[x] + ",";
		}
		text = text + columns[a-1];
		export.append(text).append("\r\n");
		text="";
		for (int y=0; y<a; y++)
		{
			if (data[y].equals("")||data[y] == null) {if (y<a-1) text = text + ",";} 
			else {
				text = text + "\"" + data[y].replace("\"", "\"\"") + "\"";
				if (y<a-1)
				{
					text = text + ",";
				}
			}
		}
	export.append(text).append("\r\n");
	export.close();
	}
}
