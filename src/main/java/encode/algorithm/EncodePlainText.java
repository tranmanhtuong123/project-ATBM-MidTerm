package encode.algorithm;

import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import encode.common.KeyStore;
import encode.common.Warehouse;

public class EncodePlainText {

    public EncodePlainText() {

    }

    public static String encrypt(String plainText, String keyType, String keyContent, String algo, String mode,
            String padding) throws Exception {

        Security.addProvider(new BouncyCastleProvider());
        byte[] clean = plainText.getBytes();

        Cipher cipher = CipherPlainText(keyType, keyContent, clean, Cipher.ENCRYPT_MODE, algo, mode,
                padding);

        byte[] encrypted = cipher.doFinal(clean);
        byte[] encryptedIVAndText = encrypted;
        if (Warehouse.listSymmetricAlgo.contains(algo)) {
            if (Warehouse.listIVRequired.contains(mode) || keyType.equals("XTC")) {
                encryptedIVAndText = new byte[Warehouse.ivSize + encrypted.length];
                System.arraycopy(Warehouse.iv, 0, encryptedIVAndText, 0, Warehouse.ivSize);
                System.arraycopy(encrypted, 0, encryptedIVAndText, Warehouse.ivSize, encrypted.length);
            }
        }
        String encoded = Base64.getEncoder().encodeToString(encryptedIVAndText);
        return encoded;
    }

    public static String decrypt(String encodedPlanText, String keyType, String keyContent, String algo,
            String mode, String padding) throws Exception {

        Security.addProvider(new BouncyCastleProvider());
        byte[] encryptedTextBytes = Base64.getDecoder().decode(encodedPlanText);
        byte[] encryptedBytes = encryptedTextBytes;
        if (Warehouse.listSymmetricAlgo.contains(algo)) {
            if (Warehouse.listIVRequired.contains(mode) || keyType.equals("XTC")) {

                int encryptedSize = encryptedTextBytes.length - Warehouse.ivSize;
                encryptedBytes = new byte[encryptedSize];
                System.arraycopy(encryptedTextBytes, Warehouse.ivSize, encryptedBytes, 0, encryptedSize);

            }
        }
        Cipher cipher = CipherPlainText(keyType, keyContent, encryptedTextBytes, Cipher.DECRYPT_MODE,
                algo, mode, padding);
        byte[] decoded = cipher.doFinal(encryptedBytes);
        return new String(decoded);

    }

    public static Cipher CipherPlainText(String keyType, String keyContent, byte[] input, int modeOP, String algo,
            String mode, String padding) throws Exception {

        Key key = null;
        // modeOP ==1 => Encrypt
        if (modeOP == 1) {
            // create iv PlainText
            SecureRandom random = new SecureRandom();
            random.nextBytes(Warehouse.iv);
        } else {
            Warehouse.iv = new byte[Warehouse.ivSize];
            System.arraycopy(input, 0, Warehouse.iv, 0, Warehouse.iv.length);// input = "encrypted iv TextBytes"
        }

        if (Warehouse.listSymmetricAlgo.contains(algo)) {
            key = KeyStore.keySYM(keyType, keyContent, algo, modeOP);
        } else if (Warehouse.listPBEAlgo.contains(algo)) {
            key = KeyStore.keyPBE(keyContent, algo);
        } else {
            key = KeyStore.keyASYM(keyContent, algo, modeOP);
        }

        String cipherInstance = algo + "/" + mode + "/" + padding;
        if (Warehouse.listPBEAlgo.contains(algo)) {
            cipherInstance = algo;
        }

        Cipher cipher = Cipher.getInstance(cipherInstance, "BC");
        if (Warehouse.listIVRequired.contains(mode)) {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(Warehouse.iv);
            cipher.init(modeOP, key, ivParameterSpec);
        } else if (Warehouse.listPBEAlgo.contains(algo)) {
            byte[] salt = new byte[8];
            PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 42);
            cipher.init(modeOP, key, pbeParamSpec);
        } else {
            cipher.init(modeOP, key);
        }
        return cipher;
    }
}