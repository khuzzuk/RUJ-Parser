package PMainWindow;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class Duplicates extends SwingWorker<String, Integer>
{
	private MetAnWindow window;
	private String[][][] duplicatesID;
	private ExcludedDuplicatesFile exclusions;
	private String[][] input;
	private String[][] convData;
	private JProgressBar progressbar;
	private int maximum, filled;
	int nID = 0;
	private String[] columnNamesDuplicates = {"id", "Tytu³","URL"};
	private int[][] hided;
	private int[][] toHide;
	
	public Duplicates(String[][] input, JProgressBar progressBar, MetAnWindow window, ExcludedDuplicatesFile exclusions) throws IOException
	{
		this.window = window;
		this.progressbar = progressBar;
		this.input = input;
		this.exclusions = exclusions;
		progressbar.setIndeterminate(true);
	}
	protected String doInBackground()
		{
		ConvCSV convert = new ConvCSV(input);
		convData = convert.getData();
		int c = convData[0].length-1;
		String title1;
		String title2;
		int hs[][] = new int[1000][2];
		int z = 0;
		double vector;
		duplicatesID = new String[10000][2][3];
		int nID = 0;
		char k;
		progressbar.setMaximum(convData.length);
		progressbar.setIndeterminate(false);
		maximum = convData.length;
		boolean condition;
		for (int x=0; x<convData.length; x++)
		{
			if (x%100==0) {int progress = x/100; filled = x; publish(progress);}
			for (int y=x+1; y<convData.length; y++)
			{
				vector = Integer.valueOf(convData[y][c])-Integer.valueOf(convData[x][c]);
				if (vector<5)//pierwszy rozmiar wektora
				{
					condition = exclusions.checkExclusions(convData[x][18], convData[y][18]);
					if (!condition)
					{
						if (checkByWordIndex(x, y)) addRecordToReport(x, y);
					}
				}
				else break;
			}
		}
		return "Finished";
	}
	private boolean checkByWordIndex(int x, int y)
	{
		String[] title1 = convData[x][19].split(" ");
		String title2 = convData[y][19];
		int a=0;
		for (int z=0; z<title1.length; z++)
		{
			if (title2.indexOf(title1[z])>-1) a++;
		}
		if (title1.length>5)
		{
			if (a>title1.length/2+title1.length/4) return true;
		}
		else
		{
			if (a>title1.length/2+1) return true;
		}
		return false;
	}
	private boolean checkVectorInHyperspace(int x, int y)
	{
		//variables
		String title1, title2;
		int z=0;
		int hs[][] = new int[1000][2];
		char k;
		double vector;
		
		title1 = convData[x][0];
		title2 = convData[y][0];
			for (z=0; z<10000; z++)
			{
				if (title1.length()==0) break;
				k = title1.charAt(0);
				hs[z][0] = title1.length()-title1.replace(Character.toString(k), "").length();
				hs[z][1] = title2.length()-title2.replace(Character.toString(k), "").length();
				title1 = title1.replace(Character.toString(k), "");
				title2 = title2.replace(Character.toString(k), "");
			}
			if (!title2.equals(""))
			{
				for (z=z+1; z<10000; z++)
				{
					if (title2.length() == 0) break;
					k = title2.charAt(0);
					hs[z][1] = title2.length()-title2.replace(Character.toString(k), "").length();
					title2 = title2.replace(Character.toString(k), "");
				}
			}
		vector = 0;
		for (int v=0; v<=z; v++)
		{
			vector += Math.sqrt((hs[v][0]*hs[v][0])-(hs[v][1]*hs[v][1]));
		}
		if (vector<5) return true;//drugi rozmiar wektora
		else return false;
	}
	private void addRecordToReport(int x, int y)
	{
		duplicatesID[nID][0][0] = convData[x][18];
		duplicatesID[nID][0][1] = convData[x][19];
		if (convData[x][14].equals("")) duplicatesID[nID][0][2] = convData[x][15]; else duplicatesID[nID][0][2] = convData[x][14];
		duplicatesID[nID][1][0] = convData[y][18];
		duplicatesID[nID][1][1] = convData[y][19];
		if (convData[y][14].equals("")) duplicatesID[nID][1][2] = convData[y][15]; else duplicatesID[nID][1][2] = convData[y][14];
		nID++;
	}
	@Override
	protected void process(List<Integer> progress)
	{
		progressbar.setValue(filled);
	}
	@Override
	protected void done()
	{
		try{get();}catch (Exception e) {e.printStackTrace();}
		Toolkit.getDefaultToolkit().beep();
		progressbar.setValue(maximum);
		window.createTable(getDuplicatesTable(), columnNamesDuplicates);
		window.colorTable();
	}
	public String[][][] getID()
	{
		return duplicatesID;
	}
	public int countDuplicates()
	{
		int count=0;
		for (int x=0; x<duplicatesID.length; x++) {if (duplicatesID[x][0][0] == null) break; count++;}
		return count;
	}
	public String[][] getDuplicatesTable()
	{
		int a = countDuplicates()*2;
		String [][] output = new String[a][3];
		for (int x=0; x<a; x++)
		{
			output[x][0] = duplicatesID[x/2][x%2][0];
			output[x][1] = duplicatesID[x/2][x%2][1];
			output[x][2] = duplicatesID[x/2][x%2][2];
		}
		return output;
	}
	private void readFile()
	{
		try
		{
			String userDir = System.getProperty("user.home");
			File Dir = new File(userDir, ".MetAn");
			if (!Dir.exists()) Dir.mkdir();
			File exclusionList = new File(Dir, "hide.txt");
			String[] separatedLine;
			if (exclusionList.exists())
			{
				int a = MeasureTable.measureList(exclusionList);
				BufferedReader reader = new BufferedReader(new FileReader(exclusionList));
				hided = new int[a][];
				String line;
				for (int x=0; x<a; x++)
				{
					line = reader.readLine();
					separatedLine = line.split("—");
					hided[x] = new int[separatedLine.length];
					for (int y=0; y<separatedLine.length; y++)
					{
						hided[x][y] = Integer.parseInt(separatedLine[y]);
					}
				}
				reader.close();
			}
			else 
				{
				hided = new int[][]{{0, 0}};
				}
		}
		catch (IOException e) {};
		hided = new int[][]{{0, 0}};
	}
	private void hidedCopy()
	{
		for (int x=0; x<hided.length; x++) toHide[x] = hided[x];
	}
	private boolean checkHided(int a, int b)
	{
		for (int x=0; x<hided.length; x++)
		{
			if (hided[x][0]==a)
			{
				for (int y=0; y<hided[x].length; y++)
				{
					if (hided[x][y]==b) return false;
				}
			}
		}
		return true;
	}
	private void saveHidingResult()
	{
		try
		{
			String userDir = System.getProperty("user.home");
			File Dir = new File(userDir, ".MetAn");
			if (!Dir.exists()) Dir.mkdir();
			File exclusionList = new File(Dir, "hide.txt");
			Writer writer = new BufferedWriter(new FileWriter(exclusionList));
			String line;
			for (int x=0; x<toHide.length; x++)
			{
				line = "" + toHide[x][0];
				for (int y=1; y<toHide[x].length; y++)
				{
					line = line + "—" + toHide[x][y]; 
				}
				writer.append(line).append("\r\n");
			}
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	private void addRecordToHide(int a, int b)
	{
		int[] temp;
		for (int x=0; x<toHide.length; x++)
		{
			if (toHide[x]==null)
			{
				toHide[x] = new int[2];
				toHide[x][0] = a;
				toHide[x][1] = b;
				break;
			}
			else if (a==toHide[x][0] || toHide[x][0]==0)
			{
				temp = toHide[x];
				toHide[x] = new int[toHide[x].length];
				toHide[x][0] = a;
				for (int y=1; y<temp.length; y++) toHide[x][y] = temp[y];
				toHide[x][toHide[x].length-1] = b;
				break;
			}
		}
	}
	public static int[][] clearHidedArray(int[][] input)
	{
		int c=0;
		for (int x=0; x<input.length; x++)
		{
			if (input[x]==null) break;
			if (input[x][0]!=0)
			{
				c++;
			}
		}
		int[][] out = new int[c][];
		for (int x=0; x<input.length; x++)
		{
			if (input[x]==null) break;
			if (input[x][0]!=0) out[x] = input[x];
		}
		return out;
	}
}
