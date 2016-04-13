package Imports;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import PMainWindow.MetAnWindow;
import Records.RFL;

public class LoadXML {
	private ArrayList<String[]> rList;
	private String[][] dataAsArray;
	private String[] fields;
	public LoadXML(File file) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(file);
		convertXMLtoRecords(doc);
	}
	private void convertXMLtoRecords(Document doc){
		Element root = doc.getDocumentElement();
		NodeList records = root.getChildNodes();
		NodeList recordElements, eElements;
		Node actualRecord, element, subelement;
		String[][] stats = RFL.list();
		fields = new String[stats.length];
		fields[0] = stats[39][2];
		for (int x=0; x<39; x++){
			fields[x+1] = stats[x][2];
		}
		for (int x=40; x<stats.length; x++){
			fields[x] = stats[x][2];
		}
		
		String[] line;
		String name;
		ArrayList<String[]> list = new ArrayList<String[]>();
		for (int x=0; x<records.getLength(); x++){
			actualRecord = records.item(x);
			if (actualRecord instanceof Element){
				line = new String[2];
				recordElements = actualRecord.getChildNodes();
				for (int y=0; y<recordElements.getLength(); y++){
					element = recordElements.item(y);
					if (element instanceof Element){
						name = ((Element) element).getTagName();
						switch (name) {
						case "author":
							if (line[1]==null || line[1].equals("")){
								line[1] = getAuthorsToArray((Element) element);
							}
							else line[1] = line[1]+"||"+getAuthorsToArray((Element) element);
							break;
						case "system-identifier":
							line[0] = element.getTextContent();
							break;
						default:
							break;
						}
					}
				}
				list.add(line);
			}
		}
		
		String[][] out = new String[list.size()][];
		for (int x=0; x<list.size(); x++){
			out[x] = list.get(x);
		}
		dataAsArray = out;
	}
	private String getAuthorsToArray(Element e){
		NodeList list = e.getChildNodes();
		Node element;
		String[] authorEntry = new String[3];
		String name;
		for (int x=0; x<list.getLength(); x++){
			element = list.item(x);
			if (element instanceof Element){
				name = ((Element) element).getTagName();
				switch (name) {
				case "given-names":
					authorEntry[1] = element.getTextContent();
					break;
				case "family-name":
					authorEntry[0] = element.getTextContent();
					break;
				case "system-identifier":
					authorEntry[2] = "[" + element.getTextContent() + "]";
					break;
				}
			}
		}
		return authorEntry[0] + ", " + authorEntry[1] + " " + authorEntry[2];
	}
	public String[][] getData(){
		return dataAsArray;
	}
	public String[] getColumns(){
		return new String[]{"id","dc.contributor.author[pl]"};
	}
}
