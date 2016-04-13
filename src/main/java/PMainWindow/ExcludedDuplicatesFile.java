package PMainWindow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class ExcludedDuplicatesFile
{
	private String[][] excluded;
	private File exclusionList;
	public ExcludedDuplicatesFile() throws IOException
	{
		String userDir = System.getProperty("user.home");
		File duplicatedDir = new File(userDir, ".MetAn");
		if (!duplicatedDir.exists()) duplicatedDir.mkdir();
		exclusionList = new File(duplicatedDir, "excluded.txt");
		if (exclusionList.exists())
		{
			int a = MeasureTable.measureList(exclusionList);
			BufferedReader reader = new BufferedReader(new FileReader(exclusionList));
			excluded = new String[a][2];
			String line;
			for (int x=0; x<a; x++)
			{
				line = reader.readLine();
				excluded[x] = line.split("-");
			}
			reader.close();
		}
		else excluded = null;
	}
	public void writeExclusions() throws IOException
	{
		String line;
		Writer writer = new BufferedWriter(new FileWriter(exclusionList));
		for (int x=0; x<excluded.length; x++)
		{
			line = excluded[x][0] + "-" + excluded[x][1];
			writer.append(line).append("\r\n");
		}
		writer.close();
	}
	public void addRecord(String id1, String id2) throws IOException
	{
		if (excluded==null) excluded = new String[1][2];
		else excluded = Func.addRow(excluded);
		int a = excluded.length-1;
		excluded[a][0] = id1; excluded[a][1] = id2;
		writeExclusions();
	}
	public String[][] getExclusions()
	{
		return excluded;
	}
	public boolean checkExclusions(String id1, String id2)
	{
		if (excluded!=null)
		{
			int a = excluded.length;
			for (int x=0; x<a; x++)
			{
				if (excluded[x][0].equals(id1) && excluded[x][1].equals(id2)) return true;
				else if (excluded[x][0].equals(id2) && excluded[x][1].equals(id1)) return true;
			}
		}
		return false;
	}
}