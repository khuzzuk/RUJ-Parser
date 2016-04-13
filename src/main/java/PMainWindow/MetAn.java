package PMainWindow;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MetAn
{
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				}
				catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
				MetAnWindow startWindow = new MetAnWindow();
				startWindow.setTitle("RUJ - program do przetwarzania plików CSV");
				startWindow.setVisible(true);
			}
		});
	}
}
