package PMainWindow;

import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class FindRecordByISSN extends SwingWorker<String, Integer>
{
	private String issn;
	private MetAnWindow outputWindow;
	private JProgressBar progressBar;
	private int filled = 0;
	private String[][] data;
	private String[][] outputData;
	String[] dcFields = new String[16];
	
	public FindRecordByISSN(String[][] data, String issn, MetAnWindow outputWindow, JProgressBar progressBar)
	{
		progressBar.setIndeterminate(true);
		this.issn = issn;
		this.outputWindow = outputWindow;
		this.progressBar = progressBar;
		this.data = data;
	}
	protected String doInBackground()
	{
		int a = dcFields.length;
		int b = data.length;
		progressBar.setMaximum(b);
		int[] dcCol = new int[a];
		int counter = 0;
		dcFields[0] = "id";
		dcFields[1] = "dc.description.number[pl]";
		dcFields[2] = "dc.description.volume[pl]";
		dcFields[3] = "dc.title.volume[pl]";
		dcFields[4] = "dc.identifier.eissn[pl]";
		dcFields[5] = "dc.identifier.issn[pl]";
		dcFields[6] = "dc.description.series[pl]";
		dcFields[7] = "dc.date.issued[pl]";
		dcFields[8] = "dc.title.journal[pl]";
		dcFields[9] = "dc.title.container[pl]";
		dcFields[10] = "dc.contributor.editor[pl]";
		dcFields[11] = "dc.contributor.other[pl]";
		dcFields[12] = "dc.type[pl]";
		dcFields[13] = "dc.subtype[pl]";
		dcFields[14] = "dc.description.physical[pl]";
		dcFields[15] = "dc.identifier.uri";
		for (int x=0; x<a; x++)
		{
			dcCol[x] = Func.findColumn(data[0], dcFields[x]);
		}

		for (int x=0; x<b; x++)
		{
			for (int y=4; y<7; y++)
			{
				if (data[x][dcCol[y]].contains(issn)) counter++;
			}
		}
		progressBar.setIndeterminate(false);
		int c = 0;
		outputData = new String[counter][a];
		for (int x=1; x<data.length; x++)
		{
			if (x%100==0) {filled = x; int progress = x; publish(progress);}
			for (int y=4; y<7; y++)
			{
				if (data[x][dcCol[y]].contains(issn))
				{
					for (int z=0; z<a; z++)
					{
						outputData[c][z] = data[x][dcCol[z]];
					}
					c++;
				}
			}
		}
		return "Finished.";
	}
	protected void process(List<Integer> progress)
	{
		progressBar.setValue(filled);
	}
	protected void done()
	{
		outputWindow.menuShowStart();
		outputWindow.menuShowAfterLoadCSV();
		outputWindow.menuShowAfter_MI4();
		outputWindow.addEditButtons(outputData, dcFields);
	}
}
