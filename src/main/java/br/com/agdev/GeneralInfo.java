package br.com.agdev;

import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GeneralInfo {
    public static final int FREQUENCY = 10;
    public static final int INTERVAL_BETWEEN_CLICKS = 2;
    public static final Path RESOURCES = Paths.get(System.getProperty("user.dir"), "src", "main", "resources");

    public static final int SAMSUNG = 0;
    public static final int NOTEBOOK = 1;

    public static final Region NOTEBOOK_REGION = Region.create(Screen.getMonitor(NOTEBOOK));
    public static final Region SAMSUNG_REGION = Region.create(Screen.getMonitor(SAMSUNG));
}
