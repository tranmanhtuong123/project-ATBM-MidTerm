package encode.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FilenameUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import encode.common.KeyStore;
import encode.common.Warehouse;

public class EncodeFile {

    public EncodeFile() {

    }

    public static void encrypt(String fileInputPath, String folderOutputPath, String keyType, String keyContent,
            String algo, String mode, String padding) throws Exception {

        Security.addProvider(new BouncyCastleProvider());
        Key key = null;
        String fileInputName = new File(fileInputPath).getName();
        String fileOutputPath = folderOutputPath + File.separator + fileInputName + ".en";

        FileInputStream fis = new FileInputStream(fileInputPath);
        FileOutputStream fos = new FileOutputStream(fileOutputPath);
        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        secureRandom.nextBytes(Warehouse.iv);
        fos.write(Warehouse.iv);
        fos.flush();
        if (keyType.equals("PlainText")) {
            byte[] decodedKey = Base64.getDecoder().decode(keyContent);
            key = new SecretKeySpec(decodedKey, 0, decodedKey.length, algo);
        } else if (Warehouse.listSymmetricAlgo.contains(algo)) {
            if (keyType.equals("File Key")) {
                key = KeyStore.keySYMRSA(keyContent, algo, Cipher.ENCRYPT_MODE, Warehouse.iv, fos, fis);
            } else {
                key = KeyStore.keySYM(keyType, keyContent, algo, Cipher.ENCRYPT_MODE);
            }
        } else if (Warehouse.listPBEAlgo.contains(algo)) {
            key = KeyStore.keyPBE(keyContent, algo);
        } else {
            key = KeyStore.keyASYMRSA(keyContent, algo, mode, padding, Cipher.ENCRYPT_MODE, Warehouse.iv, fos, fis);
        }

        String cipherInstance = algo + "/" + mode + "/" + padding;
        if (Warehouse.listPBEAlgo.contains(algo)) {
            cipherInstance = algo;
        } else if (algo.equals("RSA")) {
            cipherInstance = "AES" + "/" + mode + "/" + "PKCS5Padding";
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

        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        try {
            byte[] buffer = new byte[1024];
            int n = 0;
            while ((n = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, n);
            }
            cos.flush();
            cos.close();
            fis.close();
        } catch (Exception e) {
            cos.close();
            fis.close();
            e.printStackTrace();
        }

    }

    public static void decrypt(String fileInputPath, String folderOutputPath, String keyType, String keyContent,
            String algo, String mode, String padding) throws Exception {

        Security.addProvider(new BouncyCastleProvider());
        Key key = null;
        String fileInputName = new File(fileInputPath).getName().replace(".en", "");
        String fileNameNoExt = FilenameUtils.removeExtension(fileInputName);
        String fileNameExt = FilenameUtils.getExtension(fileInputName);
        String fileOutputName = fileNameNoExt + "-de." + fileNameExt;
        String fileOutputPath = folderOutputPath + File.separator + fileOutputName;

        FileInputStream fis = new FileInputStream(fileInputPath);
        FileOutputStream fos = new FileOutputStream(fileOutputPath);
        fis.read(Warehouse.iv);
        if (keyType.equals("PlainText")) {
            byte[] decodedKey = Base64.getDecoder().decode(keyContent);
            key = new SecretKeySpec(decodedKey, 0, decodedKey.length, algo);
        } else if (Warehouse.listSymmetricAlgo.contains(algo)) {
            if (keyType.equals("File Key")) {
                key = KeyStore.keySYMRSA(keyContent, algo, Cipher.DECRYPT_MODE, Warehouse.iv, fos, fis);
            } else {
                key = KeyStore.keySYM(keyType, keyContent, algo, Cipher.DECRYPT_MODE);
            }
        } else if (Warehouse.listPBEAlgo.contains(algo)) {
            key = KeyStore.keyPBE(keyContent, algo);
        } else {
            key = KeyStore.keyASYMRSA(keyContent, algo, mode, padding, Cipher.DECRYPT_MODE, Warehouse.iv, fos, fis);
        }

        String cipherInstance = algo + "/" + mode + "/" + padding;
        if (Warehouse.listPBEAlgo.contains(algo)) {
            cipherInstance = algo;
        } else if (algo.equals("RSA")) {
            cipherInstance = "AES" + "/" + mode + "/" + "PKCS5Padding";
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

        CipherInputStream cis = new CipherInputStream(fis, cipher);
        try {
            byte[] buffer = new byte[1024];
            int n = 0;
            while ((n = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, n);
            }
            fos.flush();
            fos.close();
            cis.close();
        } catch (Exception e) {
            fos.close();
            cis.close();
            e.printStackTrace();
        }

    }

}