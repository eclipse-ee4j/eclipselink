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
 *     tware,mkeith - Initial OSGi support for JPA 
 *     ssmith - Cleanup of activator/listener into this class
 *     dclarke - 20090916 - Added clearCachedProviders support
 *     ssmith - 20091126 - Bug 296010 - providers map not initialized before use
 * 
 ******************************************************************************/
package org.eclipse.persistence.javax.persistence.osgi;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolver;
import javax.persistence.spi.PersistenceProviderResolverHolder;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * Uses OSGi services to discover JPA persistence providers
 * 
 * Also acts as a resolver class (called by the Persistence class when resolving
 * providers).
 */
public class Activator implements BundleActivator, PersistenceProviderResolver {

    public static final String PERSISTENCE_PROVIDER = "javax.persistence.spi.PersistenceProvider";

    private BundleContext ctx;
    private ServiceTracker serviceTracker;
    private Map<String, PersistenceProvider> providers = new Hashtable<String, PersistenceProvider>();

    public void start(BundleContext context) throws Exception {
        log(Level.FINE, context.getBundle().getSymbolicName() + " - starting...");

        // Init the bundle context
        this.ctx = context;

        // Set up a tracker to add providers as they register themselves
        ServiceTrackerCustomizer customizer = new ProviderTrackerCustomizer(this);
        serviceTracker = new ServiceTracker(ctx, PERSISTENCE_PROVIDER, customizer);
        serviceTracker.open();

        // Set the persistence provider resolver to use OSGi services
        PersistenceProviderResolverHolder.setPersistenceProviderResolver(this);

        log(Level.FINE, context.getBundle().getSymbolicName() + " - started");
    }

    /**
     * Obtain the provider from the reference and store it under it's name, if
     * supplied or a generated name otherwise.
     */
    PersistenceProvider addProvider(ServiceReference ref) {
        PersistenceProvider provider = (PersistenceProvider) ctx.getService(ref);
        String providerName = getProviderName(ref);
        this.providers.put(providerName, provider);
        return provider;
    }

    /**
     * Remove the provider keyed by it's name.
     */
    protected void removeProvider(ServiceReference ref) {
        String providerName = getProviderName(ref);
        this.providers.remove(providerName);
    }

    public void stop(BundleContext context) throws Exception {
        log(Level.FINE, context.getBundle().getSymbolicName() + " - stopping...");

        // Close the service tracker
        this.serviceTracker.close();
        this.serviceTracker = null;

        // Clear the known provider list
        // Note: the holder is in this bundle so it will no longer be available
        // anyways. This is done to ensure all refs to providers in other
        // bundles is removed
        PersistenceProviderResolverHolder.setPersistenceProviderResolver(null);
        this.providers.clear();

        log(Level.FINE, context.getBundle().getSymbolicName() + " - stopped...");
    }

    /**
     * Assumes that provider services are registered with property that
     * indicates the name of the provider impl class.
     */
    public String getProviderName(ServiceReference ref) {
        String providerName = (String) ref.getProperty(PERSISTENCE_PROVIDER);

        if (providerName == null) {
            providerName = "PersistenceProvider-" + ref.hashCode();
        }

        return providerName;
    }

    protected Map<String, PersistenceProvider> getProviders() {
        return this.providers;
    }

    /**
     * Resolve/return existing providers (Implementation method for the
     * ProviderResolver interface.)
     */
    public List<PersistenceProvider> getPersistenceProviders() {
        return new ArrayList<PersistenceProvider>(getProviders().values());
    }

    private static final String LOGGER_SUBSYSTEM = "org.eclipse.persistence.javax.persistence.osgi";

    private Logger logger;

    private void log(Level level, String message) {
        if (this.logger == null) {
            this.logger = Logger.getLogger(LOGGER_SUBSYSTEM);
        }
        this.logger.log(level, LOGGER_SUBSYSTEM + "::" + message);
    }

    /**
     * Within OSGi where the providers are tracked dynamically the clear
     * operation does nothing.
     */
    public void clearCachedProviders() {
    }

}
