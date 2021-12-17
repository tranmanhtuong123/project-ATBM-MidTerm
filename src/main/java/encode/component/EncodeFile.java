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
        String fileInputName = new File(fileInputPath).getName();
        String fileOutputPath = folderOutputPath + File.separator + fileInputName + ".en";

        FileInputStream fis = new FileInputStream(fileInputPath);
        FileOutputStream fos = new FileOutputStream(fileOutputPath);

        Cipher cipher = CipherFile(keyType, keyContent, fis, fos, Cipher.ENCRYPT_MODE, algo, mode,
                padding);
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);

        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = fis.read(buffer)) != -1) {
            cos.write(buffer, 0, n);
        }
        cos.flush();
        cos.close();
        fis.close();

    }

    public static void decrypt(String fileInputPath, String folderOutputPath, String keyType, String keyContent,
            String algo, String mode, String padding) throws Exception {

        Security.addProvider(new BouncyCastleProvider());
        String fileInputName = new File(fileInputPath).getName().replace(".en", "");
        String fileNameNoExt = FilenameUtils.removeExtension(fileInputName);
        String fileNameExt = FilenameUtils.getExtension(fileInputName);
        String fileOutputName = fileNameNoExt + "-de." + fileNameExt;
        String fileOutputPath = folderOutputPath + File.separator + fileOutputName;

        FileInputStream fis = new FileInputStream(fileInputPath);
        FileOutputStream fos = new FileOutputStream(fileOutputPath);

        Cipher cipher = CipherFile(keyType, keyContent, fis, fos, Cipher.DECRYPT_MODE, algo, mode,
                padding);
        CipherInputStream cis = new CipherInputStream(fis, cipher);

        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = cis.read(buffer)) != -1) {
            fos.write(buffer, 0, n);
        }
        fos.flush();
        fos.close();
        cis.close();

    }

    public static Cipher CipherFile(String keyType, String keyContent, FileInputStream fis, FileOutputStream fos,
            int modeOP, String algo, String mode, String padding) throws Exception {

        Key key = null;
        byte[] iv = new byte[16];
        // modeOP ==1 => Encrypt
        if (modeOP == 1) {
            // create iv PlainText
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            secureRandom.nextBytes(iv);
            fos.write(iv);
            fos.flush();
        } else {
            fis.read(iv);
        }

        if (keyType.equals("PlainText")) {
            byte[] decodedKey = Base64.getDecoder().decode(keyContent);
            key = new SecretKeySpec(decodedKey, 0, decodedKey.length, algo);
        } else if (Warehouse.listSymmetricAlgo.contains(algo)) {
            if (keyType.equals("File Key")) {
                key = KeyStore.keySYMRSA(keyContent, algo, modeOP, Warehouse.iv, fos, fis);
            } else {
                key = KeyStore.keySYM(keyType, keyContent, algo, modeOP);
            }
        } else if (Warehouse.listPBEAlgo.contains(algo)) {
            key = KeyStore.keyPBE(keyContent, algo);
        } else {
            key = KeyStore.keyASYMRSA(keyContent, algo, mode, padding, modeOP, Warehouse.iv, fos, fis);
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
            cipher.init(modeOP, key, ivParameterSpec);
        } else if (Warehouse.listPBEAlgo.contains(algo)) {
            byte[] salt = new byte[8];
            PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 10000);
            cipher.init(modeOP, key, pbeParamSpec);
        } else {
            cipher.init(modeOP, key);
        }
        return cipher;
    }

}