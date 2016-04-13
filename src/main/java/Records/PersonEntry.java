package Records;

import PMainWindow.Func;

public class PersonEntry {
	private String id;
	private String system;
	private String name;
	private String lastName;
	private String role;
	private String[][] affiliations; //unitID, unitName, date
	public PersonEntry(String person){
		if (person.indexOf(",")>-1){
			lastName = person.substring(0, person.indexOf(","));
		}
		else {
			lastName = person;
		}
		if (person.indexOf("SAP")>-1){
			id = person.substring(person.indexOf("[SAP")+4,person.indexOf("]"));
			name = person.substring(person.indexOf(", ")+2,person.indexOf("[SAP"));
			system = "SAP";
		}
		else if (person.indexOf("USOS")>-1){
			id = person.substring(person.indexOf("[USOS")+5,person.indexOf("]"));
			name = person.substring(person.indexOf(", ")+2,person.indexOf("[USOS"));
			system = "USOS";
		}
		else if (person.indexOf(",")>-1){
			name = person.substring(person.indexOf(", ")+2);
		}
	}
	@Override
	public String toString(){
		if (id!=null && !id.equals(""))
			return (lastName+", "+name+" ["+system+id+"]").replaceAll("  ", " ");
		return (lastName+", "+name).replaceAll("  ", " ");
	}
	public String toStringNames(){
		return (lastName+", "+name).replaceAll("  ", " ");
	}
	public String toStringDepartment(int x){
		if (affiliations!=null && affiliations.length>x && affiliations[x].length>1)
			return affiliations[x][1];
		else return "";
	}
	public String toStringDateOfAffiliation(int x){
		if (affiliations!=null && affiliations.length>x && affiliations[x].length>2)
			return affiliations[x][2];
		else return "";
	}
	public int countAffiliations(){
		if (affiliations==null) return 0;
		return affiliations.length;
	}
	public boolean isAffiliatedOrEmployed(){
		if (id!=null && !id.equals("")) return true;
		return false;
	}
	public boolean isSAP(){
		if (system!=null && system.equals("SAP")) return true;
		return false;
	}
	public boolean isUSOS(){
		if (system!=null && system.equals("USOS")) return true;
		return false;
	}
	/**
	 * Metoda dodaje jedn¹ afiliacje kluczem unitID, unitName, date.
	 * @param affiliation <code>String[] {unitID, unitName, date}</code>
	 */
	public void addAffiliation(String[] affiliation){
		String[][] out;
		if (affiliations==null){
			out = new String[1][];
			out[0] = affiliation;
		}
		else{
			out = new String[affiliations.length+1][];
			for (int x=0; x<affiliation.length; x++){
				out[x] = affiliation;
			}
			out[out.length-1] = affiliation;
		}
		affiliations = out;
	}
	/**
	 * Metoda dodaje szereg afiliacji kluczem unitID, unitName, date.
	 * @param affiliation <code>String[] {unitID, unitName, date}</code>
	 */
	public void addAffiliation(String[][] affiliation){
		String[][] out;
		if (affiliation==null){
			affiliations = affiliation;
		}
		else{
			affiliations = Func.mergeArrays(affiliations, affiliation);
		}
	}
	public void setRole(String role){
		this.role = role;
	}
	/**
	 * @return zwraca id bez przedrostków
	 */
	public String getID(){
		return id;
	}
	/**
	 * @return zwraca afiliacjê kluczem unitID, unitName, date.
	 * Je¿eli <code>z</code> przekracza indeks, zwraca null.
	 */
	public String[] getAffiliation(int z){
		if (z+1>affiliations.length) return null;
		return affiliations[z];
	}
}
