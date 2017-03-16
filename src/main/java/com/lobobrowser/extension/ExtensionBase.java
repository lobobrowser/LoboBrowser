package com.lobobrowser.extension;

/**
 * Represents a base {@link Extension}
 * <p>
 * Extend this class if your Extension is not a {@link
 * com.lobobrowser.extension.java.JavaExtension}
 */
public abstract class ExtensionBase implements Extension {

    @Override
    public final int hashCode() {
        return getName().hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Extension)) {
            return false;
        }
        return getName().equals(((Extension) obj).getName());
    }

    public final String getName() {
        return getDescription().getName();
    }

}
