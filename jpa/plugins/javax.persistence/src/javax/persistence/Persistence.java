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
 * The API for this class and its comments are derived from the JPA 2.0 specification 
 * which is developed under the Java Community Process (JSR 317) and is copyright 
 * Sun Microsystems, Inc. 
 *
 * Contributors:
 * 		dclarke -	Java Persistence API 2.0 Public Draft
 *     				Specification and licensing terms available from
 *     		   		http://jcp.org/en/jsr/detail?id=317
 * 		mkeith - 	Add ability to run in OSGi and other environments by making 
 *              	provider discovery a strategy. Also allow providers to be 
 *              	added and removed dynamically.
 *
 * EARLY ACCESS - PUBLIC DRAFT
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP) and is made available for testing and evaluation 
 * purposes only. The code is not compatible with any specification of the JCP.
 ******************************************************************************/
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * Mike Keith - Add ability to run in OSGi and other environments by making 
 *              provider discovery a strategy. Also allow providers to be added
 *              and removed dynamically.
 *               
 * Mike Keith elects to include this software in this distribution under 
 * the CDDL license.
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package javax.persistence;

// J2SE imports
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.Enumeration;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// persistence imports
import javax.persistence.spi.PersistenceProvider;

/**
 * Bootstrap class that is used to obtain an {@link EntityManagerFactory}.
 * 
 * @since Java Persistence 1.0
 */
public class Persistence {

	protected static Set<PersistenceProvider> providers = new HashSet<PersistenceProvider>();

	// Used to resolve and return providers.
	protected static ProviderResolver providerResolver = new DefaultProviderResolver();

	/**
	 * Get the resolver being used to obtain the available providers.
	 * 
	 * @return The resolver used to obtain all available providers
	 */
	public static ProviderResolver getProviderResolver() {
		return providerResolver;
	}

	/**
	 * Set the resolver to an alternate provider resolution strategy
	 * (implemented externally). This can be used to override the default
	 * resolution strategy.
	 * 
	 * @param resolver
	 *            The resolver to use when obtaining all available providers
	 */
	public static void setProviderResolver(ProviderResolver resolver) {
		providerResolver = resolver;
	}

	/**
	 * Create and return an EntityManagerFactory for the named persistence unit.
	 * 
	 * @param persistenceUnitName
	 *            The name of the persistence unit
	 * @return The factory that creates EntityManagers configured according to
	 *         the specified persistence unit
	 */
	public static EntityManagerFactory createEntityManagerFactory(String persistenceUnitName) {
		return createEntityManagerFactory(persistenceUnitName, null);
	}

	/**
	 * Create and return an EntityManagerFactory for the named persistence unit
	 * using the given properties.
	 * 
	 * @param persistenceUnitName
	 *            The name of the persistence unit
	 * @param properties
	 *            Additional properties to use when creating the factory. The
	 *            values of these properties override any values that may have
	 *            been configured elsewhere.
	 * @return The factory that creates EntityManagers configured according to
	 *         the specified persistence unit.
	 */
	public static EntityManagerFactory createEntityManagerFactory(String persistenceUnitName, Map properties) {

		EntityManagerFactory emf = null;
		if (providers.size() == 0) {
			try {
				providers.addAll(providerResolver.findAllProviders());
			} catch (IOException exc) {
			}
		}
		for (PersistenceProvider provider : providers) {
			emf = provider.createEntityManagerFactory(persistenceUnitName, properties);
			if (emf != null) {
				break;
			}
		}
		if (emf == null) {
			throw new PersistenceException("No Persistence provider for EntityManager named " + persistenceUnitName);
		}
		return emf;
	}

	// ====================
	// Internal API methods
	// ====================

	/**
	 * Dynamically add a provider to the set of available providers.
	 */
	public static void addProvider(PersistenceProvider provider) {
		providers.add(provider);
	}

	/**
	 * Dynamically remove a named provider from the set of available providers.
	 */
	public static void removeProvider(String providerName) {
		for (Iterator<PersistenceProvider> i = providers.iterator(); i.hasNext();) {
			if (i.next().getClass().getName().equals(providerName)) {
				i.remove();
			}
		}
	}

	/**
	 * Reinitialize to have no providers.
	 */
	public static void resetProviders() {
		providers = new HashSet<PersistenceProvider>();
	}

	// ==============================================================
	// Interface for overriding available provider discovery strategy
	// ==============================================================

	/**
	 * Interface used by the Persistence class to obtain all available
	 * persistence providers.
	 */
	public static interface ProviderResolver {
		Collection<PersistenceProvider> findAllProviders() throws IOException;
	}

	// =============================
	// Internal implementation class
	// =============================

	/**
	 * Default provider resolver class to use when none is explicitly set.
	 * 
	 * Uses the META-INF/services approach as described in the Java Persistence
	 * specification. A getResources() call is made on the current context
	 * classloader to find the service provider files on the classpath. Any
	 * service files found are then read to obtain the classes that implement
	 * the persistence provider interface.
	 */
	public static class DefaultProviderResolver implements ProviderResolver {

		public static final String PERSISTENCE_PROVIDER = "javax.persistence.spi.PersistenceProvider";
		public static final String SERVICE_PROVIDER_FILE = "META-INF/services/" + PERSISTENCE_PROVIDER;

		public Collection<PersistenceProvider> findAllProviders() throws IOException {

			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Enumeration<URL> resources = loader.getResources(SERVICE_PROVIDER_FILE);

			Set<String> providerNames = new HashSet<String>();
			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();
				InputStream is = url.openStream();
				try {
					providerNames.addAll(providerNamesFromReader(new BufferedReader(new InputStreamReader(is))));
				} finally {
					is.close();
				}
			}
			Collection<PersistenceProvider> loadedProviders = new HashSet<PersistenceProvider>();
			for (String s : providerNames) {
				try {
					loadedProviders.add((PersistenceProvider) loader.loadClass(s).newInstance());
				} catch (ClassNotFoundException exc) {
				} catch (InstantiationException exc) {
				} catch (IllegalAccessException exc) {
				}
			}
			return loadedProviders;
		}

		private static final Pattern nonCommentPattern = Pattern.compile("^([^#]+)");

		private Set<String> providerNamesFromReader(BufferedReader reader) throws IOException {
			Set<String> providerNames = new HashSet<String>();
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				Matcher m = nonCommentPattern.matcher(line);
				if (m.find()) {
					providerNames.add(m.group().trim());
				}
			}
			return providerNames;
		}
	}
}
