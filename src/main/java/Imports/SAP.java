package Imports;

import java.io.IOException;

public class SAP {
	public static String[][] SAP;
	private static int mid; 
	/**
	 * Metoda nie mo¿e mieæ zmiennej SAP=null. Algorytm wyszukiwania numeru osobowego z O=log_2n.
	 * @param id
	 * @return String[7][] z wpisami osób z SAP
	 */
	public static String[][] findPerson(String id){
		mid = SAP.length/2-1;
		int pos = lookAt(3,SAP.length, Integer.parseInt(id));
		if (pos==0) return null;
		String[][] list = new String[7][];
		int a=0;
		for (int x=-3; x<3; x++){
			if (SAP[pos+x][0].equals(id)){
				list[a] = SAP[pos+x];
				a++;
			}
		}
		return list;
	}
	private static int lookAt(int min, int max, int id){
		if (max<min) return 0;
		mid = (max-min)/2+min;
		if (mid==SAP.length) return 0;
		int check = Integer.parseInt(SAP[mid][0]);
		if (id<check){
			return lookAt(min, mid-1, id);
		}
		if (id>check){
			return lookAt(mid+1,max,id);
		}
		else return mid;
	}
}
