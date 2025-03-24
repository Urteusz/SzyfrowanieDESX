public class DES {
    private int[] key;

    public DES() {
        key = new int[64];
    }

    private static final int[] IP = {
            58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7
    };

    private static final int[] FP = {
            40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9, 49, 17, 57, 25
    };

    // Tabele permutacji dla generowania kluczy
    private static final int[] PC1 = {
            57, 49, 41, 33, 25, 17, 9,
            1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27,
            19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15,
            7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29,
            21, 13, 5, 28, 20, 12, 4
    };

    private static final int[] PC2 = {
            14, 17, 11, 24, 1, 5,
            3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8,
            16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53,
            46, 42, 50, 36, 29, 32
    };

    // Tabela przesunięć dla kluczy
    private static final int[] SHIFTS = {
            1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1
    };

    // Tabela rozszerzenia E
    private static final int[] E = {
            32, 1, 2, 3, 4, 5,
            4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32, 1
    };

    // Tabela permutacji P
    private static final int[] P = {
            16, 7, 20, 21, 29, 12, 28, 17,
            1, 15, 23, 26, 5, 18, 31, 10,
            2, 8, 24, 14, 32, 27, 3, 9,
            19, 13, 30, 6, 22, 11, 4, 25
    };

    // S-boxy
    private static final int[][][] S_BOXES = {
            // S1
            {
                    {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
                    {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
                    {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
                    {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}
            },
            // S2
            {
                    {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
                    {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
                    {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
                    {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}
            },
            // S3
            {
                    {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
                    {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
                    {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
                    {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}
            },
            // S4
            {
                    {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
                    {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
                    {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
                    {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}
            },
            // S5
            {
                    {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
                    {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
                    {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
                    {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}
            },
            // S6
            {
                    {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
                    {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
                    {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
                    {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}
            },
            // S7
            {
                    {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
                    {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
                    {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
                    {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}
            },
            // S8
            {
                    {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
                    {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
                    {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
                    {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}
            }
    };

    private int[] permute(int[] input, int[] table, int size) {
        int [] output = new int[size];
        for (int i = 0; i < size; i++) {
            output[i] = input[table[i] - 1];
        }
        return output;
    }

    private int[] leftShift(int[] input, int shifts){
        int[] output = new int[input.length];
        for (int i = 0; i < input.length - shifts; i++) {
            output[i] = input[i + shifts];
        }
        for (int i = input.length - shifts; i < input.length; i++) {
            output[i] = input[i - (input.length - shifts)];
        }
        return output;
    }

    private int[][] generateKeys(int[] key){
        int[] permutedKey = permute(key, PC1,64);
        int[] left = new int[28];
        int[] right = new int[28];
        System.arraycopy(permutedKey,0,left,0,28);
        System.arraycopy(permutedKey,0,right,0,28);

        int[][] keys = new int[16][48];

        for(int i=0;i<16;i++){
            left = leftShift(left, SHIFTS[i]);
            right = leftShift(right, SHIFTS[i]);
            int[] combined = new int[56];
            System.arraycopy(left,0,combined,0,28);
            System.arraycopy(right,0,combined,28,28);
            keys[i] = permute(combined, PC2, 48);
        }

        return keys;
    }

    private int[] feistel(int[] input, int[] key){
        int[] extended = permute(input, E, 48);

        int[] xored = xor(extended, key);

        // 3. S-boxy (substytucja)
        int[] sBoxOutput = new int[32];
        int sBoxOutputIndex = 0;

        for (int i = 0; i < 8; i++) { // 8 S-boxów
            // Pobieramy 6-bitowy fragment
            int row = (xored[i*6] << 1) | xored[i*6 + 5]; // Pierwszy i ostatni bit to indeks wiersza
            int col = (xored[i*6 + 1] << 3) | (xored[i*6 + 2] << 2) | (xored[i*6 + 3] << 1) | xored[i*6 + 4]; // Środkowe 4 bity to indeks kolumny

            // Pobieramy wartość z S-boxa (od 0 do 15)
            int sBoxValue = S_BOXES[i][row][col];

            // Konwertujemy wartość na 4 bity i dodajemy do wyniku
            for (int j = 3; j >= 0; j--) {
                sBoxOutput[sBoxOutputIndex++] = (sBoxValue >> j) & 1;
            }
        }

        // 4. Permutacja P
        int[] result = new int[32];
        for (int i = 0; i < 32; i++) {
            result[i] = sBoxOutput[P[i] - 1]; // -1 bo indeksy w tablicy zaczynają się od 0
        }

        return result;

    }

    private int[] xor(int[] a, int[] b){
        int[] result = new int[a.length];
        for(int i=0;i<a.length;i++){
            result[i] = a[i] ^ b[i];
        }
        return result;
    }

    public int[] encrypt(){
        int[] input = this.input;
        int[] key = this.key;
        int[] input_permuted = permute(input, IP, 64);
      int[] left = new int[32];
      int[] right = new int[32];
      System.arraycopy(input_permuted,0,left,0,32);
      System.arraycopy(input_permuted,32,right,0,32);
      int[][] keys = generateKeys(key);
      for(int i=0; i<16; i++){
          int[] temp = right;
          right = xor(left, feistel(right, keys[i]));
          left = temp;
      }
      int [] output = new int[64];
      System.arraycopy(right,0,output,0,32);
      System.arraycopy(left,0,output,32,32);
      return permute(output, FP, 64);
    }


//    public static void main(String[] args) {
//        DES des = new DES();
//        int[] output = des.encrypt();
//        for (int i = 0; i < 64; i++) {
//            System.out.print(output[i]);
//        }
//        System.out.println();
//    }


    public int[] decrypt() {
        int[] input_permuted = permute(input, IP, 64);
        int[] left = new int[32];
        int[] right = new int[32];
        System.arraycopy(input_permuted, 0, left, 0, 32);
        System.arraycopy(input_permuted, 32, right, 0, 32);
        int[][] keys = generateKeys(key);

        for (int i = 15; i >= 0; i--) {
            int[] temp = right;
            right = xor(left, feistel(right, keys[i]));
            left = temp;
        }

        int[] output = new int[64];
        System.arraycopy(right, 0, output, 0, 32);
        System.arraycopy(left, 0, output, 32, 32);
        return permute(output, FP, 64);
    }

    public static void main(String[] args) {
        DES des = new DES("Hello", "133457799BBCDFF1");

        int[] encrypted = des.encrypt();
        System.out.print("Encrypted: ");
        for (int bit : encrypted) {
            System.out.print(bit);
        }
        System.out.println();

        des.input = encrypted;
        int[] decrypted = des.decrypt();
        System.out.print("Decrypted: ");
        for (int bit : decrypted) {
            System.out.print(bit);
        }
        System.out.println();
        System.out.println(binaryArrayToString(decrypted));
    }
}
