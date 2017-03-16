package com.lobobrowser.extension;
import java.util.Set;

import com.lobobrowser.extension.java.JavaExtension;

/**
 * Represents a concept that a Extension is aware of.
 * <p>
 * The internal representation may be singleton, or be a parameterized
 * instance, but must be immutable.
 */
public interface ExtensionAwareness {

    /**
     * Each entry here represents a particular extension's awareness. These can
     * be checked by using {@link ExtensionDescriptionFile#getAwareness()}.{@link
     * Set#contains(Object) contains(flag)}.
     */
    public enum Flags implements ExtensionAwareness {
        /**
         * This specifies that all (text) resources stored in a plugin's jar
         * use UTF-8 encoding.
         *
         * @see JavaExtension#getTextResource(String)
         */
        UTF8,
        ;
    }

}
