package Inspector;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import PMainWindow.Func;
import PMainWindow.MetAnWindow;

public class ConferenceIndex extends SwingWorker<String, Integer> {
	private String[][] data, index;
	private JProgressBar progressBar;
	private String[][] out;
	public ConferenceIndex(String[][] data, String[][] index, JProgressBar progressBar){
		this.data = data;
		this.index = index;
		this.progressBar = progressBar;
		}
	public String doInBackground(){
		ArrayList<String[]> list = new ArrayList<String[]>();
		int colTitleCont = Func.findColumn(data[0], "dc.title.container[pl]");
		int colTitle = Func.findColumn(data[0], "dc.title[pl]");
		int colConference = Func.findColumn(data[0], "dc.conference[pl]");
		int colSubtype = Func.findColumn(data[0], "dc.subtype[pl]");
		int colAff = Func.findColumn(data[0], "dc.affiliation[pl]");
		//String[] dataIndex = convertData(data);
		//String[] conferenceTitles = convertConferenceIndex(index);
		String[] conference;
		String[] record;
		String date, date2;
		progressBar.setMaximum(data.length);
		progressBar.setIndeterminate(false);
		for (int x=0; x<data.length; x++){
			if (x%100==0) publish(x);
			if (data[x][colSubtype].equals("ConferenceProceedings")){
				conference = data[x][colConference].split(";");
				if (conference.length>2 /*&& data[x][colAff].indexOf("Matematyki")>-1*/){
					for (int y=0; y<index.length; y++){
						if (/*(conference[1].equals(index[y][3]) && conference[2].equals(index[y][4])) &&
								*/Func.checkSimilaritiesLevel(conference[0], index[y][0], 0.5F)){
							record = new String[3];
							record[0] = data[x][0];
							record[1] = data[x][colConference];
							record[2] = data[x][colTitle];
							list.add(record);
							break;
						}
					}
				}
			}
		}
		out = new String[list.size()][];
		for (int x=0; x<list.size(); x++){
			out[x] = list.get(x);
		}
		return "done";
	}
	private static String[] convertData(String[][] data){
		int colConference = Func.findColumn(data[0], "dc.conference[pl]");
		String[] out = new String[data.length];
		for (int x=0; x<out.length; x++){
			out[x] = data[x][colConference];
			out[x] = out[x].replace(" ","");
			out[x] = out[x].replace(",","");
			out[x] = out[x].replace(".","");
			out[x] = out[x].replace(":","");
			out[x] = out[x].replace("-","");
			out[x] = out[x].replace("(","");
			out[x] = out[x].replace(")","");
			out[x] = out[x].replace("[SAP","");
			out[x] = out[x].replace("[","");
			out[x] = out[x].replace("]","");
			out[x] = out[x].toLowerCase();
		}
		return out;
	}
	private static String[] convertConferenceIndex(String[][] data){
		String[] out = new String[data.length];
		for (int x=0; x<out.length; x++){
			out[x] = data[x][0];
			out[x] = out[x].replace(" ","");
			out[x] = out[x].replace(",","");
			out[x] = out[x].replace(".","");
			out[x] = out[x].replace(":","");
			out[x] = out[x].replace("-","");
			out[x] = out[x].replace("(","");
			out[x] = out[x].replace(")","");
			out[x] = out[x].replace("[SAP","");
			out[x] = out[x].replace("[","");
			out[x] = out[x].replace("]","");
			out[x] = out[x].toLowerCase();
		}
		return out;
	}
	protected void process(List<Integer> progress)
	{
		progressBar.setValue(progress.get(progress.size()-1));
	}
	protected void done(){
		Toolkit.getDefaultToolkit().beep();
		MetAnWindow.myIdentity.conferencesShowTable(out);
	}
	public static boolean chechWoSIndexing(String[][] data, String input){
		for (int x=0; x<data.length; x++){
			if (data[x][0].indexOf(input)>-1) return true;
			if (data[x][6].indexOf(input)>-1) return true;
			if (data[x][7].indexOf(input)>-1) return true;
		}
		return false;
	}
}
