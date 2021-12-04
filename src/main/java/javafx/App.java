package javafx;

import java.io.IOException;

import javafx.application.Application;
import javafx.controller.component.SplashScreen;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private Stage mainWindow;

    private static Scene scene;

    private Image splash_image = new Image(
            "https://previews.123rf.com/images/daniilphotos/daniilphotos1905/daniilphotos190500450/122482393-pink-matrix-digital-background-abstract-cyberspace-concept-characters-fall-down-matrix-from-symbols-.jpg",
            570, 400, false, false);

    // public void start(Stage stage) throws IOException {
    // scene = new Scene(loadFXML("main.fxml"), 563, 350);
    // stage.setScene(scene);
    // stage.show();
    // }

    // public static void setRoot(String fxml) throws IOException {
    // scene.setRoot(loadFXML(fxml));
    // }

    private static Parent loadFXML(String fxml) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
        // return fxmlLoader.load();

        AnchorPane root = fxmlLoader.load();
        Pane pane = FXMLLoader.load(App.class.getResource("button/GenerateKey.fxml"));
        root.getChildren().add(pane);
        pane.setLayoutX(0);
        pane.setLayoutY(26);
        root.requestFocus();
        return root;

    }

    public static void main(String[] args) {
        launch();
    }

    public void start(Stage stage) throws IOException {
        new SplashScreen(stage, splash_image, () -> {

            try {
                showGame("main.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

    }

    private void showGame(String fxml) throws IOException {

        mainWindow = new Stage();
        // mainWindow.initStyle(StageStyle.UNDECORATED); // an thanh bar
        scene = new Scene(loadFXML(fxml), 563, 367);

        final Rectangle2D bounds = Screen.getPrimary().getBounds();
        mainWindow.setX(bounds.getMinX() + bounds.getWidth() / 2 - 580 / 2);
        mainWindow.setY(bounds.getMinY() + bounds.getHeight() / 2 - 400 / 2);

        mainWindow.setScene(scene);
        mainWindow.show();

    }

}