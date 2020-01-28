package dk.jyskit.salescloud.application;

public interface Initializer {
	String getName();
	boolean needsInitialization();
	void initialize();
	void makeUpgrades();
}
