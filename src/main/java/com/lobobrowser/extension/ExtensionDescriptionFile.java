package com.lobobrowser.extension;

import java.io.*;
import java.util.*;
import java.util.jar.JarFile;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.lobobrowser.exceptions.InvalidDescriptionException;

public final class ExtensionDescriptionFile {

    private static final String ATTRIBUTE_EXTENSION_NAME = "extension.name";
    private static final String ATTRIBUTE_EXTENSION_DESCRIPTION = "extension.description";
    private static final String ATTRIBUTE_EXTENSION_AUTHOR = "extension.author";
    private static final String ATTRIBUTE_EXTENSION_VERSION = "extension.version";
    private static final String ATTRIBUTE_EXTENSION_MAIN = "extension.main";
    private static final String ATTRIBUTE_EXTENSION_PRIORITY = "extension.priority";

    /**
     * The minimum priority.
     */
    public static final int LOW_PRIORITY = 0;

    /**
     * The highest priority, only available to the default platform extension.
     */
    public static final int PRIMARY_EXTENSION_PRIORITY = 10;

    /**
     * The highest priority allowed for non-primary platform extensions.
     */
    public static final int HIGH_PRIORITY = 9;

    /**
     * The default priority.
     */
    public static final int NORMAL_PRIORITY = 5;

    private static final ThreadLocal<Yaml> YAML = new ThreadLocal<Yaml>() {
        @Override
        protected Yaml initialValue() {
            return new Yaml(new SafeConstructor() {
                {
                    yamlConstructors.put(null, new AbstractConstruct() {
                        @Override
                        public Object construct(final Node node) {
                            if (!node.getTag().startsWith("!@")) {
                                // Unknown tag - will fail
                                return SafeConstructor.undefinedConstructor.construct(node);
                            }
                            // Unknown awareness - provide a graceful substitution
                            return new ExtensionAwareness() {
                                @Override
                                public String toString() {
                                    return node.toString();
                                }
                            };
                        }
                    });
                    for (final ExtensionAwareness.Flags flag : ExtensionAwareness.Flags.values()) {
                        yamlConstructors.put(new Tag("!@" + flag.name()), new AbstractConstruct() {
                            @Override
                            public ExtensionAwareness.Flags construct(final Node node) {
                                return flag;
                            }
                        });
                    }
                }
            });
        }
    };


    //the things we need
    private String name = null;
    private String main = null;
    private String version = null;
    private String description = null;
    private List<String> authors = null;
    private String priority = null;
    private Set<ExtensionAwareness> awareness = ImmutableSet.of();
    private List<String> depend = ImmutableList.of();
    private List<String> softDepend = ImmutableList.of();
    private List<String> loadBefore = ImmutableList.of();

    public ExtensionDescriptionFile(final InputStream stream) throws InvalidDescriptionException {
        loadMap(asMap(YAML.get().load(stream)));
    }

    /**
     * Loads a ExtensionDescriptionFile from the specified reader
     *
     * @param reader The reader
     * @throws InvalidDescriptionException If the ExtensionDescriptionFile is
     *     invalid
     */
    public ExtensionDescriptionFile(final Reader reader) throws InvalidDescriptionException {
        loadMap(asMap(YAML.get().load(reader)));
    }

    /**
     * Creates a new PluginDescriptionFile with the given detailed
     *
     * @param extensionName Name of this extension
     * @param extensionVersion Version of this extension
     * @param mainClass Full location of the main class of this extension
     */
    public ExtensionDescriptionFile(final String extensionName, final String extensionVersion, final String mainClass) {
        name = extensionName.replace(' ', '_');
        version = extensionVersion;
        main = mainClass;
    }

    /**
     * Gets the name of the Extension
     *
     * @return the Extension name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the version of the Extension
     *
     * @return the Extension version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the main class of the Extension
     *
     * @return the Extension main class
     */
    public String getMain() {
        return main;
    }

    /**
     * Gets the description of the Extension
     *
     * @return the Extension description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gives the list of authors for the Extension.
     * <ul>
     * <li>Gives credit to the developer.
     * <li>Used in some Extension error messages to provide helpful feedback on
     *     who to contact when an error occurs.
     * displayed in the ExtensionManager window
     * <li><code>authors</code> must be in <a
     *     href="http://en.wikipedia.org/wiki/YAML#Lists">YAML list
     *     format</a>.
     * </ul>
     * <p>
     * In the extension.yml, this has two entries, <code>author</code> and
     * <code>authors</code>.
     * <p>
     * Single author example:
     * <blockquote><pre>author: LoboBrowser Team</pre></blockquote>
     * Multiple author example:
     * <blockquote><pre>authors: [Cogito, verrier, EvilSeph]</pre></blockquote>
     * When both are specified, author will be the first entry in the list, so
     * this example:
     * <blockquote><pre>author: Grum
     *authors:
     *- feildmaster
     *- amaranth</pre></blockquote>
     * Is equivilant to this example:
     * <blockquote><pre>authors: [Grum, feildmaster, aramanth]<pre></blockquote>
     *
     * @return an immutable list of the plugin's authors
     */
    public List<String> getAuthors() {
        return authors;
    }

    /**
     * Gives a list of other extensions that the extension requires.
     * <ul>
     * <li>Use the value in the {@link #getName()} of the target extension to
     *     specify the dependency.
     * <li>If any extension listed here is not found, your extension will fail to
     *     load at startup.
     * <li>If multiple extensions list each other in <code>depend</code>,
     *     creating a network with no individual extension does not list another
     *     extension in the <a
     *     href=https://en.wikipedia.org/wiki/Circular_dependency>network</a>,
     *     all extensions in that network will fail.
     * <li><code>depend</code> must be in must be in <a
     *     href="http://en.wikipedia.org/wiki/YAML#Lists">YAML list
     *     format</a>.
     * </ul>
     * <p>
     * In the extension.yml, this entry is named <code>depend</code>.
     * <p>
     * Example:
     * <blockquote><pre>depend:
     *- OneExtension
     *- AnotherExtension</pre></blockquote>
     *
     * @return immutable list of the extension's dependencies
     */
    public List<String> getDepend() {
        return depend;
    }

    /**
     * Gives a list of other extensions that the extension requires for full
     * functionality. The {@link ExtensionManager} will make best effort to treat
     * all entries here as if they were a {@link #getDepend() dependency}, but
     * will never fail because of one of these entries.
     * <ul>
     * <li>Use the value in the {@link #getName()} of the target extension to
     *     specify the dependency.
     * <li>When an unresolvable extension is listed, it will be ignored and does
     *     not affect load order.
     * <li>When a circular dependency occurs (a network of extensions depending
     *     or soft-dependending each other), it will arbitrarily choose a
     *     extension that can be resolved when ignoring soft-dependencies.
     * <li><code>softdepend</code> must be in <a
     *     href="http://en.wikipedia.org/wiki/YAML#Lists">YAML list
     *     format</a>.
     * </ul>
     * <p>
     * In the extension.yml, this entry is named <code>softdepend</code>.
     * <p>
     * Example:
     * <blockquote><pre>softdepend: [OneExtension, AnotherExtension]</pre></blockquote>
     *
     * @return immutable list of the extension's preferred dependencies
     */
    public List<String> getSoftDepend() {
        return softDepend;
    }

    /**
     * Gets the list of extensions that should consider this extension a
     * soft-dependency.
     * <ul>
     * <li>Use the value in the {@link #getName()} of the target extension to
     *     specify the dependency.
     * <li>The extension should load before any other extensions listed here.
     * <li>Specifying another extension here is strictly equivalent to having the
     *     specified extension's {@link #getSoftDepend()} include {@link
     *     #getName() this extension}.
     * <li><code>loadbefore</code> must be in <a
     *     href="http://en.wikipedia.org/wiki/YAML#Lists">YAML list
     *     format</a>.
     * </ul>
     * <p>
     * In the extension.yml, this entry is named <code>loadbefore</code>.
     * <p>
     * Example:
     * <blockquote><pre>loadbefore:
     *- OneExtension
     *- AnotherExtension</pre></blockquote>
     *
     * @return immutable list of extensions that should consider this extension a
     *     soft-dependency
     */
    public List<String> getLoadBefore() {
        return loadBefore;
    }

    /**
     * Gives a set of every {@link ExtensionAwareness} for a extension. An awareness
     * dictates something that a extension developer acknowledges when the extension
     * is compiled. Some implementions may define extra awarenesses that are
     * not included in the API. Any unrecognized
     * awareness (one unsupported or in a future version) will cause a dummy
     * object to be created instead of failing.
     * <p>
     * <ul>
     * <li>Currently only supports the enumerated values in {@link
     *     ExtensionAwareness.Flags}.
     * <li>Each awareness starts the identifier with bang-at
     *     (<code>!@</code>).
     * <li>Unrecognized (future / unimplemented) entries are quietly replaced
     *     by a generic object that implements ExtensionAwareness.
     * <li>A type of awareness must be defined by the runtime and acknowledged
     *     by the API, effectively discluding any derived type from any
     *     extension's classpath.
     * <li><code>awareness</code> must be in <a
     *     href="http://en.wikipedia.org/wiki/YAML#Lists">YAML list
     *     format</a>.
     * </ul>
     * <p>
     * In the extension.yml, this entry is named <code>awareness</code>.
     * <p>
     * Example:<blockquote><pre>awareness:
     *- !@UTF8</pre></blockquote>
     * <p>
     * <b>Note:</b> Although unknown versions of some future awareness are
     * gracefully substituted, previous versions of LoboBrowser/CobraParser (ones prior to the
     * first implementation of awareness) will fail to load a extension that
     * defines any awareness.
     *
     * @return a set containing every awareness for the extension
     */
    public Set<ExtensionAwareness> getAwareness() {
        return awareness;
    }

    /**
     * Returns the name of a extension, including the version. This method is
     * provided for convenience; it uses the {@link #getName()} and {@link
     * #getVersion()} entries.
     *
     * @return a descriptive name of the extension and respective version
     */
    public String getFullName() {
        return name + " v" + version;
    }

    /**
     * Saves this ExtensionDescriptionFile to the given writer
     *
     * @param writer Writer to output this file to
     */
    public void save(Writer writer) {
        YAML.get().dump(saveMap(), writer);
    }

    private void loadMap(Map<?, ?> map) throws InvalidDescriptionException {
        try {
            name = map.get("name").toString();

            if (!name.matches("^[A-Za-z0-9 _.-]+$")) {
                throw new InvalidDescriptionException("name '" + name + "' contains invalid characters.");
            }

            name = name.replace(' ', '_');
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "name is not defined");
        } catch (ClassCastException ex) {
            throw new InvalidDescriptionException(ex, "name is of wrong type");
        }

        try {
            version = map.get("version").toString();
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "version is not defined");
        } catch (ClassCastException ex) {
            throw new InvalidDescriptionException(ex, "version is of wrong type");
        }

        try {
            main = map.get("main").toString();
            if (main.startsWith("com.lobobrowser.") || main.startsWith("org.cobraparser.*")) {
                throw new InvalidDescriptionException("main may not be within the com.lobobrowser or org.cobraparser namespace");
            }
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "main is not defined");
        } catch (ClassCastException ex) {
            throw new InvalidDescriptionException(ex, "main is of wrong type");
        }

        depend = makePluginNameList(map, "depend");
        softDepend = makePluginNameList(map, "softdepend");
        loadBefore = makePluginNameList(map, "loadbefore");

        if (map.get("description") != null) {
            description = map.get("description").toString();
        }

        if (map.get("authors") != null) {
            ImmutableList.Builder<String> authorsBuilder = ImmutableList.<String>builder();
            if (map.get("author") != null) {
                authorsBuilder.add(map.get("author").toString());
            }
            try {
                for (Object o : (Iterable<?>) map.get("authors")) {
                    authorsBuilder.add(o.toString());
                }
            } catch (ClassCastException ex) {
                throw new InvalidDescriptionException(ex, "authors are of wrong type");
            } catch (NullPointerException ex) {
                throw new InvalidDescriptionException(ex, "authors are improperly defined");
            }
            authors = authorsBuilder.build();
        } else if (map.get("author") != null) {
            authors = ImmutableList.of(map.get("author").toString());
        } else {
            authors = ImmutableList.<String>of();
        }


        if (map.get("awareness") instanceof Iterable) {
            Set<ExtensionAwareness> awareness = new HashSet<ExtensionAwareness>();
            try {
                for (Object o : (Iterable<?>) map.get("awareness")) {
                    awareness.add((ExtensionAwareness) o);
                }
            } catch (ClassCastException ex) {
                throw new InvalidDescriptionException(ex, "awareness has wrong type");
            }
            this.awareness = ImmutableSet.copyOf(awareness);
        }

        try {
            priority = map.get("priority").toString();
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "priority is not defined");
        } catch (ClassCastException ex) {
            throw new InvalidDescriptionException(ex, "priority is of wrong type");
        }

    }

    private static List<String> makePluginNameList(final Map<?, ?> map, final String key) throws InvalidDescriptionException {
        final Object value = map.get(key);
        if (value == null) {
            return ImmutableList.of();
        }

        final ImmutableList.Builder<String> builder = ImmutableList.<String>builder();
        try {
            for (final Object entry : (Iterable<?>) value) {
                builder.add(entry.toString().replace(' ', '_'));
            }
        } catch (ClassCastException ex) {
            throw new InvalidDescriptionException(ex, key + " is of wrong type");
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "invalid " + key + " format");
        }
        return builder.build();
    }

    private Map<String, Object> saveMap() {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("name", name);
        map.put("main", main);
        map.put("version", version);
        map.put("priority", priority);

        if (depend != null) {
            map.put("depend", depend);
        }

        if (softDepend != null) {
            map.put("softdepend", softDepend);
        }

        if (description != null) {
            map.put("description", description);
        }

        if (authors.size() == 1) {
            map.put("author", authors.get(0));
        } else if (authors.size() > 1) {
            map.put("authors", authors);
        }

        return map;
    }

    private Map<?,?> asMap(Object object) throws InvalidDescriptionException {
        if (object instanceof Map) {
            return (Map<?,?>) object;
        }
        throw new InvalidDescriptionException(object + " is not properly structured.");
    }
}
