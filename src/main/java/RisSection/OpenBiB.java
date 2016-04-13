package RisSection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import PMainWindow.Func;
import PMainWindow.MeasureTable;

public class OpenBiB
{
	public static String getAuthors(File bibFile) throws IOException
	{
		int a = MeasureTable.measureList(bibFile);
		BufferedReader readBIB = new BufferedReader(new InputStreamReader(new FileInputStream(bibFile), "UTF8"));
		String line;
		String authorsLines = "";
		String out = "";
		ArrayList<String> outputList = new ArrayList<String>();
		outputList.ensureCapacity(a);
		for (int x=0; x<a; x++)
		{
			line = readBIB.readLine();
			if (line.startsWith("  author="))
			{
				authorsLines=line.substring(line.indexOf("{")+1, Math.max(line.indexOf("}"), line.length()));
				for (int y=x+1; y<a; y++)
				{
					x++;
					line = readBIB.readLine();
					if (!line.startsWith(" ") && !line.startsWith("}")) authorsLines = authorsLines + " " + line.replaceAll("},", "");
					else break;
				}
			}
		}
		String[] authors = authorsLines.split(" and  ");
		String[][] person;
		if (authors.length==1) authors = authorsLines.split(" and ");
		for (int x=0; x<authors.length; x++)
		{
			person = Func.splitToNamesAndSurnames(authors[x]);
			if (x>0) out = out + "—";
			for (int y=0; y<person[0].length; y++)
				{
					if (y>0) out = out + " ";
					out=out + person[0][y];
				}
			out = out + ", ";
			for (int y=0; y<person[1].length; y++)
			{
				if (y>0) out = out + " ";
				out = out + person[1][y] + ".";
			}
		}
		readBIB.close();
		return out;
	}
	public static String getID(File inputFile, String[][] data) throws IOException
	{
		String id = "";
		int a = MeasureTable.measureList(inputFile);
		BufferedReader readBIB = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));
		String line;
		String journal="",volume="",number="",pages="",year="";
		for (int x=0; x<a; x++)
		{
			line = readBIB.readLine();
			if (line.startsWith("  journal=")) journal = line.substring(line.indexOf("{")+1, Math.max(line.indexOf("}")-1, line.length()-2));
			if (line.startsWith("  volume=")) volume = line.substring(line.indexOf("{")+1, Math.max(line.indexOf("}")-1, line.length()-2));
			if (line.startsWith("  number=")) number = line.substring(line.indexOf("{")+1, Math.max(line.indexOf("}")-1, line.length()-2));
			if (line.startsWith("  pages=")) pages = line.substring(line.indexOf("{")+1, Math.max(line.indexOf("}")-1, line.length()-2));
			if (line.startsWith("  year=")) year = line.substring(line.indexOf("{")+1, Math.max(line.indexOf("}")-1, line.length()-2));
		}
		int b = data.length;
		String[][] search = new String[6][2];
		int[] cols = new int[6];
		search[0][0] = "dc.title.journal[pl]"; search[0][1] = journal;
		search[1][0] = "dc.description.number[pl]"; search[1][1] = number;
		search[2][0] = "dc.description.volume[pl]"; search[2][1] = volume;
		search[3][0] = "dc.description.physical[pl]"; search[3][1] = pages;
		search[4][0] = "dc.date.issued[pl]"; search[4][1] = year;
		search[5][0] = "dc.identifier.articleid[pl]"; search[5][1] = pages;
		for (int x=0; x<search.length; x++)
		{
			cols[x] = Func.findColumn(data[0], search[x][0]);
		}
		int points;
		int doiCol = Func.findColumn(data[0], "dc.identifier.doi[pl]");
		String doiId;
		for (int x=1; x<b; x++)
		{
			points = 0;
			doiId = Func.lookForArticleID(data[x][doiCol]);
			if (!doiId.equals("") && doiId.equals(search[5][1])) points++;
			for (int y=0; y<search.length; y++)
			{
				if (data[x][cols[y]].equals(search[y][1])) points++;
			}
			if (points>4)
			{
				id=data[x][0];
				break;
			}
		}
		readBIB.close();
		return id;
	}
}