package Imports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import ExportXML.ParseXML;
import PMainWindow.Func;
import PMainWindow.MeasureTable;
import PMainWindow.MetAnWindow;

public class ImportCSV extends SwingWorker<String, Integer>
{
	private File file;
	private String[][] loadedCSV;
	private JProgressBar progBar;
	public static String[][] readData(String resource, String handler) throws IOException
	{
		String line;
		BufferedReader reader = new
				BufferedReader(new
						InputStreamReader(ParseXML.class.getResourceAsStream(resource), "UTF8"));
		ArrayList<String[]> list = new ArrayList<String[]>();
		while ((line=reader.readLine())!=null && !line.equals("")){
			if (line.endsWith("\""))
				list.add(line.substring(1, line.length()-1).split(handler+","+handler));
			else
				list.add(line.substring(1).split(handler+","+handler));
		}
	return Func.toArray(list);
	}
	public static String[][] readData(File file, String handler) throws IOException
	{
		String line;
		BufferedReader reader = new
				BufferedReader(new
						InputStreamReader(new FileInputStream(file), "UTF8"));
		ArrayList<String[]> list = new ArrayList<String[]>();
		reader.readLine();
		while ((line=reader.readLine())!=null && !line.equals("")){
			if (line.endsWith("\""))
				list.add(line.substring(1, line.length()-1).split(handler+","+handler));
			else
				list.add(line.substring(1).split(handler+","+handler));
		}
	return Func.toArray(list);
	}
	public ImportCSV(File file){
		this.file = file;
	}
	public String doInBackground() throws IOException
	{
		progBar = MetAnWindow.myIdentity.getProgressBar();
		int dimensionRange[] = MeasureTable.measure2DTable(file.getPath());
		progBar.setMaximum(dimensionRange[0]);
		String line;
		String[] splitted;
		BufferedReader reader = new
				BufferedReader(new
						InputStreamReader(new FileInputStream(file), "UTF8"));
		ArrayList<String[]> list = new ArrayList<String[]>(dimensionRange[0]);
		line=reader.readLine();
		list.add(line.split(","));
		int cols = list.get(0).length;
		progBar.setIndeterminate(false);
		while ((line=reader.readLine())!=null && !line.equals("")){
			if (list.size()%100==0) publish(list.size());
			line = line.replaceAll("\"\"", "\"").replaceAll(",,", ",\"\",").replaceAll(",\"\",,", ",\"\",\"\",").replaceAll(",\"\",,", ",\"\",\"\",");
			splitted = line.substring(1, line.length()-1).split("\",\"");
			while (splitted.length<cols){
				line = reader.readLine().replaceAll("\"\"", "\"").replaceAll(",,", ",\"\",").replaceAll(",\"\",,", ",\"\",\"\",").replaceAll(",\"\",,", ",\"\",\"\",");
				splitted = Func.mergeArrays(splitted, line.substring(1, line.length()-1).split("\",\""));
			}
			list.add(splitted);
		}
		reader.close();
		loadedCSV = Func.toArray(list);
	return "gotowe";
	}
	@Override
	protected void process(List<Integer> progress)
	{
		progBar.setValue(progress.get(progress.size()-1));
	}
	@Override
	protected void done()
	{
		MetAnWindow.myIdentity.menuShowAfterLoadCSV();
		MetAnWindow.myIdentity.reportLoadedFile(loadedCSV);
	}
}