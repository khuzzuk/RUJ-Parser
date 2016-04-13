package Imports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ExportXML.ParseXML;
import PMainWindow.MeasureTable;

public class MNiSWPoints
{
	public static String[][] loadPoints() throws IOException
	{
		String line;
		int a = MeasureTable.measureResource("/lista.csv");
		String[][] out = new String[a-1][3];
		
		BufferedReader reader = new
				BufferedReader(new
						InputStreamReader(ParseXML.class.getResourceAsStream("/lista.csv"), "UTF8"));
		
		
		for (int x=0; x<a-1; x++)
		{
			line = reader.readLine();
			out[x] = line.substring(1, line.length()-1).split("','");
		}
	return out;
	}
	public static int[][] findCols(String[] cols)
	{
		int counter = 0;
		String[] temp;
		for (int x=0; x<cols.length; x++)
		{
			if (cols[x].indexOf("punkty")>-1) counter++;
		}
		int[][] out = new int[counter][2];
		int a = 0;
		for (int x=3; x<cols.length; x++)
		{
			if (cols[x].indexOf("punkty")>-1)
			{
				temp = cols[x].split("_");
				out[a][0] = x;
				out[a][1] = Integer.parseInt(temp[0]);
				a++;
			}
		}
		return out;
	}
}