package Inspector;

import java.util.ArrayList;

import PMainWindow.Func;


public class Check
{
	public static String[][] ISBNs(String[][] input)
	{
		int[] col = new int[4];
		col[0] = Func.findColumn(input[0], "dc.title[pl]");
		col[1] = Func.findColumn(input[0], "dc.contributor.author[pl]");
		col[2] = Func.findColumn(input[0], "dc.identifier.isbn[pl]");
		col[3] = Func.findColumn(input[0], "dc.identifier.eisbn[pl]");
		ArrayList<String[]> list = new ArrayList<String[]>();
		String[] isbn, isbn2;
		boolean exit;
		if (col[0]!=-1 && col[1]!=-1 && col[2]!=-1 && col[3]!=-1)
		{
			read:
			for (int x=1; x<input.length; x++)
			{
				exit = true;
				isbn = input[x][col[2]].split("\\|\\|");
				isbn2 = input[x][col[3]].split("\\|\\|");
				if (isbn!=null && !isbn.equals(""))
				{
					for (int y=0; y<isbn.length; y++)
					{
						if (isbn[y].replaceAll("[-?0-9?X]+", "").length()>0)
						{
							list.add(getRecord(input[x], col));
							exit = false;
							break;
						}
						else if(isbn[y].length()<10 && isbn[y].length()>17)
						{
							list.add(getRecord(input[x], col));
							exit = false;
							break;
						}
					}
					if (exit)
					{
						for (int y=0; y<isbn2.length; y++)
						{
							if (isbn2[y].replaceAll("[-?0-9?X]+", "").length()>0)
							{
								list.add(getRecord(input[x], col));
								break;
							}
							else if(isbn2[y].length()<10 && isbn2[y].length()>17)
							{
								list.add(getRecord(input[x], col));
								break;
							}
						}
					}
				}
			}
			String[][] out = new String[list.size()][5];
			for (int x=0; x<list.size(); x++)
			{
				out[x] = list.get(x);
			}
			return out;
		}
		return null;
	}
	private static String[] getRecord(String[] record, int[] cols)
	{
		String[] out = new String[cols.length+1];
		out[0] = record[0];
		for (int x=0; x<cols.length; x++)
		{
			out[x+1] = record[cols[x]];
		}
		return out;
	}
}