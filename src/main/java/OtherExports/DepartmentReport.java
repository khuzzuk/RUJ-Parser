package OtherExports;

import java.io.IOException;
import java.util.ArrayList;

import Imports.ImportCSV;
import PMainWindow.Func;
import PMainWindow.MetAnWindow;
import Records.FieldsFromArray;

public class DepartmentReport
{
	private String[][] sapData;
	private String unitId;
	private String[][] report;
	public DepartmentReport(String[][] data, String unitId) throws IOException
	{
		sapData = ImportCSV.readData("/osoby.csv", "\"");
		this.unitId = unitId;
		ArrayList<String[]> list = new ArrayList<String[]>();
		int[] Cols = new int[6];
		String type;
		String[] persons;
		Cols[0] = Func.findColumn(data[0], "dc.type[pl]");
		Cols[1] = Func.findColumn(data[0], "dc.contributor.author[pl]");
		Cols[2] = Func.findColumn(data[0], "dc.contributor.other[pl]");
		Cols[3] = Func.findColumn(data[0], "dc.contributor.translator[pl]");
		Cols[4] = Func.findColumn(data[0], "dc.title[pl]");
		Cols[5] = Func.findColumn(data[0], "dc.date.issued[pl]");
		int[] aut = new int[] {Cols[1], Cols[2], Cols[3]};
		for (int x=1; x<data.length; x++)
		{
			type = data[x][Cols[0]];
			if (type.equals("JournalArticle")||type.equals("BookSection")||type.equals("Book"))
			{
				persons = FieldsFromArray.getSAPIDs(data[x], aut);
				for (int y=0; y<persons.length; y++)
				{
					if (isAuthorExportable(persons[y], data[x][Cols[5]]))
					{
						list.add(new String[] {data[x][Cols[5]],
								data[x][Cols[4]],
								data[x][Cols[1]],
								data[x][Cols[2]],
								data[x][Cols[3]]});
						break;
					}
				}
			}
		}
		report = new String[list.size()][]; 
		for (int x=0; x<list.size(); x++)
		{
			report[x] = list.get(x);
		}
	}
	private boolean isAuthorExportable(String id, String date)
	{
		int[] dates = new int[20];
		int a = 0;
		for (int x=1; x<sapData.length; x++)
		{
			if (id.equals(sapData[x][0]) && sapData[x][2].equals(unitId))
			{
				dates[a] = Integer.parseInt(sapData[x][1]);
				a++;
			}
		}
		
		int startDate = (Integer.parseInt(date.replaceAll("[^0-9]+", ""))-1900)*365+30;
		int endDate = startDate + 365;
		a = 0;
		for (int x=0; x<dates.length; x++)
		{
			if (dates[x]==0) break;
			if (dates[x]<endDate)
				a++;
		}
		if (a==1) return true;
		a=0;
		for (int x=0; x<dates.length; x++)
		{
			if (dates[x]<endDate && dates[x]>=startDate) a++;
		}
		if (a==1) return true;
		return false;
	}
	public void passReport()
	{
		String[] colsHead = new String[]{"Data", "Tytu³", "Autorzy", "Wspó³twórcy", "T³umacze"};
		MetAnWindow.get().createTable(report, colsHead);
	}
	public String[][] getReport() {return report;}
}