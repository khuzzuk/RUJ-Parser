package Imports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import ExportXML.ParseXML;
import PMainWindow.MetAnWindow;

public class LoadScopusIndex extends SwingWorker<String, Integer>{
	private String file;
	private String[][] csvData;
	private JProgressBar progressBar;
	private int filled;
	public LoadScopusIndex(String file, JProgressBar progressBar)
	{
		this.file = file;
		this.progressBar = progressBar;
	}
	public String doInBackground() throws IOException
	{
		ArrayList<String[]> list = new ArrayList<String[]>();
		BufferedReader tableRead = new BufferedReader(new InputStreamReader(ParseXML.class.getResourceAsStream(file), "UTF8"));
		String firstLine = tableRead.readLine();
		int column = 0;
		char xChar;
		String line;
		while ((line=tableRead.readLine())!=null && !line.equals("")){
			list.add(line.split(","));
		}
		tableRead.close();
		csvData = new String[list.size()][];
		for (int x=0; x<csvData.length; x++){
			csvData[x] = list.get(x);
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
		MetAnWindow.myIdentity.setScopusIndex(csvData);
		if (MetAnWindow.myIdentity.conferenceFullReport)
			MetAnWindow.myIdentity.reportScopusConferences();
	}
}
