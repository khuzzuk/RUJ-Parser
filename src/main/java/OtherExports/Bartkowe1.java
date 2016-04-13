package OtherExports;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.AbstractAction;

import PMainWindow.Func;
import PMainWindow.MetAnWindow;

public class Bartkowe1 extends AbstractAction
{
	private MetAnWindow window;
	public Bartkowe1(MetAnWindow window){
		this.window = window;
	}
	public void actionPerformed(ActionEvent element)
	{
		pages(window.getLoadedCSV(), window.getSettings());
	}
	public static void pages(String[][] input, Properties settings)
	{
		int col = Func.findColumn(input[0], "dc.description.physical[pl]");
		String[] current;
		ArrayList<String[]> out = new ArrayList<String[]>();
		for (int x=0; x<input.length; x++)
		{
			current = countPages(input[x][col], input[x][0]);
			if (current!=null)
			{
				out.add(current);
			}
		}
		String[][] toExport = new String[out.size()][2];
		for (int x=0; x<out.size(); x++)
		{
			toExport[x] = out.get(x);
		}
		String[] cols = new String[] {"DSpace", "pbn", "id"};
		try {
			new PMainWindow.SaveCSV("D:\\Ruj\\strony.csv", toExport, cols);
		} catch (IOException e) {e.printStackTrace();}
	}
	private static String[] countPages(String input, String id)
	{
		String[] out = new String[3];
		out[0] = input;
		out[1] = Func.countPages(input);
		out[2] = id;
		return out;
	}
}