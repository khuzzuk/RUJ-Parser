package Interfaces;

import javax.swing.JProgressBar;

public interface CSVDataHolder {
	JProgressBar progressBar = new JProgressBar(1,100);
	public void reportLoadedFile(String[][] inputData);
	public String[][] getCSVData();
	public void startAction();
	public void endAction();
}
