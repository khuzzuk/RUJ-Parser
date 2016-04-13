package PMainWindow;

import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class GenericProgressBar extends JProgressBar implements PropertyChangeListener
{
	private JProgressBar progressBar;
	private JButton cancelButton;
	//private Duplicates task;
	private Task task;
	private String[][] loadedCSV;
	private String[][] duplicatesTable;
	private Duplicates findingResult;
	private MetAnWindow startWindow;
	private int progress;
	
	class Task extends SwingWorker<Void, Void>
	{
		@Override
		public Void doInBackground() throws IOException, InterruptedException
		{
			progress = 0;
			setProgress(0);
			for (int i=0; i<=100; i++) progress = i; setProgress(progress); Thread.sleep(1000); System.out.println(progress);
			return null;
		}
		public void done()
		{
			Toolkit.getDefaultToolkit().beep();
			//startWindow.createTable(findingResult.getDuplicatesTable());
		}
		//createTable(findingResult.getDuplicatesTable());
	}
	public GenericProgressBar() throws IOException
	{
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setIndeterminate(true);
		task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
	}
	public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setIndeterminate(false);
            progressBar.setValue(progress);
        }
    }
}
