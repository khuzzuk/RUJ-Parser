package CustomActions;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import OtherDialogs.ChooseAffiliationDialog;
import Records.CompleteRecord;

public class AddABAction extends AbstractAction {
	private int personX, affiliationY;
	private CompleteRecord record;
	private JToggleButton button;
	/**
	 * @param x - numer osoby z tablicy w rekordzie
	 * @param y - numer afiliacji z tablicy osoby
	 */
	public AddABAction(int x, int y, CompleteRecord record, JToggleButton button){
		personX = x;
		affiliationY = y;
		this.record = record;
		this.button = button;
	}
	public void actionPerformed(ActionEvent event){
		String value = "";
		if (button.isSelected()){
			if (record.getPerson(personX).isSAP()){
				String[] affiliations = record.getPerson(personX).getAffiliation(affiliationY);
				if (affiliations==null){
					try {
						affiliations = new String[]{chooseAffiliation((JFrame) SwingUtilities.getWindowAncestor((java.awt.Component) event.getSource()))};
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				value = "[AB]"+record.getPerson(personX).toString()+" "+affiliations[0];
			}
			else if (record.getPerson(personX).isUSOS()){
				String aff = record.getPerson(personX).getAffiliation(affiliationY)[0];
				value = "[AB]"+record.getPerson(personX).toString()+" "+aff;
			}
			else{
				try {
					JFrame frame = (JFrame) SwingUtilities.getWindowAncestor((java.awt.Component) event.getSource());
					String aff;
					aff = chooseAffiliation((JFrame) SwingUtilities.getWindowAncestor((java.awt.Component) event.getSource()));
					value = "[AB]"+record.getPerson(personX).toString()+" "+aff;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			record.addDataToField("dc.description.admin[]", value);
		}
		else{
			record.removeAB(record.getPerson(personX).toString(),
					record.getPerson(personX).getAffiliation(affiliationY)[0]);
		}
		try {
			record.getAuthorsAffiliations();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private String chooseAffiliation(JFrame frame) throws IOException{
		String aff;
		aff = ChooseAffiliationDialog.dialog(frame);
		return aff;
	}
}
