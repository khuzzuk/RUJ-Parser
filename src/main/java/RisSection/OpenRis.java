package RisSection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import PMainWindow.Func;
import PMainWindow.MeasureTable;

public class OpenRis
{
	public static String getAuthors(String inputFile) throws UnsupportedEncodingException, IOException
	{
		int a = MeasureTable.measureList(inputFile);
		BufferedReader readRIS = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));
		String line;
		ArrayList outputList = new ArrayList<String>();
		outputList.ensureCapacity(a);
		for (int x=0; x<a; x++)
		{
			line = readRIS.readLine();
			if (line.indexOf("AU  - ")>-1)
			{
				outputList.add(line.replaceAll("AU  - ", ""));
			}
		}
		readRIS.close();
		outputList.trimToSize();
		String fieldValue = "[RT]";
		int b = outputList.size();
		for (int x=0; x<b-1; x++)
		{
			fieldValue = fieldValue + outputList.get(x) + "—";
		}
		fieldValue = fieldValue + outputList.get(b-1);
		return fieldValue;
	}
	public static String getAuthors(File inputFile) throws UnsupportedEncodingException, IOException
	{
		int a = MeasureTable.measureList(inputFile);
		BufferedReader readRIS = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));
		String line;
		ArrayList<String> outputList = new ArrayList<String>();
		outputList.ensureCapacity(a);
		for (int x=0; x<a; x++)
		{
			line = readRIS.readLine();
			if (line.indexOf("AU  - ")>-1 || line.indexOf("A1  - ")>-1)
			{
				if (line.length()<80)
				{
					outputList.add(line.replaceAll("AU  - ", "").replaceAll("A1  - ", ""));
				}
				else if (line.indexOf(" and ")>-1)
				{
					outputList.add(splitAuthors(line));
				}
				else
				{
					outputList.add(reparseAuthors(line));
				}
				for (int y=x+1; y<a; y++)
				{
					x++;
					line = readRIS.readLine();
					if (line.indexOf("  -")==-1)
					{
						outputList.add(splitAuthors(line));
					}
					else break;
				}
			}
		}
		readRIS.close();
		outputList.trimToSize();
		String fieldValue = "[RT]";
		int b = outputList.size();
		for (int x=0; x<b-1; x++)
		{
			fieldValue = fieldValue + outputList.get(x) + "—";
		}
		fieldValue = fieldValue + outputList.get(b-1);
		return fieldValue;
	}
	public static String getDOI(File inputFile) throws IOException
	{
		String output = "";
		int a = MeasureTable.measureList(inputFile);
		BufferedReader readRIS = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));
		String line;
		for (int x=0; x<a; x++)
		{
			line = readRIS.readLine();
			if (line.indexOf("DO  - ")>-1) output = line.replaceAll("DO  - ", "");
			else if (line.indexOf("ID  - ")>-1) output = line.replaceAll("ID  - ", "");
		}
		output = output.replaceAll("http://dx\\.doi\\.org/", "");
		return output;
	}
	public static String lookForID(String doi, String[][] csvData)
	{
		int col = Func.findColumn(csvData[0], "dc.identifier.doi[pl]");
		String id="";
		int a = csvData.length;
		for (int x=0; x<a; x++)
		{
			if (csvData[x][col].indexOf(doi)>-1)
			{
				if (id.equals("")) id = csvData[x][0];
				else id = id + "||" + csvData[x][0];
			}
		}
		return id;
	}
	private static String reparseAuthors(String line)
	{
		line = line.replaceAll("AU  - ", "").replaceAll("A1  - ", "")/*.replaceAll("\\. ", "\\.").replaceAll("\\.", "\\. ")*/;
		String[] splitted = line.split(" ");
		int a = splitted.length;
		int b = a/2+1;
		String output = "";
		if (a%2==0)
		{
			output = splitted[0] + ", " + splitted[a/2+1];
			for (int x=1; x<a/2; x++)
			{
				output = output + "—" + splitted[x] + ", " + splitted[x+a/2];
				
			}
		}
		return output;
	}
	private static String splitAuthors(String line)
	{
		line = line.replaceAll("AU  - ", "").replaceAll("A1  - ", "");
		String[] splitted = line.split(" and ");
		int a = splitted.length;
		String output = splitted[0];
		for (int x=1; x<a; x++)
		{
			output = output + "—" + splitted[x];
		}
		return output;
	}
}
