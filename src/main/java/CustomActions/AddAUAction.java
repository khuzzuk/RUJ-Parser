package CustomActions;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import OtherDialogs.ChooseAffiliationDialog;
import Records.CompleteRecord;

public class AddAUAction extends AbstractAction {
	private int personX, affiliationY;
	private CompleteRecord record;
	private JToggleButton button;
	/**
	 * @param x - numer osoby z tablicy w rekordzie
	 */
	public AddAUAction(int x, CompleteRecord record, JToggleButton button){
		personX = x;
		this.record = record;
		this.button = button;
	}
	public void actionPerformed(ActionEvent event){
		String value = "";
		if (button.isSelected()){
			value = "[AU]"+record.getPerson(personX).toString();
			record.addDataToField("dc.description.admin[]", value);
		}
		else{
			record.removeAU(record.getPerson(personX).toString());
		}
	}
}
