package PMainWindow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import Interfaces.CSVDataHolder;

public class LoadCSV extends SwingWorker<String, Integer>
{
	private File file;
	private String[][] csvData;
	private CSVDataHolder frame;
	private JProgressBar progressBar;
	private int filled;
	public LoadCSV(File file, JProgressBar progressBar, CSVDataHolder frame)
	{
		this.file = file;
		this.progressBar = progressBar;
		this.frame = frame;
	}
	public String doInBackground() throws IOException
	{
		frame.startAction();
		int dimensionRange[] = MeasureTable.measure2DTable(file.getPath());
		int a = dimensionRange[0];
		int b = dimensionRange[1];
		csvData = new String [a][b];
		BufferedReader tableRead = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		String firstLine = tableRead.readLine();
		int column = 0;
		char xChar;
		for (int x=0; x<a; x++)
		{
			for (int y=0; y<b; y++)
			{
				csvData[x][y] = "";
			}
		}
		for (int x=0; x<firstLine.length(); x++)
		{
			xChar = firstLine.charAt(x);
			if (Character.toString(xChar).equals(","))
				{
					column++;
					x++;
					xChar = firstLine.charAt(x);
				}
			csvData[0][column] = csvData[0][column] + xChar;
		}
		progressBar.setMaximum(a);
		progressBar.setIndeterminate(false);
		String line;
		StringBuilder mergedLine = new StringBuilder();
        Pattern spchars = Pattern.compile("([\\\\*+\\[\\](){}\\$.?\\^|])");
        Matcher match = spchars.matcher("\",\"");
        String escapedFieldSeparator = match.replaceAll("\\\\$1");
        String[] subline;
		for (int x=1; x<a; x++)
		{
			if (x%10 == 0)
			{
				filled = x;
				publish(filled);
			}
			line = tableRead.readLine().replaceAll(",,", ",\"\",").replaceAll(",\"\",,", ",\"\",\"\",").replaceAll(",\"\",,", ",\"\",\"\",").replaceFirst("\"", "");
			if (line.endsWith(",")){
				subline = line.substring(0, line.length()-2).split(escapedFieldSeparator);
				subline = Func.addColumns(1, subline);
				subline[subline.length-1] = "";
			}
			else subline = line.substring(0, line.length()-1).split(escapedFieldSeparator);
			if (subline.length<b){
				mergedLine = new StringBuilder(2000);
				mergedLine.append(line);
				while (subline.length<b)
				{
					line = tableRead.readLine().replaceAll(",,", ",\"\",").replaceAll(",\"\",,", ",\"\",\"\",").replaceAll(",\"\",,", ",\"\",\"\",");
					mergedLine.append(line);
					line = mergedLine.toString();
					subline = line.substring(0, line.length()-1).split(escapedFieldSeparator);
					//x++;
				}
			}
			csvData[x] = subline;
			//csvData[x] = line.substring(0, line.length()-1).split(escapedFieldSeparator);
			for (int y=0; y<csvData[x].length; y++)
			{
				csvData[x][y] = csvData[x][y].replaceAll("\"\"", "\"");
			}
		}
		tableRead.close();
		return "";
	}
	@Override
	protected void process(List<Integer> progress)
	{
		progressBar.setValue(filled);
	}
	@Override
	protected void done()
	{
		String[][] out = Func.removeEmptyRowsFromArray(csvData);
		frame.endAction();
		frame.reportLoadedFile(out);
	}
	
}