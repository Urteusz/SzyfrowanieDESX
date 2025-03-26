public class DESX {
    private final DES des;
    private final boolean[] k1, k2;

    public DESX(String input, String key, String k1, String k2) {
        this.des = new DES(input, key);
        this.k1 = des.hexToBooleanArray(k1);
        this.k2 = des.hexToBooleanArray(k2);
    }

    public String encrypt() {
        boolean[][] input = des.getInput();
        boolean[][] FirstXOR = new boolean[input.length][64];
        boolean[][] CipherDES;
        boolean[][] SecondXOR = new boolean[input.length][64];
        int i = 0;
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
            result.append(des.BooleanArrayToHex(SecondXOR[i]));
        }
        return result.toString();

    }

    public String decrypt(String encrypted) {
        int j = 0;
        int blocks = encrypted.length() / 16;
        System.out.println(blocks);
        boolean[][] encryptedArray = new boolean[blocks][64];

        for (int i = 0; i < encrypted.length(); i += 16) {

            String input_cut = encrypted.substring(i, Math.min(i + 16, encrypted.length()));
            encryptedArray[j] = des.hexToBooleanArray(input_cut);
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
            result.append(des.hexToText(des.BooleanArrayToHex(SecondXOR[i])));
        }
        return result.toString();
    }

    public static void main(String[] args) {
        DESX desx = new DESX("Wszystko dziala ciesze sie","5555555555555555", "5555555555555555","5555555555555555");
        String potezne_wielkie_dupsko = desx.encrypt();
        System.out.println(potezne_wielkie_dupsko);
        String gigantycznie_male_dupsko = desx.decrypt(potezne_wielkie_dupsko);
        System.out.println(gigantycznie_male_dupsko);
    }

}
