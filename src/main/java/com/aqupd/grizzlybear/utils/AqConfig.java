package com.aqupd.grizzlybear.utils;

import java.io.*;
import java.util.Properties;

public class AqConfig {
    private AqConfig() {}

    public static final AqConfig INSTANCE = new AqConfig();

    private boolean loaded;
    private final Properties aqprop = new Properties();


    public boolean getBooleanProperty(String key) {
        if (!loaded) load();
        return Boolean.parseBoolean(aqprop.getProperty(key));
    }
    public String getStringProperty(String key) {
        if (!loaded) load();
        return aqprop.getProperty(key);
    }
    public int getNumberProperty(String key) {
        if (!loaded) load();
        return Integer.parseInt(aqprop.getProperty(key));
    }
    public double getDoubleProperty(String key) {
        if (!loaded) load();
        return Double.parseDouble(aqprop.getProperty(key));
    }

    private final File file = new File("./config/AqMods/caracal.config");

    private void load() {
        loaded = true;
        try {
            new File("./config/AqMods").mkdir();

            if(file.exists()) {
                Reader reader = new FileReader(file);
                aqprop.load(reader);
                reader.close();
            } else {
                FileOutputStream writer = new FileOutputStream(file);
                file.createNewFile();
                aqprop.setProperty("config.version","1");
                aqprop.setProperty("debug","false");
                aqprop.setProperty("spawn.weight","50");
                aqprop.setProperty("spawn.min","2");
                aqprop.setProperty("spawn.max","4");
                aqprop.setProperty("entity.angertimemin","20");
                aqprop.setProperty("entity.angertimemax","39");
                aqprop.setProperty("entity.friendlytoplayer","false");
                aqprop.setProperty("entity.health","30.0");
                aqprop.setProperty("entity.speed","0.25");
                aqprop.setProperty("entity.follow","20.0");
                aqprop.setProperty("entity.damage","6.0");
                aqprop.setProperty("spawn.biomes","TAIGA");
                aqprop.store(writer, "Configuration file for Grizzly bear mod");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
