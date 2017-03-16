package com.lobobrowser.extension.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lobobrowser.LoboBrowser;
import com.lobobrowser.extension.*;
import org.apache.commons.lang3.Validate;

/**
 * Represents a Java plugin
 */
public abstract class JavaExtension extends ExtensionBase {

    private ExtensionLoader loader = null;
    private LoboBrowser browser = null;
    private File file = null;
    private ExtensionDescriptionFile description = null;
    private File dataFolder = null;
    private ClassLoader classLoader = null;
    private ExtensionLogger logger = null;

}
