package CustomActions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.AbstractAction;

import OtherDialogs.ChangeContributorDialog;
import PMainWindow.Func;

public class ChangeAllContributorsAction extends AbstractAction
{
	private Map<String, String[]> record;
	private int column, line;
	private String fieldA, fieldB;
	private ChangeContributorDialog parent;
	public ChangeAllContributorsAction(Map<String, String[]> record, String actualField, String previousField, ChangeContributorDialog parent)
	{
		this.record = record;
		this.column = column;
		this.parent = parent;
		fieldA = actualField;
		fieldB = previousField;
	}
	public void actionPerformed(ActionEvent event)
	{
		String[] dataIn = record.get(fieldA);
		String[] dataOut = record.get(fieldB);
		if (dataOut.length==0) dataOut = new String[1];
		for (int x=0; x<dataIn.length; x++)
		{
			if (!dataOut[0].equals("")) dataOut = Func.addRow(dataOut);
			dataOut[dataOut.length-1] = dataIn[x];
		}
		dataIn = new String[] { "" };
		record.put(fieldA, dataIn);
		record.put(fieldB, dataOut);
		parent.clear();
	}
}