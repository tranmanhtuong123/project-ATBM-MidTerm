package encode.algorithm;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

/**
 * Hashing
 */
public class Hashing {

    public Hashing() {

    }

    public String hashFile(String fileName, String algorithm) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        byte[] buffer = new byte[1024];
        int count = 0;
        MessageDigest mess = MessageDigest.getInstance(algorithm, "BC");
        FileInputStream bis = new FileInputStream(fileName);
        while ((count = bis.read(buffer)) > 0) {
            mess.update(buffer, 0, count);
        }
        bis.close();
        byte[] hash = mess.digest();
        return new String(Hex.encode(hash));

    }

    public String hashPlainText(String input, String algorithm) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        MessageDigest mess = MessageDigest.getInstance(algorithm, "BC");
        byte[] hash = mess.digest(input.getBytes(StandardCharsets.UTF_8));
        String sha256hex = new String(Hex.encode(hash));
        return sha256hex;

    }

}