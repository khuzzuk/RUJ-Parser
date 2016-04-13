package OtherExports;

import java.io.IOException;

import Imports.ImportCSV;
import PMainWindow.Func;
import PMainWindow.MetAnWindow;

public class RemoveDuplicatesFromSAP {
	public static void remove(){
		String[][] sap;
		try {
			sap = ImportCSV.readData("/osoby.csv", "\"");
			makeNewTable(sap);
		} catch (IOException e) {e.printStackTrace();}
	}
	private static void makeNewTable(String[][] sap){
		String[][] med = new String[sap.length][5];
		int a=0;
		for (int x=1; x<sap.length-4; x++){
			for (int y=0; y<5; y++){
				if (sap[x].length>y) med[a][y] = sap[x][y].replace("\"","");
				else med[a][y]="";
			}
			while (isRemovable(sap[x][0],sap[x+1][0],sap[x][2],sap[x+1][2])){
				x++;
			}
			while (isRetired(sap[x][0],sap[x+1][0],sap[x][2].replace("\"",""),sap[x+1][2].replace("\"",""))){
				med[a][4] = sap[x+1][1];
				x++;
			}
			a++;
		}
		String[][] out = Func.removeEmptyRowsFromArray(med);
		MetAnWindow.myIdentity.createTable(out, new String[]{"SAPID","Date","Unit_id","Unit_name","retirement"});
	}
	private static boolean isRemovable(String id1, String id2, String unit1, String unit2){
		if (id1.equals(id2) && unit1.equals(unit2))
			return true;
		return false;
	}
	private static boolean isRetired(String id1, String id2, String unit1, String unit2){
		if (id1.equals(id2) && !unit1.equals("0") && unit2.equals("0"))
			return true;
		return false;
	}
}
