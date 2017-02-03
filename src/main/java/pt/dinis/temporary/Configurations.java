package pt.dinis.temporary;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * Created by diogo on 03-02-2017.
 */
public class Configurations {
    private static final Logger logger = Logger.getLogger(Configurations.class);

    private static Properties defaultProps, userProps;
    private static final String propsFileName = "config.properties";
    private static final String userPropsFileName = "userPropsFileName";

    public static void setPropertiesFromFile() throws IOException {
        defaultProps = prepareProperties(propsFileName);
        userProps = prepareProperties(defaultProps.getProperty(userPropsFileName), defaultProps);
    }

    public static String getProperty(String key) {
        return userProps.getProperty(key);
    }

    private static Properties prepareProperties(String fileName) throws IOException {
        Properties props = new Properties();

        try (FileInputStream in = new FileInputStream("./src/main/resources/" + fileName)) {
            props.load(in);
            logger.info("Load properties from default configurations file.");
        } catch (FileNotFoundException e) {
            logger.fatal("Default configuration file not found.");
            throw new FileNotFoundException();
        }

        return props;
    }

    private static Properties prepareProperties(String fileName, Properties defaultProps) throws IOException {
        Properties props = new Properties(defaultProps);

        try (FileInputStream in = new FileInputStream("./" + fileName)) {
            props.load(in);
            logger.info("Load properties from user's configurations file.");
        } catch (FileNotFoundException e) {
            logger.info("User's configuration file not found.");
        }

        return props;
    }

}
