package javafxApp.controller.button;

import java.net.URL;
import java.util.ResourceBundle;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafxApp.App;

public class MusicController implements Initializable {

    @FXML
    private Button buttonMain;
    @FXML
    private Slider volumeSlider;
    @FXML
    private AnchorPane primaryStagePane;
    @FXML
    private BorderPane mainAll;
    @FXML
    private Pane pane;
    @FXML
    private CheckBox checkBoxVolume;
    @FXML
    private Text nameOfFileEncryptChosen, nameOfFileDecryptChosen, nameOfFolderEncryptChosen, nameOfFolderDecryptChosen;
    @FXML
    private ImageView imagePlay;
    @FXML
    private FontIcon audioControl;

    private Media media;
    private MediaPlayer mediaPlayer;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        URL url = App.class.getResource("music/Alone.mp3");
        media = new Media(url.toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.seek(Duration.ZERO);
            }
        });
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                if (checkBoxVolume.isSelected() == false) {
                    mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
                }
            }
        });
        playMedia();
    }

    public void playMedia() {
        // mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
        mediaPlayer.play();
    }

    public void checkCheckBox(ActionEvent actionEvent) {
        if (checkBoxVolume.isSelected()) {
            mediaPlayer.setVolume(0);
        } else {
            mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
        }
    }
}
