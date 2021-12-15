package encode.algorithm;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

public class Hashing {

    public Hashing() {

    }

    public String hashFile(String source, String algo) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        byte[] buffer = new byte[1024];
        int count = 0;
        MessageDigest md = MessageDigest.getInstance(algo, "BC");
        FileInputStream bis = new FileInputStream(source);
        while ((count = bis.read(buffer)) > 0) {
            md.update(buffer, 0, count);
        }
        bis.close();
        byte[] hash = md.digest();
        return new String(Hex.encode(hash));

    }

    public String hashPlainText(String plainText, String algo) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        MessageDigest md = MessageDigest.getInstance(algo, "BC");
        byte[] hash = md.digest(plainText.getBytes(StandardCharsets.UTF_8));
        return new String(Hex.encode(hash));

    }

}