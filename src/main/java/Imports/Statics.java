package Imports;

public class Statics {
	public final static String[][] facUSOS = new String[][]{
		{"50000136","WH","Wydzia� Historyczny"},
		{"50000145","WSM","Wydzia� Studi�w Mi�dzynarodowych i Politycznych"},
		{"50000143","WBt", "Wydzia� Biochemii, Biofizyki i Biotechnologii"},
		{"50000141","WCh", "Wydzia� Chemii"},
		{"50000140","WMI", "Wydzia� Matematyki i Informatyki"},
		{"50000138","WPl","Wydzia� Polonistyki"},
		{"50000134","WPA", "Wydzia� Prawa i Administracji"},
		{"50000135","WFz", "Wydzia� Filozoficzny"},
		{"50000137","WF","Wydzia� Filologiczny"},
		{"50000139","WFAI","Wydzia� Fizyki, Astronomii i Informatyki Stosowanej"},
		{"50000144","WZ", "Wydzia� Zarz�dzania i Komunikacji Spo�ecznej"},
		{"50000142","WBl","Wydzia� Biologii i Nauk o Ziemi"}
	};
	/**
	 * 
	 * @param id - numer jednostki, zaczynaj�cy si� od "50000..."
	 * @return Zwraca Pe�n� nazw� wydzia�u na podstawie id. Zwraca pusty String, je�eli nie znajdzie id.
	 */
	public static String findDepartmentName(String id){
		for (int x=0; x<facUSOS.length; x++){
			if (facUSOS[x][0].equals(id)) return facUSOS[x][2];
		}
		return "";
	}
}