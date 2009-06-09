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
 *     smith - Cleanup of activator/listener into this class
 ******************************************************************************/
package org.eclipse.persistence.javax.persistence.osgi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolver;
import javax.persistence.spi.PersistenceProviderResolverHolder;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
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
    private Map<String, PersistenceProvider> providers;

    public void start(BundleContext context) throws Exception {
        log("Persistence bundle starting...");

        // Init the bundle context
        this.ctx = context;

        // Set up a tracker to add providers as they register themselves
        ServiceTrackerCustomizer customizer = new ProviderTrackerCustomizer(this);
        serviceTracker = new ServiceTracker(ctx, PERSISTENCE_PROVIDER, customizer);
        serviceTracker.open();

        // Set the persistence provider resolver to use OSGi services
        PersistenceProviderResolverHolder.setPersistenceProviderResolver(this);

        this.providers = new HashMap<String, PersistenceProvider>();

        // Provider bundles should require this bundle to be started before
        // they start, so it is very unlikely that provider bundles will have
        // already loaded and registered but we'll do this just in case...
        ServiceReference[] refs = null;
        try {
            refs = ctx.getServiceReferences(PERSISTENCE_PROVIDER, null);
        } catch (InvalidSyntaxException invEx) {
        } // Can't happen since filter is null
        if (refs != null) {
            for (ServiceReference ref : refs) {
                addProvider(ref);
            }
        }

        log("Persistence bundle started.");
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
        log("Persistence bundle stopping...");

        // Close the service tracker
        serviceTracker.close();
        serviceTracker = null;

        // Nil out the known provider list
        // Note: the holder is in this bundle so it will no longer be available
        // anyways
        // This is done to ensure all refs to providers in other bundles is
        // removed
        PersistenceProviderResolverHolder.setPersistenceProviderResolver(null);

        log("Persistence bundle stopped.");
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

    private void log(String message) {
        System.out.println(message);
    }

}
