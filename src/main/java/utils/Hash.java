package utils;

import database.DatabaseService;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
//this was planned to use for hashing sensitive information, but unfortunately bits to string didn't really work
public class Hash {
    private static Hash instance;

    private Hash() {
    }

    public static Hash getInstance() {
        if (Hash.instance == null) {
            Hash.instance = new Hash();
        }
        return Hash.instance;
    }

    public String hashPassword(String password){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            String hashString = new String(hash, StandardCharsets.UTF_8);

            return hashString;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return "error";
    }
}
