package OtherExports;

import java.util.ArrayList;

import Imports.SAP;
import PMainWindow.Func;
import Records.CompleteRecord;
import Records.FieldsFromArray;
import Records.RFL;

public class BookEditorshipQuery {
	public static CompleteRecord record;
	public static String[][] query(String[][] data){
		int typeCol = Func.findColumn(data[0], RFL.F[RFL.TYPE][2]);
		int edsCol = Func.findColumn(data[0], RFL.F[RFL.EDITOR][2]);
		int contrCol = Func.findColumn(data[0], RFL.F[RFL.AUTHORS][2]);
		ArrayList<String[]> list = new ArrayList<String[]>();
		String[] eds;
		for (int x=1; x<data.length; x++){
			if (data[x][typeCol].equals("Book")){
				if (!data[x][edsCol].equals("")){
					record = new CompleteRecord(data[x], data[0]);
					eds = FieldsFromArray.getField(data[x], edsCol);
				}
			}
		}
		return new String[][]{};
	}
	private String isMarkedAsAffiliated(String id){
		String[] admin = record.getAdmins();
		for (int x=0; x<admin.length; x++){
			if (admin[x].startsWith("[AB]") && admin[x].indexOf(id)>-1) 
				return admin[x].substring(admin[x].indexOf("5000"));
		}
		return "";
	}
/*	private String isAuthorExportable(String idSAP, String date)
	{
		String id=idSAP;
		ArrayList<String> depIds = new ArrayList<String>();
		ArrayList<String> depIds2 = new ArrayList<String>();
		if (idSAP.indexOf("SAP")>-1) id = idSAP.substring(3);
		else if (idSAP.indexOf("USOS")>-1) id = idSAP.substring(4);
		String marked = isMarkedAsAffiliated(id);
		if (!marked.equals("")) return marked;
		if (!isAffiliated(id)) return false;
		int[] dates = new int[20];
		if (idSAP.indexOf("USOS")>-1){
			String usos = rMap.get(fields[43][2])[0];
			if (usos.length()==0) return false;
			String[] usosField = usos.substring(0, usos.length()-1).split("; ");
			for (int x=0; x<usosField.length; x++){
				if (usosField[x].indexOf(unitUsos)>-1 && usosField[x].indexOf(idSAP)>-1){
					return true;
				}
			}
			return false;
		}
		int a = 0;
		String[][] personInSAP = SAP.findPerson(id);
		if (personInSAP==null) return false;
		for (int x=0; x<personInSAP.length; x++)
		{
			if (personInSAP[x]!=null)
			{
				dates[a] = Integer.parseInt(personInSAP[x][1]);
				depIds.add(personInSAP[x][2]);
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
		if (a==1 && depIds.get(0).equals(unitId[1])) return true;
		else if (a==1) return false;
		for (int x=0; x<dates.length; x++)
		{
			if (dates[x]==0) break;
			if (dates[x]<endDate && dates[x]>=startDate-10){
				depIds2.add(depIds.get(x));
			}
		}
		if (depIds2.size()==0) depIds2.add(depIds.get(depIds.size()-1));
		a=0;
		if (depIds2.size()==1 && depIds2.get(0).equals(unitId[1])) return true;
		else if (depIds2.size()==1) return false;
		else {
			for (int x=0; x<depIds2.size(); x++){
				if (depIds2.get(x).equals(unitId)) a++;
			}
			if (a==depIds2.size()) return true;
		}
		return false;
	}
	private String isAffiliated(String id)
	{
		String[] check = record.getAdmins();
		for (int x=0; x<check.length; x++)
		{
			if (check[x].startsWith("[AU]") && check[x].contains(id)) return "";
		}
		return null;
	}*/
}
