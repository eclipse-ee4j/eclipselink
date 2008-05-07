/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jpa.osgi;

import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;

import org.eclipse.persistence.exceptions.PersistenceUnitLoadingException;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.JavaSECMPInitializer;
import org.eclipse.persistence.internal.jpa.OSGiInitializer;
import org.eclipse.persistence.internal.jpa.PersistenceInitializationActivator;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.config.PersistenceUnitProperties;
import org.osgi.framework.Bundle;

/**
 * EclipseLink JPA provider within OSGi
 */
public class PersistenceProvider implements javax.persistence.spi.PersistenceProvider, PersistenceInitializationActivator  {
    private static Map<String, Bundle> puToBundle = Collections.synchronizedMap(new HashMap<String,Bundle>());
    private static Map<Bundle, String[]> bundleToPUs = Collections.synchronizedMap(new HashMap<Bundle, String[]>());
    
    public static void addBundle(Bundle bundle, String[] persistenceUnitNames) {
        for (int i = 0; i < persistenceUnitNames.length; i++) {
            String name = persistenceUnitNames[i];
            puToBundle.put(name, bundle);
        }
        bundleToPUs.put(bundle, persistenceUnitNames);
    }

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
     * Called by Persistence class when an EntityManagerFactory
     * is to be created.
     *
     * @param emName The name of the persistence unit
     * @param map A Map of properties for use by the
     * persistence provider. These properties may be used to
     * override the values of the corresponding elements in
     * the persistence.xml file or specify values for
     * properties not specified in the persistence.xml.
     * @return EntityManagerFactory for the persistence unit,
     * or null if the provider is not the right provider
     */
    public EntityManagerFactory createEntityManagerFactory(String emName, Map properties){
        ClassLoader classloader = getClassLoader(emName, properties);
        return createEntityManagerFactory(emName, properties, classloader);
    }

    /**
     * @param emName The name of the persistence unit
     * @param map A Map of properties for use by the
     * persistence provider. These properties may be used to
     * override the values of the corresponding elements in
     * the persistence.xml file or specify values for
     * properties not specified in the persistence.xml.
     * @param classloader The ClassLoader to use to obtain all
     * entity classes and resources.
     * @return EntityManagerFactory for the persistence unit,
     * or null if the provider is not the right provider
     */
    public EntityManagerFactory createEntityManagerFactory(String emName,Map properties,
            ClassLoader classLoader) {
        Map nonNullProperties = (properties == null) ? new HashMap() : properties;
        String name = emName;
        if (name == null){
            name = "";
        }
        if (classLoader == null) {
            //TODO replace with EclipseLink exception
            throw new RuntimeException("EntityManager creation requires a classloader");
        }

        OSGiInitializer initializer = new OSGiInitializer(classLoader);
        EntityManagerSetupImpl emSetupImpl = null;

        try {
            Enumeration<URL> resources = classLoader.getResources("META-INF/persistence.xml");
            boolean initialized = false;
            while (resources.hasMoreElements()) {
                String puName = PersistenceUnitProcessor.buildPersistenceUnitName(PersistenceUnitProcessor.computePURootURL(resources.nextElement()), name);
                
                synchronized (EntityManagerFactoryProvider.emSetupImpls){
                    emSetupImpl = EntityManagerFactoryProvider.getEntityManagerSetupImpl(puName);
                    if (emSetupImpl == null || emSetupImpl.isUndeployed()){
                        if (!initialized) {
                            initializer.initialize(nonNullProperties, this);
                            initialized = true;
                        }
                            
                        emSetupImpl = EntityManagerFactoryProvider.getEntityManagerSetupImpl(puName);
                    }
                }

                // We found a match, stop looking.
                if (emSetupImpl != null) {
                    break;
                }
            }
        } catch (Exception e){
            throw PersistenceUnitLoadingException.exceptionSearchingForPersistenceResources(classLoader, e);
        }

        //gf bug 854  Returns null if EntityManagerSetupImpl for the name doesn't exist (e.g. a non-existant PU)
        if (emSetupImpl == null) {
            return null;
        }
            
        if (!isPersistenceProviderSupported(emSetupImpl.getPersistenceUnitInfo().getPersistenceProviderClassName())){
            return null;
        }

        // synchronized to prevent overriding of the class loader
        // and also calls to predeploy and undeploy by other threads -
        // the latter may alter result of shouldRedeploy method.
        synchronized(emSetupImpl) {
            if(emSetupImpl.shouldRedeploy()) {
                SEPersistenceUnitInfo persistenceInfo = (SEPersistenceUnitInfo)emSetupImpl.getPersistenceUnitInfo();
                persistenceInfo.setClassLoader(classLoader);
                if (emSetupImpl.isUndeployed()){
                    persistenceInfo.setNewTempClassLoader(JavaSECMPInitializer.getMainLoader());
                }
            }
            // call predeploy
            // this will just increment the factory count since we should already be deployed
            emSetupImpl.predeploy(emSetupImpl.getPersistenceUnitInfo(), nonNullProperties);
        }
            
        EntityManagerFactoryImpl factory = null;
        try {
            factory = new EntityManagerFactoryImpl(emSetupImpl, nonNullProperties);
        
            // This code has been added to allow validation to occur without actually calling createEntityManager
            if (emSetupImpl.shouldGetSessionOnCreateFactory(nonNullProperties)) {
                factory.getServerSession();
            }
            return factory;
        } catch (RuntimeException ex) {
            if(factory != null) {
                factory.close();
            } else {
                emSetupImpl.undeploy();
            }
            throw ex;
        }
    }

    /**
     * Answer the classloader to use to create an EntityManager.
     * If a classloader is not found in the properties map then 
     * attempt to locate one in the bundle registry.
     * 
     * @param emName
     * @param properties
     * @return ClassLoader
     */
    private ClassLoader getClassLoader(String emName, Map properties) {
        ClassLoader bundleClassLoader = null;
        if (properties != null) {
            bundleClassLoader = (ClassLoader)properties.get(PersistenceUnitProperties.CLASSLOADER);
        }
        if (bundleClassLoader == null) {
            Bundle bundle = puToBundle.get(emName);
            if (bundle == null) {
                //TODO replace with EclipseLink Exception
                throw new RuntimeException(
                        "Bundle providing Persistence Unit '" + emName
                                + "' not found.");
            }
            bundleClassLoader = getBundleClassLoader(bundle);
        }
        return bundleClassLoader;
    }

    public static ClassLoader getBundleClassLoader(Bundle bundle)  {
        Class loadClass = null;
        try {
            String activatorClassName = (String) bundle.getHeaders().get("Bundle-Activator");
            if (activatorClassName == null) {
                throw new RuntimeException("Bundle Activator Class not specified!");
            } else {
                loadClass = bundle.loadClass(activatorClassName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot obtain class loader from bundle");
        }
        ClassLoader classLoader = loadClass.getClassLoader();
        return classLoader;
    }
       

    /**
     * Returns whether the given persistence provider class is supported by this implementation
     * @param providerClassName
     * @return
     */
    public boolean isPersistenceProviderSupported(String providerClassName){
        return (providerClassName == null) || providerClassName.equals("") || providerClassName.equals(PersistenceProvider.class.getName());
    }

    public EntityManagerFactory createContainerEntityManagerFactory(
            PersistenceUnitInfo info, Map map) {
        throw new RuntimeException("createContainerEntityManagerFactory not supported in OSGi");
    }
}
