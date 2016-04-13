package OtherDialogs;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import PMainWindow.MetAnWindow;

public class XMLReport {
	private static int status; //0-ok, 1-generuj csv z osobami
	/**
	 * 
	 * @param exportedRecords - liczba wszystkich rekordów w xml
	 * @return - ustala status operacji. Je¿eli 1 - raport afiliacji do CSV.
	 */
	public static int dialog(int exportedRecords){
		JDialog dialog = new JDialog(MetAnWindow.myIdentity, "Eksport zakoñczono", true);
		JLabel label = new JLabel("Eksportowano "+exportedRecords+" rekordów");
		JButton button = new JButton("ok");
		button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				status=0;
				dialog.dispose();
			}
		});
		JButton buttonReport = new JButton("Raport");
		buttonReport.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				status=1;
				dialog.dispose();
			}			
		});
		Container panel = dialog.getContentPane();
		GridBagLayout mainLayout = new GridBagLayout();
		GridBagConstraints constr = new GridBagConstraints();
		constr.weightx = 0;
		constr.weighty = 50;
		constr.gridwidth = 2;
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.insets = new Insets(5, 5, 5, 5);
		panel.setLayout(mainLayout);
		panel.add(label, constr);
		constr.gridwidth = 1;
		constr.gridy = 1;
		panel.add(button, constr);
		constr.gridx = 1;
		panel.add(buttonReport, constr);
		dialog.pack();
		dialog.setLocationRelativeTo(MetAnWindow.myIdentity);
		dialog.setVisible(true);
		return status;
	}
}
