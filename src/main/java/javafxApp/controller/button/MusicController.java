package javafxApp.controller.button;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
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
    @FXML
    private ProgressBar songProgressBar;

    private Media media;
    private MediaPlayer mediaPlayer;
    private Timer timer;
    private TimerTask task;
    private boolean running;

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

        songProgressBar.setStyle("-fx-accent: #00FF00;");
        playMedia();
    }

    public void playMedia() {
        beginTimer();
        // mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
        mediaPlayer.play();
    }

    public void checkCheckBox(ActionEvent event) {
        if (checkBoxVolume.isSelected()) {
            mediaPlayer.setVolume(0);
        } else {
            mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
        }
    }

    public void beginTimer() {

        timer = new Timer();

        task = new TimerTask() {

            public void run() {

                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                songProgressBar.setProgress(current / end);

                if (current / end == 1) {

                    cancelTimer();
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void cancelTimer() {

        running = false;
        timer.cancel();
    }

}
