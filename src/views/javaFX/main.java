package views.javaFX;

import java.io.IOException;

import engine.GameManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class main extends Application {
	  
	private Stage primaryStage;
	private GridPane rootLayout;
    private GameManager gameManager = new GameManager();
//	WordiadaController controller = new WordiadaController(gameManager); 
    GameController controller; 

	@Override
	public void start(Stage primaryStage) {
        try {
        	 this.primaryStage = primaryStage;
             this.primaryStage.setTitle("Wordiada");
             controller = new GameController(primaryStage);

            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("Overview.fxml"));
            loader.setController(controller);
            rootLayout = (GridPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
        	System.out.println(e.getCause());
        	System.out.println(e.getMessage());
            e.printStackTrace();
        }
	}

	public static void main(String[] args) {
		try {
			launch(args);
		} catch (Exception e) {
			int a = 4;
			a = 2 + 4;
			Math.abs(a);
//			System.out.println(e.getCause());
//			System.out.println(e.getMessage());
		}
	}
}