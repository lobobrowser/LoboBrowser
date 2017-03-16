package com.lobobrowser.extension;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * The ExtensionLogger class is a modified {@link Logger} that prepends all
 * logging calls with the name of the Extension doing the logging. The API for
 * ExtensionLogger is exactly the same as {@link Logger}.
 *
 * @see Logger
 */
public class ExtensionLogger extends Logger {

    private String extensionName;

    /**
     * Creates a new ExtensionLogger that extracts the name from a Extension.
     *
     * @param context A reference to the Extension
     */
    public ExtensionLogger(Extension context) {
        super(context.getClass().getCanonicalName(), null);
        String prefix = context.getDescription().getName();
        extensionName = prefix != null ? new StringBuilder().append("[").append(prefix).append("] ").toString() : "[" + context.getDescription().getName() + "] ";
        setParent(context.getBrowser().getLogger());
        setLevel(Level.ALL);
    }

    @Override
    public void log(LogRecord logRecord) {
        logRecord.setMessage(extensionName + logRecord.getMessage());
        super.log(logRecord);
    }

}
