package CustomActions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import PMainWindow.MetAnWindow;
import Records.CompleteRecord;
import Records.PassRecordToCSV;

public class SaveContributorsToCSV extends AbstractAction
{
	private MetAnWindow mainWindow;
	private CompleteRecord record;

	public SaveContributorsToCSV(MetAnWindow mainWindow, CompleteRecord record)
	{
		this.mainWindow = mainWindow;
		this.record = record;
	}
	public void actionPerformed(ActionEvent event)
	{
		PassRecordToCSV.saveToCSV(mainWindow, record);
	}
}