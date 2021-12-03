package javafxApp;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafxApp.controller.SplashScreen;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    private Image splash_image = new Image(
            "https://previews.123rf.com/images/daniilphotos/daniilphotos1905/daniilphotos190500450/122482393-pink-matrix-digital-background-abstract-cyberspace-concept-characters-fall-down-matrix-from-symbols-.jpg",
            563, 350, false, false);

    // public void start(Stage stage) throws IOException {
    // scene = new Scene(loadFXML("main.fxml"), 563, 350);
    // stage.setScene(scene);
    // stage.show();
    // }

    @Override
    public void start(Stage stage) throws IOException {
        new SplashScreen(stage, splash_image, () -> {

            try {
                scene = new Scene(loadFXML("main.fxml"), 563, 350);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(scene);
            stage.show();

        });

    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
        // return fxmlLoader.load();

        AnchorPane root = fxmlLoader.load();
        Pane pane = FXMLLoader.load(App.class.getResource("button/GenerateKey.fxml"));
        root.getChildren().add(pane);
        root.requestFocus();
        return root;

    }

    public static void main(String[] args) {
        launch();
    }

    // private Stage mainWindow;
    // private void showGame() throws IOException {

    // mainWindow = new Stage();
    // mainWindow.initStyle(StageStyle.UNDECORATED);
    // FXMLLoader fxmlLoader = new
    // FXMLLoader(MainAllToGet.class.getResource("component/view.fxml"));
    // Parent root = fxmlLoader.load();

    // BorderPane borderPane = new BorderPane();
    // ToolBar toolBar = new ToolBarHeader(borderPane);
    // Pane pane = new Header(toolBar, mainWindow);
    // borderPane.setTop(pane);
    // borderPane.setCenter(root);
    // Scene scene = new Scene(borderPane, 600, 590);
    // final Rectangle2D bounds = Screen.getPrimary().getBounds();
    // mainWindow.setX(bounds.getMinX() + bounds.getWidth() / 2 - 590 / 2);
    // mainWindow.setY(bounds.getMinY() + bounds.getHeight() / 2 - 600 / 2);

    // scene.getStylesheets().add(MainAllToGet.class.getResource("Css/mainCss.css").toExternalForm());
    // mainWindow.setScene(scene);
    // mainWindow.show();
    // root.requestFocus();
    // }

}