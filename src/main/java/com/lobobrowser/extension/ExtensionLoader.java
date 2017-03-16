package com.lobobrowser.extension;

import com.lobobrowser.exceptions.InvalidDescriptionException;
import com.lobobrowser.exceptions.InvalidExtensionException;
import com.lobobrowser.exceptions.UnknownDependencyException;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Represents a Extension loader, which handles direct access to specific types
 * of Extensions
 */
public interface ExtensionLoader {

    /**
     * Loads the Extension contained in the specified file
     *
     * @param file File to attempt to load
     * @return Extension that was contained in the specified file, or null if
     *     unsuccessful
     * @throws InvalidExtensionException Thrown when the specified file is not a
     *     Extension
     * @throws UnknownDependencyException If a required dependency could not
     *     be found
     */
    public Extension loadExtension(File file) throws InvalidExtensionException, UnknownDependencyException;

    /**
     * Loads a ExtensionDescriptionFile from the specified file
     *
     * @param file File to attempt to load from
     * @return A new ExtensionDescriptionFile loaded from the extension.properties in the
     *     specified file
     * @throws InvalidDescriptionException If the Extension description file
     *     could not be created
     */
    public ExtensionDescriptionFile getExtensionDescription(File file) throws InvalidDescriptionException;

    /**
     * Returns a list of all filename filters expected by this ExtensionLoader
     *
     * @return The filters
     */
    public Pattern[] getExtensionFileFilters();

    /**
     * Enables the specified Extension
     * <p>
     * Attempting to enable a Extension that is already enabled will have no
     * effect
     *
     * @param extension Extension to enable
     */
    public void enableExtension(Extension extension);

    /**
     * Disables the specified Extension
     * <p>
     * Attempting to disable a Extension that is not enabled will have no effect
     *
     * @param extension Extension to disable
     */
    public void disableExtension(Extension extension);

}
