
public class DESX {
    private final int [] key1;
    private final int [] key3;
    private final String[] key2;
    private DES[] desInstances;

    public DESX(int [] key1, String[] key2, int [] key3) {
        this.key1 = key1;
        this.key2 = key2;
        this.key3 = key3;
    }

    public int [] encrypt(String[] input) {
        int[] xorInput = new int[input.length];
        int[] desEncrypted = new int[input.length];
        int[] encrypted = new int[input.length];

        // Przetwarzanie każdego bloku danych
        for (int i = 0; i < input.length; i++) {
            // Tworzenie obiektu DES z bieżącym tekstem i odpowiednim kluczem K2
            DES des = new DES(input[i], key2[i]);

            // Przed-szyfrowanie XOR z K1
            xorInput[i] = stringToBinaryInt(input[i]) ^ key1[i];

            // Szyfrowanie za pomocą DES
            desEncrypted[i] = binaryArrayToInt(des.encrypt()); // encrypt() zwraca wynik w formie binarnej

            // Po-szyfrowanie XOR z K3
            encrypted[i] = desEncrypted[i] ^ key3[i];
        }


        return encrypted;
    }

    private int stringToBinaryInt(String binaryString) {
        return Integer.parseInt(binaryString, 2);
    }

    // Przykład metody pomocniczej do konwersji tablicy binarnej na int
    private int binaryArrayToInt(int[] binaryArray) {
        int result = 0;
        for (int i = 0; i < binaryArray.length; i++) {
            result = (result << 1) | binaryArray[i];
        }
        return result;
    }
}
