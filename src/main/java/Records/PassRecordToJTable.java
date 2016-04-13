package Records;

import java.util.Map;

import PMainWindow.Func;
import PMainWindow.MetAnWindow;

public class PassRecordToJTable
{
	public static void actualizeJTable(MetAnWindow mainWindow, CompleteRecord record)
	{
		String[][] fieldList = RFL.list();
		String id = record.getID();
		Map<String, String[]> recordMap = record.getMap();
		String[][] temporaryData = mainWindow.getTemporaryData();
		String[] temporaryColumnNames = mainWindow.getTemporaryColumnNames();
		String[] currentField;
		String export;
		int currentcolumn;
		for (int x=0; x<temporaryData.length; x++)
		{
			if (id.equals(temporaryData[x][0]))
			{
				for (int y=0; y<fieldList.length; y++)
				{
					currentcolumn = -1;
					currentcolumn = Func.findColumn(temporaryColumnNames, fieldList[y][2]);
					if (currentcolumn>-1){
						currentField = recordMap.get(fieldList[y][2]);
						if (currentField!=null && currentField.length>0)
						{
							export = currentField[0];
							for (int z=1; z<currentField.length; z++)
							{
								export = export + "||" + currentField[z];
							}
						}
						else export="";
						temporaryData[x][currentcolumn] = export;
					}
				}
				break;
			}
		}
		mainWindow.refreshTable();
	}
}