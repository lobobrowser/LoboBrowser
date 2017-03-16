package com.lobobrowser.extension;

import com.lobobrowser.LoboBrowser;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

public interface Extension {

    /**
     * Returns the folder that the Extension data's files are located in. The
     * folder may not yet exist.
     *
     * @return The folder
     */
    public File getDataFolder();

    /**
     * Returns the extension.yml file containing the details for this plugin
     *
     * @return Contents of the extension.yml file
     */
    public ExtensionDescriptionFile getDescription();

    /**
     * Gets a {@link FileConfiguration} for this Extension, read through
     * "config.yml"
     * <p>
     * If there is a default config.yml embedded in this Extension, it will be
     * provided as a default for this Configuration.
     *
     * @return Extension configuration
     */
    //public FileConfiguration getConfig();

    /**
     * Gets an embedded resource in this Extension
     *
     * @param filename Filename of the resource
     * @return File if found, otherwise null
     */
    public InputStream getResource(String filename);

    /**
     * Saves the {@link FileConfiguration} retrievable by {@link #getConfig()}.
     */
    //public void saveConfig();

    /**
     * Saves the raw contents of the default config.yml file to the location
     * retrievable by {@link #getConfig()}. If there is no default config.yml
     * embedded in the plugin, an empty config.yml file is saved. This should
     * fail silently if the config.yml already exists.
     */
    //public void saveDefaultConfig();

    /**
     * Saves the raw contents of any resource embedded with a Extension's .jar
     * file assuming it can be found using {@link #getResource(String)}.
     * <p>
     * The resource is saved into the plugin's data folder using the same
     * hierarchy as the .jar file (subdirectories are preserved).
     *
     * @param resourcePath the embedded resource path to look for within the
     *     plugin's .jar file. (No preceding slash).
     * @param replace if true, the embedded resource will overwrite the
     *     contents of an existing file.
     * @throws IllegalArgumentException if the resource path is null, empty,
     *     or points to a nonexistent resource.
     */
    public void saveResource(String resourcePath, boolean replace);

    /**
     * Discards any data in {@link #getConfig()} and reloads from disk.
     */
    //public void reloadConfig();

    /**
     * Gets the associated Loader responsible for this Extension
     *
     * @return ExtensionLoader that controls this Extension
     */
    public ExtensionLoader getExtensionLoader();

    /**
     * Returns the browser instance currently running this Extension
     *
     * @return LoboBrowser instance running this extension
     */
    public LoboBrowser getBrowser();

    /**
     * Returns a value indicating whether or not this Extension is currently
     * enabled
     *
     * @return true if this plugin is enabled, otherwise false
     */
    public boolean isEnabled();

    /**
     * Called when this Extension is disabled
     */
    public void onDisable();

    /**
     * Called after a plugin is Extension but before it has been enabled.
     * <p>
     * When mulitple Extension are loaded, the onLoad() for all Extensions is
     * called before any onEnable() is called.
     */
    public void onLoad();

    /**
     * Called when this Extension is enabled
     */
    public void onEnable();


    /**
     * Returns the Extension logger associated with LoboBrowser's logger. The
     * returned logger automatically tags all log messages with the plugin's
     * name.
     *
     * @return Logger associated with this Extension
     */
    public Logger getLogger();

    /**
     * Returns the name of the Extension.
     * <p>
     * This should return the bare name of the plugin and should be used for
     * comparison.
     *
     * @return name of the Extension
     */
    public String getName();
}
