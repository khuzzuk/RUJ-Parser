package OtherExports;

import java.io.IOException;
import java.util.ArrayList;

import PMainWindow.Func;
import PMainWindow.SaveCSV;

public class OpenAccessReport {
	public OpenAccessReport(String[][] input, String unit) throws IOException{
		int affcol = Func.findColumn(input[0], "dc.affiliation[pl]");
		int[] cols = new int[9];
		cols[0] = 0;
		cols[1] = Func.findColumn(input[0], "dc.title[pl]");
		cols[2] = Func.findColumn(input[0], "dc.contributor.author[pl]");
		cols[3] = Func.findColumn(input[0], "dc.date.issued[pl]");
		cols[4] = Func.findColumn(input[0], "dc.description.physical[pl]");
		cols[5] = Func.findColumn(input[0], "dc.rights.original[pl]");
		cols[6] = Func.findColumn(input[0], "dc.title.original[pl]");
		cols[7] = Func.findColumn(input[0], "dc.subtype[pl]");
		cols[8] = Func.findColumn(input[0], "dc.type[pl]");
		String[] columns = new String[cols.length];
		for (int x=0; x<columns.length; x++){
			columns[x] = input[0][cols[x]];
		}
		ArrayList<String[]> list;
		String[] line;
		String[][] writer;
		SaveCSV save;
		int date;
		String[] faculties = Func.findUniqueStrings(input, unit, affcol);
		for (int x=0; x<faculties.length; x++){
			list = new ArrayList<String[]>();
			for (int y=1; y<input.length; y++){
				if (input[y][cols[3]].replaceAll("[^0-9]+", "").equals(""))
					date = 0;
				else date = Integer.parseInt(input[y][cols[3]].replaceAll("[^0-9]+", ""));
				if (input[y][affcol].indexOf(faculties[x])>-1){
					if (date>2012){
						if(input[y][cols[8]].equals("Book") || input[y][cols[8]].equals("BookSection") ||
								input[y][cols[8]].equals("JournalArticle")){
							line = new String[cols.length];
							for (int z=0; z<cols.length; z++){
								line[z] = input[y][cols[z]];
							}
							list.add(line);
						}
					}
				}
			}
			writer = new String[list.size()][];
			for (int y=0; y<list.size(); y++){
				writer[y] = list.get(y);
			}
			String file = "D:/RUJ/Tabelki/"+faculties[x].substring(Math.min(faculties[x].indexOf(":")+2,faculties[x].length()-1))+".csv";
			if (writer.length>0)
			save = new SaveCSV(file, writer, columns);
		}
	}
}
