package dk.jyskit.waf.application.services.passwordencryption;

public interface PasswordEncryptionService {

	public abstract boolean authenticate(String attempted, byte[] encrypted, byte[] salt);

	public abstract byte[] encrypt(String password, byte[] salt);

	public abstract byte[] generateSalt();

}