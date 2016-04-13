package CustomActions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import PMainWindow.MetAnWindow;
import Records.CompleteRecord;
import Records.PassRecordToJTable;

public class SaveContributorsAction extends AbstractAction
{
	private MetAnWindow mainWindow;
	private CompleteRecord record;
	public SaveContributorsAction(MetAnWindow mainWindow, CompleteRecord record)
	{
		this.mainWindow = mainWindow;
		this.record = record;
	}
	public SaveContributorsAction(CompleteRecord record)
	{
		this.mainWindow = MetAnWindow.myIdentity;
		this.record = record;
	}
	public void actionPerformed(ActionEvent event)
	{
		PassRecordToJTable.actualizeJTable(mainWindow, record);
	}
}