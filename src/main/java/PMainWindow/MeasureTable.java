package PMainWindow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ExportXML.ParseXML;

public class MeasureTable
{
	public static int[] measure2DTable(String tableFile) throws IOException
	{
		BufferedReader tableRead = new BufferedReader(new FileReader(tableFile));
		String firstLine = tableRead.readLine();
		int columns = firstLine.length() - firstLine.replace(",", "").length()+1;
		int lines = 1;
		while (tableRead.readLine() != null) lines++;
		tableRead.close();
		return new int[] {lines, columns};
	}
	public static String convertPath(String previousPath)
	{
		String newPath = previousPath.replace("\\","\\\\");
		return newPath;
	}
	public static int measureList(String listFile) throws IOException
	{
		int output = 0;
		BufferedReader listRead = new BufferedReader(new FileReader(listFile));
		while (listRead.readLine() != null) output++;
		listRead.close();
		return output;
	}
	public static int measureList(File listFile) throws IOException
	{
		int output = 0;
		BufferedReader listRead = new BufferedReader(new FileReader(listFile));
		while (listRead.readLine() != null) output++;
		listRead.close();
		return output;
	}
	public static int measureResource(String resource) throws IOException
	{
		int output = 0;
		BufferedReader listRead = new BufferedReader(new
				InputStreamReader(ParseXML.class.getResourceAsStream(resource), "UTF8"));
		while (listRead.readLine() != null) output++;
		listRead.close();
		return output;
	}
	public static int[] measureResource2D(String resource) throws IOException
	{
		int[] output = new int[2];
		output[0] = measureResource(resource);
		BufferedReader listRead = new BufferedReader(new
				InputStreamReader(ParseXML.class.getResourceAsStream(resource), "UTF8"));
		String line = listRead.readLine();
		listRead.close();
		output[1] = line.length() - line.replace(",", "").length()+1;
		return output;
	}
}
