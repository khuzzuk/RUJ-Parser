package Functions;

import java.util.Comparator;

import javax.swing.JOptionPane;

public class CompareRecordsInArray implements Comparator<String[]>{
	public int compare(String[]a, String[] b){
		try{
			/*if (a[0].indexOf("id")>-1) return 1;
			if (b[0].indexOf("id")>-1) return -1;*/
			int A = Integer.parseInt(a[0]);
			int B = Integer.parseInt(b[0]);
			return A-B;
		}
		catch (NumberFormatException e){
			new JOptionPane("Nieprawid³owy id rekordu w porównaniu: "+a[0]+" - "+b[0]);
			return -1;
		}
	}
}
