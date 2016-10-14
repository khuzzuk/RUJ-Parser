package ExportXML;

import Imports.ImportCSV;
import Imports.MNiSWPoints;
import Imports.SAP;
import Imports.Statics;
import OtherDialogs.XMLReport;
import PMainWindow.Func;
import PMainWindow.MetAnWindow;
import PMainWindow.SaveCSV;
import Records.Person;
import Records.CompleteRecord;
import Records.FieldsFromArray;
import Records.RFL;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ParseXML extends SwingWorker<String, Integer>
{
	private String namespace = "http://pbn.nauka.gov.pl/-/ns/bibliography";
	private File file = new File("d:/RUJ/export");
	private CompleteRecord record;
	private Map<String, String[]> rMap;
	private Document docx;
	private Element head; private Element rec;
	private String[][] fields;
	private String[] unitId;
	private String[][] sapData;
	private String[][] langCodes;
	private String[][] countryCodes;
	private String[] admin;
	private String inputData[][];
	private Collection<Person> authorsInRecord = new HashSet<>();
	private JProgressBar progressbar;
	private int maximum, filled;
	private int authors, affiliated, X;
	private int typeCol, subTypeCol;
	private Properties settings;
	private String[] exportedRecordIds;
	private ArrayList<String> wrongCountryCodes = new ArrayList<String>();
	private String unitUsos="";
	private String date;
	private String[] year, temp;
	private String[][] previousData;
	private String[][] personData, personDataForRecord;
	private ArrayList<String[]> reportList = new ArrayList<String[]>(10000);
	private List<String> idsFromCorrectsList = new ArrayList<>();
	public ParseXML(String[][] previousData, String[][] personData, String[] unitID, String[] year)
	{
		this.unitId = unitID;
		this.inputData = MetAnWindow.myIdentity.loadedCSV;
		this.personData = personData;
		this.progressbar = MetAnWindow.myIdentity.progressBar;
		this.settings = MetAnWindow.settings;
		this.year = year;
		this.previousData = previousData;
	}
	public ParseXML(String[][] previousData, String[][] personData, String[] unitID, String[] year, Document doc)
	{
		this.unitId = unitID;
		this.inputData = MetAnWindow.myIdentity.loadedCSV;
		this.personData = personData;
		this.progressbar = MetAnWindow.myIdentity.progressBar;
		this.settings = MetAnWindow.settings;
		this.year = year;
		this.previousData = previousData;
		docx = doc;
	}
	public void setDocument(Document previousDoc){
		docx=previousDoc;
	}
	protected String doInBackground()
	{
		if (inputData==null) return"";
		try {
		for (int x=0; x<Statics.facUSOS.length; x++){
			if (unitId[1].equals(Statics.facUSOS[x][0])) unitUsos = Statics.facUSOS[x][1];
		}
		maximum = inputData.length;
		filled = 0;
		String[][] points;
			points = MNiSWPoints.loadPoints();
		int[][] cols = MNiSWPoints.findCols(points[0]);
		sapData = ImportCSV.readData("/osoby.csv", "\"");
		//temporaryData = ImportCSV.readData("/bioch.csv", "\"");
		SAP.SAP = sapData;
		langCodes = ImportCSV.readData("/languages.csv", "\"");
		countryCodes = ImportCSV.readData(new File("./csv/countries.csv"), "\"");
		fields = RFL.list();
		String[] ids;
		String[] lineToExport;
		typeCol = Func.findColumn(inputData[0], fields[36][2]);
		subTypeCol = Func.findColumn(inputData[0], fields[37][2]);
		int dateCol = Func.findColumn(inputData[0], fields[17][2]);
		int serCol = Func.findColumn(inputData[0], fields[22][2]);
		int usosCol = Func.findColumn(inputData[0], fields[43][2]);
		int admin1Col = Func.findColumn(inputData[0], fields[35][2]);
		int admin2Col = Func.findColumn(inputData[0], fields[46][2]);
		createHead();
		String[] series;
		String type, subType, issn;
		boolean MNiSW;
		progressbar.setMaximum(maximum);
		progressbar.setIndeterminate(false);
		X=0;
		exportedRecordIds = new String[inputData.length];
		for (int x=1; x<inputData.length; x++)
		{
			if (x%100==0) {filled = x; publish(x);}
			if (previousData!=null && !FieldsFromArray.isNewID(previousData, inputData[x][0])) continue;
			type = inputData[x][typeCol];
			subType = inputData[x][subTypeCol]; 
			int[] col = contributorsColumns(inputData[0], type, subType);
			ids = FieldsFromArray.getSAPIDs(inputData[x], col);
			admin = FieldsFromArray.getAdmin(inputData[x], admin1Col, admin2Col);
			if (personData!=null && isListed(inputData[x][0], inputData[x][dateCol])){
				lineToExport = inputData[x];
				if (type.equals("BookSection"))
				{
					series = Func.dcSeriesToFields(inputData[x][serCol]);
					if (!series[1].equals("") || !series[2].equals(""))
					{
						if (!series[1].equals("")) issn=series[1];
						else issn=series[2];
						MNiSW = Func.isApprovedByAuthority(cols, Integer.parseInt(lineToExport[dateCol]), points, issn); 
						if (MNiSW)
						{
							lineToExport = Func.changeToJournal(lineToExport, inputData[0]);
							type = "JournalArticle";
						}
					}
				}
				if (startRecord(type))
				{
					record = new CompleteRecord(lineToExport, inputData[0]);
					rMap = record.getMap();
					exportedRecordIds[x] = inputData[x][0];
					addFields(type, subType);
				}
			}
			else if (personData==null && isExportable(ids, inputData[x][dateCol], inputData[x][usosCol], inputData[x][0]))
			{
				lineToExport = inputData[x];
				if (type.equals("BookSection"))
				{
					series = Func.dcSeriesToFields(inputData[x][serCol]);
					if (!series[1].equals("") || !series[2].equals(""))
					{
						if (!series[1].equals("")) issn=series[1];
						else issn=series[2];
						MNiSW = Func.isApprovedByAuthority(cols, Integer.parseInt(lineToExport[dateCol]), points, issn); 
						if (MNiSW)
						{
							lineToExport = Func.changeToJournal(lineToExport, inputData[0]);
							type = "JournalArticle";
						}
					}
				}
				if (startRecord(type))
				{
					record = new CompleteRecord(lineToExport, inputData[0]);
					rMap = record.getMap();
					exportedRecordIds[x] = inputData[x][0];
					addFields(type, subType);
				}
			}
		}		
		} catch (IOException|ParserConfigurationException e) {e.printStackTrace();}
		return "finished";
	}
	private void createHead() throws ParserConfigurationException
	{
		if (docx==null){
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			docx = builder.newDocument();
			head = docx.createElementNS(namespace, "works");
			head.setAttribute("pbn-unit-id", unitId[0]);
			head.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance",
					"xsi:schemaLocation",
					"http://pbn.nauka.gov.pl/-/ns/bibliography PBN-report.xsd ");
			docx.appendChild(head);
		}
		else {
			head = docx.getDocumentElement();
			addModificationMark("chapter");
			addModificationMark("article");
			addModificationMarkToBook("book");
		}
	}
	private void addModificationMark(String elementName){
		NodeList items = head.getElementsByTagName(elementName);
		int max = items.getLength();
		for (int x=0; x<max; x++){
			Element archiveMark = docx.createElement("archive-action");
			archiveMark.setTextContent("OVERWRITE");
			items.item(x).appendChild(archiveMark);
		}
	}
	private void addModificationMarkToBook(String elementName){
		NodeList items = head.getElementsByTagName(elementName);
		int max = items.getLength();
		for (int x=0; x<max; x++){
			Element item = (Element) items.item(x);
			if (item.getElementsByTagName("system-identifier").getLength()>0){
				Element archiveMark = docx.createElement("archive-action");
				archiveMark.setTextContent("OVERWRITE");
				items.item(x).appendChild(archiveMark);
			}
		}
	}
	private boolean isListed(String recordId, String date){
		if (date.replaceAll("[^0-9]+", "").length()<4) return false;
		String year = date.substring(0, 4);
		int checkYear=0;
		for (int x=0; x<this.year.length; x++){
			if (year.equals(this.year[x])) checkYear++;
		}
		if (checkYear==0) return false;
		personDataForRecord = FieldsFromArray.getListFromArray(personData, recordId);
		if (personDataForRecord.length==0 || personDataForRecord[0].length==0) return false;
		for (int x=0; x<personDataForRecord.length; x++){
			if (personDataForRecord[x][2].equals(unitId[1])) return true;
		}
		return false;
	}
	private boolean isExportable(String[] ids, String date, String usos, String recordId)
	{
		if (date.replaceAll("[^0-9]+", "").length()<4) return false;
		String year = date.substring(0, 4);
		this.date = year;
		int checkYear=0;
		for (int x=0; x<this.year.length; x++){
			if (year.equals(this.year[x])) checkYear++;
		}
		if (checkYear==0) return false;
		for (int x=0; x<admin.length; x++){
			if (admin[x].indexOf("[AB]")>-1 && admin[x].indexOf(unitId[1])>-1) return true;
		}
		int[][] dates = new int[ids.length][7];
		int a;
		int b=0;
		ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();
		String[][] author;
		outer:
		for (int x=0; x<ids.length; x++)
		{
			lists.add(new ArrayList<String>());
			a=0;
			if (ids[x].indexOf("" + "SAP")>-1){
				for (int y=0; y<admin.length; y++){
					if (admin[y].startsWith("[AU]") && admin[y].indexOf(ids[x])>-1) continue outer;
				}
				author = SAP.findPerson(ids[x].substring(3));
				if (author!=null){
					for (int y=0; y<author.length; y++){
						if (author[y]==null) break;
						dates[x][a] = Integer.parseInt(author[y][1]);
						lists.get(x).add(author[y][2]);
						a++; b++;
						/*if (author[y][2].equals(unitId[1]))
						{
						}*/
					}
				}
			}
			else if (ids[x].indexOf("USOS")>-1 && usos.length()>0){
				String[] usosField = usos.substring(0, usos.length()-1).split("; ");
				for (int y=0; y<usosField.length; y++){
					if (usosField[y].indexOf(unitUsos)>-1 && usosField[y].indexOf(ids[x])>-1){
						if (unitUsos.equals("WF") && (usosField[y].indexOf("WFAI")>-1 || usosField[y].indexOf("WFz")>-1)){
							break;
						}
						else {
							return true;
						}
					}
				}
			}
		}
		if (b==0) return false;
		int startDate = (Integer.parseInt(date.replaceAll("[^0-9]+", ""))-1900)*365+30;
		int endDate = startDate + 365;
		for (int x=0; x<dates.length; x++)
		{
			a=b=0;
			for (int y=0; y<lists.get(x).size(); y++)
			{
				//if (dates[x][y]==0) break; 
				if (dates[x][y]<endDate){
					a++;
					if (lists.get(x).get(y).equals(unitId[1])) b++;
				}
			}
			if (a==1 && b==1) return true; //sprawdza� rekordy doktorant�w
			a=b=0;
			for (int y=0; y<dates[x].length; y++)
			{
				if (dates[x][y]<endDate && dates[x][y]>=startDate){
					a++;
					if (lists.get(x).get(y).equals(unitId[1])) b++;
				}
			}
			if (a==1 && b==1) return true; //je�eli jest tylko jedna data mniejsza ni� startDate, w�wczas a=0;
			else{
				b=0;
				for (int y=0; y<dates[x].length; y++){
					if (dates[x][y]==0) break;
					if (dates[x][y]<endDate && dates[x][y]>=startDate && lists.get(x).get(y).equals(unitId[1])){
						b++;
					}
				}
				if (a!=0 && a==b) return true;
			}
		}
		return false;
	}
	private boolean isMarkedAsAffiliated(String id){
		for (int x=0; x<admin.length; x++){
			if (admin[x].startsWith("[AB]") && admin[x].indexOf(id)>-1 && admin[x].indexOf(unitId[1])>-1) return true;
		}
		return false;
	}
	private boolean isListedAsAffiliated(String author){
		if (personData==null) return false;
		for (int x=0; x<personDataForRecord.length; x++){
			if (personDataForRecord[x][1].equals(author) && personDataForRecord[x][2].equals(unitId[1])){
				if (personDataForRecord[x][5].equals("1")) return true;
			}
		}
		return false;
	}
	private boolean isListedAsEmployed(String author){
		if (personData==null) return false;
		for (int x=0; x<personDataForRecord.length; x++){
			if (personDataForRecord[x][1].equals(author) && personDataForRecord[x][2].equals(unitId[1])){
				if (personDataForRecord[x][4].equals("1")) return true;
				else return false;
			}
		}
		return false;
	}
	private boolean isAuthorExportable(String idSAP, String date)
	{
		String id=idSAP;
		ArrayList<String> depIds = new ArrayList<String>();
		ArrayList<String> depIds2 = new ArrayList<String>();
		if (idSAP.indexOf("SAP")>-1) id = idSAP.substring(3);
		else if (idSAP.indexOf("USOS")>-1) id = idSAP.substring(4);
		if (isMarkedAsAffiliated(id)) return true;
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
	/**
	 * Metoda dla p�l [AU]
	 * @param id - identyfikator, mo�na wprowadzi� zar�wno pe�ny jak i sam numer.
	 * @return zwraca <code>true</code> je�eli numer ID nie zosta� przypisany do pola [AU], <code>false</code> gdy znajdzie pole AU zawieraj�ce identyfikator.
	 */
	private boolean isAffiliated(String id)
	{
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
	private boolean startRecord(String type)
	{
		if (type.equals("JournalArticle"))
		{
			rec = docx.createElementNS(namespace, "article");
			head.appendChild(rec);
			return true;
		}
		else if (type.equals("BookSection"))
		{
			rec = docx.createElementNS(namespace, "chapter");
			head.appendChild(rec);
			return true;
		}
		else if(type.equals("Book"))
		{
			rec = docx.createElementNS(namespace, "book");
			head.appendChild(rec);
			return true;
		}
		return false;
	}
	private void addFields(String type, String subType)
	{
		if (personData!=null) personDataForRecord = FieldsFromArray.getListFromArray(personData, record.getID());
		X++;
		authors=affiliated=0;
        authorsInRecord.clear();
		addTitle(subType);
		if (!subType.equals("Review")){
			addAuthors(3, rec);
		}
		addAuthors(5, rec);
		addAuthors(6, rec);
		//addResearchTeam();
        countResearchTeam();
		if (subType.equals("SourceEditorship") || subType.equals("CriticalEdition"))
		{
			addAuthors(4, rec);
			addElementOthers(new int[] {3, 4, 5, 6});
		}
		else addElementOthers(new int[] {3, 5, 6});
		if (!type.equals("JournalArticle")) {
			addElementOthersEditors();
		}
		addIdentifiers();
		addConference();
		addSize();
		addFeatures();
		addElementID();
		//addSize();
		if (type.equals("JournalArticle"))
		{
			addJournalCitation();
		}
		else if (type.equals("BookSection"))
		{
            addEditors(authorsInRecord, "editor");
			addBookCitation();
			addPages(type);
			//addSeries();
		}
		else if (type.equals("Book"))
		{
			addEditors(authorsInRecord, "editor");
			addISBN(23, rec);//isbn
			addISBN(24, rec);//eisbn
			addSeries();
			addElementToParent(rec, "edition", 13);
			addPages(type);
			addPublishers(rec);
		}
		addOpenAccess(type);
		head.appendChild(rec);
	}

	private void addTitle(String subType)
	{
		String rTitle = rMap.get(fields[0][2])[0];
		String rOrgTitle = rMap.get(fields[2][2])[0];
		Element title;
		if (!rTitle.equals(""))
		{
			title = docx.createElementNS(namespace, "title");
			title.setTextContent(rTitle);
		}
		else
		{
			if (subType.equals("Review"))
			{
				String reviewTitle = rOrgTitle + ", " + rMap.get(fields[17][2])[0];
				String[] orgAuthors = rMap.get(fields[3][2]);
				for (int x=0; x<orgAuthors.length; x++) reviewTitle = reviewTitle + ", " + orgAuthors[x].replaceAll(".\\[(.*?)\\]", "");
				title = docx.createElementNS(namespace, "title");
				title.setTextContent(reviewTitle);
			}
			else
			{
				title = docx.createElementNS(namespace, "title");
				title.setTextContent(rOrgTitle);
			}
		}
		rec.appendChild(title);
	}

	private void addAuthors(int fieldIndex, Element parent)
	{
		String[] authors = rMap.get(fields[fieldIndex][2]);
		if (authors[0].equals("")) return;
		String[][] output = new String[authors.length][5];
        Person[] outputAuthors = new Person[authors.length];
		String id;
		String date = rMap.get(fields[17][2])[0];
		for (int x=0; x<authors.length; x++)
		{
			outputAuthors[x] = null;
			if (authors[x].contains("[") && authors[x].contains(", ")) {
				output[x][1] = authors[x].substring(authors[x].indexOf(", ")+2, authors[x].indexOf(" ["));
				outputAuthors[x] = new Person(authors[x].substring(authors[x].indexOf(", ")+2, authors[x].indexOf(" [")));
			} else if (authors[x].contains(", ")) {
				output[x][1] = authors[x].substring(authors[x].indexOf(", ") + 2);
				outputAuthors[x] = new Person(authors[x].substring(authors[x].indexOf(", ") + 2));
			} else {
				output[x][1] = authors[x].substring(authors[x].indexOf(" ") + 1);
				outputAuthors[x] = new Person(authors[x].substring(authors[x].indexOf(" ") + 1));
			}
			if (authors[x].contains(", ")){
				output[x][0] = authors[x].substring(0, authors[x].indexOf(", "));
				outputAuthors[x].setFamilyName(authors[x].substring(0, authors[x].indexOf(", ")));
			}
			else{
				output[x][0] = authors[x];
				outputAuthors[x].setFamilyName(authors[x]);
			}
			if (authors[x].contains("[")){
				id = authors[x].substring(authors[x].indexOf("[")+1, authors[x].indexOf("]"));
				outputAuthors[x].setId(id);
			}
			else id="";
			output[x][2] = id;
			if (isListedAsAffiliated(authors[x])){
				output[x][3]="1";
                outputAuthors[x].affiliate();
			}
			else if (personData==null && !id.equals(""))
			{
				if (isAuthorExportable(id, date))
				{
					output[x][3]="1";
                    outputAuthors[x].affiliate();
				}
			}
			else if (personData==null){
				if (isMarkedAsAffiliated(authors[x])){
					output[x][3]="1";
                    outputAuthors[x].affiliate();
				}
			}
		}
		Element[][] authorsElements = new Element[output.length][6];
		String role = fields[fieldIndex][0];
		boolean isNotAdded;
		for (int x=0; x<output.length; x++)
		{
			if (output[x]!=null && output[x][0]!=null)
			{
				if (!authorsInRecord.contains(outputAuthors[x]))
				{
					this.authors++;
					refreshTemp();
					temp[2] = authors[x];
					authorsElements[x][0] = docx.createElementNS(namespace, role);
					authorsElements[x][1] = docx.createElementNS(namespace, "given-names");
					authorsElements[x][1].setTextContent(outputAuthors[x].getName());
					authorsElements[x][2] = docx.createElementNS(namespace, "family-name");
					authorsElements[x][2].setTextContent(outputAuthors[x].getFamilyName());
					authorsElements[x][3] = docx.createElementNS(namespace, "system-identifier");
					authorsElements[x][3].setTextContent(outputAuthors[x].getId());
					authorsElements[x][4] = docx.createElementNS(namespace, "affiliated-to-unit");
					/*if (isTemporaryAffiliated(authors[x], record.getID())){
						authorsElements[x][4].setTextContent("true");
						affiliated++;
					}*/
					if (outputAuthors[x].isAffiliated()){
						authorsElements[x][4].setTextContent("true");
						temp[3] = "tak";
					}
					else{
						authorsElements[x][4].setTextContent("false");
						temp[3] = "nie";
					}
					authorsElements[x][5] = docx.createElementNS(namespace, "employed-in-unit");
					/*if (isTemporaryEmployed(authors[x], record.getID())){
						authorsElements[x][5].setTextContent("true");
					}*/
					if (personData!=null){
						if (isListedAsEmployed(authors[x])){
							authorsElements[x][5].setTextContent("true");
							temp[4] = "tak";
							outputAuthors[x].setEmployed();
						}
						else {
							authorsElements[x][5].setTextContent("false");
							temp[4] = "nie";
						}
					}
					else if (output[x][2].contains("SAP") && output[x][3]!=null && output[x][3].equals("1")){
						if (isMarkedAsAffiliated(outputAuthors[x].getId())){
							authorsElements[x][5].setTextContent("false");
							temp[4] = "nie";
						}
						else {
							authorsElements[x][5].setTextContent("true");
							temp[4] = "tak";
						}
					}
					else if (output[x][2].contains("SAP") && !isAffiliated(output[x][2])){
						authorsElements[x][5].setTextContent("true");
						temp[4] = "tak";
					}
					else {
						authorsElements[x][5].setTextContent("false");
						temp[4] = "nie";
					}
					reportList.add(temp);
                    if (Objects.equals(temp[3], "tak") || Objects.equals(temp[4], "tak")) {
                        rec.appendChild(authorsElements[x][0]);
                    }
					authorsElements[x][0].appendChild(authorsElements[x][1]);
					authorsElements[x][0].appendChild(authorsElements[x][2]);
					if (output[x].length>2 && !output[x][2].equals(""))
						authorsElements[x][0].appendChild(authorsElements[x][3]);
					authorsElements[x][0].appendChild(authorsElements[x][4]);
					authorsElements[x][0].appendChild(authorsElements[x][5]);
				}
                if (outputAuthors[x].isAffiliated() || outputAuthors[x].isEmployed()) {
                    affiliated++;
                }
			}
		}
	}
	private void countResearchTeam() {
        for (String s : record.getAdmins()) {
            if (s.startsWith("[RT]")) {
                authors+=s.split("—").length+1-authorsInRecord.size();
            }
        }
    }

    private void addResearchTeam(){
		String[] admin1 = rMap.get(fields[35][2]);
		String[] admin2 = rMap.get(fields[46][2]);
		String[] admin = record.getAdmins();
		String[] authors;
		String[] names = new String[2];
		Element[] authorsElement;
		int a;
		for (int x=0; x<admin.length; x++){
			if (admin[x]!=null && admin[x].startsWith("[RT]")){
				authors = admin[x].substring(4).split("—");
				authorsElement = new Element[3];
				for (int y=0; y<authors.length; y++){
					a=authors[y].indexOf(",");
					authorsElement[0] = docx.createElementNS(namespace, "author");
					authorsElement[1] = docx.createElementNS(namespace, "given-names");
					authorsElement[2] = docx.createElementNS(namespace, "family-name");
					if (a>-1){
						names[0] = authors[y].substring(0, a);
						names[1] = authors[y].substring(a+2);
					}
					else if((y!=0|| !authors[y].contains("ATLAS"))){
						a = authors[y].indexOf(" ");
						if (a>-1){
							names[0] = authors[y].substring(0, a);
							names[1] = authors[y].substring(a+2);
						}
						else break;
					}
					else break;
					this.authors++;
					authorsElement[1].setTextContent(names[1]);
					authorsElement[2].setTextContent(names[0]);
					rec.appendChild(authorsElement[0]);
					authorsElement[0].appendChild(authorsElement[1]);
					authorsElement[0].appendChild(authorsElement[2]);
				}
				break;
			}
		}
	}

	private void addElementOthers(int[] fieldIndex)
	{
		/*int allAuthors = 0;
		String[] field;
		for (int x=0; x<fieldIndex.length; x++)
		{
			field = rMap.get(fields[fieldIndex[x]][2]);
			if (field.length>0 && !field[0].equals(""))
			allAuthors += field.length;
		}*/
		Element others = docx.createElementNS(namespace, "other-contributors");
		others.setTextContent(Integer.toString(authors - affiliated));
		rec.appendChild(others);
	}
	private void addElementOthersEditors() {
        authorsInRecord.clear();
        String[] editors = rMap.get(RFL.F[RFL.EDITOR][2]);
        String id;
        for (int x = 0; x < editors.length; x++) {
            Person editor = null;
            id = editors[x].replaceAll("[^0-9]+", "");
            if (editors[x].contains(", ")) {
                if (!id.equals("")) {
					editor = new Person(editors[x].substring(editors[x].indexOf(", ") + 2, editors[x].indexOf(" [")));
				} else {
					editor = new Person(editors[x].substring(editors[x].indexOf(", ") + 2));
				}
				editor.setFamilyName(editors[x].substring(0, editors[x].indexOf(", ")));
				if (editors[x].contains("[")) {
                    editor.setId(editors[x].substring(editors[x].indexOf("[") + 1, editors[x].indexOf("]")));
                    if (personData != null) {
                        if (isListedAsAffiliated(editors[x])) {
                            editor.affiliate();
                        }
                        if (isListedAsEmployed(editors[x])) {
                            editor.setEmployed();
                        }
                    } else {
                        if (editor.getId().contains("SAP") && isAuthorExportable(editor.getId(), rMap.get(fields[17][2])[0])) {
                            editor.affiliate();
                        }
                        if (!isMarkedAsAffiliated(editor.getId())) {
                            editor.setEmployed();
                        }
                    }
                }
            }
			Optional.ofNullable(editor).ifPresent(authorsInRecord::add);
        }
        Element others = docx.createElementNS(namespace, "other-editors");
        others.setTextContent(Long.toString(authorsInRecord.stream().filter(e -> !e.isAffiliated() && !e.isEmployed()).count()));
        rec.appendChild(others);
    }

    private void addEditors(Collection<Person> persons, String role) {
        persons.stream().filter(e -> e.isEmployed() || e.isAffiliated()).forEach(person -> {
            Element parent = docx.createElementNS(namespace, role);
            rec.appendChild(parent);
            if (person.getName() != null) {
                Element givenName = docx.createElementNS(namespace, "given-names");
                givenName.setTextContent(person.getName());
                parent.appendChild(givenName);
            }
            if (person.getFamilyName() != null) {
                Element familyName = docx.createElementNS(namespace, "family-name");
                familyName.setTextContent(person.getFamilyName());
                parent.appendChild(familyName);
            }
            if (person.getId() != null) {
                Element id = docx.createElementNS(namespace, "system-identifier");
                id.setTextContent(person.getId());
                parent.appendChild(id);
            }
            if (person.isAffiliated()) {
                Element affiliated = docx.createElementNS(namespace, "affiliated-to-unit");
                affiliated.setTextContent("true");
                parent.appendChild(affiliated);
            }
            if (person.isEmployed()) {
                Element employed = docx.createElementNS(namespace, "employed-in-unit");
                employed.setTextContent("true");
                parent.appendChild(employed);
            }
        });
    }

	private void addIdentifiers()
	{
		//doi
		String doi = rMap.get(fields[29][2])[0];
		if (!doi.equals(""))
		{
			Element elementDoi = docx.createElementNS(namespace, "doi");
			elementDoi.setTextContent(doi);
			rec.appendChild(elementDoi);
		}
		//lang
		String lang = rMap.get(fields[27][2])[0];
		lang = changeLangCodes(lang);
		Element elementLang = docx.createElementNS(namespace, "lang");
		elementLang.setTextContent(lang.toUpperCase());
		rec.appendChild(elementLang);
		
		addKeywords();
		addFirstElementToParentWithAttribute(rec, "public-uri", 30, "href");
		addElementToParent(rec, "publication-date", 17);
	}
	private void addElementID()
	{
		//ID
		Element sysID = docx.createElementNS(namespace, "system-identifier");
		//sysID.setAttribute("system", "RUJ");
		sysID.setTextContent(rMap.get(fields[39][2])[0]);
		rec.appendChild(sysID);
	}
	private void addSize()
	{
		String rSize = rMap.get(fields[40][2])[0];
		Element size = docx.createElementNS(namespace, "size");
		size.setAttribute("unit", "sheets");
		if (rSize!=null && !rSize.equals(""))
		{
			size.setTextContent(rSize);
			rec.appendChild(size);
		}
		/*else
		{
			size.setTextContent(Func.countSheets(rMap.get(fields[15][2])[0]));
		}
		rec.appendChild(size);*/
	}
	private void addConference()
	{
		String[] values = rMap.get(fields[41][2]);
		if (values[0].length()>10)
		{
			String[] rConference;
			Element[] conference = new Element[9];
			rConference = values[0].substring(0, values[0].length()-1).split("; ");
			if (rConference.length>1){
				if (rConference.length>4){
					rConference[4] = Func.convertCountryNameToISO(rConference[4], countryCodes);
					if (rConference[4].equals("")) wrongCountryCodes.add(rMap.get(fields[39][2])[0]);
				}
				conference[0] = docx.createElementNS(namespace, "conference");
				conference[1] = docx.createElementNS(namespace, "name");
				conference[2] = docx.createElementNS(namespace, "start-date");
				conference[3] = docx.createElementNS(namespace, "end-date");
				conference[4] = docx.createElementNS(namespace, "location");
				conference[5] = docx.createElementNS(namespace, "country");
				conference[1].setTextContent(rConference[0]);
				conference[2].setTextContent(Func.prepareConferenceDate(rConference[1]));
				conference[0].appendChild(conference[1]);
				conference[0].appendChild(conference[2]);
				if (rConference.length==4)
				{
					conference[3].setTextContent(Func.prepareConferenceDate(rConference[1]));
					conference[4].setTextContent(rConference[2].replaceFirst(" ", ""));
					conference[5].setTextContent(rConference[3].replaceFirst(" ", ""));
				}
				else if (rConference.length>4)
				{
					conference[3].setTextContent(Func.prepareConferenceDate(rConference[2]));
					conference[4].setTextContent(rConference[3].replaceFirst(" ", ""));
					conference[5].setTextContent(rConference[4].replaceFirst(" ", ""));
				}
				conference[0].appendChild(conference[3]);
				conference[0].appendChild(conference[4]);
				conference[0].appendChild(conference[5]);
				if (rConference.length>5){
					if (rConference[5].equals("indeksowana w Web of Science")){
						conference[6] = docx.createElementNS(namespace, "web-of-science-indexed");
						conference[6].setTextContent("true");
						conference[0].appendChild(conference[6]);
					}
					if (rConference.length>6 && rConference[6].equals("indeksowana w Scopus")){
						conference[7] = docx.createElementNS(namespace, "scopus-indexed");
						conference[7].setTextContent("true");
						conference[0].appendChild(conference[7]);
					}
					if (rConference.length>7 && !rConference[7].equals("")){
						conference[8] = docx.createElementNS(namespace, "other-indexes");
						conference[8].setTextContent(rConference[7]);
						conference[0].appendChild(conference[8]);
					}
				}
				rec.appendChild(conference[0]);
			}
		}
	}
	private void addJournalCitation()
	{
		Element journal = docx.createElementNS(namespace, "journal");
		rec.appendChild(journal);
		Element journalTitle = docx.createElementNS(namespace, "title");
		String lang = rMap.get(fields[28][2])[0].toLowerCase();
		journalTitle.setAttribute("lang", changeLangCodes(lang).toUpperCase());
		journalTitle.setTextContent(rMap.get(fields[8][2])[0]);
		journal.appendChild(journalTitle);
		addISSN(journal);
		addEISSN(journal);
		//issue
		String rIssue = rMap.get(fields[12][2])[0];
		if (rIssue.length()>0)
		{
			Element issue = docx.createElementNS(namespace, "issue");
			issue.setTextContent(rIssue);
			rec.appendChild(issue);
		}
		//volume
		String rVolume = rMap.get(fields[11][2])[0];
		if (rVolume.length()>0)
		{
			Element volume = docx.createElementNS(namespace, "volume");
			volume.setTextContent(rVolume);
			rec.appendChild(volume);
		}
		//pages
		Element pages = docx.createElementNS(namespace, "pages");
		String rPages = rMap.get(fields[15][2])[0];
		//rPages = Func.countPages(rPages);
		if (rPages!=null && !rPages.equals(""))
		{
			/*if (rPages.indexOf("-")==-1 && rPages.indexOf("�")==-1)
				rPages = rPages + "-" + rPages;*/
			pages.setTextContent(rPages);
			rec.appendChild(pages);
		}
		else{
			rPages = rMap.get(fields[16][2])[0];
			if (rPages!=null){
				pages.setTextContent(rPages);
				rec.appendChild(pages);
			}
		}
	}
	private void addBookCitation()
	{
		Element book = docx.createElementNS(namespace, "book");
		rec.appendChild(book);
		//title
		Element bookTitle = docx.createElementNS(namespace, "title");
		bookTitle.setTextContent(rMap.get(fields[9][2])[0]);
		book.appendChild(bookTitle);
		//date
		Element bookDate = docx.createElementNS(namespace, "publication-date");
		bookDate.setTextContent(rMap.get(fields[17][2])[0]);
		book.appendChild(bookDate);
		addISBN(23, book);
		addISBN(24, book);
		addPublishers(book);
	}
	private void addISSN(Element parent)
	{
		String[] issns = rMap.get(fields[25][2]);
		if (!issns[0].equals("")) addISSNs(parent, issns, "issn");
	}
	private void addEISSN(Element parent)
	{
		String[] issns = rMap.get(fields[26][2]);
		if (!issns[0].equals("")) addISSNs(parent, issns, "eissn");
	}
	private void addISSNs(Element parent, String[] issns, String elementName)
	{
		Element[] issnElement = new Element[issns.length];
		boolean necessary;
		for (int x=0; x<issns.length; x++)
		{
			necessary = parent.getElementsByTagName(elementName).getLength()==0;
			if (necessary)
			{
				issnElement[x] = docx.createElementNS(namespace, elementName);
				issnElement[x].setTextContent(issns[x]);
				parent.appendChild(issnElement[x]);
			}
		}
	}
	private void addElementToParent(Element parent, String elementName, int fieldIndex)
	{
		String fieldValue[] = rMap.get(fields[fieldIndex][2]);
		for (int x=0; x<fieldValue.length; x++)
		{
			if (fieldValue[x].length()>0)
			{
				Element element = docx.createElementNS(namespace, elementName);
				element.setTextContent(fieldValue[x]);
				parent.appendChild(element);
			}
		}
	}
	private void addElementToParentWithAttribute(Element parent, String elementName, int fieldIndex, String attribute)
	{
		String fieldValue[] = rMap.get(fields[fieldIndex][2]);
		for (int x=0; x<fieldValue.length; x++)
		{
			if (fieldValue[x].length()>0)
			{
				Element element = docx.createElementNS(namespace, elementName);
				element.setAttribute(attribute, fieldValue[x]);
				parent.appendChild(element);
			}
		}
	}
	private void addFirstElementToParentWithAttribute(Element parent, String elementName, int fieldIndex, String attribute){
		String fieldValue[] = rMap.get(fields[fieldIndex][2]);
		if (fieldValue[0].length()>0)
		{
			Element element = docx.createElementNS(namespace, elementName);
			element.setAttribute(attribute, fieldValue[0]);
			parent.appendChild(element);
		}
	}
	private void addPublishers(Element parent)
	{
		String[] values = rMap.get(fields[20][2]);
		String[][] pubinfo = new String[values.length][2];
		Element[][] elements = new Element[values.length][2];
		int a, b=0, c=0;
		for (int x=0; x<values.length; x++)
		{
			a = values[x].indexOf(":");
			if (a>0)
			{
				pubinfo[x][0] = values[x].substring(0, values[x].indexOf(":")-1);
				pubinfo[x][1] = values[x].substring(values[x].indexOf(":")+2);
			}
			else if (a==0)
			{
				pubinfo[x][0] = "";
				pubinfo[x][1] = values[x].substring(1);
			}
			else
			{
				pubinfo[x][0] = values[x];
				pubinfo[x][1] = "";
			}
		}
		for (int x=0; x<pubinfo.length; x++){
			if (!pubinfo[x][1].equals(""))
			{
				elements[x][1] = docx.createElementNS(namespace, "publisher-name");
				elements[x][1].setTextContent(pubinfo[x][1]);
				parent.appendChild(elements[x][1]);
				break;
			}
		}
		for (int x=0; x<pubinfo.length; x++){
			if (!pubinfo[x][1].equals(""))
			{
				elements[x][0] = docx.createElementNS(namespace, "publication-place");
				elements[x][0].setTextContent(pubinfo[x][0]);
				parent.appendChild(elements[x][0]);
				break;
			}
		}
	}
	private void addISBN(int field, Element parent)
	{
		String role = fields[field][0];
		String[] values = rMap.get(fields[field][2]);
		Element[] elements = new Element[values.length];
		String isbn;
		String[] temp;
		boolean necessary;
		for (int x=0; x<values.length; x++)
		{
			necessary = parent.getElementsByTagName(fields[23][0]).getLength()==0
					&& parent.getElementsByTagName(fields[24][0]).getLength()==0;
			if (necessary)
			{
				temp = values[x].split(" ");
				isbn = temp[0].replaceAll("[^-?0-9?X]+", "");
				if (isbn.length()>0)
				{
					elements[x] = docx.createElementNS(namespace, role);
					elements[x].setTextContent(isbn);
					parent.appendChild(elements[x]);
				}
			}
		}
	}
	private void addPersons(int field, String role)
	{
		String[] persons = rMap.get(fields[field][2]);
		String[][] output = new String[persons.length][5];
		String id;
		for (int x=0; x<persons.length; x++)
		{
			id = persons[x].replaceAll("[^0-9]+", "");
			if (persons[x].contains(", "))
			{
				output[x][0] = persons[x].substring(0, persons[x].indexOf(", "));
				if (!id.equals(""))
				output[x][1] = persons[x].substring(persons[x].indexOf(", ")+2, persons[x].indexOf(" ["));
				else
				output[x][1] = persons[x].substring(persons[x].indexOf(", ")+2);
				output[x][2] = id;
				if (persons[x].contains("[")){
					output[x][3] = persons[x].substring(persons[x].indexOf("[")+1, persons[x].indexOf("]"));
				}
			}
		}
		Element[][] personsElements = new Element[output.length][6];
		for (int x=0; x<output.length; x++)
		{
			if (output[x]!=null || !output[x][0].equals(""))
			{
				personsElements[x][0] = docx.createElementNS(namespace, role);
				personsElements[x][1] = docx.createElementNS(namespace, "given-names");
				personsElements[x][1].setTextContent(output[x][1]);
				personsElements[x][2] = docx.createElementNS(namespace, "family-name");
				personsElements[x][2].setTextContent(output[x][0]);
				rec.appendChild(personsElements[x][0]);
				personsElements[x][0].appendChild(personsElements[x][1]);
				personsElements[x][0].appendChild(personsElements[x][2]);
				if (output[x][2]!=null && !output[x][2].equals(""))
				{
					personsElements[x][3] = docx.createElementNS(namespace, "system-identifier");
					//personsElements[x][3].setAttribute("system", "RUJ");
					if (persons[x].contains("SAP")) {
						personsElements[x][3].setTextContent("SAP" + output[x][2]);
					} else if (persons[x].contains("USOS")) {
						personsElements[x][3].setTextContent("USOS" + output[x][2]);
					}
					personsElements[x][0].appendChild(personsElements[x][3]);
				}
				if (output[x][3]!=null && isListedAsAffiliated(persons[x])){
						output[x][4] = "1";
						personsElements[x][4] = docx.createElementNS(namespace, "affiliated-to-unit");
						personsElements[x][4].setTextContent("true");
						personsElements[x][0].appendChild(personsElements[x][4]);
				}
				if (personData == null && output[x][4]!=null && output[x][4].equals("1")){
					if (!isMarkedAsAffiliated(output[x][2])){
						personsElements[x][5] = docx.createElementNS(namespace, "employed-in-unit");
						personsElements[x][5].setTextContent("true");
						personsElements[x][0].appendChild(personsElements[x][5]);
					}
				}
				else if (personData!=null && isListedAsEmployed(persons[x])){
					personsElements[x][5] = docx.createElementNS(namespace, "employed-in-unit");
					personsElements[x][5].setTextContent("true");
					personsElements[x][0].appendChild(personsElements[x][5]);
				}
			}
		}
	}
	private void addSeries()
	{
		String[] values = rMap.get(fields[22][2]);
		String[] series;
		String numberValue;
		Element[][] elements = new Element[values.length][2];
		for (int x=0; x<values.length; x++)
		{
			series = Func.dcSeriesToFields(values[x]);
			elements[x][0] = docx.createElementNS(namespace, "series");
			elements[x][0].setTextContent(series[0]);
			rec.appendChild(elements[x][0]);
			if (!series[3].equals(""))
			{
				elements[x][1] = docx.createElementNS(namespace, "number-in-series");
				elements[x][1].setTextContent(series[3]);
				rec.appendChild(elements[x][1]);
			}
			break;
		}
	}
	private void addPages(String type)
	{
		//String rPages = Func.countPages(rMap.get(fields[15][2])[0]);
		String rPages = rMap.get(fields[15][2])[0];
		if (rPages!=null && !rPages.equals(""))
		{
			//if (type.equals("BookSection") && rPages.indexOf("-")==-1)
				//rPages = rPages + "-" + rPages;
			Element pages = docx.createElementNS(namespace, "pages");
			pages.setTextContent(rMap.get(fields[15][2])[0]);//wy��czone liczenie stron
			rec.appendChild(pages);
		}
		else {
			rPages = rMap.get(fields[16][2])[0];
			if (rPages!=null)
			{
				Element pages = docx.createElementNS(namespace, "pages");
				pages.setTextContent(rMap.get(fields[15][2])[0]);//wy��czone liczenie stron
				rec.appendChild(pages);
			}
		}
	}
	private void addFeatures()
	{
		String tempCollection = rMap.get(fields[38][2])[0];
		int collection = Integer.parseInt(tempCollection.substring(tempCollection.indexOf("/")+1));
		String type = rMap.get(fields[36][2])[0];
		String subType = rMap.get(fields[37][2])[0];
		//Warunki typ�w
		//Rozdzia�
		if (type.equals("BookSection"))
		{
			if (subType.equals("Report"))
			{
				Element isAccount = docx.createElementNS(namespace, "is");
				isAccount.setTextContent("account");
				rec.appendChild(isAccount);
			}
			if (subType.equals("Encyclopedia"))
			{
				Element isEncyclopedia = docx.createElementNS(namespace, "is");
				isEncyclopedia.setTextContent("encyclopaedia-entry");
				rec.appendChild(isEncyclopedia);
			}
			if (subType.equals("Foreword"))
			{
				Element isEditorial = docx.createElementNS(namespace, "is");
				isEditorial.setTextContent("introduction-preface");
				rec.appendChild(isEditorial);
			}
			if (subType.equals("Map"))
			{
				Element isMap = docx.createElementNS(namespace, "is");
				isMap.setTextContent("map");
				rec.appendChild(isMap);
			}
			if (subType.equals("OtherDocuments"))
			{
				Element isOtherDocuments = docx.createElementNS(namespace, "is");
				isOtherDocuments.setTextContent("note");
				rec.appendChild(isOtherDocuments);
			}
			if (subType.equals("Anthology"))
			{
				Element isTextInAnthology = docx.createElementNS(namespace, "is");
				isTextInAnthology.setTextContent("text-in-anthology");
				rec.appendChild(isTextInAnthology);
			}
			if (collection==3)
			{
				Element isPopular = docx.createElementNS(namespace, "is");
				isPopular.setTextContent("popular-science-text");
				rec.appendChild(isPopular);
			}
		}
		//Ksi��ka
		if (type.equals("Book"))
		{
			if (collection==3)
			{
				Element isPopular = docx.createElementNS(namespace, "is");
				isPopular.setTextContent("popular-science-book");
				rec.appendChild(isPopular);
			}
			if (collection==2)
			{
				Element isMonograph = docx.createElementNS(namespace, "is");
				isMonograph.setTextContent("scholarly-monograph");
				rec.appendChild(isMonograph);
			}
			if (subType.equals("Anthology"))
			{
				Element isAnthology = docx.createElementNS(namespace, "is");
				isAnthology.setTextContent("anthology");
				rec.appendChild(isAnthology);
			}
			if (subType.equals("Map"))
			{
				Element isMap = docx.createElementNS(namespace, "is");
				isMap.setTextContent("atlas-map");
				rec.appendChild(isMap);
			}
			if (subType.equals("Encyclopedia"))
			{
				Element isEncyclopedia = docx.createElementNS(namespace, "is");
				isEncyclopedia.setTextContent("encyclopedia-dictionary");
				rec.appendChild(isEncyclopedia);
			}
			if (subType.equals("Encyclopedia"))
			{
				Element isReport = docx.createElementNS(namespace, "is");
				isReport.setTextContent("expertise-report");
				rec.appendChild(isReport);
			}
			if (subType.equals("Foreword"))
			{
				Element isEditorial = docx.createElementNS(namespace, "is");
				isEditorial.setTextContent("introduction-preface");
				rec.appendChild(isEditorial);
			}
		}
		//Artyku� w czasopismie
		if (type.equals("JournalArticle"))
		{
			if (collection==3)
			{
				Element isPopular = docx.createElementNS(namespace, "is");
				isPopular.setTextContent("popular-science-article");
				rec.appendChild(isPopular);
			}
			if (subType.equals("Article") && collection==2)
			{
				Element isArticle = docx.createElementNS(namespace, "is");
				isArticle.setTextContent("original-article");
				rec.appendChild(isArticle);
			}
			if (subType.equals("Encyclopedia"))
			{
				Element isEncyclopedia = docx.createElementNS(namespace, "is");
				isEncyclopedia.setTextContent("encyclopaedia-entry");
				rec.appendChild(isEncyclopedia);
			}
			if (subType.equals("Map"))
			{
				Element isMap = docx.createElementNS(namespace, "is");
				isMap.setTextContent("map");
				rec.appendChild(isMap);
			}
			if (subType.equals("OtherDocuments"))
			{
				Element isOtherDocuments = docx.createElementNS(namespace, "is");
				isOtherDocuments.setTextContent("note");
				rec.appendChild(isOtherDocuments);
			}
			if (subType.equals("Foreword"))
			{
				String[] pages = rMap.get(fields[15][2])[0].split("-");
				if (pages.length<1){
					int size = Integer.parseInt(pages[1])-Integer.parseInt(pages[0]);
					if (size<4)
					{
						Element isEditorial = docx.createElementNS(namespace, "is");
						isEditorial.setTextContent("editorial");
						rec.appendChild(isEditorial);
					}
					else
					{
						Element isIntroduction = docx.createElementNS(namespace, "is");
						isIntroduction.setTextContent("introduction-preface");
						rec.appendChild(isIntroduction);
					}
				}
			}
		}
		//Warunki kolekcji
		if (collection==2)
		{
			Element isReviewed = docx.createElementNS(namespace, "is");
			isReviewed.setTextContent("peer-reviewed");
			rec.appendChild(isReviewed);
			if (subType.equals("Foreword"))
			{
				Element isForeword = docx.createElementNS(namespace, "is");
				isForeword.setTextContent("chapter-in-a-book");
				rec.appendChild(isForeword);
			}
		}
		//Warunki podtyp�w
		if (subType.equals("Afterword"))
		{
			Element isAfterword = docx.createElementNS(namespace, "is");
			isAfterword.setTextContent("afterword");
			rec.appendChild(isAfterword);
		}
		if (subType.equals("Bibliography"))
		{
			Element isBibliography = docx.createElementNS(namespace, "is");
			isBibliography.setTextContent("bibliography");
			rec.appendChild(isBibliography);
		}
		if (subType.equals("Catalogue"))
		{
			Element isCatalogue = docx.createElementNS(namespace, "is");
			isCatalogue.setTextContent("catalogue");
			rec.appendChild(isCatalogue);
		}
		if (subType.equals("Commentary"))
		{
			Element isCommentary = docx.createElementNS(namespace, "is");
			isCommentary.setTextContent("commentary-on-the-law");
			rec.appendChild(isCommentary);
		}
		if (subType.equals("CriticalEdition"))
		{
			Element isCriticalEdition = docx.createElementNS(namespace, "is");
			isCriticalEdition.setTextContent("critical-edition-of-literary-texts");
			rec.appendChild(isCriticalEdition);
		}
		if (subType.equals("SourceEditorship"))
		{
			Element isSourceEditorship = docx.createElementNS(namespace, "is");
			isSourceEditorship.setTextContent("edition-of-source-texts");
			rec.appendChild(isSourceEditorship);
		}
		if (subType.equals("OtherArticle"))
		{
			Element isPublicistic = docx.createElementNS(namespace, "is");
			isPublicistic.setTextContent("journalistic-article");
			rec.appendChild(isPublicistic);
		}
		if (subType.equals("Review"))
		{
			Element isReview = docx.createElementNS(namespace, "is");
			isReview.setTextContent("review");
			rec.appendChild(isReview);
		}
		if (subType.equals("Overview"))
		{
			Element isReviewArticle = docx.createElementNS(namespace, "is");
			isReviewArticle.setTextContent("review-article");
			rec.appendChild(isReviewArticle);
		}
		if (subType.equals("Handbook"))
		{
			Element isHandbook = docx.createElementNS(namespace, "is");
			isHandbook.setTextContent("scholarly-textbook");
			rec.appendChild(isHandbook);
		}
	}
	private void addKeywords(){
		String[] keyPL = rMap.get(fields[44][2])[0].split("\\|\\|");
		if (!keyPL[0].equals("")){
			Element keywordPL = docx.createElementNS(namespace, "keywords");
			keywordPL.setAttribute("lang", "PL");
			Element[] kPL = new Element[keyPL.length];
			for (int x=0; x<keyPL.length; x++){
				kPL[x] = docx.createElementNS(namespace, "k");
				kPL[x].setTextContent(keyPL[x]);
				keywordPL.appendChild(kPL[x]);
			}
			rec.appendChild(keywordPL);
		}
		
		String[] keyEN = rMap.get(fields[45][2])[0].split("\\|\\|");
		if (!keyEN[0].equals("")){
			Element keywordEN = docx.createElementNS(namespace, "keywords");
			keywordEN.setAttribute("lang", "EN");
			Element[] kEN = new Element[keyEN.length];
			for (int x=0; x<keyEN.length; x++){
				kEN[x] = docx.createElementNS(namespace, "k");
				kEN[x].setTextContent(keyEN[x]);
				keywordEN.appendChild(kEN[x]);
			}
			rec.appendChild(keywordEN);
		}
	}
	private void addOpenAccess(String type){
		String[] values = rMap.get(fields[42][2]);
		if (values[0].equals("") || values[0].equals("bez licencji")) return;
		String[] field;
		boolean isJournal = false;
		if (type.equals("JournalArticle")) isJournal = true;
		Element[] elements = new Element[6];
		field = values[0].substring(0, values[0].length()-1).split("; ");
		elements[0] = docx.createElementNS(namespace, "open-access");
		
		elements[1] = docx.createElementNS(namespace, "open-access-text-version");
		elements[1].setTextContent(Func.translateTermOpenAccess(field[2], isJournal));
		elements[0].appendChild(elements[1]);
		
		elements[2] = docx.createElementNS(namespace, "open-access-license");
		elements[2].setTextContent(field[0]);
		elements[0].appendChild(elements[2]);
		
		elements[3] = docx.createElementNS(namespace, "open-access-release-time");
		elements[3].setTextContent(Func.translateTermOpenAccess(field[3], isJournal));
		elements[0].appendChild(elements[3]);
		
		if (field[3].equals("po opublikowaniu")){
			String months;
			if (field.length>4) months = field[4];
			else months = "1";
			if (months.equals("0")) months = "1";
			elements[4] = docx.createElementNS(namespace, "open-access-months");
			elements[4].setTextContent(months);
			elements[0].appendChild(elements[4]);
		}
		
		elements[5] = docx.createElementNS(namespace, "open-access-mode");
		elements[5].setTextContent(Func.translateTermOpenAccess(field[1],isJournal));
		elements[0].appendChild(elements[5]);
		
		rec.appendChild(elements[0]);
	}
	public void saveXML() throws TransformerFactoryConfigurationError, TransformerException, IOException
	{
		JFileChooser choose = new JFileChooser();
		choose.setCurrentDirectory(new File(settings.getProperty("lastFilePath")));
		int result = choose.showSaveDialog(null);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			file = choose.getSelectedFile();
		}
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		FileOutputStream writer = new FileOutputStream(file);
		transformer.transform(new DOMSource(docx), new StreamResult(writer));
		//progressbar.getParent().removeAll();
		writer.close();
	}
	private int[] contributorsColumns(String[] headlines, String type, String subType)
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
	private String changeLangCodes(String code)
	{
		for (int x=0; x<langCodes.length; x++)
		{
			if (langCodes[x][0].equals(code))
			{
				return langCodes[x][1];
			}
		}
		return "";
	}
	private void refreshTemp(){
		temp = new String[5];
		temp[0] = record.getID();
		temp[1] = rMap.get(RFL.F[RFL.TITLE][2])[0];
	}
	protected void process(List<Integer> progress)
	{
		progressbar.setValue(filled);
	}
	protected void done()
	{
		try{get();}catch (Exception e) {
			System.out.println(X);e.printStackTrace();
			JOptionPane.showMessageDialog(MetAnWindow.get(), "Pojawi� si� b��d, program nie m�g� wyeksportowa� wszystkich rekord�w");
		}
		Toolkit.getDefaultToolkit().beep();
		progressbar.setValue(maximum);
		//JOptionPane.showMessageDialog(MetAnWindow.myIdentity, "Wyeksportowano "+X+" rekord�w.");
		int status = XMLReport.dialog(X);
		if (status==1){
			JFileChooser chooseCSV = new JFileChooser();
			chooseCSV.setCurrentDirectory(new File(settings.getProperty("lastSavePath")));
			int result = chooseCSV.showSaveDialog(null);
			if (result == JFileChooser.APPROVE_OPTION)
			{
				String outputFile = chooseCSV.getSelectedFile().getPath();
				try {
					SaveCSV saveFile = new SaveCSV(outputFile, Func.toArray(reportList), new String[]{
							"id","tytu�","autor","afiliacja","zatrudnienie"
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else if (wrongCountryCodes.size()>0) listWrongISOCodes();
		else {
			listExcludedRecords();
			try {
				saveXML();
			} catch (TransformerFactoryConfigurationError
					| TransformerException | IOException e) {e.printStackTrace();}
		}
	}
	private void listWrongISOCodes(){
		int colConf = Func.findColumn(inputData[0], fields[41][2]);
		int a = wrongCountryCodes.size();
		ArrayList<String[]> list = new ArrayList<>();
		String id;
		for (int x=0; x<a; x++){
			id = wrongCountryCodes.get(x);
			for (int y=1; y<inputData.length; y++){
				if (id.equals(inputData[y][0])){
					list.add(new String[]{id,inputData[y][colConf]});
					break;
				}
			}
		}
		String[][] out = Func.toArray(list);
		MetAnWindow.myIdentity.createTable(out, new String[]{"id", "dc.conference[pl]"});
		JOptionPane.showMessageDialog(MetAnWindow.myIdentity, "Znaleziono "+out.length+" �le wprowadzonych nazw kraj�w w konferencjach");
	}
	private void listExcludedRecords(){
		ArrayList<String[]> list = new ArrayList<String[]>();
		int colAf = Func.findColumn(inputData[0], "dc.affiliation[pl]");
		int colTitle = Func.findColumn(inputData[0], "dc.title[pl]");
		int colDate = Func.findColumn(inputData[0], "dc.date.issued[pl]");
		int admin1 = Func.findColumn(inputData[0], "dc.description.admin[pl]");
		int admin2 = Func.findColumn(inputData[0], "dc.description.admin[]");
		if (colAf==-1) return;
		int a = exportedRecordIds.length;
		int checkYear;
		boolean check;
		for (int x=1; x<inputData.length; x++){
			checkYear=0;
			if (inputData[x][colAf].contains(unitId[2]) && exportedRecordIds[x]==null){
				for (int y=0; y<year.length; y++){
					if (inputData[x][colDate].equals(year[y])) checkYear++; 
				}
				if (checkYear>0){
					if (inputData[x][typeCol].equals("JournalArticle") || 
							inputData[x][typeCol].equals("BookSection") || 
							inputData[x][typeCol].equals("Book"))
							list.add(new String[]{inputData[x][0], inputData[x][colAf], inputData[x][colTitle],
									inputData[x][admin1], inputData[x][admin2]});
				}
			}
		}
		String[][] out = Func.toArray(list);
		MetAnWindow.myIdentity.createTable(out, new String[]{"id", "dc.affiliation[pl]", "dc.title[pl]", "dc.description.admin[pl]", "dc.description.admin[]"});
	}
}