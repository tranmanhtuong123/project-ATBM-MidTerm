package encode.common;

import java.util.Arrays;
import java.util.List;

public class Warehouse {
        public static int ivSize = 16;

        public static byte[] iv = new byte[ivSize];

        public static List<String> listIVRequired = Arrays.asList("CBC", "OFB", "CFB", "SIC", "CTS", "CCM", "EAX",
                        "GCM", "GCM-SIV", "OCB");
        // mode Symmetric & Asymmetric
        public static List<String> listSymmetricMode = Arrays.asList("ECB", "CBC", "OFB", "SIC", "CTS", "FF1", "FF3-1",
                        "GOFB", "GCFB", "CCM", "EAX", "GCM", "GCM-SIV", "OCB");
        // algorithm Symmetric
        public static List<String> listSymmetricAlgo = Arrays.asList("AES", "ARIA", "Blowfish", "Camellia", "CAST5",
                        "CAST6", "DES", "GCM", "RC2", "RC5");
        // algorithm Asymmetric
        public static List<String> listASymmetricAlgo = Arrays.asList("RSA");
        // algorithm PBE
        public static List<String> listPBEAlgo = Arrays.asList("PBEWithMD2AndDES", "PBEWithMD5AndDES");
        // algorithm Hashing
        public static List<String> listHashAlgo = Arrays.asList("Blake2b-160", "Blake2b-256", "Blake2b-384",
                        "Blake2b-512", "Blake2s-128", "Blake2s-160", "Blake2s-224", "Blake2s-256", "DSTU7564-256",
                        "DSTU7564-384", "DSTU7564-512", "GOST3411", "GOST3411-2012-256", "GOST3411-2012-512",
                        "Keccak-224", "Keccak-288", "Keccak-256", "Keccak-384", "Keccak-512", "MD2", "MD4", "MD5",
                        "RipeMD128", "RipeMD160", "RipeMD256", "RipeMD320", "SHA1", "SHA-224", "SHA-256", "SHA-384",
                        "SHA-512", "SHA3-224", "SHA3-256", "SHA3-384", "SHA3-512", "Skein-256-*", "Skein-512-*",
                        "Skein-1024-*", "SM3", "Tiger", "Whirlpool");
        // padding Symmetric
        public static List<String> listDefualtPadding = Arrays.asList("PKCS7Padding", "PKCS5Padding", "ISO10126Padding",
                        "ISO7816-4Padding");
        // padding Asymmetric
        public static List<String> listRSAPadding = Arrays.asList("PKCS1Padding", "OAEPWithMD5AndMGF1Padding",
                        "OAEPWithSHA1AndMGF1Padding", "OAEPWithSHA224AndMGF1Padding", "OAEPWithSHA256AndMGF1Padding",
                        "OAEPWithSHA384AndMGF1Padding", "OAEPWithSHA512AndMGF1Padding",
                        "OAEPWithSHA3-224AndMGF1Padding", "OAEPWithSHA3-256AndMGF1Padding",
                        "OAEPWithSHA3-384AndMGF1Padding");
        // Key typte PBE
        public static List<String> listKeyTypePlainText = Arrays.asList("PasswordHASH");
        // Key typte Symmetric
        public static List<String> listKeyTypeFile = Arrays.asList("PlainText", "Password", "PasswordHASH", "File Key");
        // GenerateKey
        public static List<String> listGenerateKey = Arrays.asList("Asymmetric", "Symmetric", "PBE");
        public static List<String> listKeyTypeRSA = Arrays.asList("File Key");
        public static List<String> listKeyTypePBE = Arrays.asList("Password");
        public static List<String> listKeyTypeSYM = Arrays.asList("PlainText", "PasswordHASH", "File Key");
        // Key length
        public static List<String> listKeyLengthASYM = Arrays.asList("512", "1024", "2048");
        public static List<String> listKeyLengthSYM = Arrays.asList("128", "192", "256");

}
