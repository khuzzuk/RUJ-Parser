package OtherExports;

import java.io.IOException;
import java.util.ArrayList;

import Imports.ImportCSV;
import PMainWindow.Func;
import Records.FieldsFromArray;
import Records.RFL;

public class UnafiliatedReport {
	public static String[][] table(String[][] data){
		String[][] out;
		String[] authors, ids;
		String date, id, type, subType;
		ArrayList<String[]> list = new ArrayList<String[]>();
		String[][] fields = RFL.list();
		int autCol = Func.findColumn(data[0],"dc.contributor.author[pl]");
		int dateCol = Func.findColumn(data[0],"dc.date.issued[pl]");
		int typeCol = Func.findColumn(data[0], fields[36][2]);
		int subTypeCol = Func.findColumn(data[0], fields[37][2]);
		int titleCol = Func.findColumn(data[0], fields[0][2]);
		int flag;
		try {
			String[][] sapData = ImportCSV.readData("/osoby.csv", "\"");
			String[][] units = ImportCSV.readData("/units.csv", "\"");
			for (int x=1; x<data.length; x++){
				if (data[x][typeCol].equals("JournalArticle")||data[x][typeCol].equals("BookSection")||data[x][typeCol].equals("Book")){
					flag = 0;
					authors = data[x][autCol].split("\\|\\|");
					date = data[x][dateCol];
					type = data[x][typeCol];
					subType = data[x][subTypeCol]; 
					ids = FieldsFromArray.getSAPIDs(data[x], contributorsColumns(data[0], type, subType, fields));
					if (ids.length!=0){
						for (int y=0; y<ids.length; y++){
							if (date.replaceAll("[^0-9]+", "").length()>0 &&
									!isAuthorExportable(ids[y],date,sapData,units)){
								flag++;
							}
						}
						if (flag==authors.length) list.add(new String[]{data[x][0], data[x][titleCol]});
					}
					//else list.add(new String[]{data[x][0], data[x][titleCol]});
				}
			}
		} catch (IOException e) {e.printStackTrace();}
		out = new String[list.size()][];
		for (int x=0; x<out.length; x++){
			out[x] = list.get(x);
		}
		return out;
	}
	private static boolean isAuthorExportable(String idSAP, String date, String[][] sapData, String[][] units)
	{
		String id=idSAP;
		if (idSAP.indexOf("SAP")>-1) id = idSAP.substring(3);
		else if (idSAP.indexOf("USOS")>-1) id = idSAP.substring(4);
		int[] dates = new int[20];
		int a = 0;
		for (int x=0; x<sapData.length; x++)
		{
			if (id.equals(sapData[x][0]))
			{
				if (sapData[x][2].equals("0")) return false;
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
		if (a<2) return true;
		return false;
	}
	private static int[] contributorsColumns(String[] headlines, String type, String subType, String[][] fields)
	{
		int a = 3;
		if (subType.equals("SourceEditorship") || subType.equals("Translation") || subType.equals("CriticalEdition"))
			a++;
		if (type.equals("Book"))
			a++;
		
		int[] out = new int[a];
		out[0] = Func.findColumn(headlines, fields[3][2]);//authors
		out[1] = Func.findColumn(headlines, fields[5][2]);//translators
		out[2] = Func.findColumn(headlines, fields[6][2]);//reviewers
		if (subType.equals("SourceEditorship") || subType.equals("Translation") || subType.equals("CriticalEdition"))
		out[3] = Func.findColumn(headlines, fields[4][2]);//others
		if (type.equals("Book"))
		out[a-1] = Func.findColumn(headlines, fields[14][2]);//editors
		return out;
	}
}
