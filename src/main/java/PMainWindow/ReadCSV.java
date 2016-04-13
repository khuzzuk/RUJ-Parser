package PMainWindow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class ReadCSV
{
	public ReadCSV()
	{
		
	}
	public static String[][] read(String pathCSV) throws IOException, UnsupportedEncodingException
	{
		String filePath = pathCSV;
		int dimensionRange[] = MeasureTable.measure2DTable(filePath);
		int a = dimensionRange[0];
		int b = dimensionRange[1];
		String[][] csvData = new String [a][b];
		File csvFile = new File(filePath);
		BufferedReader tableRead = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF8"));
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
		String actualLine;
		char zChar;
		char n1Char; char n2Char; char n3Char;
		
		//Odczyt CSV
		for (int x=1; x<a; x++)
		{
			actualLine = tableRead.readLine();
			actualLine = actualLine.replace(",\"\",", ",,");
			int charMax = actualLine.length();
			int z=0;
			for (int y=0; y<=b; y++)
			{
				for (z=z; z<charMax; z++)
				{
					if(z+2>charMax)
						{
						z++;
						break;
						}
					zChar = actualLine.charAt(z);
					if (zChar==34)
					{
						n1Char = actualLine.charAt(z+1);
						switch (n1Char)
						{
						case 34:
							csvData[x][y] = csvData[x][y] + n1Char;
							z+=2;
						default:
							z++;
							for (z=z; z<charMax; z++)
							{
								zChar = actualLine.charAt(z);
								if(zChar==34) //34="""
								{
									if (z+3>charMax) break;
									n1Char = actualLine.charAt(z+1);
									n2Char = actualLine.charAt(z+2);
									n3Char = actualLine.charAt(z+3);
									if (n1Char==34 && n3Char==44)
									{
										csvData[x][y] = csvData[x][y] + zChar;
										z+=3;
										break;
									}
									else if (n1Char==34 && n2Char!=34)
									{
										csvData[x][y] = csvData[x][y] + zChar;
										z++;
									}
									else if (n1Char==44)
									{
										z++;
										break;
									}
									else
									{
										csvData[x][y] = csvData[x][y] + zChar;
									}
								}
								else
								{
									csvData[x][y] = csvData[x][y] + zChar;
								}
							}
						}
					}
					z++;
					break;
				}
			}
		}
		tableRead.close();
		return csvData;
	}
}
