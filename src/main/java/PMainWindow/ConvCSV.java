package PMainWindow;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JOptionPane;

class ConvCSV
{
	private String[][] indA;

	public ConvCSV(String[][] input)
	{
		int a = input.length - 1;
		int b = input[0].length - 1;
		String dcList[] = new String [19];
		int c = dcList.length-1;
		int kolInd[] = new int[c+1];
		dcList[0] = "dc.title[pl]";
		dcList[1] = "dc.description.physical[pl]";
		dcList[2] = "dc.date.issued[pl]";
		dcList[3] = "dc.contributor.author[pl]";
		dcList[4] = "dc.contributor.editor[pl]";
		dcList[5] = "dc.identifier.isbn[pl]";
		dcList[6] = "dc.identifier.eisbn[pl]";
		dcList[7] = "dc.identifier.issn[pl]";
		dcList[8] = "dc.identifier.eissn[pl]";
		dcList[9] = "dc.title.journal[pl]";
		dcList[10] = "dc.title.container[pl]";
		dcList[11] = "dc.description.volume[pl]";
		dcList[12] = "dc.description.number[pl]";
		dcList[13] = "dc.title.volume[pl]";
		dcList[14] = "dc.identifier.uri";
		dcList[15] = "dc.identifier.uri[]";
		dcList[16] = "dc.type[]";
		dcList[17] = "dc.subtype[]";
		dcList[18] = "id";
		for (int x=0; x<=c; x++)
		{
			for (int y=0; y<b; y++)
			if (input[0][y].equals(dcList[x]))
			{
				kolInd[x] = y;
				break;
			}
		}
		//Wyeliminowanie Redakcji czasopisma
		int aMinus = 0;
		int xa = 0;
		int ya;
		for (int x=1; x<a; x++)
		{
			if (input[x][kolInd[16]].equals("JournalEditorship") || input[x][kolInd[0]].length()==0) aMinus++;
		}
		indA = new String[a-aMinus-1][c+5];
		for (int x=1; x<a; x++)
		{
			if (!input[x][kolInd[16]].equals("JournalEditorship") && input[x][kolInd[0]].length()>0)
			{
				indA[xa][indA[0].length-4] = input[x][kolInd[0]];
				ya = 0;
				for (int y=0; y<14; y++)
				{
					indA[xa][ya] = input[x][kolInd[y]];
					indA[xa][ya] = indA[xa][ya].replace(" ","");
					indA[xa][ya] = indA[xa][ya].replace(",","");
					indA[xa][ya] = indA[xa][ya].replace(".","");
					indA[xa][ya] = indA[xa][ya].replace(":","");
					indA[xa][ya] = indA[xa][ya].replace(";","");
					indA[xa][ya] = indA[xa][ya].replace("-","");
					indA[xa][ya] = indA[xa][ya].replace("(","");
					indA[xa][ya] = indA[xa][ya].replace(")","");
					indA[xa][ya] = indA[xa][ya].replace("[SAP","");
					indA[xa][ya] = indA[xa][ya].replace("[","");
					indA[xa][ya] = indA[xa][ya].replace("]","");
					indA[xa][ya] = indA[xa][ya].toLowerCase();
					ya++;
				}
				for (int y=14; y<=c;y++) {indA[xa][ya] = input[x][kolInd[y]]; ya++;}
				xa++;
			}
		}
		int p;
		int[] fX = new int[3];
		int[] fY = new int[3];
		double[] Sxy = new double[3];
		for (int x=0; x<xa; x++)
		{
			for (int y=0; y<3; y++) fY[y] = 0;
			fX[0] = indA[x][0].length();
			for (int z=0; z<fX[0]; z++)
			{
				fY[0] += indA[x][0].charAt(z);
				//indA[x][indA.length-4] += p;
			}
			fX[1] = indA[x][1].length();
			for (int z=1; z<fX[1]; z++)
			{
				fY[1] += indA[x][1].charAt(z);
			}
			fX[2] = 0;
			for (int y=2; y<14; y++)
			{
				p = indA[x][y].length();
				fX[2] += p;
				for (int z=1; z<p; z++)
				{
					fY[2] += indA[x][y].charAt(z); 
				}
			}
			//Obliczanie wspó³rzêdnych punktu S
			/*Sxy[0] = ((((fY[0]+fY[1])/2)-(-fX[0]-fX[1])/((2*(fY[1]-fY[0]))/(fX[1]-fX[0])))-(((fY[1]+fY[2])/2)-((fX[1]-fX[2])/2*((fY[2]-fY[1])/(fX[2]-fX[1])))))/((-1/((fY[2]-fY[1])/(fX[2]-fX[1])))-(-1/((fY[1]-fY[0])/(fX[1]-fX[0]))));
			Sxy[1] = (-Sxy[0]/(fY[1]-fY[0])/(fX[1]-fX[0]))+((fY[0]+fY[1])/2-(-fX[1]-fX[1])/2*(fY[1]-fY[0])/(fX[1]-fX[0]));
			Sxy[2] = Math.sqrt(Math.pow(Sxy[0], 2)+Math.pow(Sxy[1], 2));
			indA[x][indA[0].length-5] = new DecimalFormat("#").format(Sxy[0]);
			indA[x][indA[0].length-4] = new DecimalFormat("#").format(Sxy[1]);
			indA[x][indA[0].length-3] = new DecimalFormat("#").format(Sxy[2]);*/
			
			Sxy[0] = (fX[0]+fX[1]+fX[2])/3;
			Sxy[1] = (fY[0]+fY[1]+fY[2])/30;
			Sxy[2] = Math.sqrt(Math.pow(Sxy[0], 2)+Math.pow(Sxy[1], 2));
			indA[x][indA[0].length-3] = new DecimalFormat("#").format(Sxy[0]);
			indA[x][indA[0].length-2] = new DecimalFormat("#").format(Sxy[1]);
			indA[x][indA[0].length-1] = new DecimalFormat("#").format(Sxy[2]);
		}
		//pouk³adaj wyniki wzglêdem punktu S
		/*Arrays.sort(indA, new Comparator<String[]>(){
		    @Override
		    public int compare(final String[] first, final String[] second){
		        return Integer.valueOf(second[indA[0].length-1]).compareTo(
		            Integer.valueOf(first[indA[0].length-1])
		        );
		    }
		});*/
		Arrays.sort(indA, new CompareVector());
	}
	public String[][] getData()
	{
		return indA;
	}
	public class CompareVector implements Comparator<String[]>
	{
		@Override
		public int compare(String[] a, String[] b)
		{
			try
			{
				int A = Integer.parseInt(a[a.length-1]);
				int B = Integer.parseInt(b[b.length-1]);
				return A-B;
			}
			catch (NumberFormatException e)
			{
				JOptionPane message = new JOptionPane(a[0] + "\n" + b[0]);
				return -1;
			}
		}
	}
}