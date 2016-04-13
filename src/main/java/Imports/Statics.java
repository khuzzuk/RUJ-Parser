package Imports;

public class Statics {
	public final static String[][] facUSOS = new String[][]{
		{"50000136","WH","Wydzia³ Historyczny"},
		{"50000145","WSM","Wydzia³ Studiów Miêdzynarodowych i Politycznych"},
		{"50000143","WBt", "Wydzia³ Biochemii, Biofizyki i Biotechnologii"},
		{"50000141","WCh", "Wydzia³ Chemii"},
		{"50000140","WMI", "Wydzia³ Matematyki i Informatyki"},
		{"50000138","WPl","Wydzia³ Polonistyki"},
		{"50000134","WPA", "Wydzia³ Prawa i Administracji"},
		{"50000135","WFz", "Wydzia³ Filozoficzny"},
		{"50000137","WF","Wydzia³ Filologiczny"},
		{"50000139","WFAI","Wydzia³ Fizyki, Astronomii i Informatyki Stosowanej"},
		{"50000144","WZ", "Wydzia³ Zarz¹dzania i Komunikacji Spo³ecznej"},
		{"50000142","WBl","Wydzia³ Biologii i Nauk o Ziemi"}
	};
	/**
	 * 
	 * @param id - numer jednostki, zaczynaj¹cy siê od "50000..."
	 * @return Zwraca Pe³n¹ nazwê wydzia³u na podstawie id. Zwraca pusty String, je¿eli nie znajdzie id.
	 */
	public static String findDepartmentName(String id){
		for (int x=0; x<facUSOS.length; x++){
			if (facUSOS[x][0].equals(id)) return facUSOS[x][2];
		}
		return "";
	}
}