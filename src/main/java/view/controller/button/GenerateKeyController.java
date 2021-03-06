package view.controller.button;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import encode.common.Main;
import encode.common.Warehouse;
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
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import view.App;

public class GenerateKeyController implements Initializable {
    @FXML
    ComboBox<String> algoCombobox, keySizeCombobox, typeCombobox, keyLengthComboBox;
    @FXML
    TextField keyFileTextField, inputFileTextField, outputTextField;
    @FXML
    Label keyFileLabel, keyTypeLabel, inputFileLabel, outputFileLabel, keyLengthLabel;
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
    String keyType = "File Key";

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        typeCombobox.getItems().addAll(Warehouse.listGenerateKey);
        typeCombobox.getSelectionModel().selectFirst();
        keySizeCombobox.getItems().addAll(Warehouse.listKeyTypeRSA);
        keySizeCombobox.getSelectionModel().selectFirst();
        algoCombobox.getItems().addAll(Warehouse.listASymmetricAlgo);
        algoCombobox.getSelectionModel().selectFirst();
        keyLengthComboBox.getItems().addAll(Warehouse.listKeyLengthASYM);
        keyLengthComboBox.getSelectionModel().selectFirst();

    }

    @FXML
    private void start(ActionEvent event) throws Exception {

        String keyContent = inputFileTextField.getText();
        String folderOutputPath = outputTextField.getText();
        String algorithm = (String) algoCombobox.getValue();
        String type = (String) typeCombobox.getValue();

        Main main = new Main("", "", folderOutputPath, keyType, keyContent, algorithm, "", "", type, "generate Key",
                modeOP, false);

        progressBar.progressProperty().bind(main.progressProperty());
        startButton.setDisable(true);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        main.setOnSucceeded(evt -> {
            progressBar.setStyle("-fx-accent: palegreen");
            if (!main.getValue().equals("")) {
                outputTextField.setText(main.getValue());
            } else {
                outputTextField.setText("Completed!");
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
            extFilter = new FileChooser.ExtensionFilter("Public Key (*.public )", "*.public ");
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
                inputFileLabel.setVisible(true);
                inputFileTextField.setVisible(true);
                inputFileLabel.setText("File Key Name");
                outputFileLabel.setText("File Output Name");
                outputFileButton.setText("Browse");

            } else {
                if (keyType.equals("PlainText")) {
                    inputFileLabel.setVisible(false);
                    inputFileTextField.setVisible(false);
                } else {
                    inputFileLabel.setVisible(true);
                    inputFileTextField.setVisible(true);
                }

                inputFileLabel.setText("PlainText");
                outputFileLabel.setText("Result");
                outputFileButton.setText("Copy");
            }
        } catch (Exception e) {
        }
    }

    @FXML
    private void keyTypeAlgoSelect(ActionEvent event) {
        try {
            keyType = typeCombobox.getValue().toString();
            algoCombobox.getItems().clear();
            keySizeCombobox.getItems().clear();
            keyLengthComboBox.getItems().clear();
            if (keyType.equals("Asymmetric")) {
                algoCombobox.getItems().addAll(Warehouse.listASymmetricAlgo);
                keySizeCombobox.getItems().addAll(Warehouse.listKeyTypeRSA);
                keyLengthComboBox.getItems().addAll(Warehouse.listKeyLengthASYM);
                keyLengthComboBox.setVisible(true);
            } else if (keyType.equals("Symmetric")) {
                keyLengthComboBox.getItems().addAll(Warehouse.listKeyLengthSYM);
                keySizeCombobox.getItems().addAll(Warehouse.listKeyTypeSYM);
                algoCombobox.getItems().addAll(Warehouse.listSymmetricAlgo);
                keyLengthComboBox.setVisible(true);

            }
            algoCombobox.getSelectionModel().selectFirst();
            keySizeCombobox.getSelectionModel().selectFirst();
            keyLengthComboBox.getSelectionModel().selectFirst();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void openSymmetric(ActionEvent event) throws IOException {
        AnchorPane root = (AnchorPane) pane1.getParent();
        Pane pane = FXMLLoader.load(App.class.getResource("controller/button/Symmetric.fxml"));
        root.getChildren().remove(pane1);
        root.getChildren().add(pane);
        pane.setLayoutX(0);
        pane.setLayoutY(327);
    }

    @FXML
    private void openAsymmetric(ActionEvent event) throws IOException {
        AnchorPane root = (AnchorPane) pane1.getParent();
        Pane pane = FXMLLoader.load(App.class.getResource("controller/button/Asymmetric.fxml"));
        root.getChildren().remove(pane1);
        root.getChildren().add(pane);
        pane.setLayoutX(0);
        pane.setLayoutY(327);
    }

    @FXML
    private void openPBE(ActionEvent event) throws IOException {
        AnchorPane root = (AnchorPane) pane1.getParent();
        Pane pane = FXMLLoader.load(App.class.getResource("controller/button/PBE.fxml"));
        root.getChildren().remove(pane1);
        root.getChildren().add(pane);
        pane.setLayoutX(0);
        pane.setLayoutY(327);
    }

    @FXML
    private void openHashing(ActionEvent event) throws IOException {
        AnchorPane root = (AnchorPane) pane1.getParent();
        Pane pane = FXMLLoader.load(App.class.getResource("controller/button/Hashing.fxml"));
        root.getChildren().remove(pane1);
        root.getChildren().add(pane);
        pane.setLayoutX(0);
        pane.setLayoutY(327);
    }

}