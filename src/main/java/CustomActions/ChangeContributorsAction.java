package CustomActions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.AbstractAction;

import OtherDialogs.ChangeContributorDialog;
import PMainWindow.Func;

public class ChangeContributorsAction extends AbstractAction
{
	private Map<String, String[]> record;
	private int column, line;
	private String fieldA, fieldB;
	private ChangeContributorDialog parent;
	public ChangeContributorsAction(Map<String, String[]> record, String actualField, String previousField, int line, ChangeContributorDialog parent)
	{
		this.record = record;
		this.column = column;
		this.line = line-2;
		this.parent = parent;
		fieldA = actualField;
		fieldB = previousField;
	}
	public void actionPerformed(ActionEvent event)
	{
		String[] dataIn = record.get(fieldA);
		String[] dataOut = record.get(fieldB);
		if (dataOut.length==0) dataOut = new String[1];
		else if (!dataOut[0].equals("")) dataOut = Func.addRow(dataOut);
		dataOut[dataOut.length-1] = dataIn[line];
		if (dataIn.length>1) dataIn = Func.removeRow(dataIn, line);
		else dataIn[0] = "";
		record.put(fieldA, dataIn);
		record.put(fieldB, dataOut);
		parent.clear();
	}
}