package pt.dinis.temporary;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;
import pt.dinis.common.Display;

/**
 * Created by diogo on 03-02-2017.
 */
public class Configurations {
    private static final Logger logger = Logger.getLogger(Configurations.class);

    private static Properties properties;
    private static final String PROPERTIES_FILE_NAME = "application.conf";

    public static void setPropertiesFromFile() throws IOException {
        properties = loadProperties(PROPERTIES_FILE_NAME);
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    private static Properties loadProperties(String fileName) throws IOException {
        Properties properties = new Properties();

        try (FileInputStream in = new FileInputStream("./src/main/resources/" + fileName)) {
            properties.load(in);
            logger.info("Load properties from default configurations file.");
        } catch (FileNotFoundException e) {
            logger.fatal("Default configuration file ('application.conf') not found.");
            Display.alert("Properties not found.");
            throw new IOException();
        } catch (IOException e) {
            logger.fatal("Can't connect with standard input stream.");
            Display.alert("I/O problems.");
            throw new IOException();
        }

        // user's properties
        try (FileInputStream in = new FileInputStream("./" + properties.getProperty("user.properties.file.name"))) {
            properties.load(in);
            logger.info("Load properties from user's configurations file.");
        } catch (FileNotFoundException e) {
            logger.info("User's configuration file not found.");
        }

        return properties;
    }
}
