package pt.dinis.main;

import org.apache.log4j.Logger;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;

public class App {
    private final static Logger logger = Logger.getLogger(Dealer.class);

    private static Properties defaultProps, userProps;
    private static final String propsFileName = "config.properties";

    public static void main( String[] args ) throws IOException {
        Dealer dealer;

        try {
            defaultProps = prepareProperties(propsFileName);
        } catch (FileNotFoundException e) {
            Display.alert("Important files not found, this program will close now.");
            return;
        }

        userProps = prepareProperties(defaultProps.getProperty("userPropsFileName"), defaultProps);
        dealer = new Dealer(Integer.parseInt(userProps.getProperty("port")));
        dealer.start();
    }

    private static Properties prepareProperties(String fileName) throws IOException {
        Properties props = new Properties();

        try (FileInputStream in = new FileInputStream("./src/main/resources/" + fileName)) {
            props.load(in);
        } catch (FileNotFoundException e) {
            logger.fatal("Configuration file not found.");
            throw new FileNotFoundException();
        }

        return props;
    }

    private static Properties prepareProperties(String fileName, Properties defaultProps) throws IOException {
        Properties props = new Properties(defaultProps);

        try (FileInputStream in = new FileInputStream("./" + fileName)) {
            props.load(in);
        } catch (FileNotFoundException e) {
            logger.info("User's configuration file not found.");
        }

        return props;
    }

    /*
    tem que ser possível acrescentar propriedades de utilizador com a classe scanner
    poderá ser possível e apenas possível alterar a propriedade 'userPropsFileName' das propriedades padrão
     */
}
