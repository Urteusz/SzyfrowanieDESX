package pl.kryptografia.model;

public class DES {
    private boolean[] key = new boolean[64];
    private boolean[][] input;

//    Konstruktor - przekazuje input do inputCuttera aby przerobic go na tablice wielowymiarowa boolow
    public DES(String input_string, String k) {
        this.input = inputCutter(input_string);
        this.key = hexToBooleanArray(k);
    }

//    Gettery i setter
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

//    ______________________________________Funkcje pomocnicze_________________________________________________________

    public boolean[][] inputCutter(String input) {
        int j = 0;
        String input_old = padInput(input);
        int blocks = input_old.length() / 8;
        boolean[][] input_new = new boolean[blocks][64];

        for (int i = 0; i < input.length(); i += 8) {

            String input_cut = input.substring(i, Math.min(i + 8, input.length()));
            input_new[j] = stringToBoolean(input_cut);
            j++;
        }
        return input_new;
    }

    private String padInput(String input) {
        if (input.length() % 8 == 0) {
            return input;
        }
        int padLength = 8 - input.length() % 8;

        StringBuilder paddedInput = new StringBuilder(input);
        for (int i = input.length(); i < input.length() + padLength; i++) {
            paddedInput.append((char) 0);
        }

        return paddedInput.toString();
    }
//____________________________________Przekształcenia___________________________________________________________________
//    Różne przekształcenia ze Stringa (tekstu jawnego), tablicy Booleanów oraz wartości heksadecymalnych.
    public boolean[] stringToBoolean(String input) {
        boolean[] bitArray = new boolean[64];

        for (int i = 0; i < 8; i++) {
            if (i <= input.length() - 1) {
                char c = input.charAt(i);
                for (int j = 0; j < 8; j++) {
                    bitArray[i * 8 + j] = ((c >> (7 - j)) & 1) == 1;
                }
            } else {
                for (int j = 0; j < 8; j++) {
                    bitArray[i * 8 + j] = false;
                }
            }
        }

        return bitArray;
    }

    public static String hexToText(String hex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            char c = (char) Integer.parseInt(str, 16);
            if (c != '\0') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

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

    public static boolean[] hexToBooleanArray(String hex) {
        hex = String.format("%16s", hex).replace(' ', '0');

        boolean[] booleanArray = new boolean[64];

        for (int i = 0; i < 8; i++) {
            String byteHex = hex.substring(i * 2, i * 2 + 2);
            int byteValue = Integer.parseInt(byteHex, 16);
            for (int j = 0; j < 8; j++) {
                booleanArray[i * 8 + j] = ((byteValue >> (7 - j)) & 1) == 1;
            }
        }

        return booleanArray;
    }

//    _________________________________Algorytm DES_____________________________________________________________________
//    Operacje algorytmu DES
    private boolean[] permute(boolean[] input, int[] table, int size) {
        boolean[] output = new boolean[size];
        for (int i = 0; i < size; i++) {
            output[i] = input[table[i] - 1];
        }
        return output;
    }

    private boolean[] leftShift(boolean[] input, int shifts) {
        boolean[] output = new boolean[input.length];
        for (int i = 0; i < input.length - shifts; i++) {
            output[i] = input[i + shifts];
        }
        for (int i = input.length - shifts; i < input.length; i++) {
            output[i] = input[i - (input.length - shifts)];
        }
        return output;
    }

    private boolean[][] generateKeys(boolean[] key) {
        boolean[] permutedKey = permute(key, Values.PC1, 56);
        boolean[] left = new boolean[28];
        boolean[] right = new boolean[28];
        System.arraycopy(permutedKey, 0, left, 0, 28);
        System.arraycopy(permutedKey, 28, right, 0, 28);

        boolean[][] keys = new boolean[16][48];

        for (int i = 0; i < 16; i++) {
            left = leftShift(left, Values.SHIFTS[i]);
            right = leftShift(right, Values.SHIFTS[i]);
            boolean[] combined = new boolean[56];
            System.arraycopy(left, 0, combined, 0, 28);
            System.arraycopy(right, 0, combined, 28, 28);
            keys[i] = permute(combined, Values.PC2, 48);
        }

        return keys;
    }

    private boolean[] feistel(boolean[] input, boolean[] key) {
        // 1. Rozszerzanie wejściowego bloku do 48 bitów (permutacja)
        boolean[] extended = permute(input, Values.E, 48);

        // 2. Operacja XOR z kluczem
        boolean[] xored = xor(extended, key);

        // 3. S-boxy (substytucja)
        boolean[] sBoxOutput = new boolean[32];  // Przechowywanie wyników 32 bitów (po S-boxach)
        int sBoxOutputIndex = 0;

        for (int i = 0; i < 8; i++) {  // 8 S-boxów
            // Pobieramy 6-bitowy fragment
            int sBoxValue = getSBoxValue(xored, i);

            // Konwertujemy wartość na 4 bity i dodajemy do wyniku
            for (int j = 3; j >= 0; j--) {
                // Rozbijamy sBoxValue na 4 bity i zapisujemy je w sBoxOutput
                sBoxOutput[sBoxOutputIndex++] = (sBoxValue >> j & 1) == 1;  // Przypisujemy boolean
            }
        }

        // 4. Permutacja P
        boolean[] result = new boolean[32];
        for (int i = 0; i < 32; i++) {
            result[i] = sBoxOutput[Values.P[i] - 1]; // -1 bo indeksy w tablicy zaczynają się od 0
        }

        return result;

    }

    private static int getSBoxValue(boolean[] xored, int i) {
        boolean rowBit1 = xored[i * 6];
        boolean rowBit2 = xored[i * 6 + 5];
        int row = (rowBit1 ? 1 : 0) << 1 | (rowBit2 ? 1 : 0);  // Pierwszy i ostatni bit to indeks wiersza

        boolean colBit1 = xored[i * 6 + 1];
        boolean colBit2 = xored[i * 6 + 2];
        boolean colBit3 = xored[i * 6 + 3];
        boolean colBit4 = xored[i * 6 + 4];
        int col = (colBit1 ? 1 : 0) << 3 | (colBit2 ? 1 : 0) << 2 | (colBit3 ? 1 : 0) << 1 | (colBit4 ? 1 : 0);  // Środkowe 4 bity to indeks kolumny

        // Pobieramy wartość z S-boxa (od 0 do 15)
        int sBoxValue = Values.S_BOXES[i][row][col];
        return sBoxValue;
    }

    public boolean[] xor(boolean[] a, boolean[] b) {
        boolean[] result = new boolean[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] ^ b[i];
        }
        return result;
    }

    public boolean[][] encrypt() {
        int blocks = this.input.length;
        boolean[] output = new boolean[64];
        boolean[] output_cut;
        boolean[][] output_whole = new boolean[blocks][64];
        for (int x = 0; x < blocks; x++) {
            boolean[] input_x = this.input[x];
            boolean[] input_permuted = permute(input_x, Values.IP, 64);
            boolean[] left = new boolean[32];
            boolean[] right = new boolean[32];
            System.arraycopy(input_permuted, 0, left, 0, 32);
            System.arraycopy(input_permuted, 32, right, 0, 32);
            boolean[][] keys = generateKeys(this.key);
            for (int i = 0; i < 16; i++) {
                boolean[] temp = right;
                right = xor(left, feistel(right, keys[i]));
                left = temp;
            }

            System.arraycopy(right, 0, output, 0, 32);
            System.arraycopy(left, 0, output, 32, 32);
            output_cut = permute(output, Values.FP, 64);
            output_whole[x] = output_cut;
        }
        return output_whole;
    }

    public boolean[][] decrypt(boolean[][] encrypted) {
        int blocks = encrypted.length;
        boolean[][] outputWhole = new boolean[blocks][64];
        for (int x = 0; x < blocks; x++) {
            boolean[] input_cut = encrypted[x];
            boolean[] input_permuted = permute(input_cut, Values.IP, 64);
            boolean[] left = new boolean[32];
            boolean[] right = new boolean[32];
            System.arraycopy(input_permuted, 0, left, 0, 32);
            System.arraycopy(input_permuted, 32, right, 0, 32);
            boolean[][] keys = generateKeys(key);
            for (int i = 15; i >= 0; i--) {
                boolean[] temp = right;
                right = xor(left, feistel(right, keys[i]));
                left = temp;
            }
            boolean[] output = new boolean[64];
            System.arraycopy(right, 0, output, 0, 32);
            System.arraycopy(left, 0, output, 32, 32);

            outputWhole[x] = permute(output, Values.FP, 64);
        }
        return outputWhole;
    }



}