package encode.algorithm;

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

// import cryptography.CipherUtils;
import encode.common.KeyStore;
// import fileIO.IOUtils;
import encode.common.Warehouse;

public class EncodeFile {
    private static final int BUFFER_SIZE = 1024 * 4;

    public static void encrypt(String fileInputPath, String folderOuputPath, String keyType, String keyContent,
            String algorithm, String mode, String padding) throws Exception {

        Security.addProvider(new BouncyCastleProvider());
        String fileInputName = new File(fileInputPath).getName();
        String fileOutputPath = folderOuputPath + File.separator + fileInputName + ".en";

        FileInputStream fis = new FileInputStream(fileInputPath);
        FileOutputStream fos = new FileOutputStream(fileOutputPath);

        Cipher cipher = CipherFile(keyType, keyContent, fis, fos, Cipher.ENCRYPT_MODE, algorithm, mode,
                padding);
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);

        byte[] buffer = new byte[BUFFER_SIZE];
        int count = 0;
        int n = 0;
        while (-1 != (n = fis.read(buffer))) {
            cos.write(buffer, 0, n);
            count += n;
        }
        cos.flush();
        cos.close();
        fis.close();
        System.out.println("encrypt: " + count);

    }

    public static void decrypt(String fileInputPath, String folderOuputPath, String keyType, String keyContent,
            String algorithm, String mode, String padding) throws Exception {

        Security.addProvider(new BouncyCastleProvider());
        String fileInputName = new File(fileInputPath).getName().replace(".en", "");
        String fileNameNoExt = FilenameUtils.removeExtension(fileInputName);
        String fileNameExt = FilenameUtils.getExtension(fileInputName);
        String fileOutputName = fileNameNoExt + "-de." + fileNameExt;
        String fileOutputPath = folderOuputPath + File.separator + fileOutputName;

        FileInputStream fis = new FileInputStream(fileInputPath);
        FileOutputStream fos = new FileOutputStream(fileOutputPath);

        Cipher cipher = CipherFile(keyType, keyContent, fis, fos, Cipher.DECRYPT_MODE, algorithm, mode,
                padding);
        CipherInputStream cis = new CipherInputStream(fis, cipher);

        byte[] buffer = new byte[BUFFER_SIZE];
        int count = 0;
        int n = 0;
        while (-1 != (n = cis.read(buffer))) {
            fos.write(buffer, 0, n);
            count += n;
        }
        fos.flush();
        fos.close();
        cis.close();
        System.out.println("decrypt: " + count);

    }

    public static Cipher CipherFile(String keyType, String keyContent, FileInputStream fis, FileOutputStream fos,
            int modeOP, String algorithm, String mode, String padding) throws Exception {

        Key key = null;
        byte[] iv = new byte[16];
        // modeOP ==1 => Encypt
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
            key = new SecretKeySpec(decodedKey, 0, decodedKey.length, algorithm);
        } else if (Warehouse.listSymmetricAlgo.contains(algorithm)) {
            if (keyType.equals("File Key")) {
                key = KeyStore.keySYMRSA(keyContent, algorithm, modeOP, Warehouse.iv, fos, fis);
            } else {
                key = KeyStore.keySYM(keyType, keyContent, algorithm, modeOP);
            }
        } else if (Warehouse.listPBEAlgo.contains(algorithm)) {
            if (keyType.equals("File Key")) {
                key = KeyStore.keyPBERSA(keyContent, algorithm, modeOP, Warehouse.iv, fos, fis);
            } else {
                key = KeyStore.keyPBE(keyContent, algorithm);
            }
        } else {
            key = KeyStore.keyRSAAES(keyContent, algorithm, mode, padding, modeOP, Warehouse.iv, fos, fis);
        }

        String cipherInstance = algorithm + "/" + mode + "/" + padding;
        if (Warehouse.listPBEAlgo.contains(algorithm)) {
            cipherInstance = algorithm;
        } else if (algorithm.equals("RSA")) {
            cipherInstance = "AES" + "/" + mode + "/" + "PKCS5Padding";
        }
        Cipher cipher = Cipher.getInstance(cipherInstance, "BC");
        if (Warehouse.listIVRequired.contains(mode)) {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(Warehouse.iv);
            cipher.init(modeOP, key, ivParameterSpec);
        } else if (Warehouse.listPBEAlgo.contains(algorithm)) {
            byte[] salt = new byte[8];
            PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 42);
            cipher.init(modeOP, key, pbeParamSpec);
        } else {
            cipher.init(modeOP, key);
        }
        return cipher;
    }

}