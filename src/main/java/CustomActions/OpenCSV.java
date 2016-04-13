package CustomActions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import Imports.LoadXML;
import Interfaces.CSVDataHolder;
import PMainWindow.LoadCSV;
import PMainWindow.MetAnWindow;

public class OpenCSV extends AbstractAction{
	private CSVDataHolder frame;
	public OpenCSV(CSVDataHolder frame){
		this.frame = frame;
	}
	public void actionPerformed(ActionEvent element)
	{
		JFileChooser chooseCSV = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("csv", "xml");
		chooseCSV.setFileFilter(filter);
		chooseCSV.setCurrentDirectory(new File(MetAnWindow.settings.getProperty("lastFilePath")));
		int result = chooseCSV.showOpenDialog(MetAnWindow.myIdentity.menuItems[0]);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			MetAnWindow.myIdentity.settings.put("lastFilePath", MetAnWindow.myIdentity.csvFile.getPath());
			MetAnWindow.myIdentity.settingsMethod.setSettings(MetAnWindow.myIdentity.settings);
			File file = chooseCSV.getSelectedFile();
			if (file.getPath().endsWith("xml")){
				try {
					LoadXML loader = new LoadXML(file);
					frame.reportLoadedFile(loader.getData());
				} catch (ParserConfigurationException | SAXException
						| IOException e) {
					e.printStackTrace();
				}
			}
			else{
				LoadCSV dataFromFile = new LoadCSV(chooseCSV.getSelectedFile(), frame.progressBar, frame);
				dataFromFile.execute();
			}
		}
	}
}
