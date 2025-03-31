package pl.kryptografia.model;

/**
 * Implementacja algorytmu szyfrowania DES (Data Encryption Standard)
 * DES to symetryczny algorytm szyfrowania blokowego z kluczem 64-bitowym (8 bajtów),
 * z czego efektywnie wykorzystywane jest 56 bitów.
 */
public class DES {
    // Klucz 64-bitowy (8 bajtów) przedstawiony jako tablica wartości logicznych
    private boolean[] key = new boolean[64];

    // Dane wejściowe podzielone na bloki 64-bitowe
    private boolean[][] input;

    /**
     * Konstruktor - inicjalizuje obiekt DES z podanym tekstem jawnym i kluczem
     * @param input_string Tekst jawny do zaszyfrowania
     * @param k Klucz w formacie szesnastkowym (hex)
     */
    public DES(String input_string, String k) {
        // Konwersja tekstu wejściowego na bloki bitów
        this.input = inputCutter(input_string);
        // Konwersja klucza hex na tablicę bitów
        this.key = hexToBooleanArray(k);
    }

    /**
     * Gettery i settery
     */
    public String getKey() {
        return BooleanArrayToHex(key);
    }

    public void setKey(boolean[] key) {
        this.key = key;
    }

    public boolean[][] getInput() {
        return input;
    }

    public void setInput(boolean[][] input) {
        this.input = input;
    }

    //-------------- FUNKCJE POMOCNICZE ------------------

    /**
     * Dzieli tekst wejściowy na bloki 64-bitowe (8 bajtów)
     * @param input Tekst jawny
     * @return Tablica bloków bitowych
     */
    public boolean[][] inputCutter(String input) {
        int j = 0;
        // Uzupełnienie tekstu do wielokrotności 8 bajtów
        String input_old = padInput(input);
        // Obliczenie liczby bloków 8-bajtowych
        int blocks = input_old.length() / 8;
        // Inicjalizacja tablicy na bloki bitowe (każdy blok ma 64 bity)
        boolean[][] input_new = new boolean[blocks][64];

        // Podział tekstu na 8-bajtowe fragmenty i konwersja na bity
        for (int i = 0; i < input.length(); i += 8) {
            String input_cut = input.substring(i, Math.min(i + 8, input.length()));
            input_new[j] = stringToBoolean(input_cut);
            j++;
        }
        return input_new;
    }

    /**
     * Uzupełnia tekst wejściowy zerami do wielokrotności 8 bajtów
     * @param input Tekst wejściowy
     * @return Uzupełniony tekst
     */
    private String padInput(String input) {
        // Sprawdzenie czy tekst ma już odpowiednią długość
        if (input.length() % 8 == 0) {
            return input;
        }

        // Obliczenie ilości bajtów do uzupełnienia
        int padLength = 8 - input.length() % 8;

        // Dodanie odpowiedniej liczby pustych znaków
        StringBuilder paddedInput = new StringBuilder(input);
        for (int i = input.length(); i < input.length() + padLength; i++) {
            paddedInput.append((char) 0);
        }

        return paddedInput.toString();
    }

    //-------------- KONWERSJE FORMATÓW ------------------

    /**
     * Konwertuje 8-znakowy ciąg na 64-bitową tablicę wartości logicznych
     * @param input Ciąg znaków (maks. 8)
     * @return 64-bitowa reprezentacja wejścia
     */
    public boolean[] stringToBoolean(String input) {
        boolean[] bitArray = new boolean[64];

        // Konwersja każdego znaku na 8 bitów
        for (int i = 0; i < 8; i++) {
            if (i <= input.length() - 1) {
                // Jeśli znak istnieje, konwertujemy go na bity
                char c = input.charAt(i);
                for (int j = 0; j < 8; j++) {
                    // Ekstrakcja bitów od najbardziej znaczącego (MSB)
                    bitArray[i * 8 + j] = ((c >> (7 - j)) & 1) == 1;
                }
            } else {
                // Wypełnienie zerami, jeśli tekst był krótszy niż 8 znaków
                for (int j = 0; j < 8; j++) {
                    bitArray[i * 8 + j] = false;
                }
            }
        }

        return bitArray;
    }

    /**
     * Konwertuje ciąg szesnastkowy na tekst
     * @param hex Ciąg szesnastkowy
     * @return Tekst
     */
    public static String hexToText(String hex) {
        StringBuilder sb = new StringBuilder();
        // Przetwarzanie par znaków szesnastkowych
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            // Konwersja z hex na znak
            char c = (char) Integer.parseInt(str, 16);
            // Pomijanie znaków NULL
            if (c != '\0') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Konwertuje tablicę bitów na ciąg szesnastkowy
     * @param tablica Tablica bitów
     * @return Reprezentacja szesnastkowa
     */
    public static String BooleanArrayToHex(boolean[] tablica) {
        StringBuilder hex = new StringBuilder();
        int byteValue = 0;
        int count = 0;

        // Przetwarzanie bitów w grupach po 8 (1 bajt)
        for (boolean b : tablica) {
            if (b) {
                // Ustawienie odpowiedniego bitu
                byteValue |= (1 << (7 - count));
            }
            count++;

            // Gdy mamy pełny bajt, konwertujemy go na hex
            if (count == 8) {
                hex.append(String.format("%02X", byteValue));
                byteValue = 0;
                count = 0;
            }
        }

        // Obsługa niepełnego ostatniego bajtu
        if (count > 0) {
            hex.append(String.format("%02X", byteValue));
        }

        return hex.toString();
    }

    /**
     * Konwertuje ciąg szesnastkowy na 64-bitową tablicę wartości logicznych
     * @param hex Ciąg szesnastkowy (do 16 znaków)
     * @return 64-bitowa tablica
     */
    public static boolean[] hexToBooleanArray(String hex) {
        // Uzupełnienie ciągu zerami do 16 znaków (8 bajtów)
        hex = String.format("%16s", hex).replace(' ', '0');

        boolean[] booleanArray = new boolean[64];

        // Konwersja każdego bajtu (2 znaki hex) na 8 bitów
        for (int i = 0; i < 8; i++) {
            String byteHex = hex.substring(i * 2, i * 2 + 2);
            int byteValue = Integer.parseInt(byteHex, 16);
            for (int j = 0; j < 8; j++) {
                // Ekstrakcja bitów od najbardziej znaczącego (MSB)
                booleanArray[i * 8 + j] = ((byteValue >> (7 - j)) & 1) == 1;
            }
        }

        return booleanArray;
    }

    //-------------- ALGORYTM DES ------------------

    /**
     * Wykonuje permutację bitów zgodnie z podaną tabelą
     * @param input Tablica bitów wejściowych
     * @param table Tablica indeksów permutacji
     * @param size Rozmiar wyjścia
     * @return Tablica bitów po permutacji
     */
    private boolean[] permute(boolean[] input, int[] table, int size) {
        boolean[] output = new boolean[size];
        for (int i = 0; i < size; i++) {
            // Indeksy w tabeli permutacji są 1-based, stąd -1
            output[i] = input[table[i] - 1];
        }
        return output;
    }

    /**
     * Wykonuje przesunięcie cykliczne bitów w lewo
     * @param input Tablica bitów wejściowych
     * @param shifts Liczba bitów do przesunięcia
     * @return Tablica bitów po przesunięciu
     */
    private boolean[] leftShift(boolean[] input, int shifts) {
        boolean[] output = new boolean[input.length];
        // Przesunięcie głównej części
        for (int i = 0; i < input.length - shifts; i++) {
            output[i] = input[i + shifts];
        }
        // Zawijanie bitów, które "wypadły" z lewej strony
        for (int i = input.length - shifts; i < input.length; i++) {
            output[i] = input[i - (input.length - shifts)];
        }
        return output;
    }

    /**
     * Generuje 16 podkluczy dla 16 rund DES
     * @param key Główny klucz 64-bitowy
     * @return Tablica 16 kluczy 48-bitowych
     */
    private boolean[][] generateKeys(boolean[] key) {
        // PC1 redukuje klucz z 64 do 56 bitów, usuwając bity parzystości
        boolean[] permutedKey = permute(key, Values.PC1, 56);

        // Podział klucza na dwie połowy: C i D, każda po 28 bitów
        boolean[] left = new boolean[28];
        boolean[] right = new boolean[28];
        System.arraycopy(permutedKey, 0, left, 0, 28);
        System.arraycopy(permutedKey, 28, right, 0, 28);

        // Tablica na 16 podkluczy, każdy po 48 bitów
        boolean[][] keys = new boolean[16][48];

        // Generowanie 16 podkluczy dla każdej rundy
        for (int i = 0; i < 16; i++) {
            // Przesunięcie cykliczne zgodnie z harmonogramem przesunięć
            left = leftShift(left, Values.SHIFTS[i]);
            right = leftShift(right, Values.SHIFTS[i]);

            // Połączenie przesunietych połówek
            boolean[] combined = new boolean[56];
            System.arraycopy(left, 0, combined, 0, 28);
            System.arraycopy(right, 0, combined, 28, 28);

            // Permutacja PC2 redukuje klucz z 56 do 48 bitów
            keys[i] = permute(combined, Values.PC2, 48);
        }

        return keys;
    }

    /**
     * Funkcja Feistela - kluczowy element rundy szyfrowania DES
     * @param input Prawa połowa bloku (32 bity)
     * @param key Podklucz rundy (48 bitów)
     * @return Wynik funkcji (32 bity)
     */
    private boolean[] feistel(boolean[] input, boolean[] key) {
        // 1. Ekspansja E: rozszerzenie 32 bitów do 48 bitów
        boolean[] extended = permute(input, Values.E, 48);

        // 2. XOR z kluczem rundy
        boolean[] xored = xor(extended, key);

        // 3. Substytucja przez S-boksy: redukcja z 48 do 32 bitów
        boolean[] sBoxOutput = new boolean[32];
        int sBoxOutputIndex = 0;

        // Przetwarzanie przez 8 S-boksów, każdy konwertuje 6 bitów na 4 bity
        for (int i = 0; i < 8; i++) {
            // Pobierz wartość po przejściu przez S-box
            int sBoxValue = getSBoxValue(xored, i);

            // Konwersja wartości z S-boksa (0-15) na 4 bity
            for (int j = 3; j >= 0; j--) {
                sBoxOutput[sBoxOutputIndex++] = (sBoxValue >> j & 1) == 1;
            }
        }

        // 4. Permutacja P
        boolean[] result = new boolean[32];
        for (int i = 0; i < 32; i++) {
            result[i] = sBoxOutput[Values.P[i] - 1];
        }

        return result;
    }

    /**
     * Oblicza wartość wyjściową z S-boksa dla 6-bitowego fragmentu
     * @param xored Dane po operacji XOR z kluczem
     * @param i Indeks S-boksa (0-7)
     * @return Wartość 4-bitowa (0-15)
     */
    private static int getSBoxValue(boolean[] xored, int i) {
        // Pierwszy i ostatni bit określają numer wiersza (0-3)
        boolean rowBit1 = xored[i * 6];
        boolean rowBit2 = xored[i * 6 + 5];
        int row = (rowBit1 ? 1 : 0) << 1 | (rowBit2 ? 1 : 0);

        // Środkowe 4 bity określają numer kolumny (0-15)
        boolean colBit1 = xored[i * 6 + 1];
        boolean colBit2 = xored[i * 6 + 2];
        boolean colBit3 = xored[i * 6 + 3];
        boolean colBit4 = xored[i * 6 + 4];
        int col = (colBit1 ? 1 : 0) << 3 | (colBit2 ? 1 : 0) << 2 | (colBit3 ? 1 : 0) << 1 | (colBit4 ? 1 : 0);

        // Pobierz wartość z odpowiedniej tabeli S-boksa
        return Values.S_BOXES[i][row][col];
    }

    /**
     * Wykonuje operację XOR na dwóch tablicach bitów
     * @param a Pierwsza tablica
     * @param b Druga tablica
     * @return Wynik operacji XOR
     */
    public boolean[] xor(boolean[] a, boolean[] b) {
        boolean[] result = new boolean[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] ^ b[i];
        }
        return result;
    }

    /**
     * Szyfruje dane wejściowe algorytmem DES
     * @return Zaszyfrowane dane w postaci bloków bitowych
     */
    public boolean[][] encrypt() {
        int blocks = this.input.length;
        boolean[] output = new boolean[64];
        boolean[][] output_whole = new boolean[blocks][64];

        // Przetwarzanie każdego bloku 64-bitowego
        for (int x = 0; x < blocks; x++) {
            boolean[] input_x = this.input[x];

            // 1. Permutacja początkowa IP
            boolean[] input_permuted = permute(input_x, Values.IP, 64);

            // 2. Podział na lewą i prawą połowę (L0, R0)
            boolean[] left = new boolean[32];
            boolean[] right = new boolean[32];
            System.arraycopy(input_permuted, 0, left, 0, 32);
            System.arraycopy(input_permuted, 32, right, 0, 32);

            // 3. Generowanie podkluczy
            boolean[][] keys = generateKeys(this.key);

            // 4. 16 rund sieci Feistela
            for (int i = 0; i < 16; i++) {
                // Li = Ri-1
                boolean[] temp = right;
                // Ri = Li-1 XOR f(Ri-1, Ki)
                right = xor(left, feistel(right, keys[i]));
                left = temp;
            }

            // 5. Zamiana miejscami ostatnich L16 i R16
            System.arraycopy(right, 0, output, 0, 32);
            System.arraycopy(left, 0, output, 32, 32);

            // 6. Permutacja końcowa FP (odwrotność IP)
            output_whole[x] = permute(output, Values.FP, 64);
        }
        return output_whole;
    }

    /**
     * Deszyfruje dane zaszyfrowane algorytmem DES
     * @param encrypted Zaszyfrowane dane w postaci bloków bitowych
     * @return Odszyfrowane dane w postaci bloków bitowych
     */
    public boolean[][] decrypt(boolean[][] encrypted) {
        int blocks = encrypted.length;
        boolean[][] outputWhole = new boolean[blocks][64];

        // Przetwarzanie każdego bloku 64-bitowego
        for (int x = 0; x < blocks; x++) {
            boolean[] input_cut = encrypted[x];

            // 1. Permutacja początkowa IP (tak samo jak przy szyfrowaniu)
            boolean[] input_permuted = permute(input_cut, Values.IP, 64);

            // 2. Podział na lewą i prawą połowę
            boolean[] left = new boolean[32];
            boolean[] right = new boolean[32];
            System.arraycopy(input_permuted, 0, left, 0, 32);
            System.arraycopy(input_permuted, 32, right, 0, 32);

            // 3. Generowanie podkluczy (takich samych jak przy szyfrowaniu)
            boolean[][] keys = generateKeys(key);

            // 4. 16 rund sieci Feistela, ale w odwrotnej kolejności kluczy (od 15 do 0)
            for (int i = 15; i >= 0; i--) {
                boolean[] temp = right;
                right = xor(left, feistel(right, keys[i]));
                left = temp;
            }

            // 5. Zamiana miejscami ostatnich L16 i R16
            boolean[] output = new boolean[64];
            System.arraycopy(right, 0, output, 0, 32);
            System.arraycopy(left, 0, output, 32, 32);

            // 6. Permutacja końcowa FP
            outputWhole[x] = permute(output, Values.FP, 64);
        }
        return outputWhole;
    }
}