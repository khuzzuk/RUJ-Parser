package Records;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFileChooser;

import PMainWindow.MetAnWindow;
import PMainWindow.SaveCSV;

public class PassRecordToCSV
{
	public static void saveToCSV(MetAnWindow mainWindow, CompleteRecord record)
	{
		String[][] fieldList = RFL.list();
		String id = record.getID();
		Map<String, String[]> recordMap = record.getMap();
		String[] currentField;
		int a = 0;
		for (int x=0; x<fieldList.length; x++)
			{
				currentField = recordMap.get(fieldList[x][2]);
				if (currentField!=null && !currentField[0].equals("")) a++;
			}
		String[] data = new String[a];
		String[] headlines = new String[a];
		for (int x=0; x<a; x++) {data[x]=""; headlines[x]="";}
		headlines[0]="id";
		data[0]=id;
		a=1;
		for (int x=0; x<fieldList.length; x++)
		{
			currentField = recordMap.get(fieldList[x][2]);
			if (x!=39 && currentField!=null)
			{
				if (!currentField[0].equals(""))
				{
					headlines[a] = fieldList[x][2];
					data[a] = currentField[0];
					for (int z=1; z<currentField.length; z++)
					{
						data[a] = data[a] + "||" + currentField[z];
					}
					a++;
				}
			}
		}
		Properties settings = mainWindow.getSettings();
		JFileChooser chooseCSV = new JFileChooser();
		chooseCSV.setCurrentDirectory(new File(settings.getProperty("lastFilePath")));
		int result = chooseCSV.showSaveDialog(null);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File outputFile = chooseCSV.getSelectedFile();
			try {
				new SaveCSV(outputFile, data, headlines);
			} catch (IOException e1) {e1.printStackTrace();}
			settings.put("lastSavePath", outputFile.getPath());
		}
	}
}