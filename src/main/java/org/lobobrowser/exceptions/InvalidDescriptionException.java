package org.lobobrowser.exceptions;

/**
 * Thrown when attempting to load an invalid ExtensionDescriptionFile
 */
public class InvalidDescriptionException extends Exception {

    /**
     * Constructs a new InvalidDescriptionException based on the given
     * Exception
     *
     * @param message Brief message explaining the cause of the exception
     * @param cause Exception that triggered this Exception
     */
    public InvalidDescriptionException(final Throwable cause, final String message) {
        super(message, cause);
    }

    /**
     * Constructs a new InvalidDescriptionException based on the given
     * Exception
     *
     * @param cause Exception that triggered this Exception
     */
    public InvalidDescriptionException(final Throwable cause) {
        super("Invalid extension.yml", cause);
    }

    /**
     * Constructs a new InvalidDescriptionException with the given message
     *
     * @param message Brief message explaining the cause of the exception
     */
    public InvalidDescriptionException(final String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidDescriptionException
     */
    public InvalidDescriptionException() {
        super("Invalid extension.yml");
    }

}
