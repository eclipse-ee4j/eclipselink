/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors:
 *     dclarke - Java Persistence 2.0 - Proposed Final Draft (March 13, 2009)
 *     		     Specification available from http://jcp.org/en/jsr/detail?id=317
 *
 * Java(TM) Persistence API, Version 2.0 - EARLY ACCESS
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP).  The code is untested and presumed not to be a  
 * compatible implementation of JSR 317: Java(TM) Persistence API, Version 2.0.   
 * We encourage you to migrate to an implementation of the Java(TM) Persistence 
 * API, Version 2.0 Specification that has been tested and verified to be compatible 
 * as soon as such an implementation is available, and we encourage you to retain 
 * this notice in any implementation of Java(TM) Persistence API, Version 2.0 
 * Specification that you distribute.
 ******************************************************************************/
package javax.persistence.spi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.PersistenceException;

/**
 * Holds the global PersistenceProviderResolver instance. If no
 * PersistenceProviderResolver is set by the environment, the default
 * PersistenceProviderResolver is used.
 * 
 * Implementations must be thread-safe.
 */
public class PersistenceProviderResolverHolder {

    private static PersistenceProviderResolver singleton = new DefaultPersistenceProviderResolver();

    /**
     * Returns the current persistence provider resolver
     */
    public static PersistenceProviderResolver getPersistenceProviderResolver() {
        return singleton;
    }

    /**
     * Defines the persistence provider resolver used
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

        private static final String SERVICE_PROVIDER_FILE = "META-INF/services/javax.persistence.spi.PersistenceProvider";

        public List<PersistenceProvider> getPersistenceProviders() {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            Set<String> providerNames = getProviderNames(loader);
            List<PersistenceProvider> loadedProviders = new ArrayList<PersistenceProvider>();

            for (String s : providerNames) {
                try {
                    PersistenceProvider provider = (PersistenceProvider) loader.loadClass(s).newInstance();
                    loadedProviders.add(provider);
                } catch (ClassNotFoundException exc) {
                } catch (InstantiationException exc) {
                } catch (IllegalAccessException exc) {
                }
            }
            return loadedProviders;
        }

        private Set<String> getProviderNames(ClassLoader loader) {
            Enumeration<URL> resources = null;

            try {
                resources = loader.getResources(SERVICE_PROVIDER_FILE);
            } catch (IOException ioe) {
                throw new PersistenceException("IOException caught: " + loader + "getResources(" + SERVICE_PROVIDER_FILE + ")", ioe);
            }

            Set<String> providerNames = new HashSet<String>();

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                providerNames.addAll(getProviderNames(url));
            }

            return providerNames;
        }

        private static final Pattern nonCommentPattern = Pattern.compile("^([^#]+)");

        /**
         * For each services file look for uncommented provider names on each
         * line.
         */
        private Set<String> getProviderNames(URL url) {
            Set<String> providerNames = new HashSet<String>();

            InputStream in = null;
            try {
                in = url.openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    Matcher m = nonCommentPattern.matcher(line);
                    if (m.find()) {
                        providerNames.add(m.group().trim());
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

            return providerNames;
        }

    }
}