package pl.kryptografia.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import pl.kryptografia.model.*;


import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    DESX desxObject;

    @FXML
    private TextField keyOne;

    @FXML
    private TextField keyTwo;

    @FXML
    private TextField keyThree;

    @FXML
    private TextArea areaPlain;

    @FXML
    private TextArea areaEncrypted;

    @FXML
    private TextField textOpenPlain;

    @FXML
    private TextField textOpenEncrypted;

    @FXML
    private TextField textSavePlain;

    @FXML
    private TextField textSaveEncrypted;

    @FXML
    private void onGenerateKeysClick() {
        desxObject.genAllKeys();
        keyOne.setText(desxObject.getDes().getKey());
        keyTwo.setText(desxObject.getK1());
        keyThree.setText(desxObject.getK2());
    }

    @FXML
    private void openFileClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik TXT");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Plik tekstowy", "*.txt")
        );

        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null){
            try {
                String content = Files.readString(Path.of(file.getAbsolutePath()));
                textOpenPlain.setText(file.getAbsolutePath());
                areaPlain.setText(content);
            } catch (IOException e){

            }
        }
    }


    @FXML
    private void saveFileClick() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Zapisz plik TXT");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Plik tekstowy", "*.txt")
        );

        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null){
            try {
                Files.writeString(Path.of(file.getAbsolutePath()), areaPlain.getText());
                textSavePlain.setText(file.getAbsolutePath());
            } catch (IOException e){

            }
        }

    }

    @FXML
    private void openEncryptedClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik TXT");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Plik tekstowy", "*.txt")
        );

        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null){
            try {
                String content = Files.readString(Path.of(file.getAbsolutePath()));
                textOpenEncrypted.setText(file.getAbsolutePath());
                areaEncrypted.setText(content);
            } catch (IOException _){

            }
        }
    }


    @FXML
    private void saveEncryptedClick() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Zapisz plik TXT");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Plik tekstowy", "*.txt")
        );

        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null){
            try {
                Files.writeString(Path.of(file.getAbsolutePath()), areaEncrypted.getText());
                textSaveEncrypted.setText(file.getAbsolutePath());
            } catch (IOException e){

            }
        }

    }

    @FXML
    private void onEncryptClick() {

        if(!isAscii(areaPlain.getText())){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Niepoprawny tekst");
            alert.setContentText("Tekst musi być w formacie ASCII.");
            alert.showAndWait();
            return;
        }

        String key1 = keyOne.getText();
        String key2 = keyTwo.getText();
        String key3 = keyThree.getText();
        if (key1.length() != 16 || key2.length() != 16 || key3.length() != 16) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Niepoprawne klucze");
            alert.setContentText("Każdy klucz musi mieć długość 16 znaków.");
            alert.showAndWait();
        }
        else {
            desxObject.getDes().setKey(DES.hexToBooleanArray(key1));
            desxObject.setk1(key2);
            desxObject.setk2(key3);
            String plainText = areaPlain.getText();
            desxObject.getDes().setInput(desxObject.getDes().inputCutter(plainText));
            areaEncrypted.setText(desxObject.encrypt());
        }
    }


    @FXML
    private void onDecryptClick() {

        if(!isAscii(areaEncrypted.getText())){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Niepoprawny tekst");
            alert.setContentText("Tekst musi być w formacie ASCII.");
            alert.showAndWait();
        }
        else {
            String encryptedText = areaEncrypted.getText();
            areaPlain.setText(desxObject.decrypt(encryptedText));
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.desxObject = new DESX("", "5555555555555555", "5555555555555555", "5555555555555555");
        updateText();

        ChangeListener<String> hexValidatorKey = (observable, oldValue, newValue) -> {
            if (newValue.length() > 16 || !newValue.matches("[0-9A-Fa-f]*")) {
                ((TextField) ((ReadOnlyStringProperty) observable).getBean()).setText(oldValue);
            }
        };

        keyOne.textProperty().addListener(hexValidatorKey);
        keyTwo.textProperty().addListener(hexValidatorKey);
        keyThree.textProperty().addListener(hexValidatorKey);
    }



    public void updateText(){
        keyOne.setText(desxObject.getDes().getKey());
        keyTwo.setText(desxObject.getK1());
        keyThree.setText(desxObject.getK2());
    }

    private boolean isAscii(String text) {
        for (char c : text.toCharArray()) {
            if (c > 127) {
                return false;
            }
        }
        return true;
    }



}