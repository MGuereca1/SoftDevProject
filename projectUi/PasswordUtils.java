package com.example;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

public class PasswordUtils {
    
    public static String hashPassword(String password, byte[] salt) throws Exception{
        int iterations = 65536; //iteration count
        int keyLength = 256;    // key length
    

    PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    byte[] hash = skf.generateSecret(spec).getEncoded();

    return Base64.getEncoder().encodeToString(hash);
    }

    public static boolean isPasswordCorrect(String enteredPassword, String storedSalt, String storedHash){
        try {
            //decode stored salt from base64
            byte[] salt = Base64.getDecoder().decode(storedSalt);

            // hash entered password
            String enteredHash = hashPassword(enteredPassword, salt);

            return enteredHash.equals(storedHash);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}


