package OtherDialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;

import CustomActions.AddABAction;
import CustomActions.AddAUAction;
import CustomActions.SaveContributorsAction;
import PMainWindow.MetAnWindow;
import Records.CompleteRecord;
import Records.PersonEntry;
import Records.RFL;

public class AffiliationsDialog extends JFrame {
	private JPanel mainPane;
	private GridBagConstraints constr;
	private Map<String, String[]> recordMap;
	private String[][] fieldList;
	private CompleteRecord record;
	private PersonEntry[] persons;
	public AffiliationsDialog(CompleteRecord record){
		try {
			this.record = record;
			mainPane = new JPanel();
			JScrollPane scroll = new JScrollPane();
			scroll.getVerticalScrollBar().setUnitIncrement(30);
			setContentPane(scroll);
			scroll.setViewportView(mainPane);
			GridBagLayout mainLayout = new GridBagLayout();
			constr = new GridBagConstraints();
			constr.weightx = 0;
			constr.weighty = 50;
			constr.fill = GridBagConstraints.HORIZONTAL;
			constr.insets = new Insets(5, 5, 5, 5);
			mainPane.setLayout(mainLayout);
			recordMap = record.getMap();
			fieldList = RFL.list();
			persons = record.getAuthorsAffiliations();
			createContent();
			setSize();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void createContent(){
		setTitle(recordMap.get("dc.title[pl]")[0]);
		int a = 0;
		constr.gridx = 0;
		constr.gridy = 0;
		constr.gridwidth = 1;
		JLabel label;
		JToggleButton toggleButton;
		for (int x=0; x<persons.length; x++){
			constr.gridy = a;
			constr.gridx = 0;
			label = new JLabel(persons[x].toStringNames());
			mainPane.add(label, constr);
			a++;
			if (persons[x].isAffiliatedOrEmployed()){
				if (persons[x].countAffiliations()==0){
					constr.gridx = 3;
					toggleButton = new JToggleButton("[AB]");
					toggleButton.setSelected(false);
					toggleButton.addActionListener(new AddABAction(x, 0, record, toggleButton));
					mainPane.add(toggleButton,constr);
				}
				for (int y=0; y<persons[x].countAffiliations(); y++){
					constr.gridx = 1;
					constr.gridy = a+y-1;
					label = new JLabel(persons[x].toStringDepartment(y));
					mainPane.add(label, constr);
					constr.gridx = 2;
					label = new JLabel(persons[x].toStringDateOfAffiliation(y));
					mainPane.add(label, constr);
					constr.gridx = 3;
					toggleButton = new JToggleButton("[AB]");
					toggleButton.setSelected(record.isAB(x, y));
					toggleButton.addActionListener(new AddABAction(x, y, record, toggleButton));
					mainPane.add(toggleButton,constr);
				}
				constr.gridx = 4;
				toggleButton = new JToggleButton("[AU]");
				toggleButton.setSelected(record.isPersonMarkedAU(persons[x]));
				toggleButton.addActionListener(new AddAUAction(x, record, toggleButton));
				mainPane.add(toggleButton,constr);
				a+=persons[x].countAffiliations();
				constr.gridx = 0;
				constr.gridy = a;
				constr.gridwidth = 4;
			}
			else{
				constr.gridx = 3;
				toggleButton = new JToggleButton("[AB]");
				toggleButton.setSelected(false);
				toggleButton.addActionListener(new AddABAction(x, 0, record, toggleButton));
				mainPane.add(toggleButton,constr);
				//a++;
			}
			if (x<persons.length-1){
				mainPane.add(new JSeparator(JSeparator.HORIZONTAL),constr);
			constr.gridwidth = 1;
			}
			a++;
		}
		constr.gridx = 0;
		constr.gridy = a;
		constr.gridwidth = 1;
		JButton button = new JButton("Zastosuj");
		button.addActionListener(new SaveContributorsAction(record));
		mainPane.add(button, constr);
	}
	private void setSize()
	{
		int pointX = MetAnWindow.myIdentity.getX()+50;
		int pointY = MetAnWindow.myIdentity.getY()+50;
		setLocation(pointX, pointY);
		pack();
		setSize(Math.min(getWidth()+20, 1600), Math.min(getHeight(), 1000));
	}
}
