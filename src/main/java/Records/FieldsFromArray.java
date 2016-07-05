package Records;

import PMainWindow.Func;

import java.util.ArrayList;

public class FieldsFromArray {
	private static String[][] data;
	private static String[] separateFields(String field)
	{
		String[] out = field.split("\\|\\|");
		return out;
	}
	public static String[] getField(String[] array, String[] headlines, String field)
	{
		String[] out;
		int a = Func.findColumn(headlines, field);
		String value = "";
		if (a!=-1) value=array[a];
		if (value.indexOf("||")>-1) out=separateFields(value);
		else {out = new String[1]; out[0] = value;}
		return out;
	}
	public static String[] getField(String[] array, int colnum)
	{
		String[] out;
		int a = colnum;
		String value = "";
		if (a!=-1) value=array[a];
		if (value.indexOf("||")>-1) out=separateFields(value);
		else {out = new String[1]; out[0] = value;}
		return out;
	}
	public static String[] getSAPIDs(String[] array, String[] headlines, String field)
	{
		String[] out = getField(array, headlines, field);
		for (int x=0; x<out.length; x++)
		{
			out[x] = out[x].replaceAll("[^0-9]+", "");
		}
		return out;
	}
	public static String[] getSAPIDs(String[] array, int colnum)
	{
		String[] out = getField(array, colnum);
		for (int x=0; x<out.length; x++)
		{
			if (out[x].indexOf("[SAP")>-1){
				out[x] = out[x].substring(out[x].indexOf("[SAP")+1,out[x].indexOf("]"));
			}
			else if (out[x].indexOf("[USOS")>-1){
				out[x] = out[x].substring(out[x].indexOf("[USOS")+1,out[x].indexOf("]"));
			}
			else out[x]="";
		}
		return out;
	}
	public static String[] getSAPIDs(String[] array, int[] columns)
	{
		String[][] a = new String[columns.length][];
		int b = 0;
		for (int x=0; x<columns.length; x++)
		{
			a[x] = getSAPIDs(array, columns[x]);
			for (int y=0; y<a[x].length; y++)
			{
				if (!a[x][y].equals("")) b++;
			}
		}
		int c = 0;
		String[] out = new String[b];
		for (int x=0; x<a.length; x++)
		{
			for (int y=0; y<a[x].length; y++)
			{
				if (!a[x][y].equals(""))
				{
					out[c] = a[x][y];
					c++;
				}
			}
		}
		return out;
	}
	public static String getSeriesNumber(String input)
	{
		int k = input.indexOf(";");
		if (k>-1 && k+2<input.length())
		{
			return input.substring(k+2);
		}
		else return null;
	}
	public static String[] getAdmin(String[] data, int admin1Col, int admin2Col){
		String[] admin1 = data[admin1Col].split("\\|\\|");
		String[] admin2 = data[admin2Col].split("\\|\\|");
		String[] admin = Func.mergeArrays(admin1, admin2);
		return admin;
	}
	public static String[][] checkAdminFields(String[][] inputData){
		int a = Func.findColumn(inputData[0], "dc.description.admin[]");
		int b = Func.findColumn(inputData[0], "dc.description.admin[pl]");
		int c, d, e, f, g;
		ArrayList<String[]> list = new ArrayList<String[]>();
		for (int x=1; x<inputData.length; x++){
			c=inputData[x][a].length()-inputData[x][a].replaceAll("\\[[A-Z]{2}\\]", "").length();
			e=inputData[x][a].replaceAll("[^\\|{2}]", "").length();
			d=inputData[x][b].length()-inputData[x][b].replaceAll("\\[[A-Z]{2}\\]", "").length();
			f=inputData[x][b].replaceAll("[^\\|{2}]", "").length();
			if (c>(e+2)*2 || d>(f+2)*2){
				list.add(new String[]{inputData[x][0], inputData[x][a], inputData[x][b]});
			}
		}
		return Func.toArray(list);
	}
	/**
	 * Algorytm wyszukiwania rekordu z O=log_2n;
	 * @param data - dane z CSV, które musz¹ byæ wczeœniej posortowane po kolumnie 0.
	 * @param key - wyszukiwany klucz w kolumnie 0.
	 * @return zwraca prawdê, gdy rekord nie zosta³ znaleziony, fa³sz gdy znaleziono w tabeli podany klucz.
	 */
	public static boolean isNewID(String[][] data, String key){
		FieldsFromArray.data = data;
		if (lookAt(0,data.length, Integer.parseInt(key))>-1) return false;
		else return true;
	}
	public static int getIndexFromArray(String[][] array, String key){
		data = array;
		try{
			int a = Integer.parseInt(key);
			return lookAt(0,data.length,a);
		}
		catch(NumberFormatException e){
			return -1;
		}
	}
	public static String[][] getListFromArray(String[][] array, String key){
		data = array;
		try{
			int a = Integer.parseInt(key);
			int b = lookAt(0,data.length,a);
			if (b>-1){
				ArrayList<String[]> list = new ArrayList<String[]>();
				for (int x=b; x>0; x--){
					if (!data[x][0].equals(key)) break;
					list.add(data[x]);
				}
				for (int x=b+1; x<data.length; x++){
					if (!data[x][0].equals(key)) break;
					list.add(data[x]);
				}
				return Func.toArray(list);
			}
			else return new String[][]{{}};
		}
		catch(NumberFormatException e){
			return new String[][]{{}};
		}
	}
	/**
	 * Funkcja rekurencyjna przeszukuj¹ca binarnie posortowan¹ tablicê. tablic¹ jest statyczne pole <code>String[][] data</code>. 
	 * @param min - minimalny indeks przeszukiwanej tablicy.
	 * @param max - maksymalny indeks w przeszukiwanej tablicy.
	 * @param id - klucz do wyszukiwania
	 * @return zwraca pozycjê w tablicy dla znalezionego klucza. Zwraca -1 gdy nie znaleziono klucza.
	 */
	public static int lookAt(int min, int max, int id){
		if (max<min) return -1;
		int mid = (max-min)/2+min;
		if (mid==data.length) return -1;
		int check = Integer.parseInt(data[mid][0]);
		if (id<check) return lookAt(min, mid-1, id);
		if (id>check) return lookAt(mid+1, max, id);
		else return mid;
	}
}