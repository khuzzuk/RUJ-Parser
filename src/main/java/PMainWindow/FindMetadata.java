package PMainWindow;

public class FindMetadata
{
	private String[] columns;
	private String[][] outputData;
	public FindMetadata (String[][] data, String[] searchColumns, String[] addColumns, String[] inputText)
	{
		int a = data.length;
		int b = searchColumns.length + addColumns.length;
		int c = 0; //aktualny wiersz kopiowania danych
		int d = 0; //aktualna kolumna kopiowania danych
		String[][] temporaryData = new String[a][b+1];
		int[] searchColumnsIndex = new int[searchColumns.length];
		int[] addColumnsIndex = new int[addColumns.length];
		for (int x=0; x<searchColumns.length; x++)
		{
			searchColumnsIndex[x] = Func.findColumn(data[0], searchColumns[x]);
		}
		for (int x=0; x<addColumns.length; x++)
		{
			addColumnsIndex[x] = Func.findColumn(data[0], addColumns[x]);
		}
		for (int x=1; x<a; x++)
		{
			for (int y=0; y<searchColumns.length; y++)
			{
				if(data[x][searchColumnsIndex[y]].indexOf(inputText[y])>-1)
				{
					temporaryData[c][0] = data[x][0]; 
					d=1;
					for (int z=0; z<searchColumns.length; z++)
					{
						temporaryData[c][d] = data[x][searchColumnsIndex[z]];
						d++;
					}
					for (int z=0; z<addColumns.length; z++)
					{
						if (addColumnsIndex[z]!=-1)
						{
							temporaryData[c][d] = data[x][addColumnsIndex[z]];
						}
						else temporaryData[x][d] = "";
						d++;
					}
					c++;
					break;
				}
			}
		}
		c=0;
		for (int x=0; x<a; x++)
		{
			if (temporaryData[x][0] == null || temporaryData[x][0].equals("")) break;
			c++;
		}
		outputData = new String[c][d];
		for (int x=0; x<c; x++)
		{
			outputData[x] = temporaryData[x];
		}
	}
	public String[][] getData()
	{
		return outputData;
	}
}