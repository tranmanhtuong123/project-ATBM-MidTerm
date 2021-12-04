package encode;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import encode.algorithm.EncodeFile;
import encode.algorithm.Hashing;
import encode.algorithm.EncodePlainText;
import encode.common.KeyStore;
import javafx.concurrent.Task;

public class Main extends Task<String> {
    String srcFile, plainText, destFolder, keyType, keyContent, algorithm, mode, padding, type, tab;
    int modeOP;
    boolean ifFile;

    public Main(String srcFile, String plainText, String destFolder, String keyType, String keyContent,
            String algorithm, String mode, String padding, String type, String tab, int modeOP, boolean ifFile) {
        Security.addProvider(new BouncyCastleProvider());
        this.srcFile = srcFile;
        this.plainText = plainText;
        this.destFolder = destFolder;
        this.keyType = keyType;
        this.keyContent = keyContent;
        this.algorithm = algorithm;
        this.mode = mode;
        this.padding = padding;
        this.type = type;
        this.tab = tab;
        this.modeOP = modeOP;
        this.ifFile = ifFile;

    }

    @Override
    protected String call() throws Exception {
        String result = "";
        updateProgress(1, 2);

        if (tab.equals("cryptography")) {
            if (ifFile) {
                // modeOP ==1 => Encypt
                if (modeOP == 1) {
                    EncodeFile.encrypt(srcFile, destFolder, keyType, keyContent, algorithm, mode, padding);
                } else {
                    EncodeFile.decrypt(srcFile, destFolder, keyType, keyContent, algorithm, mode, padding);
                }
            } else {
                if (modeOP == 1) {
                    // modeOP ==1 => Encypt
                    result = EncodePlainText.encrypt(plainText, keyType, keyContent, algorithm, mode, padding);
                } else {
                    result = EncodePlainText.decrypt(plainText, keyType, keyContent, algorithm, mode, padding);
                }
            }

        } else if (tab.equals("hash")) {
            Hashing hashing = new Hashing();
            if (ifFile) {
                result = (hashing.hashFile(srcFile, algorithm));
            } else {
                result = (hashing.hashPlainText(plainText, algorithm));
            }
        } else {
            result = KeyStore.createKey(type, keyType, keyContent, destFolder, algorithm);
        }
        
        updateProgress(2, 2);
        return result;
    }

}
