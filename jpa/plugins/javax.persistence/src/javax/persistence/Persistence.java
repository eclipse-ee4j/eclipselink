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
 *     dclarke - Java Persistence 2.0 - Proposed Final Draft (March 13, 2009)
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

package javax.persistence;

// J2SE imports
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolver;
import javax.persistence.spi.PersistenceProviderResolverHolder;
import javax.persistence.spi.PersistenceUtil;

/**
 * Bootstrap class that is used to obtain an {@link EntityManagerFactory}.
 * 
 * @since Java Persistence 1.0
 */
public class Persistence {
    /**
     * This final String is deprecated and should be removed and is only here for TCK backward compatibility
     */
    public static final String PERSISTENCE_PROVIDER = "javax.persistence.spi.PeristenceProvider";
    /**
     * This instance variable is deprecated and should be removed and is only here for TCK backward compatibility
     */

    protected static final Set<PersistenceProvider> providers = new HashSet<PersistenceProvider>();
    
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
        PersistenceProviderResolver resolver = PersistenceProviderResolverHolder.getPersistenceProviderResolver();

        List<PersistenceProvider> providers = resolver.getPersistenceProviders();

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

    /**
     * Return the PersistenceUtil instance
     */
    public PersistenceUtil getPersistenceUtil() {
        // TODO - Implement Util
        throw new RuntimeException("JPA 2.0 Feature Not yet Implemented");
        // return new PersistenceUtilImpl();
    }

}
