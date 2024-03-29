package com.aqupd.grizzlybear.utils;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class AqDebug {
    private AqDebug() {}

    private boolean loaded;
    public static final AqDebug INSTANCE = new AqDebug();
    private Properties aqdebug = new Properties();

    public boolean startDebug(boolean key) {
        if(key && !loaded) load();
        return(true);
    }

    private final File dfile = new File("./config/AqMods/biomes.config");

    private void load() {
        loaded = true;
        try {
            Files.createDirectories(Paths.get("./config/AqMods/"));
            var writer = new FileOutputStream(dfile);
            dfile.createNewFile();
            aqdebug.store(writer, "All Biometypes for spawnconfiguration");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
