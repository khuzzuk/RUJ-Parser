package PMainWindow;

import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Func
{
	public static int findColumn(String[] data, String dcField)
	{
		int col = -1;
		for (int x=0; x<data.length; x++)
		{
			if (data[x].equals(dcField)) {col = x; break;}
		}
		return col;
	}
	public static int findColumn(String[][] data, String dcField)
	{
		int col = -1;
		for (int x=0; x<data.length; x++)
		{
			if (data[0][x].equals(dcField)) {col = x; break;}
		}
		return col;
	}
	public static int findRowByID(String id, String[][] data)
	{
		int output = 0;
		int a = data.length;
		for (int x=0; x<a; x++) 
			{
				if (data[x][0].equals(id)) {output=x; break;} 
			}
		return output;
	}
	public static String[][] addColumns(int plus, String[][] inputData)
	{
		int a = inputData.length;
		int b = inputData[0].length;
		String[][] outputData = new String[a][b+plus];
		for (int x=0; x<a; x++)
		{
			for (int y=0; y<b; y++)
			{
				outputData[x][y] = inputData[x][y];
			}
		}
		return outputData;
	}
	public static String[] addColumns(int plus, String[] inputData)
	{
		int a = inputData.length;
		String[] outputData = new String[a+plus];
		for (int x=0; x<a; x++)
		{
			outputData[x] = inputData[x];
		}
		return outputData;
	}
	public static String[][] removeColumn(String[][] data, int columnIndex)
	{
		int a = data.length;
		int b = data[0].length;
		String[][] output = new String[a][b-1];
		for (int x=0; x<a; x++)
		{
			for (int y=0; y<b; y++)
			{
				if (y<columnIndex) output[x][y]=data[x][y];
				if (y>columnIndex) output[x][y-1]=data[x][y];
			}
		}
		return output;
	}
	public static String[] removeColumn(String[] data, int Index)
	{
		int a = data.length;
		String[] output = new String[a-1];
		for (int x=0; x<a; x++)
		{
			if (x<Index) output[x]=data[x];
			if (x>Index) output[x-1]=data[x];
		}
		return output;
	}
	public static String[] addRow(String[] in)
	{
		String[] out = new String[in.length+1];
		for (int x=0; x<in.length; x++) out[x]=in[x];
		return out;
	}
	public static String[][] addRow(String[][] in)
	{
		String[][] out = new String[in.length+1][in[0].length];
		for (int x=0; x<in.length; x++)
		{
			for (int y=0; y<in[0].length; y++)
			{
				out[x][y] = in[x][y];
			}
		}
		return out;
	}
	public static String[] removeRow(String[] in, int removeRow)
	{
		String[] out = new String[in.length-1];
		int a=0;
		for (int x=0; x<in.length; x++)
		{
			if (x!=removeRow)
			{
				out[a] = in[x];
				a++;
			}
		}
		return out;
	}
	public static String[][] removeRow(String[][] in, int removeRow)
	{
		String[][] out = new String[in.length-1][];
		int a=0;
		for (int x=0; x<in.length; x++)
		{
			if (x!=removeRow)
			{
				out[a] = in[x];
				a++;
			}
		}
		return out;
	}
	public static String[][] toArray(ArrayList<String[]> list){
		String[][] out = new String[list.size()][];
		for (int x=0; x<out.length; x++){
			out[x] = list.get(x);
		}
		return out;
	}
	public static String[][] removeEmptyRowsFromArray(String[][] array){
		int a=0;
		for (int x=0; x<array.length; x++){
			if (array[x]==null || array[x][0]==null || array[x][0].equals("")) break;
			a++;
		}
		String[][] out = new String[a][];
		for (int x=0; x<a; x++){
			out[x] = array[x];
		}
		return out;
	}
	public static String[][] changeToJournal(String[][] inputData)
	{
		int a = inputData.length;
		String series[];
		String[] seriesFields = new String[4];
		int i, j, k;
		for (int x=0; x<a; x++)
		{
			if (inputData[x][12].equals("BookSection"))
			{
				inputData[x][12]="JournalArticle";
				if(inputData[x][3].equals("")) {inputData[x][3]=inputData[x][9];}
				else{inputData[x][3]=inputData[x][3]+"||"+inputData[x][9];}
				inputData[x][9]="";
				inputData[x][10]="";
				series = inputData[x][6].split("\\|\\|");
				seriesFields = Func.dcSeriesToFields(series[0]);

				if (inputData[x][8].equals("")) inputData[x][8] = seriesFields[0];
				else inputData[x][8] = inputData[x][8] + "||" + seriesFields[0];
				if (inputData[x][5].equals("")) inputData[x][5] = seriesFields[1];
				else inputData[x][5] = inputData[x][5] + "||" + seriesFields[1];
				if (inputData[x][4].equals("")) inputData[x][4] = seriesFields[2];
				else inputData[x][4] = inputData[x][4] + "||" + seriesFields[2];
				if (inputData[x][2].equals("")) inputData[x][2] = seriesFields[3];
				else inputData[x][2] = inputData[x][2] + "||" + seriesFields[3];
				
				if (inputData[x][6].indexOf("||")==-1) inputData[x][6]="";
				else inputData[x][6]=inputData[x][6].substring(inputData[x][6].indexOf("||")+2);
			}
		}
		return inputData;
	}
	public static String[] changeToJournal(String[] inputData, String[] cols)
	{
		String[] out = inputData.clone();
		String temp;
		int type = findColumn(cols, "dc.type[pl]");
		int jTitle = findColumn(cols, "dc.title.journal[pl]");
		int cTitle = findColumn(cols, "dc.title.container[pl]");
		int vTitle = findColumn(cols, "dc.title.volume[pl]");
		int iss = findColumn(cols, "dc.description.number[pl]");
		int vol = findColumn(cols, "dc.description.volume[pl]");
		int ser = findColumn(cols, "dc.description.series[pl]");
		int issn = findColumn(cols, "dc.identifier.issn[pl]");
		int eissn = findColumn(cols, "dc.identifier.eissn[pl]");
		out[type]="JournalArticle";
		temp = out[ser];
		if (temp.indexOf("||")>-1) temp = temp.substring(0, temp.indexOf("||"));
		String[] series = dcSeriesToFields(temp);
		out[jTitle] = series[0];
		out[vTitle] = out[cTitle];
		out[cTitle] = "";
		out[vol] = series[3];//getNumberValueFromSeries(series[3]);
		out[issn] = series[1];
		out[eissn] = series[2];
		out[ser] = "";
		return out;
	}
	public static String[][] changeToSeries(String[][] inputData)
	{
		int a = inputData.length;
		int b = inputData[0].length;
		String series;
		for (int x=0; x<a; x++)
		{
			if (inputData[x][12].equals("JournalArticle"))
			{
				inputData[x][12]="BookSection";
				inputData[x][9]=inputData[x][3];
				inputData[x][3]="";
				series = inputData[x][8];
				inputData[x][8]="";
				if (!inputData[x][5].equals("")) {series = series + ", ISSN " + inputData[x][5]; inputData[x][5]="";}
				if (!inputData[x][4].equals("")) {series = series + ", eISSN " + inputData[x][4]; inputData[x][4]="";}
				if (!inputData[x][1].equals("") && !inputData[x][2].equals("")) {series = series + "; nr " + inputData[x][1] + ", t. " + inputData[x][2]; inputData[x][1]=""; inputData[x][2]="";}
				if (!inputData[x][1].equals("")) {series = series + "; nr " + inputData[x][1]; inputData[x][1]="";}
				if (!inputData[x][2].equals("")) {series = series + "; t. " + inputData[x][2]; inputData[x][2]="";}
				if (inputData[x][6].equals("")) inputData[x][6] = series;
				else inputData[x][6] = series + "||" + inputData[x][6];
			}
			
		}
		return inputData;
	}
	public static String[] dcSeriesToFields(String input)
	{
		String[] output = new String[4];
		int i, j, k;
		i = input.indexOf(", ISSN ");
		j = input.indexOf(", eISSN ");
		k = input.indexOf(";");
		for (int x=0; x<output.length; x++) output[x]="";
		if (i>-1 && j>-1 && k>-1)
		{
			output[0] = input.substring(0, i);
			output[1] = input.substring(i+7, i+16);
			output[2] = input.substring(j+8, j+17);
			output[3] = input.substring(k+2);
		}
		else if (i>-1 && k>-1)
		{
			output[0] = input.substring(0, i);
			output[1] = input.substring(i+7, i+16);
			output[3] = input.substring(k+1);
		}
		else if (j>-1 && k>-1)
		{
			output[0] = input.substring(0, j);
			output[2] = input.substring(j+8, j+17);
			output[3] = input.substring(k+2);
		}
		else if (j>-1 && i>-1)
		{
			output[0] = input.substring(0, i);
			output[1] = input.substring(i+7, i+16);
			output[2] = input.substring(j+8, j+17);
		}
		else if (i>-1)
		{
			output[0] = input.substring(0, i);
			output[1] = input.substring(i+7, i+16);
		}
		else if (j>-1)
		{
			output[0] = input.substring(0, j);
			output[2] = input.substring(j+7, j+16);
		}
		else if (k>-1)
		{
			output[0] = input.substring(0, k);
			output[3] = input.substring(k+1);
		}
		else
		{
			output[0] = input;
		}
		output[3] = extractNumbers(output[3]);
		return output;
	}
	public static String extractNumbers(String input)
	{
		String output = "";
		for (int x=0; x<input.length(); x++)
		{
			if ((input.charAt(x)>=48 && input.charAt(x)<=58)||(input.charAt(x)>=40 && input.charAt(x)<=41)) output = output + input.charAt(x);
		}
		return output;
	}
	public static String[][] findArticleID(String[][] inputData, String[][] loadedData)
	{
		int a = inputData.length;
		int b = inputData[0].length;
		int c = loadedData.length;
		int colDOI = findColumn(loadedData, "dc.identifier.doi[pl]");
		String[][] outputData = addColumns(1, inputData);
		for (int x=0; x<a; x++)
		{
			for (int y=1; y<c; y++)
			{
				if (outputData[x][0].equals(loadedData[y][0]))
				{
					outputData[x][b] = lookForArticleID(loadedData[y][colDOI]);
					if (!outputData[x][b].equals("")) outputData[x][b-2]="";
					break;
				}
			}
		}
		return outputData;
	}
	public static String lookForArticleID(String input)
	{
		int a = input.length();
		char aChar;
		String articleID = "";
		for (int x=a; x>0; x--)
		{
			if (input.equals("")) break;
			aChar = input.charAt(x-1);
			if (aChar==45 || aChar==46 || aChar==47) break;
			articleID = new StringBuilder(articleID).insert(0, aChar).toString();
		}
		return articleID;
	}
	public static String[] addArticleIDColumn(String[] input)
	{
		String[] output = addColumns(1, input);
		output[output.length-1] = "dc.identifier.articleid[pl]";
		return output;
	}
	/**
	 * Kopiowanie dwóch tablic za pomoc¹ funkcji System.arraycopy
	 */
	public static String[] mergeArraysWithID(String[] a, String[] b)
	{
		int aLen = a.length;
		int bLen = b.length;
		String[] output = new String[aLen+bLen+1];
		output[0] = "id";
		System.arraycopy(a, 0, output, 1, aLen);
		System.arraycopy(b, 0, output, aLen+1, bLen);
		return output;
	}
	/**
	 * Dok³adne kopiowanie dwóch tablic przez iteracje
	 */
	public static String[] mergeArrays(String[] a, String[] b){
		int aLen = a.length;
		if (a==null) aLen=0;
		int bLen = b.length;
		if (b==null) bLen=0;
		String[] output = new String[aLen+bLen];
		int x=0;
		if (aLen>0){
			for (x=0; x<aLen; x++){
				output[x] = a[x];
			}
		}
		if (bLen>0){
			for (int y=0; y<bLen; y++){
				output[x] = b[y];
				x++;
			}
		}
		return output;
	}
	/**
	 * Dok³adne kopiowanie dwóch tablic dwuwymiarowych przez iteracje
	 */
	public static String[][] mergeArrays(String[][] a, String[][] b){
		int aLen, bLen;
		if (a==null) aLen=0;
		else aLen = a.length;
		if (b==null) bLen=0;
		else bLen = b.length;
		String[][] output = new String[aLen+bLen][];
		int x=0;
		if (aLen>0){
			for (x=0; x<aLen; x++){
				output[x] = a[x];
			}
		}
		if (bLen>0){
			for (int y=0; y<bLen; y++){
				output[x] = b[y];
				x++;
			}
		}
		return output;
	}
	public static int countContentInArray(String[] array){
		int a = 0;
		if (array==null) return 0;
		for (int x=0; x<array.length; x++){
			if (!array[x].equals("")) a++;
		}
		return a;
	}
	public static String[] deleteColumnFromArray(String[] input, int column)
	{
		if (column>input.length) return input;
		String[] output = new String[input.length-1];
		int y = 0;
		for (int x=0; x<input.length; x++)
		{
			if (x!=column) {output[y] = input[x]; y++;}
		}
		return output;
	}
	public static String[][] splitToNamesAndSurnames(String input)
	{
		String[] all = input.split(" ");
		int a=0;
		int b=0;
		for (int x=0; x<all.length; x++)
		{
			if (all[x].length()==1) a++;
			else b++;
		}
		String[][] person = new String[2][];
		person[0] = new String[b];
		person[1] = new String[a];
		int c=0;
		int d=0;
		for (int x=0; x<all.length; x++)
		{
			if (all[x].length()==1)
			{
				person[1][d] = all[x];
				d++;
			}
			else
			{
				person[0][c] = all[x];
				c++;
			}
		}
		return person;
	}
	public static URI getURIFromID(String[][] inputData, String id)
	{
		URI output;
		int a = inputData.length;
		int b = Func.findColumn(inputData[0], "dc.identifier.uri");
		int c = Func.findColumn(inputData[0], "dc.identifier.uri[]");
		for (int x=0; x<a; x++)
		{
			if (inputData[x][0].equals(id))
			{
				if (!inputData[x][b].equals(""))
				{
					output = URI.create(inputData[x][b]);
					return output;
				}
				else if (!inputData[x][c].equals(""))
				{
					output = URI.create(inputData[x][c]);
					return output;
				}
			}
		}
		return null;
	}
	public static String countSheets(String pages)
	{
		float sum = 0;
		if (pages.indexOf(",")>-1)
		{
			String[] splitted = pages.split(",");
			for (int x=0; x<splitted.length; x++)
			{
				if  (splitted[x].replaceAll("[^0-9]+", "").length()==0)
				{
					sum += convertRomanToArabic(splitted[x]);
				}
				else
				{
					sum += Integer.parseInt(splitted[x].replaceAll("[^0-9]+", ""));
				}
			}
		}
		else if (pages.indexOf("-")>-1)
		{
			int a, b;
			String[] splitted = pages.split("-");
			if (splitted[0].replaceAll("[^0-9]+", "").length()==0)
			{
				a = convertRomanToArabic(splitted[0]);
				b = convertRomanToArabic(splitted[1]);
			}
			else
			{
				a = Integer.parseInt(splitted[0].replaceAll("[^0-9]+", ""));
				b = Integer.parseInt(splitted[1].replaceAll("[^0-9]+", ""));
			}
			sum = b-a;
		}
		else if (pages.indexOf("–")>-1)
		{
			int a, b;
			String[] splitted = pages.split("–");
			if (splitted[0].replaceAll("[^0-9]+", "").length()==0)
			{
				a = convertRomanToArabic(splitted[0]);
				b = convertRomanToArabic(splitted[1]);
			}
			else
			{
				a = Integer.parseInt(splitted[0].replaceAll("[^0-9]+", ""));
				b = Integer.parseInt(splitted[1].replaceAll("[^0-9]+", ""));
			}
			sum = b-a;
		}
		else if (pages.replaceAll("[^0-9]+", "").length()>0)
		{
			sum = Integer.parseInt(pages.replaceAll("[^0-9]+", ""));
		}
		else return "";
		if (sum>1000) return "";
		DecimalFormat round = new DecimalFormat("#.##");
		sum = Float.valueOf(sum/17);
		return round.format(sum);
		//return "" + Float.valueOf(round.format(sum/15));
	}
	public static int convertRomanToArabic(String number)
	{
		number = number.replace(" ", "");
	    if (number.equals("")) return 0;
	    if (number.startsWith("M")) return 1000 + convertRomanToArabic(number.replaceFirst("M",""));
	    if (number.startsWith("CM")) return 900 + convertRomanToArabic(number.replaceFirst("CM",""));
	    if (number.startsWith("D")) return 500 + convertRomanToArabic(number.replaceFirst("D",""));
	    if (number.startsWith("CD")) return 400 + convertRomanToArabic(number.replaceFirst("CD",""));
	    if (number.startsWith("C")) return 100 + convertRomanToArabic(number.replaceFirst("C",""));
	    if (number.startsWith("XC")) return 90 + convertRomanToArabic(number.replaceFirst("XC",""));
	    if (number.startsWith("L")) return 50 + convertRomanToArabic(number.replaceFirst("L",""));
	    if (number.startsWith("XL")) return 40 + convertRomanToArabic(number.replaceFirst("XL",""));
	    if (number.startsWith("X")) return 10 + convertRomanToArabic(number.replaceFirst("X",""));
	    if (number.startsWith("IX")) return 9 + convertRomanToArabic(number.replaceFirst("IX",""));
	    if (number.startsWith("V")) return 5 + convertRomanToArabic(number.replaceFirst("V",""));
	    if (number.startsWith("IV")) return 4 + convertRomanToArabic(number.replaceFirst("IV",""));
	    if (number.startsWith("I")) return 1 + convertRomanToArabic(number.replaceFirst("I",""));
	    else return 0;
	}
	public static String countPages(String input)
	{
		String[] splitted;
		int sum = 0;
		int a = 0; int b = 0;
		if (input.indexOf(",")>-1)
		{
			splitted = input.split(",");
			for (int y=0; y<splitted.length; y++)
			{
				a=0;b=0;
				if (splitted[y].indexOf("-")>-1)
				{
					String[] temp = splitted[y].split("-");
					if (temp[0].replaceAll("[^0-9]+", "").length()==0)
					{
						a = Func.convertRomanToArabic(temp[0]);
					}
					else
					{
						a = Integer.parseInt(temp[0].replaceAll("[^0-9]+", ""));
					}
					if (temp[1].replaceAll("[^0-9]+", "").length()==0)
					{
						b = Func.convertRomanToArabic(temp[1]);
					}
					else
					{
						b = Integer.parseInt(temp[1].replaceAll("[^0-9]+", ""));
					}
					sum += b-a;
				}
				else if (splitted[y].startsWith("["))
				{
					if  (splitted[y].replaceAll("[^0-9]+", "").length()==0)
					{
						sum += Func.convertRomanToArabic(splitted[y].substring(1));
					}
					else
					{
						sum += Integer.parseInt(splitted[y].replaceAll("[^0-9]+", ""));
					}
				}
				else if  (splitted[y].replaceAll("[^0-9]+", "").length()==0)
				{
					sum += Func.convertRomanToArabic(splitted[y]);
				}
				else
				{
					sum += Integer.parseInt(splitted[y].replaceAll("[^0-9]+", ""));
				}
			}
			return ""+sum;
		}
		else if (input.indexOf(";")>-1)
		{
			splitted = input.split(";");
			return splitted[0];
		}
		else if (input.indexOf("-")>-1 || input.indexOf("–")>-1)
		{
			if (input.indexOf("-")>-1) splitted = input.split("-");
			else splitted = input.split("–");
			if (splitted[0].replaceAll("[^0-9]+", "").length()==0)
			{
				a = Func.convertRomanToArabic(splitted[0]);
			}
			else
			{
				a = Integer.parseInt(splitted[0].replaceAll("[^0-9]+", ""));
			}
			if (splitted[1].replaceAll("[^0-9]+", "").length()==0)
			{
				b = Func.convertRomanToArabic(splitted[1]);
			}
			else
			{
				b = Integer.parseInt(splitted[1].replaceAll("[^0-9]+", ""));
			}
			return a + "-" + b;
		}
		else if (input.replaceAll("[^0-9]+", "").length()>0) return input.replaceAll("[^0-9]+", "");
		return null;
	}
	public static String getNumberValueFromSeries(String input)
	{
		String current;
		String[] splitted;
		if (input!=null)
		{
			if (input.replaceAll("[0-9]+", "").length()>0)
			{
				if (input.indexOf(",")>-1)
				{
					return input.substring(0, input.indexOf(",")).replaceAll("[^0-9]+", "");
				}
				else if (input.indexOf("(")>-1)
				{
					if (input.indexOf("(")>0)
					{
						return input.substring(0, input.indexOf("(")).replaceAll("[^0-9]+", "");
					}
					else
					{
						return input.substring(input.indexOf(")"+1), input.indexOf(",")).replaceAll("[^0-9]+", "");
					}
				}
				else if (input.indexOf("/")>-1)
				{
					return input.substring(0, input.indexOf("/")).replaceAll("[^0-9]+", "");
				}
				else if (input.indexOf("-")>-1)
				{
					return input.substring(0, input.indexOf("-")).replaceAll("[^0-9]+", "");
				}
				else
				{
					return input.replaceAll("[^0-9]+", "");
				}
			}
			else return input;
		}
		return "";
	}
	public static boolean isApprovedByAuthority(int[][] cols, int date, String[][] points, String issn)
	{
		for (int x=0; x<cols.length; x++)
		{
			if (cols[x][1]==date)
			{
				for (int y=1; y<points.length; y++)
				{
					if (issn.equals(points[y][1]) && !points[y][cols[x][0]].equals("0"))
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	public static String convertCountryNameToISO(String country, String[][] table)
	{
		String out = country;//.replaceAll(" ", "");
		for (int x=1; x<table.length; x++)
		{
			if (table[x][0].equals(out) || table[x][1].equals(out)) return table[x][2].toUpperCase();
		}
		return "";
	}
	public static String[] findUniqueStrings(String[][] data, String prefix, int col){
		ArrayList<String> list = new ArrayList<String>();
		String[] units;
		boolean incl;
		for (int x=1; x<data.length; x++){
			incl = true;
			if (data[x][col].indexOf(prefix)>-1){
				units = data[x][col].split("\\|\\|");
				for (int z=0; z<units.length; z++){
					for (int y=0; y<list.size(); y++){
						if (units[z].equals(list.get(y))){
							incl = false;
							break;
						}
					}
					if (incl && units[z].indexOf(prefix)>-1){
						list.add(units[z]);
					}
				}
			}
		}
		String[] out = new String[list.size()];
		for (int x=0; x<list.size(); x++){
			out[x] = list.get(x);
		}
		return out;
	}
	public static boolean checkSimilaritiesLevel(String a, String b, float level){
		String[] text = a.split(" ");
		int count = 0;
		for (int x=0; x<text.length; x++){
			if (b.indexOf(text[x])>-1) count++;
		}
		if (text.length>5){
			if (count>(float)text.length*level) return true;
		}
		else {
			if (count>text.length/2+1) return true;
		}
		return false;
	}
	public static String translateTermOpenAccess(String term, boolean isJournal){
		if (term.equals("oryginalna wersja autorska (preprint)")) return "ORIGINAL_AUTHOR";
		else if (term.equals("ostateczna wersja autorska (postprint)")) return "FINAL_AUTHOR";
		else if (term.equals("ostateczna wersja wydawcy")) return "FINAL_PUBLISHED";
		
		else if (term.equals("przed opublikowaniem")) return "BEFORE_PUBLICATION";
		else if (term.equals("w momencie opublikowania")) return "AT_PUBLICATION";
		else if (term.equals("po opublikowaniu")) return "AFTER_PUBLICATION";
		
		else if (term.equals("otwarte czasopismo") && isJournal) return "OPEN_JOURNAL";
		else if (term.equals("otwarte czasopismo")) return "PUBLISHER_WEBSITE";
		else if (term.equals("otwarte repozytorium")) return "OPEN_REPOSITORY";
		else if (term.equals("inne")) return "OTHER";
		
		else return "";
	}
	public static Calendar getCalendarForCalculation(){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 1900);
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 0);
		return c;
	}
	public static String prepareConferenceDate(String date){
		String out = date.replaceFirst(" ", "").replaceAll("\\.", "-");
		String[] parts = out.split("-");
		if (parts[0].length()==4) return out;
		else if (parts.length==3 && parts[2].length()==4) return parts[2]+"-"+parts[1]+"-"+parts[0];
		else return "";
	}
}
