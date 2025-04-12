package pl.kryptografia.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import pl.kryptografia.model.*;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    DESX desxObject;
    boolean[][] plainData;
    boolean[][] encryptedData;
    String encryptedDataString;
    String extensionFileName;

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
    private RadioButton radioFile;

    @FXML
    private RadioButton radioText;

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
        fileChooser.setTitle("Wybierz plik do otwarcia");

        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            try {
                getFileExtension(file);
                plainData = readBooleanArrayFromFile(file);
                desxObject.getDes().setInput(plainData);
                textOpenPlain.setText(file.getAbsolutePath());
                radioFile.setSelected(true);
                areaPlain.setDisable(true);
                areaEncrypted.setDisable(true);

                areaPlain.setText("Plik zostal wczytany pomyslnie");
            } catch (IOException e) {

            }
        }
    }


    @FXML
    private void openEncryptedClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik do otwarcia");

        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null){
            try {
                getFileExtension(file);
                encryptedData = readBooleanArrayFromFile(file);
                textOpenEncrypted.setText(file.getAbsolutePath());
                radioFile.setSelected(true);
                areaPlain.setDisable(true);
                areaEncrypted.setDisable(true);
                areaEncrypted.setText("Plik zostal wczytany pomyslnie");
            } catch (IOException _){

            }
        }
    }

    private void saveFile(boolean[][] data, int choose) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz plik");

        if(radioFile.isSelected()) {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("*." + extensionFileName, "*." + extensionFileName)
            );
        }
        else {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("*.txt", "*.txt")
            );
        }

        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try {
                if(areaPlain.isDisable() || areaEncrypted.isDisable()) {
                    byte[] bytes = convertBitsToBytes(data);
                    Files.write(file.toPath(), bytes);
                    if(choose == 1){
                        textSavePlain.setText(file.getAbsolutePath());
                    }
                    if(choose == 2){
                        textSaveEncrypted.setText(file.getAbsolutePath());
                    }
                }
                else {
                    if(choose == 1){
                        String text = areaPlain.getText();
                        Files.write(file.toPath(), text.getBytes());
                        textSavePlain.setText(file.getAbsolutePath());

                    }
                    if(choose == 2){
                        String text = areaPlain.getText();
                        Files.write(file.toPath(), text.getBytes());
                        textSaveEncrypted.setText(file.getAbsolutePath());
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void savePlainClick() {
        saveFile(plainData, 1);
    }

    @FXML
    private void saveEncryptedClick() {
        saveFile(encryptedData, 2);
    }

    // Pomocnicza metoda do konwersji boolean[][] na byte[]
    private byte[] convertBitsToBytes(boolean[][] bits) {
        List<Boolean> flatBits = new ArrayList<>();
        for (boolean[] row : bits) {
            for (boolean bit : row) {
                flatBits.add(bit);
            }
        }

        int byteCount = (flatBits.size() + 7) / 8;
        byte[] byteArray = new byte[byteCount];

        for (int i = 0; i < flatBits.size(); i++) {
            if (flatBits.get(i)) {
                byteArray[i / 8] |= (1 << (7 - (i % 8)));
            }
        }

        return byteArray;
    }


    @FXML
    private void onEncryptClick() {

        String key1 = keyOne.getText();
        String key2 = keyTwo.getText();
        String key3 = keyThree.getText();

        if(!isAscii(areaPlain.getText())){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Niepoprawny tekst");
            alert.setContentText("Tekst musi być w formacie ASCII.");
            alert.showAndWait();
            return;
        }

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
        }
        if(radioFile.isSelected())
        {
            if(plainData == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Brak danych");
                alert.setContentText("Najpierw wczytaj plik przed próbą szyfrowania.");
                alert.showAndWait();
                return;
            }
            areaEncrypted.setText("Plik gotowy do zapisu.");
            this.encryptedData = desxObject.encrypt();
        }
        else if(radioText.isSelected())
        {
            String input = areaPlain.getText();
            desxObject.getDes().setInput(DES.inputCutter(input));
            this.encryptedData = desxObject.encrypt();
            StringBuilder encrypted = new StringBuilder();
            for (int i = 0; i < encryptedData.length; i++) {
                encrypted.append(DES.BooleanArrayToHex(encryptedData[i]));
            }
            this.encryptedDataString = encrypted.toString();
            areaEncrypted.setText(encryptedDataString);
        }





    }

    @FXML
    private void onFileRadio() {
        areaPlain.setText("");
        areaEncrypted.setText("");
        areaPlain.setDisable(true);
        areaEncrypted.setDisable(true);
    }

    @FXML
    private void onTextRadio() {
        areaPlain.setText("");
        areaEncrypted.setText("");
        areaPlain.setDisable(false);
        areaEncrypted.setDisable(false);
    }


    @FXML
    private void onDecryptClick() {

        String areaEncryptedText = areaEncrypted.getText();
        if(!isAscii(areaEncryptedText)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Niepoprawny tekst");
            alert.setContentText("Tekst musi być w formacie ASCII.");
            alert.showAndWait();
        }
        else{
            this.plainData = desxObject.decrypt(encryptedData);
            areaPlain.setText("Plik zostal odszyfrowany pomyslnie");
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

        ToggleGroup fileOrTextGroup = new ToggleGroup();

        radioFile.setToggleGroup(fileOrTextGroup);
        radioText.setToggleGroup(fileOrTextGroup);

        radioText.setSelected(true);

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

    private boolean[][] readBooleanArrayFromFile(File file) throws IOException {
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        // Calculate the number of full 64-bit blocks needed
        int numBlocks = (int) Math.ceil(fileBytes.length * 8.0 / 64.0);
        boolean[][] result = new boolean[numBlocks][64];

        // Convert each byte to 8 bits and place in the appropriate positions
        for (int i = 0; i < fileBytes.length; i++) {
            byte b = fileBytes[i];
            int blockIndex = (i * 8) / 64;  // Which 64-bit block
            int bitOffset = (i * 8) % 64;   // Bit position within the block

            // Extract 8 bits from the byte
            for (int j = 0; j < 8; j++) {
                if (blockIndex < numBlocks && (bitOffset + j) < 64) {
                    result[blockIndex][bitOffset + j] = ((b >> (7 - j)) & 1) == 1;
                }
            }
        }
        return result;
    }

    private void getFileExtension(File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < name.length() - 1) {
            this.extensionFileName = name.substring(lastDotIndex + 1).toLowerCase();
        }
    }

}