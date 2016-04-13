package PMainWindow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class MetAnSettings
{
	private File settingsFile;
	private Properties defaultSettings;
	private Properties settings;

	public MetAnSettings()
	{
		String userDir = System.getProperty("user.home");
		File settingsDir = new File(userDir, ".MetAn");
		if (!settingsDir.exists()) settingsDir.mkdir();
		settingsFile = new File(settingsDir, "program.properties");
		defaultSettings = new Properties();
		defaultSettings.put("left", "0");
		defaultSettings.put("top", "0");
		defaultSettings.put("width", "600");
		defaultSettings.put("height", "400");
		defaultSettings.put("windowMaximized", "0");
		defaultSettings.put("lastFilePath", ".");
		defaultSettings.put("lastSavePath", ".");
		
		settings = new Properties(defaultSettings);
		if (settingsFile.exists())try
		{
			FileInputStream settingsIn = new FileInputStream(settingsFile);
			settings.load(settingsIn);
			settingsIn.close();
		} catch (IOException e){e.printStackTrace();}
	}
	public Properties getSettings()
	{
		return settings;
	}
	public void setSettings(Properties newSettings)
	{
		settings = newSettings;
	}
	public void saveSettings(MetAnWindow window, int x, int y, int w, int h) throws IOException
	{
		if (window.getExtendedState()!=6)
		{
			settings.put("left", ""+x);
			settings.put("top", ""+window.getY());
			settings.put("width", ""+window.getWidth());
			settings.put("height", ""+window.getHeight());
		}
		settings.put("windowMaximized", Integer.toString(window.getExtendedState()));
		FileOutputStream savingFile = new FileOutputStream(settingsFile);
		settings.store(savingFile, "Ustawienia");
		System.exit(0);

	}
}