package PMainWindow;

import CustomActions.SimpleSearch;
import Imports.ImportCSV;
import Imports.LoadScopusIndex;
import Imports.LoadWoSConferenceIndex;
import Imports.LoadXML;
import Inspector.Check;
import Inspector.ConferenceIndex;
import Inspector.RTFields;
import Inspector.ScopusIndex;
import Interfaces.CSVDataHolder;
import OtherDialogs.*;
import OtherExports.*;
import RecordEditor.CreateBasicEditor;
import Records.CompleteRecord;
import Records.FieldsFromArray;
import Records.RFL;
import Records.RecordRIS;
import RisSection.OpenBiB;
import RisSection.OpenRis;
import jTableFunctions.ButtonColumn;
import jTableFunctions.MouseRightClick;
import jTableFunctions.TableCellColor;
import jTableFunctions.TableColorByRow;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Properties;

public class MetAnWindow extends JFrame implements CSVDataHolder
{
	public static Properties settings;
	public static MetAnWindow myIdentity;
	private final JPanel mainPanel;
	private final Container mainPane;
	private final Container[] containerSets = new Container[5];
	private JTable table;
	private JScrollPane tableScroll;
	private JMenuBar menuBar = new JMenuBar();
	private final JMenu[] menus = new JMenu[6];
	public final JMenuItem[] menuItems = new JMenuItem[28];
	public JTextField simpleSearchField;
	private JPopupMenu popup;
	private JToolBar toolbar; private final JButton[] toolbarButtons = new JButton[5];
	private JList<String> list1, list2;
	
	public File csvFile;
	private File[] ris_bibFiles;
	public MetAnSettings settingsMethod;
	
	public String[][] loadedCSV, loadedWoSConferences, scopusIndex, loadedXML;
	private String[][] temporaryData;
	private String[] temporaryColumnNames;
	private ArrayList<Records.RecordRIS> listOfRis;
	private ExcludedDuplicatesFile exclusions;
	public boolean conferenceFullReport = false;
	
	final KeyStroke keyPaste = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);
	
	class OpenCSV extends AbstractAction
	{
		public void actionPerformed(ActionEvent element)
		{
			JFileChooser chooseCSV = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("csv i ris", "csv", "ris");
			chooseCSV.setFileFilter(filter);
			chooseCSV.setCurrentDirectory(new File(settings.getProperty("lastFilePath")));
			int result = chooseCSV.showOpenDialog(menuItems[0]);
			if (result == JFileChooser.APPROVE_OPTION)
			{
				menuAllHide();
				csvFile = chooseCSV.getSelectedFile();
				if (csvFile.getPath().toLowerCase().indexOf(".ris")>-1) 
				{
					try {
						myIdentity.convertRIS();
						menuShowStart();
					} catch (IOException e) {e.printStackTrace();}
				}
				else
				{
					//loadedCSV = LoadCSV.load(chooseCSV.getSelectedFile());
					settings.put("lastFilePath", csvFile.getPath());
					settingsMethod.setSettings(settings);
					listOfRis = new ArrayList<Records.RecordRIS>();
					mainPane.removeAll();
					progressBar.setValue(0);
					progressBar.setStringPainted(true);
					mainPane.add(progressBar, BorderLayout.PAGE_END);
					progressBar.setOpaque(true);
					progressBar.setIndeterminate(true);
					revalidate();
					repaint();
					LoadCSV dataFromFile = new LoadCSV(chooseCSV.getSelectedFile(), progressBar, myIdentity);
					dataFromFile.execute();
				}
			}
			menuShowStart();
		}
	}
	class FindDuplicatesAction extends AbstractAction
	{
		public FindDuplicatesAction(String element)
		{super(element);}
		public void actionPerformed(ActionEvent event)
		{
			try {
				menuAllHide();
				mainPane.removeAll();
				progressBar.setValue(0);
				progressBar.setStringPainted(true);
				mainPane.add(progressBar, BorderLayout.PAGE_END);
				progressBar.setOpaque(true);
				revalidate();
				repaint();
				exclusions = new ExcludedDuplicatesFile();
				Duplicates findingResult = new Duplicates(loadedCSV, progressBar, myIdentity, exclusions);
				findingResult.execute();
				} catch (IOException e) {
					e.printStackTrace();
				}
			menuShowAfterLoadCSV();
		}
	}
	class ExportCSV extends AbstractAction
	{
		public ExportCSV(){}
		public void actionPerformed(ActionEvent event)
		{
			JFileChooser chooseCSV = new JFileChooser();
			chooseCSV.setCurrentDirectory(new File(settings.getProperty("lastSavePath")));
			int result = chooseCSV.showSaveDialog(null);
			if (result == JFileChooser.APPROVE_OPTION)
			{
				String outputFile = chooseCSV.getSelectedFile().getPath();
				try {
					SaveCSV saveFile = new SaveCSV(outputFile, temporaryData, temporaryColumnNames);
				} catch (IOException e) {
					e.printStackTrace();
				}
				settings.put("lastSavePath", csvFile.getPath());
			}
		}
	}
	class lookForJournals extends AbstractAction
	{
		public lookForJournals(String element){super(element);}
		public void actionPerformed(ActionEvent event)
		{
			menuAllHide();
			mainPane.removeAll();
			progressBar.setValue(0);
			progressBar.setStringPainted(true);
			mainPane.add(progressBar, BorderLayout.PAGE_END);
			progressBar.setOpaque(true);
			revalidate();
			repaint();
			String inputText = JOptionPane.showInputDialog("Podaj ISSN");
			FindRecordByISSN find = new FindRecordByISSN(loadedCSV, inputText, myIdentity, progressBar);
			find.execute();
		}
	}
	class changeToJournal extends AbstractAction
	{
		public changeToJournal(String element){super(element);}
		public void actionPerformed(ActionEvent event)
		{
			temporaryData = Func.changeToJournal(temporaryData);
			createTable(temporaryData, temporaryColumnNames);
			addButtonsToTable();
		}
	}
	class changeToSeries extends AbstractAction
	{
		public changeToSeries(String element){super(element);}
		public void actionPerformed(ActionEvent event)
		{
			temporaryData = Func.changeToSeries(temporaryData);
			createTable(temporaryData, temporaryColumnNames);
			addButtonsToTable();
		}
	}
	class addArticleID extends AbstractAction
	{
		public addArticleID(String element){super(element);}
		public void actionPerformed(ActionEvent event)
		{
			mainPane.removeAll();
			menuItemHide(8);
			temporaryData = Func.removeColumn(temporaryData, temporaryData[0].length-1);
			temporaryColumnNames = Func.removeColumn(temporaryColumnNames, temporaryColumnNames.length-1);
			temporaryData = Func.findArticleID(temporaryData, loadedCSV);
			temporaryColumnNames = Func.addArticleIDColumn(temporaryColumnNames);
			addEditButtons(temporaryData, temporaryColumnNames);
		}
	}
	class metadataFind extends AbstractAction
	{
		public metadataFind(String element){super(element);}
		public void actionPerformed(ActionEvent event)
		{
			mainPane.removeAll();
			for (int x=1; x<menuItems.length; x++) menuItemHide(x);
			menuItemShow(3);menuItemShow(4);menuItemShow(5);
			list1 = new JList<String>(Func.deleteColumnFromArray(loadedCSV[0], 0));
			list2 = new JList<String>(Func.deleteColumnFromArray(loadedCSV[0], 0));
			list1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			list2.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			list1.setLayoutOrientation(JList.VERTICAL);
			list2.setLayoutOrientation(JList.VERTICAL);
			list1.setVisibleRowCount(-1);
			list2.setVisibleRowCount(-1);
			JScrollPane listScroll1 = new JScrollPane(list1);
			JScrollPane listScroll2 = new JScrollPane(list2);
			listScroll1.setPreferredSize(new Dimension(250, 80));
			listScroll2.setPreferredSize(new Dimension(250, 80));
			JButton nextButton = new JButton("Wyszukaj");
			JLabel temporaryLabel1 = new JLabel("pola wyszukiwania");
			JLabel temporaryLabel2 = new JLabel("pola listowania");
			temporaryLabel1.setHorizontalAlignment(SwingConstants.CENTER);
			temporaryLabel2.setHorizontalAlignment(SwingConstants.CENTER);
			for (int x=0; x<containerSets.length; x++){
				containerSets[x] = new Container();
				containerSets[x].setLayout(new BorderLayout());
			}
			containerSets[4].setLayout(new FlowLayout(FlowLayout.CENTER));
			containerSets[4].add(temporaryLabel1);
			Component rigidArea0 = Box.createRigidArea(new Dimension(120, 20));
			containerSets[4].add(rigidArea0);
			containerSets[4].add(temporaryLabel2);
			containerSets[0].add(containerSets[4], BorderLayout.PAGE_START);
			containerSets[0].add(containerSets[1], BorderLayout.PAGE_END);
			containerSets[1].setLayout(new FlowLayout(FlowLayout.RIGHT));
			containerSets[1].add(nextButton);
			containerSets[0].add(containerSets[2], BorderLayout.CENTER);
			containerSets[2].setLayout(new BorderLayout());
			Component rigidArea1 = Box.createRigidArea(new Dimension(20, 20));
			Component rigidArea2 = Box.createRigidArea(new Dimension(20, 20));
			containerSets[2].add(rigidArea1, BorderLayout.LINE_START);
			containerSets[2].add(rigidArea2, BorderLayout.LINE_END);
			containerSets[3].setLayout(new BoxLayout(containerSets[3], BoxLayout.X_AXIS));
			containerSets[2].add(containerSets[3], BorderLayout.CENTER);
			containerSets[3].add(listScroll1);
			Component rigidArea3 = Box.createRigidArea(new Dimension(20, 20));
			containerSets[3].add(rigidArea3);
			containerSets[3].add(listScroll2);
			nextButton.addActionListener(new getListFields("Wyszukaj"));
			mainPane.add(containerSets[0]);
			revalidate();repaint();
		}
	}
	class getListFields extends AbstractAction
	{
		public getListFields(String element){super(element);}
		public void actionPerformed(ActionEvent event)
		{
			menuAllHide();
			//List<String> searchData = list1.getSelectedValuesList();
			int[] selectedIndex = list1.getSelectedIndices();
			String[] searchData = new String[selectedIndex.length];
			for (int x=0; x<selectedIndex.length; x++)
			{
				searchData[x] = list1.getModel().getElementAt(selectedIndex[x]).toString();
			}
			int[] selectedIndex2 = list2.getSelectedIndices();
			int a = 0;
			String temp;
			for (int x=0; x<selectedIndex2.length; x++)
			{
				for (int y=0; y<searchData.length; y++)
				{
					temp = list2.getModel().getElementAt(selectedIndex2[x]).toString();
					if (temp.equals(searchData[y])) a++;
				}
			}
			String[] fieldListData = new String[selectedIndex2.length-a];
			int i = 0;
			a=0;
			for (int x=0; x<selectedIndex2.length; x++)
			{
				i = 0;
				for (int y=0; y<selectedIndex.length; y++)
				{
					if (selectedIndex2[x]==selectedIndex[y]) {i=1; break;}
				}
				if (i==0)
				{
					fieldListData[a] = list2.getModel().getElementAt(selectedIndex2[x]).toString();
					a++;
				}
			}
			String[] inputText = new String[searchData.length];
			for (int x=0; x<searchData.length; x++)
			{
				inputText[x] = JOptionPane.showInputDialog("Wyszukaj ci?g znaków dla pola " + searchData[x]);
			}
			FindMetadata results = new FindMetadata(loadedCSV, searchData, fieldListData, inputText);
			temporaryData = results.getData();
			temporaryColumnNames = Func.mergeArraysWithID(searchData, fieldListData);
			menuShowAfter_MI5();
			createTable(temporaryData, temporaryColumnNames);
			addEditButtons();
		}
	}
	class openRIS extends AbstractAction
	{
		public openRIS(){}
		public void actionPerformed(ActionEvent event)
		{
			JFileChooser chooseRIS = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("ris", "ris");
			chooseRIS.setFileFilter(filter);
			chooseRIS.setMultiSelectionEnabled(true);
			chooseRIS.setCurrentDirectory(new File(settings.getProperty("lastFilePath")));
			int result = chooseRIS.showOpenDialog(menuItems[9]);
			if (result == JFileChooser.APPROVE_OPTION)
			{
				menuAllHide();
				ris_bibFiles = chooseRIS.getSelectedFiles();
				for (int x=0; x<ris_bibFiles.length; x++)
				{
					try {
						String id="";
						String doi = OpenRis.getDOI(ris_bibFiles[x]);
						String authors = OpenRis.getAuthors(ris_bibFiles[x]);
						if (!doi.equals("")) id=OpenRis.lookForID(doi, loadedCSV);
						RecordRIS record = new RecordRIS(id, authors, doi);
						listOfRis.add(record);
						temporaryColumnNames = new String[] {"id", "dc.description.admin[pl]"};
						int a = listOfRis.size();
						temporaryData = new String[a][2];
						for (int y=0; y<a; y++)
						{
							temporaryData[y][0] = listOfRis.get(y).getID();
							temporaryData[y][1] = listOfRis.get(y).getAuthors();
						}
						myIdentity.addEditButtons(temporaryData, temporaryColumnNames);
					} catch (IOException e) {e.printStackTrace();} 
				}
			}
			menuShowAfter_MI9();
		}
	}
	class OpenBiBTeX extends AbstractAction
	{
		public OpenBiBTeX(){}
		public void actionPerformed(ActionEvent event)
		{
			menuAllHide();
			JFileChooser chooseRIS = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("BiBTeX", "bib");
			chooseRIS.setFileFilter(filter);
			chooseRIS.setMultiSelectionEnabled(true);
			chooseRIS.setCurrentDirectory(new File(settings.getProperty("lastFilePath")));
			int result = chooseRIS.showOpenDialog(menuItems[9]);
			if (result == JFileChooser.APPROVE_OPTION)
			{
				ris_bibFiles = chooseRIS.getSelectedFiles();
				String doi, authors, id;
				//temporaryData = Arrays.copyOf(temporaryData, temporaryData.length+1);
				//temporaryData = new String[ris_bibFiles.length][2];
				temporaryColumnNames = new String[] {"id", "dc.description.admin[pl]"};
					try {
						for (int x=0; x<ris_bibFiles.length; x++)
						{
							id = OpenBiB.getID(ris_bibFiles[x], loadedCSV);
							authors = OpenBiB.getAuthors(ris_bibFiles[x]);
							RecordRIS record = new RecordRIS(id, authors, "");
							listOfRis.add(record);
							temporaryData = new String[listOfRis.size()][2];
							for (int y=0; y<listOfRis.size(); y++)
							{
								temporaryData[y][0] = listOfRis.get(y).getID();
								temporaryData[y][1] = listOfRis.get(y).getAuthors();
							}
							myIdentity.addEditButtons(temporaryData, temporaryColumnNames);
						}
					} catch (IOException e) {e.printStackTrace();}
			}
			menuShowAfter_MI10();
		}
	}
	class editRecord extends AbstractAction
	{
		public editRecord(){}
		public void actionPerformed(ActionEvent event)
		{
			int row;
			if (event.getActionCommand().equals("Edytuj")) row = table.getSelectedRow();
			else row = Integer.valueOf(event.getActionCommand());
			int csvRow = Func.findRowByID(temporaryData[row][0], loadedCSV);
			CompleteRecord record = new CompleteRecord(loadedCSV[csvRow], loadedCSV[0]);
			CreateBasicEditor editorWindow = new CreateBasicEditor(record, myIdentity);
			editorWindow.setVisible(true);
		}
	}
	class EditRecordFromToolbar extends AbstractAction
	{
		public EditRecordFromToolbar(){}
		public void actionPerformed(ActionEvent event)
		{
			int row = table.getSelectedRow();
			int csvRow = Func.findRowByID(temporaryData[row][0], loadedCSV);
			CompleteRecord record = new CompleteRecord(loadedCSV[csvRow], loadedCSV[0]);
			CreateBasicEditor editorWindow = new CreateBasicEditor(record, myIdentity);
			editorWindow.setVisible(true);
		}
	}
	class Change_Contributors extends AbstractAction
	{
		public Change_Contributors(){}
		public void actionPerformed(ActionEvent event)
		{
			String id = table.getValueAt(table.getSelectedRow(), 0).toString();
			int csvRow = Func.findRowByID(id, loadedCSV);
			CompleteRecord record = new CompleteRecord(loadedCSV[csvRow], loadedCSV[0]);
			ChangeContributorDialog dialogWindow = new ChangeContributorDialog(record, myIdentity);
			dialogWindow.setVisible(true);
		}
	}
	class listAffiliationsAction extends AbstractAction{
		public listAffiliationsAction(){}
		public void actionPerformed(ActionEvent event){
			String id = table.getValueAt(table.getSelectedRow(), 0).toString();
			int csvRow = Func.findRowByID(id, loadedCSV);
			CompleteRecord record = new CompleteRecord(loadedCSV[csvRow], loadedCSV[0]);
			AffiliationsDialog affDialog = new AffiliationsDialog(record);
			affDialog.setVisible(true);
		}
	}
	class goToURLAction extends AbstractAction
	{
		public goToURLAction(){}
		public void actionPerformed(ActionEvent event)
		{
			Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
				String id = table.getValueAt(table.getSelectedRow(), 0).toString();
				URI uri = Func.getURIFromID(loadedCSV, id);
				try {
					desktop.browse(uri);
				} catch (IOException e) {e.printStackTrace();}
			}
		}
	}
	class excludeDuplicatesAction extends AbstractAction
	{
		public excludeDuplicatesAction(){}
		public void actionPerformed(ActionEvent event)
		{
			int a = table.getSelectedRow();
			String id2;
			String id1 = table.getValueAt(table.getSelectedRow(), 0).toString();
			if (a % 2 == 0)
			{
				id2 = table.getValueAt(table.getSelectedRow()+1, 0).toString();
			}
			else //a % 2 == 0
			{
				id2 = table.getValueAt(table.getSelectedRow()-1, 0).toString();
			}
			try {
				exclusions.addRecord(id1, id2);
			} catch (IOException e) {e.printStackTrace();}
		}
	}
	class ExportToXMLAction extends AbstractAction
	{
		ExportToXMLAction(){}
		public void actionPerformed(ActionEvent event)
		{
			mainPane.removeAll();
			progressBar.setValue(0);
			progressBar.setStringPainted(true);
			mainPane.add(progressBar, BorderLayout.PAGE_END);
			progressBar.setOpaque(true);
			progressBar.setIndeterminate(true);
			revalidate();
			repaint();
			ExportToPBNDialog dialog;
			try {
				dialog = new ExportToPBNDialog();
				dialog.setVisible(true);
			} catch (IOException e) {e.printStackTrace();}
		}
	}
	class ReportAction extends AbstractAction
	{
		ReportAction(){}
		public void actionPerformed(ActionEvent event)
		{
			ChooseUnitDialog dialog;
			try {
				dialog = new ChooseUnitDialog(loadedCSV, progressBar, settings, "report");
				dialog.setVisible(true);
			} catch (IOException e) {e.printStackTrace();}
		}
	}
	class ReportSeriesAction extends AbstractAction
	{
		public ReportSeriesAction(){}
		public void actionPerformed(ActionEvent event)
		{
			try {
				temporaryData = SeriesReport.ReportIndexedSeries(loadedCSV);
				createTable(temporaryData, new String[]{"id", "dc.description.series", "title", "issn", "eissn", "series number", "volume", "number", "dc.title.volume"});
			} catch (IOException e) {e.printStackTrace();}
		}
	}
	class InspectISBNsAction extends AbstractAction
	{
		public InspectISBNsAction(){}
		public void actionPerformed(ActionEvent event)
		{
			mainPane.removeAll();
			menuAllHide();
			revalidate();
			repaint();
			temporaryData = Check.ISBNs(loadedCSV);
			temporaryColumnNames = new String[] {"id", "dc.title[pl]", "dc.contributor.author[pl]", "dc.identifier.isbn[pl]", "dc.identifier.eisbn[pl]"};
			menuShowAfterLoadCSV();
			createTable(temporaryData, temporaryColumnNames);
		}
	}
	class InspectConferenceIndexAction extends AbstractAction
	{
		public InspectConferenceIndexAction(){}
		public void actionPerformed(ActionEvent event)
		{
			conferenceFullReport = true;
			if (loadedWoSConferences==null)
				loadWoSConferenceIndexAction();
			else reportConferences();
		}
	}
	class InspectScopusIndexAction extends AbstractAction
	{
		public InspectScopusIndexAction(){}
		public void actionPerformed(ActionEvent event)
		{
			conferenceFullReport = true;
			if (scopusIndex==null)
				loadScopusIndex();
			else reportScopusConferences();
		}
	}
	class checkConferenceIndexing extends AbstractAction{
		public checkConferenceIndexing(){}
		public void actionPerformed(ActionEvent event){
			conferenceFullReport = false;
			if (scopusIndex==null)
				loadScopusIndex();
			if (loadedWoSConferences==null)
				loadWoSConferenceIndexAction();
			if (scopusIndex!=null && loadedWoSConferences!=null) checkConferenceIndexing();
		}
	}
	class ReportAddOpenAccessFieldsAction extends AbstractAction{
		public ReportAddOpenAccessFieldsAction(){}
		public void actionPerformed(ActionEvent event){
			try {
				ChooseUnitDialog dialog = new ChooseUnitDialog(loadedCSV, progressBar, settings, "OpenAccess");
				dialog.setVisible(true);
			} catch (IOException e) {e.printStackTrace();}
		}
	}
	class ReportUnaffiliatedAuthorAction extends AbstractAction{
		public ReportUnaffiliatedAuthorAction(){}
		public void actionPerformed(ActionEvent event) {
			createTable(UnafiliatedReport.table(loadedCSV), new String[]{"id","autor"});
		}
	}
	class OpenXML extends AbstractAction{
		public OpenXML(){}
		public void actionPerformed(ActionEvent event){
			JFileChooser chooseXML = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("xml", "xml");
			chooseXML.setFileFilter(filter);
			chooseXML.setMultiSelectionEnabled(true);
			chooseXML.setCurrentDirectory(new File(settings.getProperty("lastFilePath")));
			int result = chooseXML.showOpenDialog(menuItems[9]);
			if (result == JFileChooser.APPROVE_OPTION)
			{
				try {
					LoadXML loader = new LoadXML(chooseXML.getSelectedFile());
					loadedXML = loader.getData();
					temporaryColumnNames = loader.getColumns();
					createTable(loadedXML, temporaryColumnNames);
				} catch (ParserConfigurationException | SAXException
						| IOException e) {e.printStackTrace();}
			}
		}
	}
	class RegenerateSapAction extends AbstractAction{
		public RegenerateSapAction(){}
		public void actionPerformed(ActionEvent event){
			RemoveDuplicatesFromSAP.remove();
		}
	}
	class checkDCAdminAction extends AbstractAction{
		public checkDCAdminAction(){}
		public void actionPerformed(ActionEvent event){
			createTable(FieldsFromArray.checkAdminFields(loadedCSV), new String[]
					{"id","dc.description.admin[]","dc.description.admin[pl]"});
			addEditButtons();
		}
	}
	class addRTDescription extends AbstractAction {
		public void actionPerformed(ActionEvent event){
			temporaryData = RTFields.list(loadedCSV);
			temporaryData = RTFields.addDescription(temporaryData);
			String[][] fields = RFL.list();
			temporaryColumnNames = new String[]{"id", fields[0][2], fields[35][2], fields[46][2], "dc.description.additional[pl]", fields[7][2]};
			createTable(temporaryData, temporaryColumnNames);
		}
	}
	class TemporaryAction extends AbstractAction{
		public void actionPerformed(ActionEvent event){
			try {
				String unitID = ChooseAffiliationDialog.dialog(myIdentity);
				String[][] units = ImportCSV.readData("/units.csv", "\"");
				String[] unit = new String[4];
				for (int x=0; x<units.length; x++){
					if (units[x][0].equals(unitID)) unit = units[x];
				}
				String[][] sap = ImportCSV.readData("/osoby.csv", "\"");
				temporaryData = PersonReport.affiliations(loadedCSV, sap, unit);
				temporaryColumnNames = new String[]{"item_id", "contributor","faculty_id","faculty_name","employed","affiliated"};
				createTable(temporaryData, temporaryColumnNames);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public MetAnWindow()
	{
		myIdentity = this;
		try {
			setIconImage(ImageIO.read(MetAnWindow.class.getResourceAsStream("/JBC_kolor.png")));
		} catch (IOException e1) {e1.printStackTrace();}
		settingsMethod = new MetAnSettings();
		settings = settingsMethod.getSettings();
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent event){
				try {
					settingsMethod.saveSettings(myIdentity, getX(), getY(), getWidth(), getHeight());
					} catch (IOException e) {e.printStackTrace();}
				}
			});
		int a = Integer.parseInt(settings.getProperty("left"));
		int b = Integer.parseInt(settings.getProperty("top"));
		int c = Integer.parseInt(settings.getProperty("width"));
		int d = Integer.parseInt(settings.getProperty("height"));
		setBounds(a,b,c,d);
		if (settings.getProperty("windowMaximized").equals("6")) setExtendedState(MAXIMIZED_BOTH);
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		createToolBar();
		createMenu(); addListenerToMenu();
		setJMenuBar(menuBar);
		mainPane = new Container();
		mainPane.setLayout(new BorderLayout());
		add(mainPane, BorderLayout.CENTER);
		createPopup();
	}
	private void createPopup()
	{
		popup = new JPopupMenu();
		fillPopup(popup);
	}
	private JPopupMenu createPopupForDuplicates()
	{
		JPopupMenu popupWithDuplicates = new JPopupMenu();
		JMenuItem exclude = new JMenuItem("To nie dublet");
		exclude.addActionListener(new excludeDuplicatesAction());
		popupWithDuplicates.add(exclude);
		fillPopup(popupWithDuplicates);
		return popupWithDuplicates;
	}
	private void fillPopup(JPopupMenu popup)
	{
		JMenuItem goToURL = new JMenuItem("Otwórz rekord w przegl?darce");
		goToURL.addActionListener(new goToURLAction());
		JMenuItem editMenu = new JMenuItem("Edytuj");
		editMenu.addActionListener(new editRecord());
		JMenuItem dcContributorMenu = new JMenuItem("Zamie? twórców");
		dcContributorMenu.addActionListener(new Change_Contributors());
		JMenuItem listAffiliations = new JMenuItem("Poka? afiliacje");
		listAffiliations.addActionListener(new listAffiliationsAction());
		popup.add(goToURL);
		popup.add(editMenu);
		popup.add(dcContributorMenu);
		popup.add(listAffiliations);
	}
	private void createMenu()
	{
		menus[0] = new JMenu("Plik");
		menus[1] = new JMenu("Wyszukaj");
		menus[2] = new JMenu("Zamie?");
		menus[3] = new JMenu("Metadane");
		menus[4] = new JMenu("Raporty");
		menus[5] = new JMenu("Inspekcja");
		menuItems[0] = new JMenuItem("Otwórz CSV");
		menuItems[1] = new JMenuItem("Eksportuj CSV");
		menuItems[2] = new JMenuItem("Wyjd?");
		menuItems[3] = new JMenuItem("Powielone opisy");
		menuItems[4] = new JMenuItem("ISSN");
		menuItems[5] = new JMenuItem("Metadane");
		menuItems[6] = new JMenuItem("na czasopismo");
		menuItems[7] = new JMenuItem("na serie");
		menuItems[8] = new JMenuItem("dodaj ID artyku?u");
		menuItems[9] = new JMenuItem("Wczytaj RIS");
		menuItems[10] = new JMenuItem("Wczytaj BiBTeX");
		menuItems[11] = new JMenuItem("dc.contributor");
		menuItems[12] = new JMenuItem("Eksportuj do XML");
		menuItems[13] = new JMenuItem("Strony");
		menuItems[14] = new JMenuItem("Numery serii");
		menuItems[15] = new JMenuItem("ISBN");
		menuItems[16] = new JMenuItem("Rekordy wydzia?y");
		menuItems[17] = new JMenuItem("Serie punktowane");
		menuItems[18] = new JMenuItem("Sprawd? indeksowanie Scopus (wszystkie rekordy)");
		menuItems[19] = new JMenuItem("Sprawd? indeksowanie Web of Science (wszystkie rekordy)");
		menuItems[20] = new JMenuItem("Tabelka dla pól OpenAccess");
		menuItems[21] = new JMenuItem("Wczytaj xml");
		menuItems[22] = new JMenuItem("Sprawd? problemy z afiliacji");
		menuItems[23] = new JMenuItem("Napraw list? SAP");
		menuItems[24] = new JMenuItem("Sprawd? pole \"admin\"");
		menuItems[25] = new JMenuItem("Sprawd? indeksowanie konferencji");
		menuItems[26] = new JMenuItem("Dodaj opis do pól [RT]");
		menuItems[27] = new JMenuItem("Temporary");
		menuBar.add(menus[0]);
		menuBar.add(menus[1]);
		menuBar.add(menus[2]);
		menuBar.add(menus[5]);
		menus[0].add(menuItems[0]);
		menus[0].add(menuItems[1]);
		menus[0].add(menuItems[9]);
		menus[0].add(menuItems[10]);
		menus[0].add(menuItems[21]);
		menus[0].add(menuItems[12]);
		menus[0].addSeparator();
		menus[0].add(menuItems[2]);
		menus[1].add(menuItems[3]);
		menus[1].add(menuItems[4]);
		menus[1].add(menuItems[5]);
		menus[1].add(menuItems[25]);
		menus[2].add(menuItems[6]);
		menus[2].add(menuItems[7]);
		menus[2].add(menuItems[8]);
		menus[2].add(menus[3]);
		menus[3].add(menuItems[11]);
		menuBar.add(menus[4]);
		menus[4].add(menuItems[13]);
		menus[4].add(menuItems[14]);
		menus[4].add(menuItems[16]);
		menus[4].add(menuItems[17]);
		menus[4].add(menuItems[20]);
		menus[5].add(menuItems[15]);
		menus[5].add(menuItems[18]);
		menus[5].add(menuItems[19]);
		menus[5].add(menuItems[22]);
		menus[5].add(menuItems[24]);
		menus[5].add(menuItems[23]);
		menus[5].add(menuItems[26]);
		menus[5].add(menuItems[27]);
		addAccelerators();
		menuAllHide();
		menuShowStart();
		}
	private void createToolBar()
	{
		toolbar = new JToolBar("Pasek narz?dzi");
		toolbarButtons[0] = new JButton(new ImageIcon(getClass().getResource("/Open.png")));
		toolbarButtons[1] = new JButton(new ImageIcon(getClass().getResource("/Save.png")));
		toolbarButtons[2] = new JButton(new ImageIcon(getClass().getResource("/Edit.png")));
		toolbarButtons[3] = new JButton(new ImageIcon(getClass().getResource("/Edit_M.png")));
		toolbarButtons[4] = new JButton(new ImageIcon(getClass().getResource("/Look.png")));
		toolbarButtons[0].addActionListener(new OpenCSV());
		toolbarButtons[1].addActionListener(new ExportCSV());
		toolbarButtons[2].addActionListener(new EditRecordFromToolbar());
		toolbarButtons[3].addActionListener(new Change_Contributors());
		toolbarButtons[4].addActionListener(new checkConferenceIndexing());
		toolbarButtons[0].setToolTipText("Otwórz plik");
		toolbarButtons[1].setToolTipText("Eksportuj do CSV");
		toolbarButtons[2].setToolTipText("Edytuj rekord");
		toolbarButtons[3].setToolTipText("Zamie? role twórców");
		toolbarButtons[4].setToolTipText("Sprawd? indeksowanie konferencji");
		for (int x=0; x<2; x++) toolbar.add(toolbarButtons[x]);
		toolbar.addSeparator();
		for (int x=2; x<toolbarButtons.length; x++) toolbar.add(toolbarButtons[x]);
		for (int x=0; x<toolbarButtons.length; x++) toolbarButtons[x].setEnabled(false);
		toolbar.addSeparator();
		//Pole wyszukiwania prostego
		simpleSearchField = new JTextField();
		simpleSearchField.setMaximumSize(new Dimension(200,30));
		toolbar.add(simpleSearchField);
		add(toolbar, BorderLayout.NORTH);
	}
	private void addAccelerators()
	{
		menuItems[0].setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
		menuItems[1].setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
		menuItems[5].setAccelerator(KeyStroke.getKeyStroke("ctrl shift M"));
		menuItems[11].setAccelerator(KeyStroke.getKeyStroke("ctrl shift C"));
		simpleSearchField.addActionListener(new SimpleSearch());
	}
	private void addListenerToMenu()
	{
		menuItems[0].addActionListener(new OpenCSV());
		menuItems[1].addActionListener(new ExportCSV());
		menuItems[3].addActionListener(new FindDuplicatesAction("Powielone opisy"));
		menuItems[4].addActionListener(new lookForJournals("Znajd? po ISSN"));
		menuItems[5].addActionListener(new metadataFind("Metadane"));
		menuItems[6].addActionListener(new changeToJournal("Zamie? na czasopismo"));
		menuItems[7].addActionListener(new changeToSeries("Zamie? na serie"));
		menuItems[8].addActionListener(new addArticleID("Dodaj Article ID"));
		menuItems[9].addActionListener(new openRIS());
		menuItems[10].addActionListener(new OpenBiBTeX());
		menuItems[11].addActionListener(new Change_Contributors());
		menuItems[12].addActionListener(new ExportToXMLAction());
		menuItems[13].addActionListener(new Bartkowe1(myIdentity));
		menuItems[14].addActionListener(new Bartkowe2(myIdentity));
		menuItems[15].addActionListener(new InspectISBNsAction());
		menuItems[16].addActionListener(new ReportAction());
		menuItems[17].addActionListener(new ReportSeriesAction());
		menuItems[18].addActionListener(new InspectScopusIndexAction());
		menuItems[19].addActionListener(new InspectConferenceIndexAction());
		menuItems[20].addActionListener(new ReportAddOpenAccessFieldsAction());
		menuItems[21].addActionListener(new OpenXML());
		menuItems[22].addActionListener(new ReportUnaffiliatedAuthorAction());
		menuItems[23].addActionListener(new RegenerateSapAction());
		menuItems[24].addActionListener(new checkDCAdminAction());
		menuItems[25].addActionListener(new checkConferenceIndexing());
		menuItems[26].addActionListener(new addRTDescription());
		menuItems[27].addActionListener(new TemporaryAction());
		menuItems[2].addActionListener(new AbstractAction("Wyjd?"){
			public void actionPerformed(ActionEvent e){
				try {
					settingsMethod.saveSettings(myIdentity, getX(), getY(), getWidth(), getHeight());
				} catch (IOException e1) {e1.printStackTrace();}
			}
		});
	}
	private void menuHide(int arrayNumber){menus[arrayNumber].setEnabled(false);}
	private void menuShow(int arrayNumber){menus[arrayNumber].setEnabled(true);}
	private void menuItemHide(int arrayNumber){menuItems[arrayNumber].setEnabled(false);}
	private void menuItemShow(int arrayNumber){menuItems[arrayNumber].setEnabled(true);}
	public void menuAllHide()
	{
		for (int x=0; x<menus.length; x++) menuHide(x);
		for (int x=0; x<menuItems.length; x++) menuItemHide(x);
		simpleSearchField.setEnabled(false);
	}
	public void menuShowStart()
	{
		menuShow(0);
		menuShow(1);
		menuShow(5);
		menuItemShow(0);
		menuItemShow(2);
		menuItemShow(21);
		menuItemShow(23);
		menuItemShow(25);
		for (int x=0; x<toolbarButtons.length; x++) toolbarButtons[x].setEnabled(false);
		toolbarButtons[0].setEnabled(true);
		toolbarButtons[4].setEnabled(true);
	}
	public void menuShowAfterLoadCSV()
	{
		menuShowStart();
		menuShow(1);
		menuItemShow(9);
		menuItemShow(10);
		menuItemShow(3);
		menuItemShow(4);
		menuItemShow(5);
		menuItemShow(12);
		menuItemShow(18);
		menuShow(4);
		menuItemShow(13);
		menuItemShow(14);
		menuItemShow(16);
		menuItemShow(17);
		menuItemShow(20);
		menuShow(5);
		menuItemShow(15);
		menuItemShow(19);
		menuItemShow(22);
		menuItemShow(24);
		menuItemShow(26);
		menuItemShow(27);
		simpleSearchField.setEnabled(true);
	}
	public void menuShowAfter_MI4()
	{
		menuShowAfterLoadCSV();
		menuShow(2);
		menuItemShow(6);
		menuItemShow(7);
		menuItemShow(8);
	}
	private void menuShowAfter_MI5()
	{
		menuShowAfterLoadCSV();
		menuItemHide(9);
		menuItemHide(10);
	}
	private void menuShowAfter_MI9()
	{
		menuShowAfterLoadCSV();
		//menuItemHide(10);
	}
	private void menuShowAfter_MI10()
	{
		menuShowAfterLoadCSV();
		//menuItemHide(9);
	}
	private void menuShowAfterTableCreation()
	{
		menuShow(0);
		menuItemShow(1);
		menuShow(2);
		menuShow(3);
		menuItemShow(11);
		toolbarButtons[1].setEnabled(true);
		toolbarButtons[2].setEnabled(true);
		toolbarButtons[3].setEnabled(true);
		}
	public void createTable(String[][] tableData, String[] columnNames)
	{
		temporaryData = tableData;
		temporaryColumnNames = columnNames;
		table = new JTable(temporaryData, temporaryColumnNames);
		table.setDefaultRenderer(Object.class, new TableCellColor());
		table.setCellSelectionEnabled(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		int[] a = new int[1];
		a[0] = 0;
		jTableFunctions.TableResize.resizeColumnWidth(table, a, 250);
		if (a[0]<mainPane.getWidth()) {a[0] = 0; jTableFunctions.TableResize.resizeColumnWidth(table, a, 600);}
		if (a[0]<mainPane.getWidth()) {a[0] = 0; jTableFunctions.TableResize.complexResize(table, a, mainPane.getWidth());}
		if (a[0]<mainPane.getWidth()) table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setAutoCreateRowSorter(true);
		ActionListener pasteAction = new ActionListener(){
			public void actionPerformed(ActionEvent event){
				try {
					jTableFunctions.TableOperations.pasteCell(table);
				} catch (UnsupportedFlavorException | IOException e){e.printStackTrace();}}
			};
		table.registerKeyboardAction(pasteAction, "Wklej", keyPaste, JComponent.WHEN_FOCUSED);
		tableScroll = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		mainPane.removeAll();
		mainPane.add(tableScroll, BorderLayout.CENTER);
		table.setComponentPopupMenu(popup);
		table.addMouseListener(new MouseRightClick(table));
		menuShowAfterTableCreation();
		repaint();
		revalidate();
	}
	public void convertRIS() throws UnsupportedEncodingException, IOException
	{
		String recordID = JOptionPane.showInputDialog("Podaj id rekordu");
		String authors = RisSection.OpenRis.getAuthors(csvFile);
		JFileChooser chooseCSV = new JFileChooser();
		chooseCSV.setCurrentDirectory(new File(settings.getProperty("lastFilePath")));
		int result = chooseCSV.showSaveDialog(null);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			String outputFile = chooseCSV.getSelectedFile().getPath();
			try {
				SaveCSV saveFile = new SaveCSV(outputFile, new String[] {recordID, authors}, new String[] {"id", "dc.description.admin[pl]"});
			} catch (IOException e) {
				e.printStackTrace();
			}
			settings.put("lastSavePath", csvFile);
		}
	}
	public void addEditButtons()
	{
		temporaryColumnNames = Func.addColumns(1, temporaryColumnNames);
		temporaryData = Func.addColumns(1, temporaryData);
		for (int x=0; x<temporaryData.length; x++) temporaryData[x][temporaryData[0].length-1] = "Edytuj";
		temporaryColumnNames[temporaryColumnNames.length-1] = "Edytuj";
		createTable(temporaryData, temporaryColumnNames);
		ButtonColumn bColumn = new ButtonColumn(table, new editRecord(), temporaryColumnNames.length-1);
		repaint();revalidate();
	}
	public void addEditButtons(String[][] inputData, String[] inputHeadlines)
	{
		temporaryData = inputData; temporaryColumnNames = inputHeadlines;
		temporaryColumnNames = Func.addColumns(1, temporaryColumnNames);
		temporaryData = Func.addColumns(1, temporaryData);
		temporaryColumnNames[temporaryColumnNames.length-1] = "Edytuj";
		createTable(temporaryData, temporaryColumnNames);
		new ButtonColumn(table, new editRecord(), temporaryColumnNames.length-1);
		repaint();revalidate();
	}
	public void addButtonsToTable()
	{
		ButtonColumn bColumn = new ButtonColumn(table, new editRecord(), temporaryColumnNames.length-1);
		repaint();revalidate();
	}
	public void refreshTable()
	{
		repaint();revalidate();
	}
	public String[][] getTemporaryData()
	{
		return temporaryData;
		
	}
	public String[] getTemporaryColumnNames()
	{
		return temporaryColumnNames;
		
	}
	public Properties getSettings()
	{
		return settings;
	}
	public void reportLoadedFile(String[][] inputData)
	{
		loadedCSV=inputData;
		mainPane.removeAll();
		mainPane.add(new JLabel("Wczytano plik"), BorderLayout.PAGE_END);
		menuShowAfterLoadCSV();
		revalidate();
		repaint();
	}
	public void reportConferences(){
		mainPane.removeAll();
		progressBar.setStringPainted(true);
		progressBar.setValue(0);
		progressBar.setIndeterminate(true);
		progressBar.setOpaque(true);
		mainPane.add(progressBar, BorderLayout.PAGE_END);
		revalidate();
		repaint();
		ConferenceIndex cinfI = new ConferenceIndex(loadedCSV, loadedWoSConferences, progressBar);
		cinfI.execute();
	}
	public void reportScopusConferences(){
		mainPane.removeAll();
		progressBar.setStringPainted(true);
		progressBar.setValue(0);
		progressBar.setIndeterminate(true);
		progressBar.setOpaque(true);
		mainPane.add(progressBar, BorderLayout.PAGE_END);
		revalidate();
		repaint();
		ScopusIndex cinfI = new ScopusIndex(loadedCSV, scopusIndex, progressBar);
		cinfI.execute();
	}
	public void setScopusIndex(String[][] data){
		scopusIndex=data;
		if (!conferenceFullReport && loadedWoSConferences!=null) checkConferenceIndexing();
	}
	public void setWoSIndex(String[][] data){
		loadedWoSConferences=data;
		if (!conferenceFullReport && scopusIndex!=null) checkConferenceIndexing();
	}
	private void loadWoSConferenceIndexAction() {
		menuAllHide();
		mainPane.removeAll();
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		mainPane.add(progressBar, BorderLayout.PAGE_END);
		progressBar.setOpaque(true);
		progressBar.setIndeterminate(true);
		revalidate();
		repaint();
		LoadWoSConferenceIndex dataFromFile = new LoadWoSConferenceIndex("/WoSConferences.csv", progressBar);
		dataFromFile.execute();
		menuShowStart();
	}
	private void loadScopusIndex(){
		menuAllHide();
		mainPane.removeAll();
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		mainPane.add(progressBar, BorderLayout.PAGE_END);
		progressBar.setOpaque(true);
		progressBar.setIndeterminate(true);
		revalidate();
		repaint();
		LoadScopusIndex dataFromFile = new LoadScopusIndex("/Scopus.csv", progressBar);
		dataFromFile.execute();
		menuShowStart();
	}
	public void checkConferenceIndexing(){
		String issn = JOptionPane.showInputDialog("Podaj issn (Scopus)");
		String confTitle = JOptionPane.showInputDialog("Podaj nazw? konferencji albo tytu? ksi??ki lub serii (Web of Science)");
		String a = "nie";
		String b = "nie";
		if (!issn.equals("") && ScopusIndex.checkScopusIndexing(scopusIndex, issn)) a = "tak";
		if (!confTitle.equals("") && ConferenceIndex.chechWoSIndexing(loadedWoSConferences, confTitle)) b = "tak";
		JOptionPane.showMessageDialog(this, "Scopus:                 "+a+"\r\nWeb of Science:   "+b);
	}
	public void conferencesShowTable(String[][] data){
		temporaryColumnNames = new String[]{"id","dc.conference[pl]","dc.title[pl]"};
		temporaryData = data;
		menuShowAfterLoadCSV();
		createTable(temporaryData, temporaryColumnNames);
	}
	public void colorTable()
	{
		table.setDefaultRenderer(Object.class, new TableColorByRow());
		table.setComponentPopupMenu(createPopupForDuplicates());
		repaint();
		revalidate();
	}
	public String[][] getLoadedCSV()
	{
		return loadedCSV;
	}
	public static MetAnWindow get()
	{
		return myIdentity;
	}
	public JProgressBar getProgressBar(){
		return progressBar;
	}
	@Override
	public String[][] getCSVData() {
		return loadedCSV;
	}
	@Override
	public void startAction(){
		menuAllHide();
		mainPane.removeAll();
		progressBar.setStringPainted(true);
		mainPane.add(progressBar, BorderLayout.PAGE_END);
		progressBar.setIndeterminate(true);
		revalidate();repaint();
	}
	@Override
	public void endAction() {
		mainPane.remove(progressBar);
		menuShowAfterLoadCSV();
	}
}