package CustomActions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.AbstractAction;

import PMainWindow.Func;
import PMainWindow.MetAnWindow;
import RecordEditor.CreateBasicEditor;
import Records.CompleteRecord;
import Records.RFL;

public class SaveEditorData extends AbstractAction
{
	private MetAnWindow mainWindow;
	private CompleteRecord record;
	private CreateBasicEditor editor;
	public SaveEditorData(MetAnWindow mainWindow, CompleteRecord record, CreateBasicEditor editor)
	{
		this.mainWindow=mainWindow;
		this.record = record;
		this.editor = editor;
	}
	public void actionPerformed(ActionEvent e)
	{
		editor.modifyRecord();
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
				//temporaryData[x][Func.findColumn(temporaryColumnNames, fieldList[0][2])] = record.getTitle();
				for (int y=0; y<fieldList.length; y++)
				{
					currentField = recordMap.get(fieldList[y][2]);
					if (currentField!=null)
					{
						export = currentField[0];
						for (int z=1; z<currentField.length; z++)
						{
							export = export + "||" + currentField[z];
						}
						currentcolumn = -1;
						currentcolumn = Func.findColumn(temporaryColumnNames, fieldList[y][2]);
						if (currentcolumn>-1) temporaryData[x][currentcolumn] = export;
					}
				}
				break;
			}
		}
		mainWindow.refreshTable();
	}
}