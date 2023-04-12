package dwc.bellview;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application entry point
 * 
 * @author Douglas Chesher
 *
 */
public class App extends Application {

	public static void main(String[] args) {
		//Manual logging configuration as Logback is unable to find
		//logback.xml configuration file
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(lc);
			// Call context.reset() to clear any previous configuration, e.g. default
			// configuration. For multi-step configuration, omit calling context.reset().
			lc.reset();
			configurator.doConfigure(App.class.getResourceAsStream("/logback.xml"));
		} catch (JoranException je) {
			StatusPrinter.print(lc);
		}
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Logger LOG = LoggerFactory.getLogger(App.class);
		LOG.info(MessageFormat.format(BellviewUtils.getMessage("msg.application.started"),
				BellviewUtils.getMessage("application.version")));
		FXMLLoader loader = new FXMLLoader();
		Parent root = loader.load(getClass().getResource("scene.fxml").openStream());
		// FXMLController controller = (FXMLController) loader.getController();
		Scene scene = new Scene(root, 800, 600);
		scene.getStylesheets().add(App.class.getResource("style.css").toExternalForm());
		primaryStage.setTitle("Bellview");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
