package copo.api;

/**
 * Thrown when digital storage is determined to be inconsistent in some way.
 * This means a bug in a DigitalStorage has corrupted the state of a drive.
 */
public class StorageInconsistentException extends RuntimeException {
	private static final long serialVersionUID = -6519333692683580672L;
	
	public StorageInconsistentException() {
		super();
	}

	public StorageInconsistentException(String message, Throwable cause) {
		super(message, cause);
	}

	public StorageInconsistentException(String message) {
		super(message);
	}

	public StorageInconsistentException(Throwable cause) {
		super(cause);
	}

}
