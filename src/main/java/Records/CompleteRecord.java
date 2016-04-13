package Records;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import Imports.ImportCSV;
import Imports.Statics;
import PMainWindow.Func;

public class CompleteRecord
{
	private String id;
	private static String[][] fieldsList;
	private static String[][] faculties;
	private static String[][] units;
	private static String[][] sapData;
	private Map<String, String[]> recordFields;
	private PersonEntry[] persons;
	public CompleteRecord (String[] csvLine, String[] csvHedlines)
	{
		id = csvLine[0];
		recordFields = new HashMap<>();
		recordFields.put("dc.affiliation[pl]", prepareField("dc.affiliation[pl]", csvHedlines, csvLine));
		recordFields.put("dc.contributor.author[pl]", prepareField("dc.contributor.author[pl]", csvHedlines, csvLine));
		recordFields.put("dc.contributor.editor[pl]", prepareField("dc.contributor.editor[pl]", csvHedlines, csvLine));
		recordFields.put("dc.contributor.intitution[pl]", prepareField("dc.contributor.intitution[pl]", csvHedlines, csvLine));
		recordFields.put("dc.contributor.other[pl]", prepareField("dc.contributor.other[pl]", csvHedlines, csvLine));
		recordFields.put("dc.contributor.reviewer[pl]", prepareField("dc.contributor.reviewer[pl]", csvHedlines, csvLine));
		recordFields.put("dc.contributor.translator[pl]", prepareField("dc.contributor.translator[pl]", csvHedlines, csvLine));
		recordFields.put("dc.date.accession[pl]", prepareField("dc.date.accession[pl]", csvHedlines, csvLine));
		recordFields.put("dc.date.created[pl]", prepareField("dc.date.created[pl]", csvHedlines, csvLine));
		recordFields.put("dc.date.issued[pl]", prepareField("dc.date.issued[pl]", csvHedlines, csvLine));
		recordFields.put("dc.date.submitted[pl]", prepareField("dc.date.submitted[pl]", csvHedlines, csvLine));
		recordFields.put("dc.description.admin[pl]", prepareField("dc.description.admin[pl]", csvHedlines, csvLine));
		recordFields.put("dc.description.admin[]", prepareField("dc.description.admin[]", csvHedlines, csvLine));
		recordFields.put("dc.description.conftype[pl]", prepareField("dc.description.conftype[pl]", csvHedlines, csvLine));
		recordFields.put("dc.description.edition[pl]", prepareField("dc.description.edition[pl]", csvHedlines, csvLine));
		recordFields.put("dc.description.number[pl]", prepareField("dc.description.number[pl]", csvHedlines, csvLine));
		recordFields.put("dc.description.physical[pl]", prepareField("dc.description.physical[pl]", csvHedlines, csvLine));
		recordFields.put("dc.description.series[pl]", prepareField("dc.description.series[pl]", csvHedlines, csvLine));
		recordFields.put("dc.description.volume[pl]", prepareField("dc.description.volume[pl]", csvHedlines, csvLine));
		recordFields.put("dc.identifier.articleid[pl]", prepareField("dc.identifier.articleid[pl]", csvHedlines, csvLine));
		recordFields.put("dc.identifier.doi[pl]", prepareField("dc.identifier.doi[pl]", csvHedlines, csvLine));
		recordFields.put("dc.identifier.eisbn[pl]", prepareField("dc.identifier.eisbn[pl]", csvHedlines, csvLine));
		recordFields.put("dc.identifier.isbn[pl]", prepareField("dc.identifier.isbn[pl]", csvHedlines, csvLine));
		recordFields.put("dc.identifier.eissn[pl]", prepareField("dc.identifier.eissn[pl]", csvHedlines, csvLine));
		recordFields.put("dc.identifier.issn[pl]", prepareField("dc.identifier.issn[pl]", csvHedlines, csvLine));
		recordFields.put("dc.identifier.uri[]", prepareURI(csvHedlines, csvLine));
		recordFields.put("dc.identifier.weblink[pl]", prepareField("dc.identifier.weblink[pl]", csvHedlines, csvLine));
		recordFields.put("dc.language[pl]", prepareField("dc.language[pl]", csvHedlines, csvLine));
		recordFields.put("dc.language.container[pl]", prepareField("dc.language.container[pl]", csvHedlines, csvLine));
		recordFields.put("dc.place[pl]", prepareField("dc.place[pl]", csvHedlines, csvLine));
		recordFields.put("dc.pubinfo[pl]", prepareField("dc.pubinfo[pl]", csvHedlines, csvLine));
		recordFields.put("dc.type[pl]", prepareField("dc.type[pl]", csvHedlines, csvLine));
		recordFields.put("dc.subtype[pl]", prepareField("dc.subtype[pl]", csvHedlines, csvLine));
		recordFields.put("dc.title[pl]", prepareField("dc.title[pl]", csvHedlines, csvLine));
		recordFields.put("dc.title.alternative[pl]", prepareField("dc.title.alternative[pl]", csvHedlines, csvLine));
		recordFields.put("dc.title.original[pl]", prepareField("dc.title.original[pl]", csvHedlines, csvLine));
		recordFields.put("dc.title.journal[pl]", prepareField("dc.title.journal[pl]", csvHedlines, csvLine));
		recordFields.put("dc.title.container[pl]", prepareField("dc.title.container[pl]", csvHedlines, csvLine));
		recordFields.put("dc.title.volume[pl]", prepareField("dc.title.volume[pl]", csvHedlines, csvLine));
		recordFields.put("collection", prepareField("collection", csvHedlines, csvLine));
		recordFields.put("id", new String[] {id});
		recordFields.put("dc.description.publication[pl]", prepareField("dc.description.publication[pl]", csvHedlines, csvLine));
		recordFields.put("dc.conference[pl]", prepareField("dc.conference[pl]", csvHedlines, csvLine));
		recordFields.put("dc.rights.original[pl]", prepareField("dc.rights.original[pl]", csvHedlines, csvLine));
		recordFields.put("dc.pbn.affiliation[pl]", prepareField("dc.pbn.affiliation[pl]", csvHedlines, csvLine));
		recordFields.put("dc.subject.pl[pl]", prepareField("dc.subject.pl[pl]", csvHedlines, csvLine));
		recordFields.put("dc.subject.en[pl]", prepareField("dc.subject.en[pl]", csvHedlines, csvLine));
}
	private String[] separateFields(String field)
	{
		String[] out = field.split("\\|\\|");
		return out;
	}
	private String[] prepareField(String fieldName, String[] headlines, String[] data)
	{
		String[] out;
		int a = Func.findColumn(headlines, fieldName);
		String value = "";
		if (a!=-1) value=data[a];
		if (value.indexOf("||")>-1) out=separateFields(value);
		else {out = new String[1]; out[0] = value;}
		return out;
	}
	private String[] prepareURI(String[] headlines, String[] data)
	{
		String[] out;
		int a = Func.findColumn(headlines, "dc.identifier.uri[]");
		String value = "";
		if (a!=-1) value=data[a];
		if (value.indexOf("||")>-1) out=separateFields(value);
		else {out = new String[1]; out[0] = value;}
		if (out[0].equals(""))
		{
			a = Func.findColumn(headlines, "dc.identifier.uri");
			if (a!=-1) value=data[a];
			if (value.indexOf("||")>-1) out=separateFields(value);
			else {out = new String[1]; out[0] = value;}
		}
		return out;
	}
	public void addDataToField(String field, String value){
		if (RFL.checkField(field)){
			String[] current = recordFields.get(field);
			if (current.length==0){
				current = new String[]{""};
			}
			if (current[0].equals("")){
				current[0]=value;
				recordFields.put(field, current);
			}
			else{
				String[] dataToAdd = {value};
				recordFields.put(field, Func.mergeArrays(current, dataToAdd));
			}
		}
	}
	/**
	 * Usuwa wybrane pole z rekordu.
	 * @param key - wartoœæ klucza w HashMap
	 * @param pos - numer indeksu w tablicy pod danym kluczem.
	 */
	public void removeDataFromField(String key, int pos){
		if (RFL.checkField(key)){
			String[] field = recordFields.get(key);
			if (field.length>pos){
				field = Func.removeRow(field, pos);
				recordFields.put(key, field);
			}
		}
	}
	public int countfields()
	{
		int a = 0;
		Field[] fields = this.getClass().getDeclaredFields();
		for (int x=0; x<fields.length; x++)
		{
			if (!fields[x].toString().equals("")) a++;
		}
		return a;
	}
	public Map<String, String[]> getMap()
	{
		return recordFields;
	}
	public String getID(){
		return id;
	}
	public static String getPersonID(String person){
		if (person.indexOf("SAP")>-1) return person.substring(person.indexOf("[SPA")+4,person.indexOf("]"));
		if (person.indexOf("USOS")>-1) return person.substring(person.indexOf("[USOS")+5,person.indexOf("]"));
		return "";
	}
	public static void loadFieldList() throws IOException{
		fieldsList = RFL.list();
		faculties = Statics.facUSOS;
		units = ImportCSV.readData("/units.csv", "\"");
		sapData = ImportCSV.readData("/osoby.csv", "\"");
	}
	/**
	 * Metoda zwracaj¹ca tablicê obiektów typu PersonEntry, nie wpisuje tych obiektów do CompleteRecord.
	 * @return Zwraca tablicê obiektów PersonEntry z uzupe³nionymi informacjami odnoœnie afiliacji
	 * @throws IOException
	 */
	public PersonEntry[] getAuthorsAffiliations() throws IOException{
		if (fieldsList==null) loadFieldList();
		String[][] authors = new String[4][];
		authors[0] = recordFields.get(fieldsList[3][2]);
		authors[1] = recordFields.get(fieldsList[4][2]);
		authors[2] = recordFields.get(fieldsList[5][2]);
		authors[3] = recordFields.get(fieldsList[6][2]);
		String[] admin1 = recordFields.get(fieldsList[35][2]);
		String[] admin2 = recordFields.get(fieldsList[46][2]);
		String[] admin = Func.mergeArrays(admin1, admin2);
		String date = recordFields.get(fieldsList[17][2])[0];
		int a=0;
		String[] role = {"autor","wspó³twórca","t³umacz","recenent"};
		int content = Func.countContentInArray(authors[0]);
		content+=Func.countContentInArray(authors[1]);
		content+=Func.countContentInArray(authors[2]);
		content+=Func.countContentInArray(authors[3]);
		persons = new PersonEntry[content];
		for (int x=0; x<authors.length; x++){
			for (int y=0; y<authors[x].length; y++){
				if (authors[x][y].equals("")) break;
				persons[a] = new PersonEntry(authors[x][y]);
				if (persons[a].isAffiliatedOrEmployed()){
					if (authors[x][y].indexOf("[USOS")>-1){
						persons[a].addAffiliation(listUSOSAffiliation(persons[a].getID()));
					}
					else if (authors[x][y].indexOf("[SAP")>-1){
						persons[a].addAffiliation(listSapAffiliation(sapData, persons[a].getID(), date));
					}
				}
				else{
					persons[a].addAffiliation(listABAffiliation(persons[a].toStringNames()));
				}
				persons[a].setRole(role[x]);
				a++;
			}
		}
		return persons;
	}
	/**
	 * @param x numer osoby z tablicy.
	 * @param xx numer afiliacji danej osoby.
	 * @return Zwraca prawdê, je¿eli afiliacja autora jest wpisana w [AB]
	 */
	public boolean isAB(int x, int xx){
		if (x>persons.length) return false;
		String[] admins = getAdmins();
		String[] affiliation;
		for (int y=0; y<admins.length; y++){
			for (int z=0; z<persons[x].countAffiliations(); z++){
				affiliation = persons[x].getAffiliation(z);
				if (admins[y].indexOf(persons[x].toString())>-1 &&
						admins[y].indexOf(persons[x].getAffiliation(xx)[0])>-1)
					return true;
			}
		}
		return false;
	}
	/**
	 * Metoda usuwa jedn¹ z wybranych afiliacji wpolu [AB]
	 * @param personString - wartoœæ z pola dc.
	 * @param unitID - id jednostki
	 */
	public void removeAB(String personString, String unitID){
		String[] admin1 = recordFields.get("dc.description.admin[pl]");
		for (int x=0; x<admin1.length; x++){
			if (admin1[x].startsWith("[AB]") && admin1[x].indexOf(personString)>-1
					&& admin1[x].indexOf(unitID)>-1){
				removeDataFromField("dc.description.admin[pl]", x);
			}
		}
		String[] admin2 = recordFields.get("dc.description.admin[]");
		for (int x=0; x<admin2.length; x++){
			if (admin2[x].startsWith("[AB]") && admin2[x].indexOf(personString)>-1
					&& admin2[x].indexOf(unitID)>-1){
				removeDataFromField("dc.description.admin[]", x);
			}
		}
	}
	public void removeAU(String personString){
		String[] admin1 = recordFields.get("dc.description.admin[pl]");
		for (int x=0; x<admin1.length; x++){
			if (admin1[x].startsWith("[AU]") && admin1[x].indexOf(personString)>-1){
				removeDataFromField("dc.description.admin[pl]", x);
			}
		}
		String[] admin2 = recordFields.get("dc.description.admin[]");
		for (int x=0; x<admin2.length; x++){
			if (admin2[x].startsWith("[AU]") && admin2[x].indexOf(personString)>-1){
				removeDataFromField("dc.description.admin[]", x);
			}
		}
	}
	/**
	 * Metoda wypisuj¹ca wszystkie afiliacje z SAP z uwzglêdnieniem daty
	 * @param sap - pe³na tabela osób rejestrowanych w SAP
	 * @param id - tylko numer
	 * @param date - data w postaci czterocyfrowej okreœlaj¹cej rok
	 * @return tablica listuj¹ca afiliacje kluczem unitID, unitName, date.
	 */
	public String[][] listSapAffiliation(String[][] sap, String id, String date){
		if (fieldsList==null){
			try { loadFieldList(); }
			catch (IOException e) { e.printStackTrace(); }
		}

		int a = 0;
		int b = 0;
		int sapStartDate;
		int startDate = (Integer.parseInt(date.replaceAll("[^0-9]+", ""))-1900)*365+30;
		int endDate = startDate + 365;
		int[] dates = new int[20];
		int[] dates2 = new int[20];
		String[][] depIds = new String[20][2];
		String[][] depIds2 = new String[20][2];
		for (int x=0; x<sapData.length; x++) {
			if (id.equals(sapData[x][0])) {
				sapStartDate = Integer.parseInt(sapData[x][1]);
				if (sapStartDate<=endDate){
					dates[a] = Integer.parseInt(sapData[x][1]);
					depIds[a][0] = sapData[x][2];
					depIds[a][1] = getUnitNameByID(sapData[x][2]);
					a++;
					if (sapStartDate>startDate){
						depIds2[b][0] = sapData[x][2];
						depIds2[b][1] = getUnitNameByID(sapData[x][2]);
						b++;
					}
				}
			}
		}
		Calendar c;
		ArrayList<String[]> aff = new ArrayList<String[]>();
		if (b==1){
			aff.add(new String[]{depIds2[0][0], depIds2[0][1], date});
		}
		else if (a==1){
			c=Func.getCalendarForCalculation();
			c.add(Calendar.DATE, dates[0]);
			aff.add(new String[]{depIds[0][0], depIds[0][1],
					""+c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DAY_OF_MONTH)});
		}
		else {
			for (int x=0; x<a; x++){
				c=Func.getCalendarForCalculation();
				c.add(Calendar.DATE, dates[0]);
				aff.add(new String[]{depIds[x][0], depIds[x][1],
						""+c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DAY_OF_MONTH)});
			}
		}
		String[][] aff2 = listABAffiliation(id);
		for (int x=0; x<aff2.length; x++){
			aff.add(aff2[x]);
		}
		return Func.toArray(aff);
	}
	/**
	 * Metoda wypisuj¹ca wszystkie afiliacje z USOS na podstawie pola afiliacji usos
	 * @param id - tylko numer
	 * @return Schemat gotowy dla PersonEntry
	 */
	public String[] listUSOSAffiliation(String id){
		if (fieldsList==null){
			try { loadFieldList(); }
			catch (IOException e) { e.printStackTrace(); }
		}
		String[] out = new String[3];
		String unitAbbr;
		String usos = recordFields.get(fieldsList[43][2])[0];
		String[] usosField = usos.substring(0, usos.length()-1).split("; ");
		for (int x=0; x<usosField.length; x++){
			if (usosField[x].indexOf(id)>-1){
				for (int y=0; y<faculties.length; y++){
					if (usosField[x].indexOf(faculties[y][1])>-1){
						unitAbbr = usosField[x].substring(usosField[x].indexOf("UJ.")+3);
						out[0] = getUnitIDUSOS(unitAbbr);
						break;
					}
				}
				if (!out[0].equals("")){
					out[1] = getUnitNameByID(out[0]);
				}
				else out[1]="";
			}
		}
		out[2] = recordFields.get(fieldsList[17][2])[0];
		return out;
	}
	public String[][] listABAffiliation(String fieldValue){
		ArrayList<String[]> aff = new ArrayList<String[]>();
		String date = recordFields.get(fieldsList[17][2])[0];
		String[] admins = getAdmins();
		String adminsUnitId;
		for (int x=0; x<admins.length; x++){
			if (admins[x].indexOf(fieldValue)>-1){
				if (admins[x].indexOf("5000")>-1){
					adminsUnitId = admins[x].substring(admins[x].indexOf("5000"));
					aff.add(new String[]{adminsUnitId, getUnitNameByID(adminsUnitId), date});
				}
			}
		}
		return Func.toArray(aff);
	}
	public String getUnitNameByID(String id){
		if (fieldsList==null){
			try { loadFieldList(); }
			catch (IOException e) { e.printStackTrace(); }
		}
		for (int x=0; x<units.length; x++){
			if (units[x][0].indexOf(id)>-1) return units[x][3];
		}
		return "";
	}
	/**
	 * Metoda zwraca id jednostki na podstawie skrótu wg skrótów z pola afiliacji USOS
	 * @param abbreviation - pole powinno zawieraæ 2-3 literowy skrót wydzia³u
	 * @return <code> String ID </code>
	 */
	public static String getUnitIDUSOS(String abbreviation){
		if (fieldsList==null){
			try { loadFieldList(); }
			catch (IOException e) { e.printStackTrace(); }
		}
		String[][] usos = Statics.facUSOS;
		for (int x=0; x<usos.length; x++){
			if (usos[x][1].equals(abbreviation)) return usos[x][0];
		}
		return "";
	}
	public String[] getAdmins(){
		if (fieldsList==null){
			try { loadFieldList(); }
			catch (IOException e) { e.printStackTrace(); }
		}
		String[] admins1 = recordFields.get(fieldsList[35][2]);
		String[] admins2 = recordFields.get(fieldsList[46][2]);
		return Func.mergeArrays(admins1, admins2);
	}
	public PersonEntry getPerson(int x){
		return persons[x];
	}
	public boolean isPersonMarkedAU(PersonEntry person){
		String[] adminField = getAdmins();
		String entry = person.toString();
		for (int x=0; x<adminField.length; x++){
			if (adminField[x].indexOf(entry)>-1) return true;
		}
		return false;
	}
	public ArrayList<String> getContributors(){
		String[][] fieldList = RFL.list();
		String type = recordFields.get(RFL.F[RFL.TYPE][2])[0];
		String subtype = recordFields.get(RFL.F[RFL.SUBTYPE][2])[0];
		ArrayList<Integer> fieldslist = new ArrayList<Integer>();
		fieldslist.add(RFL.AUTHORS);
		if (subtype.equals("SourceEditorship") || subtype.equals("CriticalEdition")){
			fieldslist.add(RFL.COAUTHORS);
		}
		fieldslist.add(RFL.REVIEWER);
		if (type.equals("Book")) fieldslist.add(RFL.EDITOR);
		ArrayList<String> authors = new ArrayList<String>();
		String[] a;
		int b;
		for (int x=0; x<fieldslist.size(); x++){
			a = recordFields.get(RFL.F[fieldslist.get(x)][2]);
			if (a!=null){
				for (int y=0; y<a.length; y++){
					if (!isAddedToList(authors, a[y])){
						authors.add(a[y]);
					}
				}
			}
		}
		return authors;
	}
	private boolean isAddedToList(ArrayList<String> list, String entry){
		for (int x=0; x<list.size(); x++){
			if (list.get(x).equals(entry)) return true;
		}
		return false;
	}
}