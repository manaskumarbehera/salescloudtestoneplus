package dk.jyskit.waf.application.services.passwordencryption.pbkdf2;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import lombok.extern.slf4j.Slf4j;
import dk.jyskit.waf.application.services.passwordencryption.PasswordEncryptionService;
import dk.jyskit.waf.application.utils.exceptions.SystemException;

@Slf4j
public class PasswordEncryptionPbkdf2 implements PasswordEncryptionService {

	/* (non-Javadoc)
	 * @see dk.jyskit.services.PasswordEncryptionService#authenticate(java.lang.String, byte[], byte[])
	 */
	@Override
	public boolean authenticate(String attempted, byte[] encrypted, byte[] salt) {
		byte[] encryptedAttemptedPassword = encrypt(attempted, salt);
		return Arrays.equals(encrypted, encryptedAttemptedPassword);
	}

	/* (non-Javadoc)
	 * @see dk.jyskit.services.PasswordEncryptionService#encrypt(java.lang.String, byte[])
	 */
	@Override
	public byte[] encrypt(String password, byte[] salt) {
		try {
			// PBKDF2 with SHA-1 as. Note that the NIST names SHA-1 as an acceptable hashing algorithm for PBKDF2
			String algorithm = "PBKDF2WithHmacSHA1";

			// SHA-1 generates 160 bit hashes
			int derivedKeyLength = 160;

			// The NIST recommends > 1,000 iterations.
			int iterations = 2000;

			KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
			SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

			return f.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			log.error("System error in pasword encryption", e);
			throw new SystemException("System error in pasword encryption", e);
		}
	}

	/* (non-Javadoc)
	 * @see dk.jyskit.services.PasswordEncryptionService#generateSalt()
	 */
	@Override
	public byte[] generateSalt() {
		try {
			// VERY important to use SecureRandom instead of just Random
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

			// Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
			byte[] salt = new byte[8];
			random.nextBytes(salt);

			return salt;
		} catch (NoSuchAlgorithmException e) {
			log.error("System error in pasword encryption", e);
			throw new SystemException("System error in pasword encryption", e);
		}
	}
}