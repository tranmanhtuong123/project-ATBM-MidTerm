package encode.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class KeyStore {
    private static final int PBKDF2_ITERATION_COUNT = 300_000;
    private static final int AES_KEY_LENGTH = 256; // in bits

    public static Key keySYM(String algorithm) throws Exception {
        SecretKey secretKey = KeyGenerator.getInstance(algorithm).generateKey();
        return secretKey;
    }

    public static Key keyASYM(String keyFilePath, String algorithm, int modeOP) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(keyFilePath));
        Key key = null;
        // modeOP ==1 => Encypt
        if (modeOP == 1) {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm, "BC");
            key = keyFactory.generatePublic(keySpec);
        } else {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm, "BC");
            key = keyFactory.generatePrivate(keySpec);
        }
        return key;
    }

    public static Key keyPBE(String password, String algorithm) throws Exception {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm, "BC");
        SecretKey key = keyFactory.generateSecret(keySpec);
        return key;

    }

    public static Key keyHasing(String password, String algorithm) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(password.getBytes("UTF-8"));
        byte[] keyBytes = new byte[16];
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, algorithm);
        return secretKeySpec;
    }

    public static void keyPair(String fileBase) throws Exception {

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        try (FileOutputStream out = new FileOutputStream(fileBase + ".private")) {
            out.write(kp.getPrivate().getEncoded());
        }

        try (FileOutputStream out = new FileOutputStream(fileBase + ".public")) {
            out.write(kp.getPublic().getEncoded());
        }
    }

    public static String createKey(String type, String keyType, String keyContent, String output, String algorithm)
            throws Exception {
        Key key = null;
        if (type.equals("Asymmetric")) {
            keyPair(output + File.separator + keyContent);
            return "";

        } else if (type.equals("Symmetric")) {

            if (keyType.equals("PlainText")) {
                key = keySYM(algorithm);
            }
            if (keyType.equals("PasswordHASH")) {
                key = keyHasing(keyContent, algorithm);
            }
            if (keyType.equals("File Key")) {
                keyPair(output + File.separator + keyContent);
                return "";
            }

        } else {
            key = keyPBE(keyContent, algorithm);
        }
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());

        return encodedKey;
    }

    public static Key keySYM(String keyType, String keyContent, String algorithm, int modeOP) throws Exception {
        Key secretKey = null;

        switch (keyType) {
            case "PlainText":
                byte[] decodedKey = Base64.getDecoder().decode(keyContent);
                secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, algorithm);
                break;
            case "Password":
                secretKey = keySYMPBE(keyContent, algorithm, Warehouse.iv);
                break;
            case "PasswordHASH":
                secretKey = keyHasing(keyContent, algorithm);
                break;
            default:
                break;
        }
        return secretKey;
    }

    public static Key keySYMPBE(String password, String algorithm, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATION_COUNT, AES_KEY_LENGTH);
        byte[] secret = factory.generateSecret(keySpec).getEncoded();
        SecretKey key = new SecretKeySpec(secret, algorithm);
        return key;
    }

    public static Key keyRSAAES(String keyFile, String algorithm, String mode, String padding, int modeOP, byte[] iv,
            OutputStream out, InputStream in) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(keyFile));
        KeyGenerator kgen = KeyGenerator.getInstance("AES", "BC");
        kgen.init(128);
        SecretKey skey = kgen.generateKey();
        Key key = null;
        String cipherInstance = algorithm + "/" + mode + "/" + padding;
        if (modeOP == 1) {
            X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
            key = kf.generatePublic(ks);
            Cipher cipher = Cipher.getInstance(cipherInstance, "BC");
            cipher.init(modeOP, key);
            byte[] b = cipher.doFinal(skey.getEncoded());
            out.write(b);
            out.write(iv);
        } else {
            PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
            key = kf.generatePrivate(ks);
            Cipher cipher = Cipher.getInstance(cipherInstance, "BC");
            cipher.init(modeOP, key);
            byte[] b = new byte[256];
            in.read(b);
            byte[] keyb = cipher.doFinal(b);
            skey = new SecretKeySpec(keyb, algorithm);
            in.read(iv);
            Warehouse.iv = iv;
        }
        return skey;
    }

    public static Key keyPBERSA(String keyFile, String algorithm, int modeOP, byte[] iv, OutputStream out,
            InputStream in) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(keyFile));
        PBEKeySpec keySpec = new PBEKeySpec("noobtohero".toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm, "BC");
        SecretKey skey = keyFactory.generateSecret(keySpec);
        Key key = null;
        if (modeOP == 1) {
            X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
            key = kf.generatePublic(ks);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
            cipher.init(modeOP, key);
            byte[] b = cipher.doFinal(skey.getEncoded());
            out.write(b);
            out.write(iv);
        } else {
            PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
            key = kf.generatePrivate(ks);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
            cipher.init(modeOP, key);
            byte[] b = new byte[256];
            in.read(b);
            byte[] keyb = cipher.doFinal(b);
            skey = new SecretKeySpec(keyb, algorithm);
            in.read(iv);
            Warehouse.iv = iv;
        }
        return skey;

    }

    public static Key keySYMRSA(String keyFile, String algorithm, int modeOP, byte[] iv, OutputStream out,
            InputStream in) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(keyFile));
        KeyGenerator kgen = KeyGenerator.getInstance(algorithm, "BC");
        kgen.init(128);
        SecretKey skey = kgen.generateKey();
        Key key = null;
        if (modeOP == 1) {
            X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
            key = kf.generatePublic(ks);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
            cipher.init(modeOP, key);
            byte[] b = cipher.doFinal(skey.getEncoded());
            out.write(b);
            out.write(iv);
        } else {
            PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
            key = kf.generatePrivate(ks);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
            cipher.init(modeOP, key);
            byte[] b = new byte[256];
            in.read(b);
            byte[] keyb = cipher.doFinal(b);
            skey = new SecretKeySpec(keyb, algorithm);
            in.read(iv);
            Warehouse.iv = iv;
        }
        return skey;
    }

}
