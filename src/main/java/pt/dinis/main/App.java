package pt.dinis.main;

import org.apache.log4j.Logger;

/**
 * Hello world!
 *
 */
public class App {
	final static Logger logger = Logger.getLogger(App.class);

    public static void main( String[] args ) {
		garbage();

		Dealer dealer = new Dealer();
		dealer.run();
    }

	private static void garbage(){
		logger.debug("Só para perceber o que está a acontecer");
		logger.info("Informações gerais");
		logger.warn("Ui, algo é muito estranho");
		logger.error("Não há nada que eu possa fazer, vou-me embora");
		try {
			// do nothing
			throw new Exception("Erro");
		} catch (Exception e) {
			logger.error("Também se pode imprimir com um erro", e);
		}
		logger.fatal("Morreu");

		System.out.println( "Hello World!" );
	}
}
