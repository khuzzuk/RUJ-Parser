package OtherExports;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;

public class RTFieldRepair {
	public static void repair(File file) throws IOException, FileNotFoundException{
		BufferedReader tableRead = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		StringBuilder builder = new StringBuilder();
		String line;
		while ((line = tableRead.readLine())!=null){
			builder.append(line);
		}
		tableRead.close();
		line = builder.toString();
		line=line.replaceAll(" ", "").replaceAll("	", "");
		String subString;
		String line2 = "";
		char c;
		for (int x=0; x<line.length(); x++){
			x=getCharacter(line, x, false);
			if (x<line.length())
				line2 = line2+line.charAt(x);
		}
		String[] persons = line2.split(",");
		String[] names;
		ArrayList<String> list = new ArrayList<String>();
		list.ensureCapacity(3000);
		for (int x=0; x<persons.length; x++){
			if (persons[x].indexOf(".")>-1){
				names=persons[x].split("\\.");
				list.add(sortNames(names));
			}
		}
		String out = list.get(0);
		for (int x=1; x<list.size(); x++){
			out = out+"—"+list.get(x);
		}
		Writer export = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
		export.append(out);
		//return out;
	}
	private static int getCharacter(String line, int x, boolean inSuppression){
		if (x==line.length()) return x;
		if (inSuppression){
			if (line.charAt(x)==44) return x; //c==44 c jest przecinkiem
		}
		String sub = line.substring(x, x+1).replaceAll("[0-9]", "");
		if (sub.length()==0){
			return getCharacter(line, x+1, true);
		}
		else return x;
	}
	private static String sortNames(String[] names){
		String out = names[names.length-1];
		for (int x=0; x<names.length-1; x++){
			out = out+" "+names[x]+".";
		}
		return out;
	}
}
