package dwc.bellview;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application entry point
 * @author Douglas Chesher
 *
 */
public class App extends Application {
	private static final Logger LOG = LoggerFactory.getLogger(App.class);
	
    public static void main(String[] args) {
        launch(args);
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		LOG.info(MessageFormat.format(BellviewUtils.getMessage("msg.application.started"), BellviewUtils.getMessage("application.version")));
		FXMLLoader loader = new FXMLLoader();
		Parent root = loader.load(getClass().getResource("scene.fxml").openStream());
		//FXMLController controller = (FXMLController) loader.getController();
		Scene scene = new Scene(root,800,600);
		scene.getStylesheets().add(App.class.getResource("style.css").toExternalForm());
		primaryStage.setTitle("Bellview");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
