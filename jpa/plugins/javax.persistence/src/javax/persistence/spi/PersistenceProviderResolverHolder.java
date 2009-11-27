/*******************************************************************************
 * Copyright (c) 2008, 2009 Sun Microsystems. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors:
 *     Linda DeMichiel - Java Persistence 2.0 - Version 2.0 (October 1, 2009)
 *     Specification available from http://jcp.org/en/jsr/detail?id=317
 *     dclarke - EclipseLink implementation and default resolver
 *
 ******************************************************************************/
package javax.persistence.spi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.PersistenceException;

/**
 * Holds the global {@link javax.persistence.spi.PersistenceProviderResolver}
 * instance. If no <code>PersistenceProviderResolver</code> is set by the
 * environment, the default </code>PersistenceProviderResolver is used.
 * 
 * Implementations must be thread-safe.
 * 
 * @since Java Persistence 2.0
 */
public class PersistenceProviderResolverHolder {

    private static PersistenceProviderResolver singleton = new DefaultPersistenceProviderResolver();

    /**
     * Returns the current persistence provider resolver.
     * 
     * @return the current persistence provider resolver
     */
    public static PersistenceProviderResolver getPersistenceProviderResolver() {
        return singleton;
    }

    /**
     * Defines the persistence provider resolver used.
     * 
     * @param resolver persistence provider resolver to be used.
     */
    public static void setPersistenceProviderResolver(PersistenceProviderResolver resolver) {
        if (resolver == null) {
            singleton = new DefaultPersistenceProviderResolver();
        } else {
            singleton = resolver;
        }
    }

    /**
     * Default provider resolver class to use when none is explicitly set.
     * 
     * Uses the META-INF/services approach as described in the Java Persistence
     * specification. A getResources() call is made on the current context
     * classloader to find the service provider files on the classpath. Any
     * service files found are then read to obtain the classes that implement
     * the persistence provider interface.
     */
    private static class DefaultPersistenceProviderResolver implements PersistenceProviderResolver {

        /**
         * Cached list of available providers cached by ClassLoader to ensure
         * there is not potential for provider visibility issues. A weak map is
         * used
         */
        private volatile WeakHashMap<ClassLoader, List<PersistenceProvider>> providers = new WeakHashMap<ClassLoader, List<PersistenceProvider>>();

        public List<PersistenceProvider> getPersistenceProviders() {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            List<PersistenceProvider> loadedProviders = this.providers.get(loader);

            if (loadedProviders == null) {
                Collection<ProviderName> providerNames = getProviderNames(loader);
                loadedProviders = new ArrayList<PersistenceProvider>();

                for (ProviderName providerName : providerNames) {
                    try {
                        PersistenceProvider provider = (PersistenceProvider) loader.loadClass(providerName.getName()).newInstance();
                        loadedProviders.add(provider);
                    } catch (ClassNotFoundException cnfe) {
                        log(Level.FINEST, cnfe + ": " + providerName);
                    } catch (InstantiationException ie) {
                        log(Level.FINEST, ie + ": " + providerName);
                    } catch (IllegalAccessException iae) {
                        log(Level.FINEST, iae + ": " + providerName);
                    } catch (ClassCastException cce) {
                        log(Level.FINEST, cce + ": " + providerName);
                    }
                }

                // If none are found we'll log the provider names for diagnostic
                // purposes.
                if (loadedProviders.isEmpty() && !providerNames.isEmpty()) {
                    log(Level.WARNING, "No valid providers found using:");
                    for (ProviderName name : providerNames) {
                        log(Level.WARNING, name.toString());
                    }
                }

                this.providers.put(loader, loadedProviders);
            }

            return loadedProviders;
        }

        private static final String LOGGER_SUBSYSTEM = "javax.persistence.spi";

        private Logger logger;

        private void log(Level level, String message) {
            if (this.logger == null) {
                this.logger = Logger.getLogger(LOGGER_SUBSYSTEM);
            }
            this.logger.log(level, LOGGER_SUBSYSTEM + "::" + message);
        }

        private static final String SERVICE_PROVIDER_FILE = "META-INF/services/javax.persistence.spi.PersistenceProvider";

        /**
         * Locate all JPA provider services files and collect all of the
         * provider names available.
         */
        private Collection<ProviderName> getProviderNames(ClassLoader loader) {
            Enumeration<URL> resources = null;

            try {
                resources = loader.getResources(SERVICE_PROVIDER_FILE);
            } catch (IOException ioe) {
                throw new PersistenceException("IOException caught: " + loader + ".getResources(" + SERVICE_PROVIDER_FILE + ")", ioe);
            }

            Collection<ProviderName> providerNames = new ArrayList<ProviderName>();

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                addProviderNames(url, providerNames);
            }

            return providerNames;
        }

        private static final Pattern nonCommentPattern = Pattern.compile("^([^#]+)");

        /**
         * For each services file look for uncommented provider names on each
         * line.
         */
        private void addProviderNames(URL url, Collection<ProviderName> providerNames) {
            InputStream in = null;
            try {
                in = url.openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    Matcher m = nonCommentPattern.matcher(line);
                    if (m.find()) {
                        providerNames.add(new ProviderName(m.group().trim(), url));
                    }
                }
            } catch (IOException ioe) {
                throw new PersistenceException("IOException caught reading: " + url, ioe);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
            }
        }

        /**
         * Clear all cached providers
         */
        public void clearCachedProviders() {
            this.providers.clear();
        }

        /**
         * A ProviderName captures each provider name found in a service file as
         * well as the URL for the service file it was found in. This
         * information is only used for diagnostic purposes.
         */
        private class ProviderName {

            /** Provider name **/
            private String name;

            /** URL for the service file where the provider name was found **/
            private URL source;

            public ProviderName(String name, URL sourceURL) {
                this.name = name;
                this.source = sourceURL;
            }

            public String getName() {
                return name;
            }

            public URL getSource() {
                return source;
            }

            public String toString() {
                return getName() + " - " + getSource();
            }
        }
    }
}
