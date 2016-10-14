package OtherDialogs;

import CustomActions.OpenCSV;
import ExportXML.ParseXML;
import Functions.CompareRecordsInArray;
import Imports.ImportCSV;
import Interfaces.CSVDataHolder;
import PMainWindow.Func;
import PMainWindow.MetAnWindow;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ExportToPBNDialog extends JFrame implements CSVDataHolder {
	private JPanel pane;
	private GridBagConstraints constr;
	private JComboBox<String> unitList, datesList;
	private String[][] previousCSV, personCSV;
	private JButton[] buttons = new JButton[5];
	private JLabel[] labels = new JLabel[5];
	private String[][] units;
	private boolean isLoading;
	private Document previousDoc;
	public ExportToPBNDialog() throws IOException{
		pane = new JPanel();
		GridBagLayout mainLayout = new GridBagLayout();
		pane.setLayout(mainLayout);
		setLocationRelativeTo(MetAnWindow.myIdentity);
		createButtons();
		refresh();
	}
	private void createButtons() throws IOException{
		buttons[0] = new JButton("Dane z RUJ");
		buttons[1] = new JButton("Dane z poprzedniego eksportu");
		buttons[2] = new JButton("Plik z afiliacjami");
		buttons[3] = new JButton("Dodaj do XML");
		buttons[4] = new JButton("Eksportuj");
		buttons[0].addActionListener(new OpenCSV(MetAnWindow.myIdentity));
		buttons[1].addActionListener(new OpenCSV(this));
		buttons[2].addActionListener(new importPersonData());
		buttons[3].addActionListener(new importPreviousXML());
		buttons[4].addActionListener(new exportToXML());
		
		//lista jednostek
		units = ImportCSV.readData(new File("./csv/units.csv"), "\"");
		String[] names = new String[units.length];
		for (int x=1; x<units.length; x++) {
			names[x] = units[x][1];
		}
		unitList = new JComboBox<String>(names);	
		
		//wyb�r lat
		int currentYear = (int) (System.currentTimeMillis()/1000/3600/24/365.25 +1970);
		String[] list = new String[currentYear-2011];
		for (int x=2013; x<=currentYear; x++){
			list[x-2013] = ""+x;
		}
		list[list.length-1] = "wszystkie";
		datesList = new JComboBox<String>(list);
	}
	private void refresh(){
		pane.removeAll();
		constr = new GridBagConstraints();
		constr.weightx = 0;
		constr.weighty = 50;
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.insets = new Insets(5, 5, 5, 5);
		
		constr.gridy = 0;
		constr.gridx = 0;
		pane.add(unitList, constr);
		constr.gridx = 1;
		pane.add(datesList, constr);
		if (MetAnWindow.myIdentity.loadedCSV!=null){
			labels[0] = new JLabel("Wczytano "+MetAnWindow.myIdentity.loadedCSV.length+" rekord�w.");
		}
		if (personCSV!=null)
			labels[2] = new JLabel("Wczytano "+personCSV.length+" os�b.");
		for (int x=0; x<buttons.length; x++){
			constr.gridy = x+1;
			constr.gridx = 0;
			pane.add(buttons[x], constr);
			if (labels[x]!=null){
				constr.gridx = 1;
				pane.add(labels[x], constr);
			}
		}
		
		if (isLoading){
			constr.gridx = 0;
			constr.gridwidth = 2;
			constr.gridy = buttons.length+2;
			progressBar.setStringPainted(true);
			pane.add(progressBar, constr);
			progressBar.setIndeterminate(true);
		}
		add(pane);
		pack();
		repaint();revalidate();
	}
	@Override
	public void reportLoadedFile(String[][] inputData) {
		if (previousCSV==null) previousCSV = inputData;
		else previousCSV = Func.mergeArrays(previousCSV, inputData);
		labels[1] = new JLabel("Wczytano "+previousCSV.length+" rekord�w.");
		refresh();
		if (previousCSV.length > 0 && previousCSV[0][0].equals("id")) {
			previousCSV = Func.removeRow(previousCSV, 0);
		}
		Arrays.sort(previousCSV, new CompareRecordsInArray());
	}
	@Override
	public String[][] getCSVData() {
		return previousCSV;
	}
	@Override
	public void startAction() {
		isLoading = true;
		refresh();
	}
	@Override
	public void endAction() {
		isLoading = false;
		refresh();
	}
	
	private class exportToXML extends AbstractAction{
		@Override
		public void actionPerformed(ActionEvent e) {
			int unitListPos = unitList.getSelectedIndex();
			int dateListPos = datesList.getSelectedIndex();
			if (unitListPos==-1 || dateListPos==-1) Toolkit.getDefaultToolkit().beep();
			else{
				String[] year;
				if (dateListPos==datesList.getItemCount()-1){
					year = new String[dateListPos];
					for (int x=0; x<dateListPos; x++){
						year[x] = ""+(2013+x);
					}
				}
				else{
					year = new String[]{""+(2013+dateListPos)};
				}
				ParseXML export = new ParseXML(previousCSV, personCSV, new String[]{
						units[unitListPos][2], units[unitListPos][0], units[unitListPos][3]},
						year);
				if (previousDoc!=null) export.setDocument(previousDoc);
				export.execute();
				dispose();
			}
		}
	}	
	private class importPersonData extends AbstractAction{
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooseFile = new JFileChooser();
			chooseFile.setCurrentDirectory(new File(MetAnWindow.settings.getProperty("lastFilePath")));
			int result = chooseFile.showOpenDialog(pane);
			if (result == JFileChooser.APPROVE_OPTION){
				try {
					String[][] persons = ImportCSV.readData(chooseFile.getSelectedFile(), "\"");
					if (persons[0][0].equals("item_id")) {
						persons = Func.removeRow(persons, 0);
					}
					personCSV = Func.mergeArrays(personCSV, persons);
					Arrays.sort(personCSV, new CompareRecordsInArray());
					refresh();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	private class importPreviousXML extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooseFile = new JFileChooser();
			chooseFile.setCurrentDirectory(new File(MetAnWindow.settings.getProperty("lastFilePath")));
			int result = chooseFile.showOpenDialog(pane);
			if (result == JFileChooser.APPROVE_OPTION){
					InputStream is;
					try {
						is = new FileInputStream(chooseFile.getSelectedFile());
						previousDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
					} catch (SAXException | IOException | ParserConfigurationException e1) {
						e1.printStackTrace();
					}
					refresh();
			}
		}
	}
}
