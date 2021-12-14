package encode.common;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import encode.algorithm.EncodeFile;
import encode.algorithm.Hashing;
import encode.algorithm.EncodePlainText;
import javafx.concurrent.Task;

public class Main extends Task<String> {
    String source, plainText, dest, keyType, keyContent, algorithm, mode, padding, type, button;
    int modeOP;
    boolean ifFile;

    public Main(String source, String plainText, String dest, String keyType, String keyContent,
            String algorithm, String mode, String padding, String type, String button, int modeOP, boolean ifFile) {
        Security.addProvider(new BouncyCastleProvider());
        this.source = source;
        this.plainText = plainText;
        this.dest = dest;
        this.keyType = keyType;
        this.keyContent = keyContent;
        this.algorithm = algorithm;
        this.mode = mode;
        this.padding = padding;
        this.type = type;
        this.button = button;
        this.modeOP = modeOP;
        this.ifFile = ifFile;

    }

    public Main() {

    }

    @Override
    protected String call() throws Exception {
        String result = "";
        updateProgress(1, 2);

        if (button.equals("cryptography")) {
            if (ifFile) {
                // modeOP ==1 => Encypt
                if (modeOP == 1) {
                    EncodeFile.encrypt(source, dest, keyType, keyContent, algorithm, mode, padding);
                } else {
                    EncodeFile.decrypt(source, dest, keyType, keyContent, algorithm, mode, padding);
                }
            } else {
                if (modeOP == 1) {
                    // modeOP ==1 => Encypt
                    result = EncodePlainText.encrypt(plainText, keyType, keyContent, algorithm, mode, padding);
                } else {
                    result = EncodePlainText.decrypt(plainText, keyType, keyContent, algorithm, mode, padding);
                }
            }

        } else if (button.equals("hashing")) {
            Hashing hashing = new Hashing();
            if (ifFile) {
                result = (hashing.hashFile(source, algorithm));
            } else {
                result = (hashing.hashPlainText(plainText, algorithm));
            }
        } else {
            result = KeyStore.generatingKey(type, keyType, keyContent, dest, algorithm);
        }

        updateProgress(2, 2);
        return result;
    }

}
