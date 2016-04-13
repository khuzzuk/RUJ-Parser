package Records;

public class RecordRIS
{
	private String id, dcAdmins, doi;
	public RecordRIS(String id, String dcAdmins, String doi)
	{
		this.id = id;
		this.dcAdmins = dcAdmins;
		this.doi = doi;
	}
	public String[] getRecord()
	{
		String[] record = new String[3];
		record[0] = id;
		record[1] = dcAdmins;
		record[2] = doi;
		return record;
	}
	public String getID()
	{
		return id;
	}
	public String getAuthors()
	{
		return dcAdmins;
	}
	public String getDOI()
	{
		return doi;
	}
}