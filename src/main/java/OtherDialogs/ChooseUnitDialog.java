package OtherDialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import ExportXML.ParseXML;
import Imports.ImportCSV;
import OtherExports.DepartmentReport;
import OtherExports.OpenAccessReport;

public class ChooseUnitDialog extends JFrame
{
	private String[][] units;
	private JComboBox unitList, datesList;
	private String[][] inputData;
	private JProgressBar progressBar;
	private Properties settings;
	private String function;
	private JPanel mainPane;
	public ChooseUnitDialog(String[][] inputData, JProgressBar progressBar, Properties settings, String function) throws IOException
	{
		this.inputData = inputData;
		this.settings = settings;
		this.progressBar = progressBar;
		this.function = function;
		mainPane = new JPanel();
		units = ImportCSV.readData("/units.csv", "\"");
		unitList = new JComboBox<String>(listUnits(units));
		JButton okButton = new JButton("Dalej");
		JButton cancelButton = new JButton("Anuluj");
		GridBagLayout mainLayout = new GridBagLayout();
		GridBagConstraints constr = new GridBagConstraints();
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.insets = new Insets(10, 10, 10, 10);
		mainPane.setLayout(mainLayout);
		constr.gridx = 0;
		constr.gridy=0;
		constr.gridwidth = 2;
		mainPane.add(unitList, constr);
		if (function.equals("XML")) addYearOption(constr);
		constr.gridwidth=1;
		constr.gridy = 1;
		mainPane.add(okButton, constr);
		constr.gridx = 1;
		mainPane.add(cancelButton, constr);
		add(mainPane);
		pack();
		setLocation(500, 500);
		okButton.addActionListener(new okButtonAction());
		cancelButton.addActionListener(new cancelButtonAction());
	}
	private String[] listUnits(String[][] units)
	{
		String[] names = new String[units.length];
		for (int x=1; x<units.length; x++)
		{
			names[x] = units[x][1];
		}
		return names;
	}
	private void addYearOption(GridBagConstraints constr){
		constr.gridx = 2;
		int currentYear = (int) (System.currentTimeMillis()/1000/3600/24/365.25 +1970);
		String[] list = new String[currentYear-2011];
		for (int x=2013; x<=currentYear; x++){
			list[x-2013] = ""+x;
		}
		list[list.length-1] = "wszystkie";
		datesList = new JComboBox<String>(list);
		mainPane.add(datesList, constr);
		constr.gridx = 0;
	}
	class okButtonAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			int a = unitList.getSelectedIndex();
			if (a==-1) Toolkit.getDefaultToolkit().beep();
			else
			{
				int b = datesList.getSelectedIndex();
				if (function.equals("XML") && b==-1) Toolkit.getDefaultToolkit().beep();
				else if (function.equals("XML"))
				{
					String[] year;
					if (b==datesList.getItemCount()-1){
						year = new String[b];
						for (int x=0; x<b; x++){
							year[x]=""+(2013+x);
						}
					}
					else year = new String[]{""+(2013+b)};
					ParseXML task = new ParseXML(null, null, new String[] {units[a][2], units[a][0], units[a][3]}, year);
					task.execute();
				}
				else if (function.equals("report"))
				{
					try {
						DepartmentReport d = new DepartmentReport(inputData, units[a][0]);
						d.passReport();
					} catch (IOException e1) {e1.printStackTrace();}
				}
				else if (function.equals("OpenAccess")){
					try {
						new OpenAccessReport(inputData, units[a][3]);
					} catch (IOException e1) {e1.printStackTrace();}
				}
			}
			Window parent = SwingUtilities.getWindowAncestor((java.awt.Component) e.getSource());
			parent.dispatchEvent(new WindowEvent(parent, WindowEvent.WINDOW_CLOSING));
		}
	}
	class cancelButtonAction extends AbstractAction
	{
		public cancelButtonAction(){}
		public void actionPerformed(ActionEvent e)
		{
			Window parent = SwingUtilities.getWindowAncestor((java.awt.Component) e.getSource());
			parent.dispatchEvent(new WindowEvent(parent, WindowEvent.WINDOW_CLOSING));
		}
	}
}