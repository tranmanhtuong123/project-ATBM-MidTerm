package view;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import view.controller.component.SplashScreen;

public class App extends Application {

    private Stage mainWindow;

    private static Scene scene;

    private Image splash_image = new Image(
            "https://previews.123rf.com/images/daniilphotos/daniilphotos1905/daniilphotos190500450/122482393-pink-matrix-digital-background-abstract-cyberspace-concept-characters-fall-down-matrix-from-symbols-.jpg",
            580, 650, false, false);

    public static void main(String[] args) {
        launch();

    }

    public void start(Stage stage) throws IOException {

        new SplashScreen(stage, splash_image, () -> {
            try {
                showGame();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void showGame() throws IOException {

        mainWindow = new Stage();
        // mainWindow.initStyle(StageStyle.UNDECORATED); // hide "exist" bar
        mainWindow.setResizable(false); // Disable maximize button and resizing window
        final Rectangle2D bounds = Screen.getPrimary().getBounds();
        mainWindow.setX(bounds.getMinX() + bounds.getWidth() / 2 - 580 / 2);
        mainWindow.setY(bounds.getMinY() + bounds.getHeight() / 2 - 650 / 2);

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Main.fxml"));
        // FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
        AnchorPane root = fxmlLoader.load();
        Pane pane = FXMLLoader.load(App.class.getResource("button/GenerateKey.fxml"));
        // Pane pane = FXMLLoader.load(getClass().getResource("button/GenerateKey.fxml"));
        root.getChildren().add(pane);
        pane.setLayoutX(0);
        pane.setLayoutY(327);
        scene = new Scene(root, 563, 610);

        root.requestFocus();
        mainWindow.setScene(scene);
        mainWindow.show();

    }

}