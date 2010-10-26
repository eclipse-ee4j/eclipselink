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
 *     tware - updates to allow support of gemini project
 ******************************************************************************/  
package org.eclipse.persistence.jpa.osgi;

import java.lang.reflect.Proxy;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.internal.jpa.deployment.osgi.OSGiPersistenceInitializationHelper;
import org.eclipse.persistence.internal.localization.LoggingLocalization;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.jpa.EntityManagerFactoryBuilder;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * Activator for JPA OSGi service.
 * 
 * This Activator provides EclipseLink's OSGi integration. When the Eclipse
 * Gemini project comes out of incubation that behavior this integration will be
 * deprecated and users will be encouraged to use OSGi JPA in the way defined by
 * the OSGi EE specification
 * 
 * @author tware, ssmith
 */
public class Activator implements BundleActivator, SynchronousBundleListener {
    public static final String MANIFEST_PU_LABEL = "JPA-PersistenceUnits";    
    public static final String PERSISTENCE_PROVIDER = "javax.persistence.spi.PersistenceProvider";
    public static final String ECLIPSELINK_OSGI_PROVIDER = "org.eclipse.persistence.jpa.osgi.PersistenceProviderOSGi";

    /**
     * Context is stored on this activator by its subclass for use 
     * in weaving.
     */
    protected static BundleContext context;
    protected static PersistenceProvider osgiProvider;
    
    public static BundleContext getContext() {
        return Activator.context;
    }
    
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
        osgiProvider = new PersistenceProvider(initializer);
        registerBundleListener();
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
            AbstractSessionLog.getLog().finest("EclipseLink OSGi - examining bundle: " + 
                    bundle.getSymbolicName() + "_" + bundle.getBundleId());
            if (!OSGiPersistenceInitializationHelper.includesBundle(bundle)) {
                try {
                    String[] persistenceUnitNames = getPersistenceUnitNames(bundle);
                    if (persistenceUnitNames != null) {
                        // Bundle contains persistence unit(s)
                        OSGiPersistenceInitializationHelper.addBundle(bundle, persistenceUnitNames);
                        registerEMFServices(persistenceUnitNames);
                    }
                } catch (Exception e) {
                    AbstractSessionLog.getLog().logThrowable(SessionLog.WARNING, e);
                }
            }
        }
    }
    
    protected void registerEMFServices(String[] persistenceUnitNames) {
        for (String puName : persistenceUnitNames) {
            AbstractSessionLog.getLog().finest("EclipseLink OSGi - registering services for PU '" + 
                    puName + "'");
            // Assemble the properties
            Dictionary<String,String> props = new Hashtable<String,String>();
            props.put("osgi.unit.name", puName);
            props.put("osgi.unit.provider", PersistenceProvider.class.getName());

            EMFServiceProxyHandler emfServiceProxyHandler = registerEMFService(puName, props);
            registerEMFBuilderService(emfServiceProxyHandler, puName, props);
        }
    }

    protected EMFServiceProxyHandler registerEMFService(String puName, Dictionary<String, String> props) {
        EMFServiceProxyHandler emfServiceProxyHandler = new EMFServiceProxyHandler(osgiProvider, puName);
        Object emfServiceProxy = null ;
   
        try {
            emfServiceProxy = 
                Proxy.newProxyInstance(this.getClass().getClassLoader(), 
                        new Class[]{EntityManagerFactory.class}, 
                        emfServiceProxyHandler);
        } catch (Exception e) {
            AbstractSessionLog.getLog().finest("EclipseLink OSGi - Failed to create proxy for EMF service: " + e); 
        }
   
        try {
            String[] serviceInterfaces = new String[]{EntityManagerFactory.class.getName()};
            context.registerService(serviceInterfaces, emfServiceProxy, props);
        } catch (Exception e) {
            AbstractSessionLog.getLog().finest("EclipseLink OSGi could not register EMF service for " + puName +  e);
        }
        AbstractSessionLog.getLog().finer("EclipseLink OSGi registered EMF service for " + puName);
        return emfServiceProxyHandler;
    }

    protected EMFBuilderServiceProxyHandler registerEMFBuilderService(EMFServiceProxyHandler emfServiceProxyHandler, String puName, Dictionary<String, String> props) {
        EMFBuilderServiceProxyHandler emfBuilderProxyHandler = new EMFBuilderServiceProxyHandler(osgiProvider, puName, emfServiceProxyHandler);
        Object emfBuilderServiceProxy = null ;
   
        try {
            emfBuilderServiceProxy = 
                Proxy.newProxyInstance(this.getClass().getClassLoader(), 
                        new Class[]{EntityManagerFactoryBuilder.class}, 
                        emfBuilderProxyHandler);
        } catch (Exception e) {
            AbstractSessionLog.getLog().finest("EclipseLink OSGi - Failed to create proxy for EMF builder service: " + e); 
        }
   
        try {
            String[] serviceInterfaces = new String[]{EntityManagerFactoryBuilder.class.getName()};
            context.registerService(serviceInterfaces, emfBuilderServiceProxy, props);
        } catch (Exception e) {
            AbstractSessionLog.getLog().finest("EclipseLink OSGi could not register EMF Builder service for " + puName +  e);
        }
        AbstractSessionLog.getLog().finer("EclipseLink OSGi registered EMF Builder service for " + puName);
        return emfBuilderProxyHandler;
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
        OSGiPersistenceInitializationHelper.removeBundle(bundle);
    }

    public void stop(BundleContext context) throws Exception {
        getContext().removeBundleListener(this);    
    }

}
