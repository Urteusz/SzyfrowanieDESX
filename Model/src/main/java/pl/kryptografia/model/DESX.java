package pl.kryptografia.model;
import java.security.SecureRandom;

/**
 * Implementacja algorytmu szyfrowania DESX (DES eXtended)
 * DESX to wzmocniona wersja algorytmu DES, która używa dodatkowych kluczy whiteningu (k1, k2)
 * aby zwiększyć odporność na ataki brute force i kryptoanalizę różnicową.
 * Schemat działania: C = k2 XOR DES(k, p XOR k1), gdzie:
 * - k to standardowy klucz DES
 * - k1, k2 to dodatkowe 64-bitowe klucze whiteningu
 * - p to tekst jawny, C to szyfrogram
 */
public class DESX {
    private final DES des;         // Instancja standardowego algorytmu DES
    private boolean[] k1;          // Pierwszy klucz whiteningu (pre-whitening)
    private boolean[] k2;          // Drugi klucz whiteningu (post-whitening)

    /**
     * Konstruktor inicjalizuje algorytm DESX z danymi wejściowymi i trzema kluczami
     * @param input Tekst jawny do zaszyfrowania
     * @param key Standardowy klucz DES (64 bity)
     * @param k1 Pierwszy klucz whiteningu (64 bity) używany przed DES
     * @param k2 Drugi klucz whiteningu (64 bity) używany po DES
     */
    public DESX(String input, String key, String k1, String k2) {
        // Inicjalizacja standardowego DES z tekstem jawnym i kluczem podstawowym
        this.des = new DES(input, key);
        // Konwersja kluczy whiteningu z formatu hex na tablice bitów
        this.k1 = DES.hexToBooleanArray(k1);
        this.k2 = DES.hexToBooleanArray(k2);
    }

    /**
     * Gettery i settery
     */
    public DES getDes() {
        return des;
    }

    /**
     * Zwraca pierwszy klucz whiteningu w formacie szesnastkowym
     * @return Reprezentacja hex klucza k1
     */
    public String getK1() {
        return DES.BooleanArrayToHex(k1);
    }

    /**
     * Zwraca drugi klucz whiteningu w formacie szesnastkowym
     * @return Reprezentacja hex klucza k2
     */
    public String getK2() {
        return DES.BooleanArrayToHex(k2);
    }

    /**
     * Ustawia nową wartość pierwszego klucza whiteningu
     * @param k1 Nowy klucz w formacie szesnastkowym
     */
    public void setk1(String k1) {
        this.k1 = DES.hexToBooleanArray(k1);
    }

    /**
     * Ustawia nową wartość drugiego klucza whiteningu
     * @param k2 Nowy klucz w formacie szesnastkowym
     */
    public void setk2(String k2) {
        this.k2 = DES.hexToBooleanArray(k2);
    }

    /**
     * Szyfruje dane wejściowe algorytmem DESX:
     * 1. XOR danych wejściowych z k1 (pre-whitening)
     * 2. Szyfrowanie standardowym DES
     * 3. XOR wyniku z k2 (post-whitening)
     *
     * @return Zaszyfrowany tekst w formacie szesnastkowym
     */
    public boolean[][] encrypt() {
        // Pobranie bloków tekstowych z obiektu DES
        boolean[][] input = des.getInput();
        // Tablica na wyniki operacji pre-whitening (XOR z k1)
        boolean[][] FirstXOR = new boolean[input.length][64];
        // Tablica na wyniki szyfrowania DES
        boolean[][] CipherDES;
        // Tablica na wyniki operacji post-whitening (XOR z k2)
        boolean[][] SecondXOR = new boolean[input.length][64];

        // Pre-whitening: XOR każdego bloku z kluczem k1
        for (int i = 0; i < input.length; i++) {
            FirstXOR[i] = des.xor(input[i], k1);
        }

        // Ustawienie wyników pre-whitening jako dane wejściowe dla DES
        des.setInput(FirstXOR);
        CipherDES = des.encrypt();

        // Post-whitening: XOR każdego zaszyfrowanego bloku z kluczem k2
        for (int i = 0; i < input.length; i++) {
            SecondXOR[i] = des.xor(CipherDES[i], k2);
        }
        return SecondXOR;
    }

    /**
     * Deszyfruje dane zaszyfrowane algorytmem DESX:
     * 1. XOR danych zaszyfrowanych z k2 (odwrócenie post-whiteningu)
     * 2. Deszyfrowanie standardowym DES
     * 3. XOR wyniku z k1 (odwrócenie pre-whiteningu)
     *
     * @param encrypted Zaszyfrowany tekst w formacie szesnastkowym
     * @return Odszyfrowany tekst jawny
     */
    public boolean[][] decrypt(boolean[][] encrypted) {
        // Konwersja szesnastkowego szyfrogramu na bloki bitowe
        int j = 0;
        // Obliczenie liczby bloków 64-bitowych (16 znaków hex = 8 bajtów = 64 bity)
        int blocks = encrypted.length;

        // Odwrócenie post-whiteningu: XOR każdego bloku z kluczem k2
        boolean[][] FirstXOR = new boolean[blocks][64];
        for(int i = 0; i < blocks; i++) {
            FirstXOR[i] = des.xor(encrypted[i], k2);
        }

        // Deszyfrowanie DES
        boolean[][] decrypted = des.decrypt(FirstXOR);

        // Odwrócenie pre-whiteningu: XOR każdego bloku z kluczem k1
        boolean[][] SecondXOR = new boolean[decrypted.length][64];
        for(int i = 0; i < decrypted.length; i++) {
            SecondXOR[i] = des.xor(decrypted[i], k1);
        }
        return SecondXOR;
    }

    /**
     * Generuje losowe wartości dla wszystkich trzech kluczy (DES, k1, k2)
     * używając kryptograficznie bezpiecznego generatora liczb losowych.
     * Ta metoda zwiększa bezpieczeństwo kryptograficzne zapewniając prawdziwie losowe klucze.
     */
    public void genAllKeys() {
        // Użycie kryptograficznie bezpiecznego generatora liczb losowych
        SecureRandom random = new SecureRandom();

        // Generowanie losowego klucza dla standardowego DES
        boolean[] desKey = new boolean[64];
        for (int i = 0; i < 64; i++) {
            desKey[i] = random.nextBoolean();
        }
        des.setKey(desKey);

        // Generowanie losowego klucza k1 (pre-whitening)
        for (int i = 0; i < 64; i++) {
            k1[i] = random.nextBoolean();
        }

        // Generowanie losowego klucza k2 (post-whitening)
        for (int i = 0; i < 64; i++) {
            k2[i] = random.nextBoolean();
        }
    }
}