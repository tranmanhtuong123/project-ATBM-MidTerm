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

    public static String createKey(String type, String keyType, String keyContent, String output, String algo)
            throws Exception {
        Key key = null;
        String keyPair = output + File.separator + keyContent;

        if (type.equals("Asymmetric")) {

            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();
            try (FileOutputStream out = new FileOutputStream(keyPair + ".private")) {
                out.write(kp.getPrivate().getEncoded());
            }

            try (FileOutputStream out = new FileOutputStream(keyPair + ".public")) {
                out.write(kp.getPublic().getEncoded());
            }
            return "";

        } else if (type.equals("Symmetric")) {

            if (keyType.equals("PlainText")) {
                key = keySYM(algo);
            }
            if (keyType.equals("PasswordHASH")) {
                key = keyHashing(keyContent, algo);
            }
            if (keyType.equals("File Key")) {

                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                kpg.initialize(2048);
                KeyPair kp = kpg.generateKeyPair();
                try (FileOutputStream out = new FileOutputStream(keyPair + ".private")) {
                    out.write(kp.getPrivate().getEncoded());
                }

                try (FileOutputStream out = new FileOutputStream(keyPair + ".public")) {
                    out.write(kp.getPublic().getEncoded());
                }
                return "";
            }

        } else {
            key = keyPBE(keyContent, algo);
        }
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());

        return encodedKey;
    }

    public static Key keySYM(String keyType, String keyContent, String algo, int modeOP) throws Exception {
        Key secretKey = null;

        switch (keyType) {
            case "PlainText":
                byte[] decodedKey = Base64.getDecoder().decode(keyContent);
                secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, algo);
                break;
            case "Password":
                secretKey = keySYMPBE(keyContent, algo, Warehouse.iv);
                break;
            case "PasswordHASH":
                secretKey = keyHashing(keyContent, algo);
                break;
            default:
                break;
        }
        return secretKey;
    }

    // PlainText
    public static Key keySYM(String algo) throws Exception {
        SecretKey secretKey = KeyGenerator.getInstance(algo).generateKey();
        return secretKey;
    }

    public static Key keyASYM(String keyFilePath, String algo, int modeOP) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(keyFilePath));
        Key key = null;
        // modeOP ==1 => Encypt
        if (modeOP == 1) {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
            KeyFactory keyFactory = KeyFactory.getInstance(algo, "BC");
            key = keyFactory.generatePublic(keySpec);
        } else {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            KeyFactory keyFactory = KeyFactory.getInstance(algo, "BC");
            key = keyFactory.generatePrivate(keySpec);
        }
        return key;
    }

    public static Key keyPBE(String password, String algo) throws Exception {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algo, "BC");
        SecretKey key = keyFactory.generateSecret(keySpec);
        return key;

    }

    // File
    public static Key keySYMPBE(String password, String algo, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 300_000, 256);
        byte[] secret = factory.generateSecret(keySpec).getEncoded();
        SecretKey key = new SecretKeySpec(secret, algo);
        return key;
    }

    public static Key keyRSAAES(String keyFile, String algo, String mode, String padding, int modeOP, byte[] iv,
            OutputStream out, InputStream in) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(keyFile));
        KeyGenerator kgen = KeyGenerator.getInstance("AES", "BC");
        kgen.init(128);
        SecretKey skey = kgen.generateKey();
        Key key = null;
        String cipherInstance = algo + "/" + mode + "/" + padding;
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
            skey = new SecretKeySpec(keyb, algo);
            in.read(iv);
            Warehouse.iv = iv;
        }
        return skey;
    }

    public static Key keySYMRSA(String keyFile, String algo, int modeOP, byte[] iv, OutputStream out,
            InputStream in) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(keyFile));
        KeyGenerator kgen = KeyGenerator.getInstance(algo, "BC");
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
            skey = new SecretKeySpec(keyb, algo);
            in.read(iv);
            Warehouse.iv = iv;
        }
        return skey;
    }

    // Hashing
    public static Key keyHashing(String password, String algo) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(password.getBytes("UTF-8"));
        byte[] keyBytes = new byte[16];
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, algo);
        return secretKeySpec;
    }
    
}
