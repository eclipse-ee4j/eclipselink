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
 *     tware, ssmith - OSGi Persistence provider
 ******************************************************************************/  
package org.eclipse.persistence.jpa.osgi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.EntityManagerSetupException;

import org.eclipse.persistence.internal.jpa.deployment.JPAInitializer;
import org.eclipse.persistence.internal.jpa.deployment.osgi.BundleProxyClassLoader;
import org.eclipse.persistence.internal.jpa.deployment.osgi.CompositeClassLoader;
import org.eclipse.persistence.internal.jpa.deployment.osgi.OSGiInitializer;
import org.eclipse.persistence.internal.localization.LoggingLocalization;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * EclipseLink JPA provider for use in OSGI Environments
 */
public class PersistenceProvider extends org.eclipse.persistence.jpa.PersistenceProvider{
    
    protected String initializerClassName;
    
    public static final String EQUINOX_INITIALIZER_NAME = "org.eclipse.persistence.internal.jpa.deployment.osgi.equinox.EquinoxInitializer";
    private static final String ORG_ECLIPSE_PERSISTENCE_CORE = "org.eclipse.persistence.core";
    
    // these maps are used to retrieve the classloader used for different bundles
    private static Map<String, Bundle> puToBundle = Collections.synchronizedMap(new HashMap<String,Bundle>());
    private static Map<Bundle, String[]> bundleToPUs = Collections.synchronizedMap(new HashMap<Bundle, String[]>());
    
    private Map<String,ClassLoader> puClassLoaders = new HashMap<String, ClassLoader>();
    
    public PersistenceProvider(){
    }
    
    public PersistenceProvider(String initializerClassName){
        this.initializerClassName = initializerClassName;
    }
    
    @Override
    public EntityManagerFactory createContainerEntityManagerFactory(
            PersistenceUnitInfo info, Map map) {
        throw EntityManagerSetupException.createContainerEntityManagerFactoryNotSupportedInOSGi();
    }
    
    /**
     * Get the initializer class
     * Here we will attempt to build an EquinoxInitializer.  It will only be available if the org.eclipse.persistence.jpa.equinox
     * fragment is available.  Else, we will return a standard OSGi initializer.
     */
    public JPAInitializer getInitializer(String emName, Map m){
        ClassLoader classLoader = getClassLoader(emName, m);
        JPAInitializer initializer;
        if (this.initializerClassName != null) {
            try {
                // try to build the passed-in initializer.  If it is available, we will build it otherwise
                // we will assume generic OSGI
                Class initializerClass = Class.forName(this.initializerClassName);
                Class[] argTypes = new Class[]{ClassLoader.class, Map.class};
                Object[] args = new Object[]{classLoader, m};
                initializer = (JPAInitializer)initializerClass.getConstructor(argTypes).newInstance(args);
                initializer.initialize(m);
                return initializer;
            } catch (Exception exception) {
                AbstractSessionLog.getLog().log(SessionLog.WARNING,
                        LoggingLocalization.buildMessage("osgi_initializer_failed", new Object[]{this.initializerClassName, exception}));
                AbstractSessionLog.getLog().logThrowable(SessionLog.WARNING, exception);
            }
        }
        initializer = new OSGiInitializer(classLoader);
        initializer.initialize(m);
        return initializer;
    }
    

    /**
     * Add a bundle to the list of bundles managed by this persistence provider
     * The bundle is indexed so it's classloader can be accessed
     * @param bundle
     * @param persistenceUnitNames
     */
    public static void addBundle(Bundle bundle, String[] persistenceUnitNames) {
        for (int i = 0; i < persistenceUnitNames.length; i++) {
            String name = persistenceUnitNames[i];
            puToBundle.put(name, bundle);
        }
        bundleToPUs.put(bundle, persistenceUnitNames);
    }

    /**
     * Removed a bundle from the list of bundles managed by this persistence provider
     * This typically happens on deactivation.
     * @param bundle
     */
    public static void removeBundle(Bundle bundle) {
        String[] persistenceUnitNames = bundleToPUs.remove(bundle);
        if (persistenceUnitNames != null) {
            for (int i = 0; i < persistenceUnitNames.length; i++) {
                String name = persistenceUnitNames[i];
                puToBundle.remove(name);
            }
        }
    }
    
    /**
     * Answer the ClassLoader to use to create an EntityManager. 
     * The result is a CompositeClassLoader capable of loading 
     * classes from the optionally provided ClassLoader, the 
     * bundle that provides the persistence unit (i.e., contains the
     * persistence.xml, and EclipseLink classes (since the 
     * persistence unit bundle may not have an explicit dependency
     * on EclipseLink).
     * 
     * @param persistenceUnitName
     * @param properties
     * @return ClassLoader
     */
    public ClassLoader getClassLoader(String persistenceUnitName, Map properties) {
        // This method is called more than once for the same persistence unit so
        // check to see if a ClassLoader already constructed and if so return it.
        ClassLoader previouslyDefinedLoader = puClassLoaders.get(persistenceUnitName);
        if (previouslyDefinedLoader != null) {
            return previouslyDefinedLoader;
        }
        
        List<ClassLoader> loaders = new ArrayList<ClassLoader>();
        ClassLoader propertyClassLoader = null;

        // Look for a ClassLoader in the properties.
        if (properties != null) {
            Object propertyValue = properties.get(PersistenceUnitProperties.CLASSLOADER);
            if ((propertyValue != null) && (propertyValue instanceof ClassLoader)) {
               propertyClassLoader = (ClassLoader)propertyValue;
                loaders.add(propertyClassLoader);
            }
        }
        
        // If a bundle has registered for a persistence unit
        // then wrap the bundle to support the ClassLoader interface.
        Bundle bundle = puToBundle.get(persistenceUnitName);
        if (bundle != null) {
            ClassLoader bundleClassLoader = new BundleProxyClassLoader(bundle);
            loaders.add(bundleClassLoader);
        }
        
        if ((propertyClassLoader == null) && (bundle == null)) {
            throw EntityManagerSetupException.couldNotFindPersistenceUnitBundle(persistenceUnitName);
        }

        // Add the EclipseLink Core bundle loader so we can see classes 
        // (such as platforms) in fragments,
        BundleContext context = Activator.getContext();
        if (context != null) {
            // context is set in the Activator which may might
            // not be called in an RCP application
            Bundle[] bundles = context.getBundles();
            Bundle coreBundle = null;
            for (int i = 0; i < bundles.length; i++) {
                if (ORG_ECLIPSE_PERSISTENCE_CORE.equals(bundles[i].getSymbolicName())) {
                    coreBundle = bundles[i];
                    loaders.add(new BundleProxyClassLoader(coreBundle));
                    break;
                }
            }
            // Add the EclipseLink JPA bundle ClassLoader so that we can load
            // EclipseLink classes.
            ClassLoader eclipseLinkJpaClassLoader = new BundleProxyClassLoader(context.getBundle());
            loaders.add(eclipseLinkJpaClassLoader);
        }
        
        // If the only classloader is the one passes as a property
        // then no point building a composite--just use the one passed.
        ClassLoader puClassLoader = null;
        if ((loaders.size() == 1) && (loaders.get(0) == propertyClassLoader)) {
            puClassLoader = propertyClassLoader;
        } else {
            puClassLoader = new CompositeClassLoader(loaders);
        }
        
      puClassLoaders.put(persistenceUnitName, puClassLoader);  // cache to avoid reconstruction
      return puClassLoader;
    }

    public static boolean includesBundle(Bundle bundle) {
        return bundleToPUs.containsKey(bundle);
    }
}
