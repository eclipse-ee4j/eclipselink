/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     mkeith
 ******************************************************************************/  
package org.eclipse.persistence.javax.persistence.osgi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.spi.LoadState;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolver;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jpa.EntityManagerFactoryBuilder;

/**
 * This class acts as the ProviderResolver as well as the 
 * PersistenceProvider to the Persistence class.
 * 
 * @author mkeith
 */
@SuppressWarnings("unchecked")
public class OSGiProviderResolver implements PersistenceProviderResolver, 
                                             PersistenceProvider {
    BundleContext ctx;
    
    public OSGiProviderResolver(BundleContext ctx) {
        this.ctx = ctx;
    }
    
    /*=====================================*/
    /* PersistenceProviderResolver methods */
    /*=====================================*/

    /**
     * ProviderResolver API method to get all of the providers. We will use 
     * this to hook ourselves in through the Persistence class so we can intercept 
     * all of the provider calls and redirect them to use OSGi services.
     */
    public List<PersistenceProvider> getPersistenceProviders() {

        List<PersistenceProvider> listOfOurself = new ArrayList<PersistenceProvider>();
        listOfOurself.add(this);
        return listOfOurself;
    }

    // Do nothing since we aren't caching anything
    public void clearCachedProviders () {}
    
    /*=============================*/
    /* PersistenceProvider methods */
    /*=============================*/
    
    public EntityManagerFactory createEntityManagerFactory(String unitName, Map props) {
        return (((props == null) || (props.isEmpty())))
            ? lookupEMF(unitName)
            : lookupEMFBuilder(unitName, props);
    }

    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map props) {
        // Not supported through the Persistence class
        return null;
    }
    
    public ProviderUtil getProviderUtil() {
        // Now it gets interesting, because we need to return something that looks and smells like 
        // a ProviderUtil, but that does the job of the Persistence class cycling through the providers
        return new ForwardingProviderUtil();
    }

    /*==========================*/
    /* ProviderUtil inner class */
    /*==========================*/
    
    public class ForwardingProviderUtil implements ProviderUtil {

        public LoadState isLoadedWithReference(Object entity, String attributeName) {
            Collection<PersistenceProvider> providers = lookupProviders();
            for (PersistenceProvider provider : providers) {
                LoadState loadstate = provider.getProviderUtil().isLoadedWithReference(entity, attributeName);
                if (loadstate != LoadState.UNKNOWN) {
                    return loadstate;
                } // else keep looking
            }
            // None of the providers knew, so the load state is unknown
            return LoadState.UNKNOWN;
        }

        public LoadState isLoadedWithoutReference(Object entity, String attributeName) {
            Collection<PersistenceProvider> providers = lookupProviders();
            for (PersistenceProvider provider : providers) {
                LoadState loadstate = provider.getProviderUtil().isLoadedWithoutReference(entity, attributeName);
                if (loadstate != LoadState.UNKNOWN) {
                    return loadstate;
                } // else keep looking
            }
            // None of the providers knew, so the load state is unknown
            return LoadState.UNKNOWN;
        }

        public LoadState isLoaded(Object entity) {
            Collection<PersistenceProvider> providers = lookupProviders();
            for (PersistenceProvider provider : providers) {
                LoadState loadstate = provider.getProviderUtil().isLoaded(entity);
                if (loadstate != LoadState.UNKNOWN) {
                    return loadstate;
                } // else keep looking
            }
            // None of the providers knew, so the load state is unknown
            return LoadState.UNKNOWN;
        }
    }

    /*================*/
    /* Helper Methods */
    /*================*/
    
    /**
     * Look up the Entity Manager Factory service based on the unit name.
     */
    public EntityManagerFactory lookupEMF(String unitName) {

        debug("Persistence class - lookupEMF, punit=", unitName);
        String filter = null;
        ServiceReference[] refs = null;        
        try {
            filter = "(osgi.unit.name="+ unitName +")";
            refs = ctx.getServiceReferences(EntityManagerFactory.class.getName(), filter);
        } catch (InvalidSyntaxException isEx) {
            new PersistenceException("Implementation error - incorrect filter specified while looking up EMF", isEx);
        }
        if ((refs != null) && (refs.length != 0)) {
            debug("Persistence class - lookupEMF, found service ", unitName, " in registry");
            // Take the first one
            return (EntityManagerFactory)ctx.getService(refs[0]);
        }
        // Didn't find any EMF service under the given name
        debug("Persistence class - lookupEMF, *** EMF service ", unitName, " not found in registry ***");
        return null; 
    }

    /**
     * Look up the Entity Manager Factory Builder service based on the unit name. 
     *
     * @return The EntityManagerFactory object that was looked up (or created from the 
     *         EntityManagerFactoryBuilder)
     */
    public EntityManagerFactory lookupEMFBuilder(String unitName, Map<?,?> props) {

        debug("Persistence class - lookupEMFBuilder, punit=", unitName);
        String filter = null;
        ServiceReference[] refs = null;        
        try {
            filter = "(osgi.unit.name="+ unitName +")";
            refs = ctx.getServiceReferences(EntityManagerFactoryBuilder.class.getName(), filter);
        } catch (InvalidSyntaxException isEx) {
            new PersistenceException("Implementation error - incorrect filter specified while looking up EMF", isEx);
        }
        if ((refs != null) && (refs.length != 0)) {
            debug("Persistence class - lookupEMFBuilder, found service ", unitName, " in registry");
            // Take the first one and create an EMF from it
            EntityManagerFactoryBuilder builder = (EntityManagerFactoryBuilder)ctx.getService(refs[0]);
            return builder.createEntityManagerFactory(props);
        }
        // Didn't find anything under the given name
        debug("Persistence class - lookupEMFBuilder, *** EMFBuilder service ", unitName, 
              " not found in registry ***");
        return null; 
    }
    
    public Collection<PersistenceProvider> lookupProviders() {

        debug("Persistence class - lookupProviders");
        Collection<PersistenceProvider> providers = new HashSet<PersistenceProvider>();
        
        ServiceReference[] refs = null;
        try { refs = ctx.getServiceReferences(PersistenceProvider.class.getName(), null); }
        catch (InvalidSyntaxException invEx) {} // Can't happen since filter is null
        
        if (refs != null) {
            for (ServiceReference ref : refs) {
                providers.add((PersistenceProvider)ctx.getService(ref));
            }
        }
        return providers;
    }

    protected void debug(String... msgs) {
        if (System.getProperty("JPA_DEBUG") != null) {
            StringBuilder sb = new StringBuilder();
            for (String msg : msgs) sb.append(msg);
            System.out.println(sb.toString()); 
        }
    }
}
