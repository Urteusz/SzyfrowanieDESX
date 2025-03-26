import java.math.BigInteger;
import java.util.Arrays;

public class DESX {
    private DES des;
    private boolean[] preWhiteningKey = new boolean[64];
    private boolean[] postWhiteningKey = new boolean[64];
    private boolean[][] input;

    public DESX(String input_string, String k, String preKey, String postKey) {
        // Inicjalizacja DES
        des = new DES(input_string, k);

        // Inicjalizacja kluczy whitening
        generateWhiteningKeys(preKey, postKey);

        // Przygotowanie danych wejściowych
        inputCutter(input_string);
    }

    // Metoda do generowania kluczy whitening
    private void generateWhiteningKeys(String preKey, String postKey) {
        // Jeśli klucze są podane, konwertuj je na tablice boolean
        if (preKey != null && !preKey.isEmpty()) {
            preWhiteningKey = stringToBoolean(preKey.length() > 8 ? preKey.substring(0, 8) : preKey);
        } else {
            // Domyślny klucz pre-whitening
            for (int i = 0; i < 64; i++) {
                preWhiteningKey[i] = i % 3 == 0;
            }
        }

        if (postKey != null && !postKey.isEmpty()) {
            postWhiteningKey = stringToBoolean(postKey.length() > 8 ? postKey.substring(0, 8) : postKey);
        } else {
            // Domyślny klucz post-whitening
            for (int i = 0; i < 64; i++) {
                postWhiteningKey[i] = i % 5 == 0;
            }
        }
    }

    public void inputCutter(String input) {
        StringBuilder final_input = new StringBuilder();

        int j = 0;
        // Dodaj padding, jeśli długość nie jest wielokrotnością 8
        input = padInput(input);
        int blocks = input.length() / 8;
        this.input = new boolean[blocks][64];

        for (int i = 0; i < input.length(); i += 8) {
            String input_cut = input.substring(i, Math.min(i + 8, input.length()));
            this.input[j] = stringToBoolean(input_cut);
            j++;
        }
    }

    private String padInput(String input) {
        // PKCS7 Padding
        int padLength = 8 - input.length() % 8;

        StringBuilder paddedInput = new StringBuilder(input);
        for (int i = input.length(); i < input.length() + padLength; i++) {
            paddedInput.append((char) 0);
        }

        return paddedInput.toString();
    }

    public boolean[] stringToBoolean(String input) {
        // Zakładamy, że chcemy przechować maksymalnie 8 znaków (8 * 8 = 64 bity)
        boolean[] bitArray = new boolean[64];

        // Konwersja każdego znaku na jego reprezentację binarną
        for (int i = 0; i < 8; i++) {
            if (i <= input.length() - 1) {
                char c = input.charAt(i);
                for (int j = 0; j < 8; j++) {
                    bitArray[i * 8 + j] = ((c >> (7 - j)) & 1) == 1;  // Sprawdzanie każdego bitu
                }
            } else {
                for (int j = 0; j < 8; j++) {
                    bitArray[i * 8 + j] = false;
                }
            }
        }

        return bitArray;
    }

    private boolean[] xor(boolean[] a, boolean[] b) {
        // Upewnij się, że tablice mają taką samą długość
        if (a.length != b.length) {
            // Obsłuż sytuację, gdy tablice mają różne długości
            // Możesz rzucić wyjątek lub dostosować długość
            int minLength = Math.min(a.length, b.length);
            boolean[] result = new boolean[minLength];
            for (int i = 0; i < minLength; i++) {
                result[i] = a[i] ^ b[i];
            }
            return result;
        }

        boolean[] result = new boolean[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] ^ b[i];
        }
        return result;
    }


    public boolean[] encrypt() {
        int blocks = this.input.length;
        boolean[][] output_whole = new boolean[blocks][64];

        for (int x = 0; x < blocks; x++) {
            // 1. Pre-whitening: XOR z pierwszym kluczem
            boolean[] preWhitened = xor(this.input[x], preWhiteningKey);

            // 2. Standardowe szyfrowanie DES
            // Przekazujemy dane do DES i wykonujemy szyfrowanie
            boolean[] desEncrypted = des.decrypt(preWhitened); // Używamy metody decrypt jako część procesu szyfrowania

            // 3. Post-whitening: XOR z drugim kluczem
            boolean[] postWhitened = xor(desEncrypted, postWhiteningKey);

            output_whole[x] = postWhitened;
        }

        // Łączenie wszystkich bloków w jeden wynik
        boolean[] finalOutput = new boolean[blocks * 64];
        int currentIndex = 0;

        for (boolean[] blockOutput : output_whole) {
            System.arraycopy(blockOutput, 0, finalOutput, currentIndex, 64);
            currentIndex += 64;
        }

        return finalOutput;
    }

    public boolean[] decrypt(boolean[] encrypted) {
        int blocks = encrypted.length / 64;
        boolean[][] encryptedBlocks = new boolean[blocks][64];

        // Podział zaszyfrowanych danych na bloki
        for (int i = 0; i < blocks; i++) {
            System.arraycopy(encrypted, i * 64, encryptedBlocks[i], 0, 64);
        }

        boolean[][] outputWhole = new boolean[blocks][64];

        for (int x = 0; x < blocks; x++) {
            // 1. Odwrócenie post-whitening: XOR z drugim kluczem
            boolean[] postWhitenedReversed = xor(encryptedBlocks[x], postWhiteningKey);

            // 2. Standardowe odszyfrowanie DES
            boolean[] desDecrypted = des.encrypt(); // Używamy metody encrypt jako część procesu deszyfrowania

            // 3. Odwrócenie pre-whitening: XOR z pierwszym kluczem
            boolean[] preWhitenedReversed = xor(desDecrypted, preWhiteningKey);

            outputWhole[x] = preWhitenedReversed;
        }

        // Łączenie wszystkich bloków w jeden wynik
        boolean[] finalOutput = new boolean[blocks * 64];
        int currentIndex = 0;

        for (boolean[] blockOutput : outputWhole) {
            System.arraycopy(blockOutput, 0, finalOutput, currentIndex, 64);
            currentIndex += 64;
        }

        return finalOutput;
    }

    // Metody pomocnicze
    public static String BooleanArrayToHex(boolean[] tablica) {
        StringBuilder hex = new StringBuilder();
        int byteValue = 0;
        int count = 0;

        for (boolean b : tablica) {
            if (b) {
                byteValue |= (1 << (7 - count));
            }
            count++;

            if (count == 8) {
                hex.append(String.format("%02X", byteValue));
                byteValue = 0;
                count = 0;
            }
        }
        if (count > 0) {
            hex.append(String.format("%02X", byteValue));
        }

        return hex.toString();
    }

    public static void printBooleanArray(boolean[] array) {
        for (boolean b : array) {
            System.out.print(b ? 1 : 0);
        }
        System.out.println();
    }

    public static String hexToText(String hex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            sb.append((char) Integer.parseInt(str, 16));
        }
        return sb.toString();
    }
    public static void main(String[] args) {
        // Tekst do zaszyfrowania
        String plaintext = "DESX test";

        // Klucze dla DESX
        String desKey = "12345678";
        String preWhiteningKey = "PreWhite";
        String postWhiteningKey = "PostWhit";

        System.out.println("Oryginalny tekst: " + plaintext);

        // Tworzenie instancji DESX
        DESX desx = new DESX(plaintext, desKey, preWhiteningKey, postWhiteningKey);

        // Szyfrowanie
        boolean[] encrypted = desx.encrypt();
        System.out.println("Zaszyfrowany (HEX): " + BooleanArrayToHex(encrypted));

        // Deszyfrowanie
        boolean[] decrypted = desx.decrypt(encrypted);
        String decryptedText = hexToText(BooleanArrayToHex(decrypted));
        System.out.println("Odszyfrowany: " + decryptedText);

        // Sprawdzenie poprawności
        if (decryptedText.startsWith(plaintext)) {
            System.out.println("Test zakończony sukcesem! Tekst został poprawnie zaszyfrowany i odszyfrowany.");
        } else {
            System.out.println("Test nie powiódł się! Odszyfrowany tekst nie zgadza się z oryginalnym.");
            System.out.println("Oczekiwano: " + plaintext);
            System.out.println("Otrzymano: " + decryptedText);
        }

        // Dodatkowy test z innymi kluczami
        System.out.println("\n--- Test z innymi kluczami ---");
        String plaintext2 = "Another test message";
        String desKey2 = "KeyForDE";
        String preKey2 = "PreKey12";
        String postKey2 = "PostKey3";

        DESX desx2 = new DESX(plaintext2, desKey2, preKey2, postKey2);

        boolean[] encrypted2 = desx2.encrypt();
        System.out.println("Zaszyfrowany (HEX): " + BooleanArrayToHex(encrypted2));

        boolean[] decrypted2 = desx2.decrypt(encrypted2);
        String decryptedText2 = hexToText(BooleanArrayToHex(decrypted2));
        System.out.println("Odszyfrowany: " + decryptedText2);

        if (decryptedText2.startsWith(plaintext2)) {
            System.out.println("Drugi test zakończony sukcesem!");
        } else {
            System.out.println("Drugi test nie powiódł się!");
        }

        // Test z pustymi kluczami whitening (używane są domyślne)
        System.out.println("\n--- Test z domyślnymi kluczami whitening ---");
        String plaintext3 = "Test default keys";

        DESX desx3 = new DESX(plaintext3, desKey, null, null);

        boolean[] encrypted3 = desx3.encrypt();
        System.out.println("Zaszyfrowany (HEX): " + BooleanArrayToHex(encrypted3));

        boolean[] decrypted3 = desx3.decrypt(encrypted3);
        String decryptedText3 = hexToText(BooleanArrayToHex(decrypted3));
        System.out.println("Odszyfrowany: " + decryptedText3);

        if (decryptedText3.startsWith(plaintext3)) {
            System.out.println("Trzeci test zakończony sukcesem!");
        } else {
            System.out.println("Trzeci test nie powiódł się!");
        }
    }

}
