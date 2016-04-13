package OtherExports;

import java.util.ArrayList;
import java.util.Map;

import com.jgoodies.forms.layout.Size;

import Imports.SAP;
import Imports.Statics;
import PMainWindow.Func;
import Records.CompleteRecord;
import Records.RFL;

public class PersonReport {
	public static String[][] affiliations(String[][] data, String[][] sap, String[] unitId){
		String unitUsos = "";
		SAP.SAP = sap;
		String[][] fields = RFL.list();
		for (int x=0; x<Statics.facUSOS.length; x++){
			if (unitId[0].equals(Statics.facUSOS[x][0])) unitUsos = Statics.facUSOS[x][1];
		}
		int colAf = Func.findColumn(data[0], "dc.affiliation[pl]");
		int colD = Func.findColumn(data[0], "dc.date.issued[pl]");
		int line = 0;
		String[] entry;
		String author;
		ArrayList<String> authors;
		String idSAP, date, year;
		String[] admin;
		ArrayList<String[]> list = new ArrayList<String[]>();
		for (int x=1; x<data.length; x++){
			year = data[x][colD];
			if (data[x][colAf].contains(unitId[3]) && (year.equals("2013") || year.equals("2014") || year.equals("2015"))){
				CompleteRecord record = new CompleteRecord(data[x], data[0]);
				authors = record.getContributors();
				admin = record.getAdmins();
				date = record.getMap().get(fields[17][2])[0];
				for (int y=0; y<authors.size(); y++){
					author = authors.get(y);
					//ID SAP
					if (authors.get(y).indexOf("[")>-1){
						idSAP = author.substring(author.indexOf("[")+1, author.indexOf("]"));
					}
					else idSAP="";
					if (isAuthorAffiliatedToPBN(idSAP, date, admin, record, fields, unitUsos, unitId)){
						String[] unit = findUnit(idSAP, record, date);
						int count = Integer.parseInt(unit[0]);
						if (unit!=null && count<50000150){
							entry = new String[6];
							entry[0] = record.getID();
							entry[1] = author;
							entry[2] = unit[0];
							entry[3] = unit[1];
							entry[5] = "1";
							if (entry[5].equals("1") && idSAP.indexOf("SAP")>-1) entry[4]="1";
							else entry[4] = "0";
							list.add(entry);
						}
					}
				}
			}
		}
		return Func.toArray(list);
	}
	private static boolean isExportable(String[] ids, String[] admin, String date, String usos, String[] unitId, String unitUsos)
	{
		for (int x=0; x<admin.length; x++){
			if (admin[x].indexOf("[AB]")>-1 && admin[x].indexOf(unitId[1])>-1) return true;
		}
		if (date.replaceAll("[^0-9]+", "").length()<4) return false;
		String year = date.substring(0, 4);
		if (!year.equals("2013") || !year.equals("2014") || !year.equals("2015")) return false;
		int[][] dates = new int[ids.length][7];
		int a;
		int b=0;
		ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();
		String[][] author;
		for (int x=0; x<ids.length; x++)
		{
			lists.add(new ArrayList<String>());
			a=0;
			if (ids[x].indexOf("" + "SAP")>-1){
				author = SAP.findPerson(ids[x].substring(3));
				if (author!=null){
					for (int y=0; y<author.length; y++){
						if (author[y]==null) break;
						if (author[y][2].equals(unitId[1]))
						{
							dates[x][a] = Integer.parseInt(author[y][1]);
							lists.get(x).add(author[y][2]);
							a++; b++;
						}
					}
				}
			}
			else if (ids[x].indexOf("USOS")>-1 && usos.length()>0){
				String[] usosField = usos.substring(0, usos.length()-1).split("; ");
				for (int y=0; y<usosField.length; y++){
					if (usosField[y].indexOf(unitUsos)>-1 && usosField[y].indexOf(ids[x])>-1){
						dates[x][a] = 11;//ma³a wartoœæ, ¿eby przesz³a warunek koñcowy
						a++;b++;
						break;
					}
				}
			}
		}
		if (b==0) return false;
		int startDate = (Integer.parseInt(date.replaceAll("[^0-9]+", ""))-1900)*365+30;
		int endDate = startDate + 365;
		for (int x=0; x<dates.length; x++)
		{
			a=0;
			for (int y=0; y<dates[x].length; y++)
			{
				if (dates[x][y]==0) break; 
				if (dates[x][y]<endDate)
					a++;
			}
			if (a==1) return true; //sprawdzaæ rekordy doktorantów
			a=0;
			for (int y=0; y<dates[x].length; y++)
			{
				if (dates[x][y]<endDate && dates[x][y]>=startDate) a++;
			}
			if (a<2) return true; //je¿eli jest tylko jedna data mniejsza ni¿ startDate, wówczas a=0;
			else{
				b=0;
				for (int y=0; y<dates[x].length; y++){
					if (dates[x][y]==0) break;
					if (dates[x][y]<endDate && dates[x][y]>=startDate && lists.get(x).get(y).equals(unitId[1])){
						b++;
					}
				}
				if (a==b) return true;
			}
		}
		return false;
	}
	private static boolean isMarkedAsAffiliated(String id, String[] admin, String[] unitId){
		for (int x=0; x<admin.length; x++){
			if (admin[x].startsWith("[AB]") && admin[x].indexOf(id)>-1 && admin[x].indexOf(unitId[1])>-1) return true;
		}
		return false;
	}
	private static boolean isAuthorExportable(String idSAP, String date, String[] admin, CompleteRecord record,
			String[][] fields, String unitUsos, String[] unitId)
	{
		String id=idSAP;
		ArrayList<String> depIds = new ArrayList<String>();
		ArrayList<String> depIds2 = new ArrayList<String>();
		if (idSAP.indexOf("SAP")>-1) id = idSAP.substring(3);
		else if (idSAP.indexOf("USOS")>-1) id = idSAP.substring(4);
		if (isMarkedAsAffiliated(id, admin, unitId)) return true;
		if (!isAffiliated(id, record, fields)) return false;
		int[] dates = new int[20];
		if (idSAP.indexOf("USOS")>-1){
			String usos = record.getMap().get(fields[43][2])[0];
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
		String[][] personInSAP=null;
		if (!id.equals(""))
			personInSAP = SAP.findPerson(id);
		if (personInSAP==null) return false;
		for (int x=0; x<personInSAP.length; x++)
		{
			if (personInSAP[x]!=null) /*&& sapData[x][2].equals(unitId[1])*/
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
		if (a==1 && depIds.get(0).equals(unitId[0])) return true;
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
		if (depIds2.size()==1 && depIds2.get(0).equals(unitId[0])) return true;
		else if (depIds2.size()==1) return false;
		else {
			for (int x=0; x<depIds2.size(); x++){
				if (depIds2.get(x).equals(unitId)) a++;
			}
			if (a==depIds2.size()) return true;
		}
		return false;
	}
	private static boolean isAuthorAffiliatedToPBN(String idSAP, String date, String[] admin, CompleteRecord record,
			String[][] fields, String unitUsos, String[] unitId)
	{
		String id=idSAP;
		ArrayList<String> depIds = new ArrayList<String>();
		ArrayList<String> depIds2 = new ArrayList<String>();
		if (idSAP.indexOf("SAP")>-1) id = idSAP.substring(3);
		else if (idSAP.indexOf("USOS")>-1) id = idSAP.substring(4);
		if (!isAffiliated(id, record, fields)) return false;
		for (int x=0; x<Statics.facUSOS.length; x++){
			if (isMarkedAsAffiliated(id, admin, Statics.facUSOS[x])) return true;
		}
		if (idSAP.equals("")) return false;
		int[] dates = new int[20];
		if (idSAP.indexOf("USOS")>-1){
			String usos = record.getMap().get(fields[43][2])[0];
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
		String[][] personInSAP=null;
		if (!id.equals(""))
			personInSAP = SAP.findPerson(id);
		if (personInSAP==null) return false;
		for (int x=0; x<personInSAP.length; x++)
		{
			if (personInSAP[x]!=null) /*&& sapData[x][2].equals(unitId[1])*/
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
		if (a>0) return true;
		return false;
	}
	private static boolean isAffiliated(String id, CompleteRecord record, String[][] fields)
	{
		Map<String, String[]> rMap = record.getMap();
		String[] check = rMap.get(fields[35][2]);
		for (int x=0; x<check.length; x++)
		{
			if (check[x].startsWith("[AU]") && check[x].contains(id)) return false;
		}
		check = rMap.get(fields[46][2]);
		for (int x=0; x<check.length; x++)
		{
			if (check[x].startsWith("[AU]") && check[x].contains(id)) return false;
		}
		return true;
	}
	/**
	 * 
	 * @param idSAP
	 * @param rMap
	 * @return Zwraca String w postaci {id jednostki, pe³na nazwa jednostki}, je¿eli zwraca null, autor nie afiliuje wg SAP
	 */
	private static String[] findUnit(String idSAP, CompleteRecord record, String date){
		Map<String, String[]> rMap = record.getMap();
		String id=idSAP;
		ArrayList<String> depIds = new ArrayList<String>();
		ArrayList<String> depIds2 = new ArrayList<String>();
		if (idSAP.indexOf("SAP")>-1) id = idSAP.substring(3);
		else if (idSAP.indexOf("USOS")>-1) id = idSAP.substring(4);
		int[] dates = new int[20];
		
		//Sprawdziæ doktorantów (USOS)
		if (idSAP.indexOf("USOS")>-1){
			String usos = rMap.get(RFL.F[RFL.USOS][2])[0];
			if (usos.length()==0) return null;
			String[] usosField = usos.substring(0, usos.length()-1).split("; ");
			for (int x=0; x<usosField.length; x++){
				if (usosField[x].indexOf(idSAP)>-1){
					for (int y=0; y<Statics.facUSOS.length; y++){
						if (usosField[x].indexOf(Statics.facUSOS[y][1])>-1){
							return new String[]{Statics.facUSOS[y][0], Statics.facUSOS[y][2]};
						}
					}
				}
			}
			return null;
		}
		
		//Sprawdziæ pole AB
		String[] r = findUnitInABFields(record, idSAP);
		if (r!=null) return r;
		
		int a = 0;
		String[][] personInSAP=null;
		if (!id.equals(""))
			personInSAP = SAP.findPerson(id);
		if (personInSAP==null) return null;
		for (int x=0; x<personInSAP.length; x++)
		{
			if (personInSAP[x]!=null) /*&& sapData[x][2].equals(unitId[1])*/
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
				depIds2.add(depIds.get(x));
				a++;
		}
		if (a==1) return new String[]{depIds2.get(0), Statics.findDepartmentName(depIds2.get(0))};
		depIds2 = new ArrayList<String>();
		for (int x=0; x<dates.length; x++)
		{
			if (dates[x]==0) break;
			if (dates[x]<endDate && dates[x]>=startDate-10){
				depIds2.add(depIds.get(x));
			}
		}
		if (depIds2.size()==0) depIds2.add(depIds.get(depIds.size()-1));
		a=0;
		if (depIds2.size()==1){
			return new String[]{depIds2.get(0), Statics.findDepartmentName(depIds2.get(0))};
		}
		else return new String[]{depIds.get(depIds.size()-1), Statics.findDepartmentName(depIds.get(depIds.size()-1))};
	}
	/**
	 * 
	 * @return Zwraca String w postaci {id jednostki, pe³na nazwa jednostki}, je¿eli zwraca null, autor nie afiliuje wg SAP
	 */
	public static String[] findUnitInABFields(CompleteRecord rec, String idSAP){
		String[] check = rec.getAdmins();
		String id;
		for (int x=0; x<check.length; x++)
		{
			if (check[x].startsWith("[AB]") && check[x].contains(idSAP) && check[x].indexOf("5000")>-1){
				id=check[x].substring(check[x].indexOf("5000")).replaceAll("[^0-9]+", "");
				return new String[]{id,Statics.findDepartmentName(id)};
			}
		}
		return null;
	}
}
