package OtherExports;

import java.io.IOException;
import java.util.ArrayList;

import Imports.MNiSWPoints;
import PMainWindow.Func;

public class SeriesReport
{
	public static String[][] ReportIndexedSeries(String[][] data) throws IOException
	{
		String[][] points = MNiSWPoints.loadPoints();
		int[][] cols = MNiSWPoints.findCols(points[0]);
		int sCol = Func.findColumn(data[0], "dc.description.series[pl]");
		int dCol = Func.findColumn(data[0], "dc.date.issued[pl]");
		String[] actual;
		String[] series;
		int date;
		ArrayList<String[]> list = new ArrayList<String[]>();
		for (int x=1; x<data.length; x++)
		{
			series = data[x][sCol].split("\\|\\|");
			if (!data[x][dCol].replaceAll("[^0-9]+", "").equals(""))
			{
				date = Integer.parseInt(data[x][dCol].replaceAll("[^0-9]+", ""));
				if (!series[0].equals(""))
				{
					actual = Func.dcSeriesToFields(series[0]);
					if (!actual[1].equals(""))
					{
						if (Func.isApprovedByAuthority(cols, date, points, actual[1]))
						{
							list.add(new String[] {
									data[x][0],//id
									data[x][sCol],//series
									actual[0],//title
									actual[1],//issn
									actual[2],//eissn
									series[0].substring(series[0].indexOf(";")+1),//stare numery
									actual[3],//volume
									"", ""//number
							});
						}
					}
					else if (!actual[2].equals(""))
					{
						if  (Func.isApprovedByAuthority(cols, date, points, actual[2]))
						{
							list.add(new String[] {
									data[x][0],//id
									data[x][sCol],//series
									actual[0],//title
									actual[1],//issn
									actual[2],//eissn
									series[0].substring(series[0].indexOf(";")+1),//stare numery
									actual[3],//volume
									"", ""//number
							});
						}
					}
				}
			}
		}
		String[][] out = new String[list.size()][];
		for (int x=0; x<list.size(); x++)
		{
			out[x] = list.get(x);
		}
		return out;
	}
}