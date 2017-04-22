package pt.dinis.common.core;

import java.io.*;
import java.net.URLClassLoader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * Created by diogo on 03-02-2017.
 */
public class Configurations {
    private static final Logger logger = Logger.getLogger(Configurations.class);

    private static Properties properties;
    private static final String PROPERTIES_FILE_NAME = "application.conf";

    public static void setPropertiesFromFile() throws IOException {
        properties = loadDefaultProperties(PROPERTIES_FILE_NAME);
    }

    public static void setPropertiesFromFile(String propertyName, boolean mandatory) throws IOException {
        setPropertiesFromFile();
        try {
            loadUserProperties(propertyName);
        } catch (Exception e) {
            if (mandatory) {
                throw e;
            }
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    private static Properties loadDefaultProperties(String fileName) throws IOException {
        Properties properties = new Properties();

        List<File> files = getFilesInClassPath(fileName);
        if (files.isEmpty()) {
            throw new IOException("No file '" + fileName + "' found");
        }

        for (File file: files) {
            try (FileInputStream in = new FileInputStream(file)) {
                properties.load(in);
                logger.info("Load properties from default configurations file: " + file.getCanonicalPath());
            } catch (FileNotFoundException e) {
                logger.fatal("Default configuration file ('" + fileName + "') not found.");
                Display.alert("Properties not found.");
                throw new IOException();
            } catch (IOException e) {
                logger.fatal("Can't connect with standard input stream.");
                Display.alert("I/O problems.");
                throw new IOException();
            }
        }

        return properties;
    }

    static void loadUserProperties(String propertyName) throws IOException {
        // user's properties
        String userFileName = "./" + properties.getProperty(propertyName);
        try (FileInputStream in = new FileInputStream(userFileName)) {
            properties.load(in);
            logger.info("Load properties from user's configurations file: " + userFileName );
        } catch (FileNotFoundException e) {
            logger.info("User's configuration file not found.");
            throw e;
        }
    }

    static private List<File> getFilesInClassPath(String fileName) {

        List<File> files = new ArrayList<>();
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader)cl).getURLs();
        for(URL url: urls){
            File file = new File(url.getFile() + fileName);
            if (file.exists()) {
                files.add(file);
            }
        }

        return files;
    }

}
