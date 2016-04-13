package Inspector;

import java.util.ArrayList;

import PMainWindow.Func;
import Records.RFL;

public class RTFields {
	/**
	 * Listuj rekordy z wpisej [RT] w uwagach administratora
	 * @param data - tabela danych z RUJ
	 * @return tablica lsituj¹ca rekordy kluczem: id, tytu³, admin1, admin2, uwagi, instytucja sprawcza
	 */
	public static String[][] list(String[][] data){
		String[][] fields = RFL.list();
		int admin1 = Func.findColumn(data[0], fields[35][2]);
		int admin2 = Func.findColumn(data[0], fields[46][2]);
		int title = Func.findColumn(data[0], fields[0][2]);
		int additional = Func.findColumn(data[0], "dc.description.additional[pl]");
		int institution = Func.findColumn(data[0], fields[7][2]);
		ArrayList<String[]> list = new ArrayList<String[]>(1000);
		for (int x=1; x<data.length; x++){
			if (data[x][admin1].indexOf("[RT]")>-1 || data[x][admin2].indexOf("[RT]")>-1){
				list.add(new String[]{data[x][0], data[x][title], data[x][admin1], data[x][admin2],
						"", data[x][institution]});
			}
		}
		return Func.toArray(list);
	}
	public static String[][] addDescription(String[][] data){
		String text;
		int collaborators;
		for (int x=0; x<data.length; x++){
			if (data[x][4].indexOf("Artyku³ powsta³ przy wspó³pracy cz³onków")==-1){
				collaborators = data[x][2].length()-data[x][2].replaceAll("—", "").length();
				collaborators += data[x][3].length()-data[x][3].replaceAll("—", "").length();
				text = "Artyku³ powsta³ przy wspó³pracy cz³onków "+data[x][5].replace("||", " - ")+" w liczbie "+collaborators+" osób.";
				if (data[x][4].length()>0){
					if (data[x][4].endsWith("."))
						data[x][4]=data[x][4]+" "+text;
					else data[x][4]=data[x][4]+". "+text;
				}
				else data[x][4] = text;
			}
		}
		return data;
	}
}
