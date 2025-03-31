package pl.kryptografia.model;
import java.security.SecureRandom;

public class DESX {
    private final DES des;
    private boolean[] k1;
    private boolean[] k2;

//    Konstruktor
    public DESX(String input, String key, String k1, String k2) {
        this.des = new DES(input, key);
        this.k1 = DES.hexToBooleanArray(k1);
        this.k2 = DES.hexToBooleanArray(k2);
    }

//    Gettery i settery
    public DES getDes() {
        return des;
    }

    public String getK1() {
        return DES.BooleanArrayToHex(k1);
    }

    public String getK2() {
        return DES.BooleanArrayToHex(k2);
    }

    public void setk1(String k1) {
        this.k1 = DES.hexToBooleanArray(k1);
    }
    public void setk2(String k2) {
        this.k2 = DES.hexToBooleanArray(k2);
    }

//    Encrypt i decrypt
    public String encrypt() {
        boolean[][] input = des.getInput();
        boolean[][] FirstXOR = new boolean[input.length][64];
        boolean[][] CipherDES;
        boolean[][] SecondXOR = new boolean[input.length][64];
        int i;
        for (i = 0; i < input.length; i++) {
            FirstXOR[i] = des.xor(input[i], k1);
        }
        des.setInput(FirstXOR);
        CipherDES = des.encrypt();
        for (i = 0; i < input.length; i++) {
            SecondXOR[i] = des.xor(CipherDES[i], k2);
        }
        StringBuilder result = new StringBuilder();
        for (i = 0; i < input.length; i++) {
            result.append(DES.BooleanArrayToHex(SecondXOR[i]));
        }
        return result.toString();

    }

    public String decrypt(String encrypted) {
        int j = 0;
        int blocks = encrypted.length() / 16;
        boolean[][] encryptedArray = new boolean[blocks][64];

        for (int i = 0; i < encrypted.length(); i += 16) {

            String input_cut = encrypted.substring(i, Math.min(i + 16, encrypted.length()));
            encryptedArray[j] = DES.hexToBooleanArray(input_cut);
            j++;
        }
        boolean[][] FirstXOR = new boolean[encryptedArray.length][64];
        for(int i = 0; i < encryptedArray.length; i++) {
            FirstXOR[i] = des.xor(encryptedArray[i], k2);
        }
        boolean[][] decrypted = des.decrypt(FirstXOR);
        boolean[][] SecondXOR = new boolean[decrypted.length][64];
        for(int i = 0; i < decrypted.length; i++) {
            SecondXOR[i] = des.xor(decrypted[i], k1);
        }
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < decrypted.length; i++) {
            result.append(DES.hexToText(DES.BooleanArrayToHex(SecondXOR[i])));
        }
        return result.toString();
    }

//    Funkcje pomocnicze
    public void genAllKeys() {
        SecureRandom random = new SecureRandom();
        boolean[] desKey = new boolean[64];
        for (int i = 0; i < 64; i++) {
            desKey[i] = random.nextBoolean();
        }
        des.setKey(desKey);
        for (int i = 0; i < 64; i++) {
            k1[i] = random.nextBoolean();
        }
        for (int i = 0; i < 64; i++) {
            k2[i] = random.nextBoolean();
        }
    }


}
