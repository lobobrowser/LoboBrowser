package org.lobobrowser.exceptions;

/**
 * Thrown when attempting to load an invalid Extension file
 */
public class InvalidExtensionException extends Exception {

    /**
     * Constructs a new InvalidExtensionException based on the given Exception
     *
     * @param cause Exception that triggered this Exception
     */
    public InvalidExtensionException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new InvalidExtensionException
     */
    public InvalidExtensionException() {

    }

    /**
     * Constructs a new InvalidExtensionException with the specified detail
     * message and cause.
     *
     * @param message the detail message (which is saved for later retrieval
     *     by the getMessage() method).
     * @param cause the cause (which is saved for later retrieval by the
     *     getCause() method). (A null value is permitted, and indicates that
     *     the cause is nonexistent or unknown.)
     */
    public InvalidExtensionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new InvalidExtensionException with the specified detail
     * message
     *
     * @param message TThe detail message is saved for later retrieval by the
     *     getMessage() method.
     */
    public InvalidExtensionException(final String message) {
        super(message);
    }

}
