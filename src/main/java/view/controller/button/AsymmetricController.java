package view.controller.button;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import encode.common.Main;
import encode.common.Warehouse;
import view.App;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class AsymmetricController implements Initializable {
    @FXML
    ComboBox<String> algoCombobox, keySizeCombobox, modeCombobox, paddingCombobox;
    @FXML
    TextField keyFileTextField, inputFileTextField, outputTextField;
    @FXML
    RadioButton isEncryptCB, isDecryptCB, isFile, isPlainText;
    @FXML
    Label keyFileLabel, keyTypeLabel, inputFileLabel, outputFileLabel;
    @FXML
    Button keyFileButton, inputFileButton, outputFileButton, startButton;
    @FXML
    FXCollections listItemCombo;
    @FXML
    ProgressBar progressBar;
    @FXML
    Pane pane1;
    ToggleGroup toggleGroup1, toggleGroup2;
    int modeOP = 1;
    boolean ifFile = true;
    String keyType = "File Key";

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        toggleGroup1 = new ToggleGroup();
        isEncryptCB.setToggleGroup(toggleGroup1);
        isDecryptCB.setToggleGroup(toggleGroup1);
        isEncryptCB.setSelected(true);
        toggleGroup1.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
                RadioButton radioButton = (RadioButton) toggleGroup1.getSelectedToggle();
                modeOP = (radioButton.getText().equals("Encrypt") ? 1 : 2);
            }
        });

        toggleGroup2 = new ToggleGroup();
        isPlainText.setToggleGroup(toggleGroup2);
        isFile.setToggleGroup(toggleGroup2);
        isFile.setSelected(true);
        toggleGroup2.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
                RadioButton radioButton = (RadioButton) toggleGroup2.getSelectedToggle();
                ifFile = (radioButton.getText().equals("File") ? true : false);
                if (ifFile) {
                    inputFileButton.setVisible(true);
                    outputFileButton.setText("Browse");
                    inputFileLabel.setText("Input File");
                    outputFileLabel.setText("Output Folder");
                    inputFileTextField.setEditable(false);
                } else {
                    inputFileButton.setVisible(false);
                    outputFileButton.setText("Coppy");
                    inputFileLabel.setText("Input Text");
                    outputFileLabel.setText("Output Text");
                    inputFileTextField.setEditable(true);
                }

            }
        });
        keySizeCombobox.getItems().addAll(Warehouse.listKeyTypeRSA);
        keySizeCombobox.getSelectionModel().selectFirst();
        algoCombobox.getItems().addAll(Warehouse.listASymmetricAlgo);
        algoCombobox.getSelectionModel().selectFirst();
        modeCombobox.getItems().addAll(Warehouse.listSymmetricMode);
        modeCombobox.getSelectionModel().selectFirst();
        paddingCombobox.getItems().addAll(Warehouse.listRSAPadding);
        paddingCombobox.getSelectionModel().selectFirst();

    }

    @FXML
    private void start(ActionEvent event) throws Exception {

        String fileInputPath = inputFileTextField.getText();
        String plainText = inputFileTextField.getText();
        String folderOuputPath = outputTextField.getText();
        String algorithm = (String) algoCombobox.getValue();
        String mode = (String) modeCombobox.getValue();
        String padding = (String) paddingCombobox.getValue();
        String keyContent = keyFileTextField.getText();

        Main main = new Main(fileInputPath, plainText, folderOuputPath, keyType, keyContent, algorithm, mode, padding,
                "", "cryptography", modeOP, ifFile);

        startButton.setDisable(true);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        progressBar.progressProperty().bind(main.progressProperty());
        main.setOnSucceeded(evt -> {
            if (!ifFile) {
                outputTextField.setText(main.getValue());
            }
            startButton.setDisable(false);
        });

        main.setOnFailed(evt -> {
            Alert alert = new Alert(AlertType.CONFIRMATION, main.getException().getMessage(), ButtonType.YES);
            alert.showAndWait();
            startButton.setDisable(false);

            main.getException().printStackTrace();
        });
        executorService.submit(main);

    }

    @FXML
    private void openKeyFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        FileChooser.ExtensionFilter extFilter = null;
        RadioButton modeOPSelected = (RadioButton) toggleGroup1.getSelectedToggle();
        if (modeOPSelected.getText().equals("Encrypt")) {
            extFilter = new FileChooser.ExtensionFilter("Public Key (*.public)", "*.public");
        } else {
            extFilter = new FileChooser.ExtensionFilter("Private Key (*.private)", "*.private");
        }
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(keyFileTextField.getParent().getScene().getWindow());
        if (file != null) {
            keyFileTextField.setText(file.getAbsolutePath());
        }

    }

    @FXML
    private void openInputFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(inputFileTextField.getParent().getScene().getWindow());
        if (file != null) {
            inputFileTextField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void openOutputFile(ActionEvent event) {
        if (outputFileButton.getText().equals("Browse")) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Open Resource File");
            File file = directoryChooser.showDialog(outputTextField.getParent().getScene().getWindow());
            if (file != null) {
                outputTextField.setText(file.getAbsolutePath());
            }
        } else {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(outputTextField.getText());
            content.putHtml("<b>Some</b> text");
            clipboard.setContent(content);
        }
    }

    @FXML
    private void keyTypeSelect(ActionEvent event) {
        try {
            keyType = keySizeCombobox.getValue().toString();
            if (keyType.equals("File Key")) {
                keyFileLabel.setText("Key File");
                keyFileButton.setVisible(true);
            } else {
                keyFileLabel.setText(keyType);
                keyFileButton.setVisible(false);
            }
        } catch (Exception e) {
        }
    }

    @FXML
    private void back(ActionEvent event) throws IOException {
        AnchorPane root = (AnchorPane) pane1.getParent();
        Pane pane = FXMLLoader.load(App.class.getResource("button/GenerateKey.fxml"));
        root.getChildren().remove(pane1);
        root.getChildren().add(pane);
        pane.setLayoutX(0);
        pane.setLayoutY(327);
    }

}