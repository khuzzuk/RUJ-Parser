package OtherDialogs;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;

import Imports.ImportCSV;
import PMainWindow.MetAnWindow;

public class ChooseAffiliationDialog {
	public static String id;
	public static String dialog(JFrame window) throws IOException{
		String[][] units = ImportCSV.readData("/units.csv", "\"");
		JComboBox<String> unitList = new JComboBox<String>(listUnits(units));
		Properties settings = MetAnWindow.settings;
		id = "";
		JDialog dialog = new JDialog(window, "Wybierz jednostkê", true);
		JButton button = new JButton("ok");
		button.addActionListener(new ActionListener() { @Override
			public void actionPerformed(ActionEvent e) {
			int a = unitList.getSelectedIndex();
			if (a==-1) Toolkit.getDefaultToolkit().beep();
			else{
				id = units[a][0];
				dialog.dispose();
			}
			}
		});
		Container panel = dialog.getContentPane();
		GridBagLayout mainLayout = new GridBagLayout();
		GridBagConstraints constr = new GridBagConstraints();
		constr.weightx = 0;
		constr.weighty = 50;
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.insets = new Insets(5, 5, 5, 5);
		panel.setLayout(mainLayout);
		panel.add(unitList, constr);
		constr.gridy = 1;
		panel.add(button, constr);
		dialog.pack();
		dialog.setLocationRelativeTo(window);
		dialog.setVisible(true);
		return id;
	}
	private static String[] listUnits(String[][] units)
	{
		String[] names = new String[units.length];
		for (int x=1; x<units.length; x++)
		{
			names[x] = units[x][1];
		}
		return names;
	}
}
