package RecordEditor;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import CustomActions.ExportEditorToCSV;
import CustomActions.SaveEditorData;
import PMainWindow.MetAnWindow;
import Records.CompleteRecord;
import Records.RFL;

public class CreateBasicEditor extends JFrame
{
	private JTextField[][] textFields;
	private Map<String, String[]> recordMap;
	private String[][] fieldList;
	public CreateBasicEditor(CompleteRecord record, MetAnWindow mainWindow)
	{
		JPanel mainPane = new JPanel();
		JScrollPane scroll = new JScrollPane();
		scroll.getVerticalScrollBar().setUnitIncrement(30);
		setContentPane(scroll);
		scroll.setViewportView(mainPane);
		int b = 0;
		GridBagLayout mainLayout = new GridBagLayout();
		mainPane.setLayout(mainLayout);
		GridBagConstraints constr = new GridBagConstraints();
		constr.weightx = 0;
		constr.weighty = 50;
		constr.gridwidth = 1;
		constr.gridheight = 1;
		constr.ipadx = 10;
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.insets = new Insets(5, 5, 5, 5);
		recordMap = record.getMap();
		fieldList = RFL.list();
		String[] actualField;
		textFields = new JTextField[fieldList.length][];
		JLabel labels[] = new JLabel[fieldList.length];
		for (int x=0; x<fieldList.length; x++)
		{
			actualField = recordMap.get(fieldList[x][2]);
			if (actualField != null && !actualField[0].equals(""))
			{
				labels[x] = new JLabel(fieldList[x][1]);
				constr.gridx=0;
				constr.gridy=b;
				constr.weightx = 0;
				textFields[x] = new JTextField[actualField.length];
				constr.gridheight=actualField.length;
				mainPane.add(labels[x], constr);
				constr.gridheight=1;
				constr.gridx=1;
				for (int y=0; y<actualField.length; y++)
				{
					textFields[x][y] = new JTextField(actualField[y]);
					constr.gridy = b;
					constr.weightx = 600;
					textFields[x][y].setMaximumSize(new Dimension(800, 20));
					mainPane.add(textFields[x][y], constr);
					b++;
				}
			}
		}
		constr.gridy = b;
		constr.gridx = 0;
		JButton saveButton = new JButton("Zapisz do CSV");
		saveButton.addActionListener(new ExportEditorToCSV(mainWindow, record, this));
		mainPane.add(saveButton, constr);
		constr.gridx = 1;
		JButton approveButton = new JButton("ZatwierdŸ");
		approveButton.addActionListener(new SaveEditorData(mainWindow, record, this));
		mainPane.add(approveButton, constr);
		int pointX = mainWindow.getX()+50;
		int pointY = mainWindow.getY()+50;
		setLocation(pointX, pointY);
		pack();
		setSize(Math.min(getWidth()+30, 1600), Math.min(getHeight(), 1000));
	}
	public void modifyRecord()
	{
		String[] export;
		for (int x=0; x<textFields.length; x++)
		{
			if (textFields[x]!=null)
			{
				export = new String[textFields[x].length];
				for (int y=0; y<textFields[x].length; y++)
				{
					if (textFields[x][y]==null) break;
					export[y] = textFields[x][y].getText();
				}
				recordMap.put(fieldList[x][2], export);
			}
		}
	}
}