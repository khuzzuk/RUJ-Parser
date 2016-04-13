package OtherExports;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.AbstractAction;

import PMainWindow.Func;
import PMainWindow.MetAnWindow;
import Records.FieldsFromArray;

public class Bartkowe2 extends AbstractAction
{
	private MetAnWindow window;
	public Bartkowe2(MetAnWindow window){
		this.window = window;
	}
	public void actionPerformed(ActionEvent element)
	{
		pages(window.getLoadedCSV(), window.getSettings());
	}
	public static void pages(String[][] input, Properties settings)
	{
		int col = Func.findColumn(input[0], "dc.description.series[pl]");
		int uri1 = Func.findColumn(input[0], "dc.identifier.uri[]");
		int uri2 = Func.findColumn(input[0], "dc.identifier.uri");
		String current, link;
		String[] splitted;
		ArrayList<String[]> out = new ArrayList<String[]>();
		for (int x=1; x<input.length; x++)
		{
			splitted = input[x][col].split("\\|\\|");
			if (!splitted[0].equals(""))
			{
				link = input[x][uri1];
				if (link.equals("")) link = input[x][uri2];
				for (int y=0; y<splitted.length; y++)
				{
					current = FieldsFromArray.getSeriesNumber(splitted[y]);
					if (current!=null)
					{
						if (current.replaceAll("[0-9]+", "").length()>0)
						{
							if (current.indexOf(",")>-1)
							{
								String[] temp = new String[3];
								temp[2] = link;
								temp[0] = current;
								temp[1] = current.substring(0, current.indexOf(",")).replaceAll("[^0-9]+", "");
								out.add(temp);
							}
							else if (current.indexOf("(")>-1)
							{
								if (current.indexOf("(")>0)
								{
									String[] temp = new String[3];
									temp[2] = link;
									temp[0] = current;
									temp[1] = current.substring(0, current.indexOf("(")).replaceAll("[^0-9]+", "");
									out.add(temp);
								}
								else
								{
									String[] temp = new String[3];
									temp[2] = link;
									temp[0] = current;
									temp[1] = current.substring(current.indexOf(")"+1), current.indexOf(",")).replaceAll("[^0-9]+", "");
									out.add(temp);
								}
							}
							else if (current.indexOf("/")>-1)
							{
								String[] temp = new String[3];
								temp[2] = link;
								temp[0] = current;
								temp[1] = current.substring(0, current.indexOf("/")).replaceAll("[^0-9]+", "");
								out.add(temp);
							}
							else if (current.indexOf("-")>-1)
							{
								String[] temp = new String[3];
								temp[2] = link;
								temp[0] = current;
								temp[1] = current.substring(0, current.indexOf("-")).replaceAll("[^0-9]+", "");
								out.add(temp);
							}
							else
							{
								String[] temp = new String[3];
								temp[2] = link;
								temp[0] = current;
								temp[1] = current.replaceAll("[^0-9]+", "");
								out.add(temp);
							}
						}
					}
				}
			}
		}
		String[][] toExport = new String[out.size()][];
		for (int x=0; x<out.size(); x++)
		{
			toExport[x] = out.get(x);
		}
		String[] cols = new String[] {"DSpace", "pbn", "link"};
		try {
			new PMainWindow.SaveCSV("D:\\Ruj\\numery_serii.csv", toExport, cols);
		} catch (IOException e) {e.printStackTrace();}
	}
}