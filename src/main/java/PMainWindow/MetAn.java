package PMainWindow;

import javax.swing.*;
import java.awt.*;

public class MetAn
{
	public static void main(String[] args)
	{
		EventQueue.invokeLater(() -> {
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
        });
	}
}
