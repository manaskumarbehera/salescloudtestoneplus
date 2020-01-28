package dk.jyskit.waf.utils.encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class SimpleStringCipher {
	public static final String STANDARD_DELIMITER 	= "¤";
	
	private static byte[] linebreak = {}; // Remove Base64 encoder default linebreak
	private static String secret = "tvnJanufg9gh5392"; // secret key length must be 16
	private static SecretKey key;
	private static Cipher cipher;
	private static Base64 coder;

	static { 
		try {
			key = new SecretKeySpec(secret.getBytes(), "AES");
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
			coder = new Base64(32, linebreak, true);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static synchronized String encrypt(String plainText)
			throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] cipherText = cipher.doFinal(plainText.getBytes());
		return new String(coder.encode(cipherText), "UTF-8");
	}

	public static synchronized String decrypt(String codedText)
			throws Exception {
		byte[] encypted = coder.decode(codedText.getBytes());
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decrypted = cipher.doFinal(encypted);
		return new String(decrypted, "UTF-8");
	}

	public static void main(String[] args) throws Exception {
		String s = "1a2gfsfgs fgsdf gsdfg  sdfg sdfgd3";
		String s1 = SimpleStringCipher.encrypt(s);
		System.out.println(s1);
		System.out.println(SimpleStringCipher.decrypt(s1));
		
		System.out.println(SimpleStringCipher.decrypt("rjwj85PO8K4s9krEd0GVtVJQ5x4YFbwV2bvihF5sOcA"));
		
		s = SimpleStringCipher.encrypt("TourOperatorUserRole¤");
		System.out.println(SimpleStringCipher.decrypt(s));
	}
}