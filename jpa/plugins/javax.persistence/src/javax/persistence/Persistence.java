/*******************************************************************************
 * Copyright (c) 2008, 2009 Sun Microsystems, Oracle Corporation. All rights reserved. 
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
 *     Oracle committers - EclipseLink implementation of Persistence and PersistenceUtil 
 *
 ******************************************************************************/
package javax.persistence;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolver;
import javax.persistence.spi.PersistenceProviderResolverHolder;
import javax.persistence.spi.LoadState;

/**
 * Bootstrap class that is used to obtain an {@link EntityManagerFactory}
 * in Java SE environments.  
 * 
 * <p> The <code>Persistence</code> class is available in a Java EE
 * container environment as well; however, support for the Java SE
 * bootstrapping APIs is not required in container environments.
 * 
 * <p> The <code>Persistence</code> class is used to obtain a {@link
 * javax.persistence.PersistenceUtil PersistenceUtil} instance in both
 * Java EE and Java SE environments.
 *
 * @since Java Persistence 1.0
 */
public class Persistence {
    
    /**
     * Create and return an EntityManagerFactory for the named
     * persistence unit.
     * 
     * @param persistenceUnitName
     *            the name of the persistence unit
     * @return the factory that creates EntityManagers configured according to
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
     *            the name of the persistence unit
     * @param properties
     *            Additional properties to use when creating the factory. The
     *            values of these properties override any values that may have
     *            been configured elsewhere.
     * @return the factory that creates EntityManagers configured according to
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
     * @return PersistenceUtil instance
     * @since Java Persistence 2.0
     */
    public static PersistenceUtil getPersistenceUtil() {
       return new PersistenceUtilImpl();
    }

    
    /**
     * Implementation of PersistenceUtil interface
     * @since Java Persistence 2.0
     */
    private static class PersistenceUtilImpl implements PersistenceUtil {
        public boolean isLoaded(Object entity, String attributeName) {
            PersistenceProviderResolver resolver = PersistenceProviderResolverHolder.getPersistenceProviderResolver();

            List<PersistenceProvider> providers = resolver.getPersistenceProviders();

            for (PersistenceProvider provider : providers) {
                LoadState loadstate = provider.getProviderUtil().isLoadedWithoutReference(entity, attributeName);
                if(loadstate == LoadState.LOADED) {
                    return true;
                } else if (loadstate == LoadState.NOT_LOADED) {
                    return false;
                } // else continue
            }

            //None of the providers could determine the load state try isLoadedWithReference
            for (PersistenceProvider provider : providers) {
                LoadState loadstate = provider.getProviderUtil().isLoadedWithReference(entity, attributeName);
                if(loadstate == LoadState.LOADED) {
                    return true;
                } else if (loadstate == LoadState.NOT_LOADED) {
                    return false;
                } // else continue
            }

            //None of the providers could determine the load state.
            return true;
        }

        public boolean isLoaded(Object entity) {
            PersistenceProviderResolver resolver = PersistenceProviderResolverHolder.getPersistenceProviderResolver();

            List<PersistenceProvider> providers = resolver.getPersistenceProviders();

            for (PersistenceProvider provider : providers) {
                LoadState loadstate = provider.getProviderUtil().isLoaded(entity);
                if(loadstate == LoadState.LOADED) {
                    return true;
                } else if (loadstate == LoadState.NOT_LOADED) {
                    return false;
                } // else continue
            }
            //None of the providers could determine the load state
            return true;
        }
    }

    /**
     * This final String is deprecated and should be removed and is only here for TCK backward compatibility
     * @since Java Persistence 1.0
     * @deprecated
     */
    @Deprecated
    public static final String PERSISTENCE_PROVIDER = "javax.persistence.spi.PeristenceProvider";
    
    /**
     * This instance variable is deprecated and should be removed and is only here for TCK backward compatibility
     * @since Java Persistence 1.0
     * @deprecated
     */
    @Deprecated
    protected static final Set<PersistenceProvider> providers = new HashSet<PersistenceProvider>();
}
