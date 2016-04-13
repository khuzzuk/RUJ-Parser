package Imports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import ExportXML.ParseXML;
import PMainWindow.MeasureTable;
import PMainWindow.MetAnWindow;

public class LoadWoSConferenceIndex extends SwingWorker<String, Integer>
{
	private String file;
	private String[][] csvData;
	private JProgressBar progressBar;
	private int filled;
	public LoadWoSConferenceIndex(String file, JProgressBar progressBar)
	{
		this.file = file;
		this.progressBar = progressBar;
	}
	public String doInBackground() throws IOException
	{
		int dimensionRange[] = MeasureTable.measureResource2D(file);
		int a = dimensionRange[0];
		int b = dimensionRange[1];
		csvData = new String [a][b];
		BufferedReader tableRead = new BufferedReader(new InputStreamReader(ParseXML.class.getResourceAsStream(file), "UTF8"));
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
			subline = line.substring(0, line.length()-1).split(escapedFieldSeparator);
			while (subline.length<b)
			{
				mergedLine.append(line);
				line = tableRead.readLine().replaceAll(",,", ",\"\",").replaceAll(",\"\",,", ",\"\",\"\",").replaceAll(",\"\",,", ",\"\",\"\",");
				mergedLine.append(line);
				line = mergedLine.toString();
				subline = line.substring(0, line.length()-1).split(escapedFieldSeparator);
				//x++;
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
		//MetAnWindow.myIdentity.menuShowAfterLoadCSV();
		MetAnWindow.myIdentity.setWoSIndex(csvData);
		if (MetAnWindow.myIdentity.conferenceFullReport)
			MetAnWindow.myIdentity.reportConferences();
	}
	
}