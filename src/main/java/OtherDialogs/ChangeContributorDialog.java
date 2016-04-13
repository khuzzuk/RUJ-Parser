package OtherDialogs;

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
import javax.swing.plaf.basic.BasicArrowButton;

import CustomActions.ChangeAllContributorsAction;
import CustomActions.ChangeContributorsAction;
import CustomActions.SaveContributorsAction;
import CustomActions.SaveContributorsToCSV;
import PMainWindow.MetAnWindow;
import Records.CompleteRecord;
import Records.RFL;

public class ChangeContributorDialog extends JFrame
{
	private MetAnWindow mainWindow;
	private JTextField[][] textFields = new JTextField[5][];
	private BasicArrowButton[][] buttons = new BasicArrowButton[8][];
	private CompleteRecord record;
	private Map<String, String[]> recordMap;
	private String[][] fieldList;
	private GridBagConstraints constr;
	private JPanel mainPane;
	private JLabel labels[];
	private String[] authors, others, translators, reviewers, editors;
	private int maxGridY;
	public ChangeContributorDialog(CompleteRecord record, MetAnWindow mainWindow)
	{
		this.mainWindow = mainWindow;
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
		createContent();
		setSize();
	}
	private void createContent()
	{
		setTitle(recordMap.get("dc.title[pl]")[0]);
		int a = 0;
		authors = recordMap.get(fieldList[3][2]);
		others = recordMap.get(fieldList[4][2]);
		translators = recordMap.get(fieldList[5][2]);
		reviewers = recordMap.get(fieldList[6][2]);
		editors = recordMap.get(fieldList[14][2]);
		maxGridY = 3 + Math.max(Math.max(authors.length, Math.max(others.length, translators.length)), Math.max(reviewers.length, editors.length));
		setHeadlineTitle();
		constr.gridwidth = 1;
		textFields[0] = new JTextField[authors.length];
		constr.gridx = a;
		setContentFields(authors);
		addButtonsEast(authors, fieldList[3][2], fieldList[4][2]);
		if (authors.length>0 && !authors[0].equals("")) a+=3;
		else a++;
		constr.gridx = a;
		setContentFields(others);
		addButtonsEast(others, fieldList[4][2], fieldList[5][2]);
		addButtonsWest(others, fieldList[4][2], fieldList[3][2]);
		if (others.length>0 && !others[0].equals("")) a+=3;
		constr.gridx = a;
		setContentFields(translators);
		addButtonsEast(translators, fieldList[5][2], fieldList[6][2]);
		addButtonsWest(translators, fieldList[5][2], fieldList[4][2]);
		if (translators.length>0 && !translators[0].equals("")) a+=3;
		constr.gridx = a;
		setContentFields(reviewers);
		addButtonsEast(reviewers, fieldList[6][2], fieldList[14][2]);
		addButtonsWest(reviewers, fieldList[6][2], fieldList[5][2]);
		if (reviewers.length>0 && !reviewers[0].equals("")) a+=3;
		constr.gridx = a;
		setContentFields(editors);
		constr.gridx++;
		addButtonsWest(editors, fieldList[14][2], fieldList[6][2]);
		//add last line buttons
		constr.gridy = maxGridY;
		constr.gridx++;
		JButton approveButton = new JButton("ZatwierdŸ");
		approveButton.addActionListener(new SaveContributorsAction(mainWindow, record));
		mainPane.add(approveButton, constr);
		//if (authors.length>0 && !authors[0].equals("")) constr.gridx = 0;
		constr.gridx -= 3;
		JButton saveButton = new JButton("Zapisz CSV");
		saveButton.addActionListener(new SaveContributorsToCSV(mainWindow, record));
		mainPane.add(saveButton, constr);
	}
	private void setHeadlineTitle()
	{
		labels = new JLabel[5];
		constr.gridwidth = 3;
		constr.gridheight = 1;
		constr.gridx = 0;
		constr.gridy = 0;
		int a = 0;
		if (authors.length>0 && !authors[0].equals("")) {labels[a] = new JLabel("Autorzy");a++;}
		if (others.length>0 && !others[0].equals("")) {labels[a] = new JLabel("Wspó³twórcy");a++;}
		if (translators.length>0 && !translators[0].equals("")) {labels[a] = new JLabel("T³umacze");a++;}
		if (reviewers.length>0 && !reviewers[0].equals("")) {labels[a] = new JLabel("Recenzenci");a++;}
		if (editors.length>0 && !editors[0].equals("")) {labels[a] = new JLabel("Redaktorzy");a++;}
		constr.gridwidth = 3;
		constr.gridy = 1;
		a = 0;
		if (authors.length==0 || authors[0].equals("")) a++;
		for (int x=0; x<labels.length; x++)
		{
			constr.gridx = a;
			if (labels[x]!=null)
				{
					//if (labels[x].getText().equals("Redaktorzy")) constr.gridwidth = 2;
					mainPane.add(labels[x], constr);
					a+=3;
				}
		}
	}
	private void setContentFields(String[] data)
	{
		if (data.length!=0 && !data[0].equals(""))
		{
			int b=2;
			int a = constr.gridx/3;
			textFields[a] = new JTextField[data.length];
			for (int x=0; x<data.length; x++)
			{
				textFields[a][x] = new JTextField(data[x]);
				constr.gridy = b;
				mainPane.add(textFields[a][x], constr);
				b++;
			}
		}
	}
	private void addButtonsEast(String[] data, String field, String field2)
	{
		int a = constr.gridx/3;
		constr.gridx++;
		int b=2;
		if (data.length!=0 && !data[0].equals(""))
		{
			buttons[a] = new BasicArrowButton[data.length+1];
			for (int x=0; x<data.length; x++)
			{
				constr.gridy = b;
				buttons[a][x] = new BasicArrowButton(BasicArrowButton.EAST);
				buttons[a][x].addActionListener(new ChangeContributorsAction(recordMap, field, field2, b, this));
				mainPane.add(buttons[a][x], constr);
				b++;
			}
			constr.gridy = 1;
			buttons[a][data.length] = new BasicArrowButton(BasicArrowButton.EAST);
			buttons[a][data.length].addActionListener(new ChangeAllContributorsAction(recordMap, field, field2, this));
			mainPane.add(buttons[a][data.length], constr);
		}
	}
	private void addButtonsWest(String[] data, String field, String field2)
	{
		int a = (constr.gridx-1)/3+3;
		constr.gridx-=2;
		int b=2;
		if (data.length!=0 && !data[0].equals(""))
		{
			buttons[a] = new BasicArrowButton[data.length+1];
			for (int x=0; x<data.length; x++)
			{
				constr.gridy = b;
				buttons[a][x] = new BasicArrowButton(BasicArrowButton.WEST);
				buttons[a][x].addActionListener(new ChangeContributorsAction(recordMap, field, field2, b, this));
				mainPane.add(buttons[a][x], constr);
				b++;
			}
			constr.gridy = 1;
			buttons[a][data.length] = new BasicArrowButton(BasicArrowButton.WEST);
			buttons[a][data.length].addActionListener(new ChangeAllContributorsAction(recordMap, field, field2, this));
			mainPane.add(buttons[a][data.length], constr);
		}
	}
	private void setSize()
	{
		int pointX = mainWindow.getX()+50;
		int pointY = mainWindow.getY()+50;
		setLocation(pointX, pointY);
		pack();
		setSize(Math.min(getWidth()+20, 1600), Math.min(getHeight(), 1000));
	}
	public void clear()
	{
		mainPane.removeAll();
		createContent();
		setSize();
		mainPane.repaint();
	}
}