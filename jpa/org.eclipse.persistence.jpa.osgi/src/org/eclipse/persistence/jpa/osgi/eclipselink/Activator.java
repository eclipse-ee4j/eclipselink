/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware, ssmith = 1.0 - Activator for EclipseLink persistence
 *     tware = 2.1 moved to org.eclipse.persistence.jpa.osgi fragment
 ******************************************************************************/  
package org.eclipse.persistence.jpa.osgi.eclipselink;

import java.util.Hashtable;

import org.eclipse.persistence.jpa.osgi.PersistenceProvider;
import org.eclipse.persistence.internal.jpa.deployment.osgi.OSGiPersistenceInitializationHelper;
import org.eclipse.persistence.internal.localization.LoggingLocalization;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * Activator for JPA OSGi service.
 * 
 * This Activator recieves forwarded events from the activator in org.eclipse.persistence and allows
 * EclipseLink's OSGi behavior to occur in an EclipseLink specific manner as defined in our 2.0 release.
 * 
 * When the Eclipse Gemini project comes out of incubation that behavior will be decremented and users
 * will be encouraged to use OSGi JPA in the way defined by the OSGi EE specification
 * 
 * @author tware
 */
public class Activator extends org.eclipse.persistence.jpa.osgi.Activator implements SynchronousBundleListener {
    public static final String MANIFEST_PU_LABEL = "JPA-PersistenceUnits";    
    public static final String PERSISTENCE_PROVIDER = "javax.persistence.spi.PersistenceProvider";
    public static final String ECLIPSELINK_OSGI_PROVIDER = "org.eclipse.persistence.jpa.osgi.PersistenceProviderOSGi";
    
    /**
     * Simply add bundles to our bundle list as they start and remove them as they stop
     */
    public void bundleChanged(BundleEvent event) {
        switch (event.getType()) {
            case BundleEvent.STARTING:
                registerBundle(event.getBundle());
                break;
    
            case BundleEvent.STOPPING:
                deregisterBundle(event.getBundle());
                break;
        }
    }

    /**
     * On start, we do two things
     * We register a listener for bundles and we start our JPA server
     */
    public void start(BundleContext context) throws Exception {
        Activator.context = context;
        String initializer = null;
        ServiceReference packageAdminRef = context.getServiceReference("org.osgi.service.packageadmin.PackageAdmin");
        PackageAdmin packageAdmin = (PackageAdmin)context.getService(packageAdminRef);
        Bundle[] fragments = packageAdmin.getFragments(context.getBundle());
        if (fragments != null){
	        for (int i=0;i<fragments.length;i++){
	            Bundle fragment = fragments[i];
	            initializer = (String)fragment.getHeaders().get("JPA-Initializer");
	            if (initializer != null){
	                AbstractSessionLog.getLog().log(SessionLog.CONFIG, LoggingLocalization.buildMessage("osgi_initializer", new Object[]{initializer}));
	                break;
	            }
	        }
        }
        registerBundleListener();
        registerProviderService(initializer);
    }

    /**
     * Add our bundle listener
     */
    private void registerBundleListener() {
        getContext().addBundleListener(this);
        Bundle bundles[] = getContext().getBundles();
        for (int i = 0; i < bundles.length; i++) {
            Bundle bundle = bundles[i];
            registerBundle(bundle);
        }
    }

    /**
     * Store a reference to a bundle as it is started so the bundle can be
     * accessed later.
     */
    private void registerBundle(Bundle bundle) {
        if ((bundle.getState() & (Bundle.STARTING | Bundle.RESOLVED | Bundle.ACTIVE)) != 0) {
            if (!OSGiPersistenceInitializationHelper.includesBundle(bundle)) {
                try {
                    String[] persistenceUnitNames = getPersistenceUnitNames(bundle);
                    if (persistenceUnitNames != null) {
                        OSGiPersistenceInitializationHelper.addBundle(bundle, persistenceUnitNames);
                    }
                } catch (Exception e) {
                    AbstractSessionLog.getLog().logThrowable(SessionLog.WARNING, e);
                }
            }
        }
    }
    
    /**
     * Extract the list of persistence unit names from the OSGi manifest.
     */
    private String[] getPersistenceUnitNames(Bundle bundle) {
        String names = (String) bundle.getHeaders().get(MANIFEST_PU_LABEL);
        if (names != null) {
            String[] values = names.split(",");
            // Must trim spaces.
            for (int index = 0; index < values.length; index++) {
                values[index] = values[index].trim();
            }
            return values;
        } else {
            return null;
        }
    }
    
    
    private void deregisterBundle(Bundle bundle) {
        org.eclipse.persistence.internal.jpa.deployment.osgi.OSGiPersistenceInitializationHelper.removeBundle(bundle);
    }

    public void stop(BundleContext context) throws Exception {
        getContext().removeBundleListener(this);    
    }

    
    /**
     * Our service provider provides the javax.persistence.spi.PersistenceProvider service.
     * In this method, we register as a provider of that service
     * @throws Exception
     */
    public void registerProviderService(String initializer) throws Exception {
        // Create and register ourselves as a JPA persistence provider service
        PersistenceProvider providerService = new PersistenceProvider(initializer);
        Hashtable<String, String> props = new Hashtable<String, String>();
        props.put(PERSISTENCE_PROVIDER, ECLIPSELINK_OSGI_PROVIDER);
        getContext().registerService(PERSISTENCE_PROVIDER, providerService, props);
    }
}


