package encode.component;

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
        Key key = null;
        byte[] textBytes = plainText.getBytes();
        SecureRandom random = new SecureRandom();

        random.nextBytes(Warehouse.iv);
        if (Warehouse.listSymmetricAlgo.contains(algo)) {
            key = KeyStore.keySYM(keyType, keyContent, algo, Cipher.ENCRYPT_MODE);
        } else if (Warehouse.listPBEAlgo.contains(algo)) {
            key = KeyStore.keyPBE(keyContent, algo);
        } else {
            key = KeyStore.keyASYM(keyContent, algo, Cipher.ENCRYPT_MODE);
        }

        String cipherInstance = algo + "/" + mode + "/" + padding;
        if (Warehouse.listPBEAlgo.contains(algo)) {
            cipherInstance = algo;
        }

        Cipher cipher = Cipher.getInstance(cipherInstance, "BC");
        if (Warehouse.listIVRequired.contains(mode)) {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(Warehouse.iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        } else if (Warehouse.listPBEAlgo.contains(algo)) {
            byte[] salt = new byte[8];
            PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 10000);
            cipher.init(Cipher.ENCRYPT_MODE, key, pbeParamSpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        }

        byte[] encrypted = cipher.doFinal(textBytes);
        byte[] encryptedIVAndText = encrypted;
        if (Warehouse.listSymmetricAlgo.contains(algo)) {
            if (Warehouse.listIVRequired.contains(mode)) {
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
        Key key = null;
        byte[] textBytes = Base64.getDecoder().decode(encodedPlanText);
        byte[] encryptedBytes = textBytes;
        if (Warehouse.listSymmetricAlgo.contains(algo)) {
            if (Warehouse.listIVRequired.contains(mode)) {
                int encryptedSize = textBytes.length - Warehouse.ivSize;
                encryptedBytes = new byte[encryptedSize];
                System.arraycopy(textBytes, Warehouse.ivSize, encryptedBytes, 0, encryptedSize);

            }
        }

        System.arraycopy(textBytes, 0, Warehouse.iv, 0, Warehouse.iv.length);
        if (Warehouse.listSymmetricAlgo.contains(algo)) {
            key = KeyStore.keySYM(keyType, keyContent, algo, Cipher.DECRYPT_MODE);
        } else if (Warehouse.listPBEAlgo.contains(algo)) {
            key = KeyStore.keyPBE(keyContent, algo);
        } else {
            key = KeyStore.keyASYM(keyContent, algo, Cipher.DECRYPT_MODE);
        }

        String cipherInstance = algo + "/" + mode + "/" + padding;
        if (Warehouse.listPBEAlgo.contains(algo)) {
            cipherInstance = algo;
        }

        Cipher cipher = Cipher.getInstance(cipherInstance, "BC");
        if (Warehouse.listIVRequired.contains(mode)) {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(Warehouse.iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
        } else if (Warehouse.listPBEAlgo.contains(algo)) {
            byte[] salt = new byte[8];
            PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 10000);
            cipher.init(Cipher.DECRYPT_MODE, key, pbeParamSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key);
        }

        byte[] decoded = cipher.doFinal(encryptedBytes);
        return new String(decoded);

    }

}