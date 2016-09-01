package CustomActions;

import PMainWindow.Func;
import PMainWindow.MetAnWindow;
import Records.RFL;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class SimpleSearch extends AbstractAction {
	public SimpleSearch(){}
	public void actionPerformed(ActionEvent event){
		search();
	}
	public static void search(){
		String[][] data = MetAnWindow.myIdentity.loadedCSV;
		String text = MetAnWindow.myIdentity.simpleSearchField.getText();
		String[][] fields = RFL.list();
		int[] cols = new int[fields.length];
		for (int x=0; x<fields.length; x++)
			cols[x] = Func.findColumn(data[0], fields[x][2]);
		ArrayList<String[]> list = new ArrayList<String[]>(1000);
		for (int x=1; x<data.length; x++){
			for (int y=0; y<cols.length; y++){
				if (data[x][cols[y]].indexOf(text)>-1){
					list.add(new String[]{
							data[x][cols[39]],
							data[x][cols[0]],
							data[x][cols[3]],
							data[x][cols[36]],
							data[x][cols[37]],
							data[x][cols[35]],
							data[x][cols[46]],
					});
					break;
				}
			}
		}
		String[][] out = Func.toArray(list);
		MetAnWindow.myIdentity.createTable(out, new String[]{
				fields[39][2],
				fields[0][2],
				fields[3][2],
				fields[36][2],
				fields[37][2],
				fields[35][2],
				fields[46][2],
		});
	}
}
