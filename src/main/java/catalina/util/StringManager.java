package catalina.util;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class StringManager {
    private static int LOCALE_CACHE_SIZE = 10;

    /**
     * The ResourceBundle for this StringManager.
     */
    private final ResourceBundle bundle;
    private final Locale locale;


    /**
     * Creates a new StringManager for a given package. This is a private method and all access to it is arbitrated by
     * the static getManager method call so that only one StringManager per package will be created.
     *
     * @param packageName Name of package to create StringManager for.
     */
    private StringManager(String packageName, Locale locale) {
        String bundleName = packageName + ".LocalStrings";
        ResourceBundle bnd = ResourceBundle.getBundle(bundleName, locale);
        bundle = bnd;
        // Get the actual locale, which may be different from the requested one
        if (bundle != null) {
            Locale bundleLocale = bundle.getLocale();
            if (bundleLocale.equals(Locale.ROOT)) {
                this.locale = Locale.ENGLISH;
            } else {
                this.locale = bundleLocale;
            }
        } else {
            this.locale = null;
        }
    }


    /**
     * Get a string from the underlying resource bundle or return null if the String is not found.
     *
     * @param key to desired resource String
     *
     * @return resource String matching <i>key</i> from underlying bundle or null if not found.
     *
     * @throws IllegalArgumentException if <i>key</i> is null
     */
    public String getString(String key) {
        if (key == null) {
            String msg = "key may not have a null value";
            throw new IllegalArgumentException(msg);
        }

        String str = null;

        try {
            // Avoid NPE if bundle is null and treat it like an MRE
            if (bundle != null) {
                str = bundle.getString(key);
            }
        } catch (MissingResourceException mre) {
            // bad: shouldn't mask an exception the following way:
            // str = "[cannot find message associated with key '" + key +
            // "' due to " + mre + "]";
            // because it hides the fact that the String was missing
            // from the calling code.
            // good: could just throw the exception (or wrap it in another)
            // but that would probably cause much havoc on existing
            // code.
            // better: consistent with container pattern to
            // simply return null. Calling code can then do
            // a null check.
            str = null;
        }

        return str;
    }


    /**
     * Get a string from the underlying resource bundle and format it with the given set of arguments.
     *
     * @param key  The key for the required message
     * @param args The values to insert into the message
     *
     * @return The request string formatted with the provided arguments or the key if the key was not found.
     */
    public String getString(final String key, final Object... args) {
        String value = getString(key);
        if (value == null) {
            value = key;
        }

        MessageFormat mf = new MessageFormat(value);
        mf.setLocale(locale);
        return mf.format(args, new StringBuffer(), null).toString();
    }


    /**
     * Identify the Locale this StringManager is associated with.
     *
     * @return The Locale associated with the StringManager
     */
    public Locale getLocale() {
        return locale;
    }


    // --------------------------------------------------------------
    // STATIC SUPPORT METHODS
    // --------------------------------------------------------------

    private static final Map<String, Map<Locale, StringManager>> managers = new HashMap<>();


    /**
     * Get the StringManager for a given class. The StringManager will be returned for the package in which the class is
     * located. If a manager for that package already exists, it will be reused, else a new StringManager will be
     * created and returned.
     *
     * @param clazz The class for which to retrieve the StringManager
     *
     * @return The instance associated with the package of the provide class
     */
    public static final StringManager getManager(Class<?> clazz) {
        return getManager(clazz.getPackage().getName());
    }


    /**
     * Get the StringManager for a particular package. If a manager for a package already exists, it will be reused,
     * else a new StringManager will be created and returned.
     *
     * @param packageName The package name
     *
     * @return The instance associated with the given package and the default Locale
     */
    public static final StringManager getManager(String packageName) {
        return getManager(packageName, Locale.getDefault());
    }


    /**
     * Get the StringManager for a particular package and Locale. If a manager for a package/Locale combination already
     * exists, it will be reused, else a new StringManager will be created and returned.
     *
     * @param packageName The package name
     * @param locale      The Locale
     *
     * @return The instance associated with the given package and Locale
     */
    public static final synchronized StringManager getManager(String packageName, Locale locale) {

        Map<Locale, StringManager> map = managers.get(packageName);
        if (map == null) {
            /*
             * Don't want the HashMap size to exceed LOCALE_CACHE_SIZE. Expansion occurs when size() exceeds capacity.
             * Therefore keep size at or below capacity. removeEldestEntry() executes after insertion therefore the test
             * for removal needs to use one less than the maximum desired size. Note this is an LRU cache.
             */
            map = new LinkedHashMap<>(LOCALE_CACHE_SIZE, 0.75f, true) {
                private static final long serialVersionUID = 1L;

                @Override
                protected boolean removeEldestEntry(Map.Entry<Locale, StringManager> eldest) {
                    if (size() > (LOCALE_CACHE_SIZE - 1)) {
                        return true;
                    }
                    return false;
                }
            };
            managers.put(packageName, map);
        }

        StringManager mgr = map.get(locale);
        if (mgr == null) {
            mgr = new StringManager(packageName, locale);
            map.put(locale, mgr);
        }
        return mgr;
    }
}
