package encode.DigitalSignature;

import java.io.*;
import java.security.*;
import java.security.spec.*;

public class VerifyDigitalSignature {
	public static void main(String args[]) {
		String digital = "C:\\Users\\Admin\\Desktop\\test\\ATBM\\dig.txt";
		String signature = "C:\\Users\\Admin\\Desktop\\test\\ATBM\\signature.private";// privateKey
		String publickey = "C:\\Users\\Admin\\Desktop\\test\\ATBM\\publickey.public"; // publicKey

		/* Verify a DSA signature */
//		if (args.length != 3) {
//			System.out.println("Usage: publickeyfile signaturefile datafile");
//		} else
		try {
			/* import encoded public key */
			FileInputStream keyfis = new FileInputStream(publickey);
			byte[] encKey = new byte[keyfis.available()];
			keyfis.read(encKey);
			keyfis.close();
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
			KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
			PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
			/* input the signature bytes */
			FileInputStream sigfis = new FileInputStream(signature);
			byte[] sigToVerify = new byte[sigfis.available()];
			sigfis.read(sigToVerify);
			sigfis.close();
			/* create a Signature object and initialize it with the public key */
			Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
			sig.initVerify(pubKey);
			/* Update and verify the data */
			FileInputStream datafis = new FileInputStream(digital);
			BufferedInputStream bufin = new BufferedInputStream(datafis);
			byte[] buffer = new byte[1024];
			int len;
			while (bufin.available() != 0) {
				len = bufin.read(buffer);
				sig.update(buffer, 0, len);
			}
			;
			bufin.close();
			boolean verifies = sig.verify(sigToVerify);
			System.out.println("signature verifies: " + verifies);
		} catch (Exception e) {
			System.err.println("Caught exception " + e.toString());
		}
		;
	}
}
