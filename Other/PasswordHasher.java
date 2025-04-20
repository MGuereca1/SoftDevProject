// used code below to get hashed password and salt and manually added it the database

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;

public class PasswordHasher {
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    public static String hashPassword(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hashed = skf.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hashed);
    }

    public static byte[] generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            byte[] salt = generateSalt();
            String hash = hashPassword(password, salt);

            System.out.println("Salt: " + Base64.getEncoder().encodeToString(salt));
            System.out.println("Hash: " + hash);
        }
    }
}




