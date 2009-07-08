/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     tware - 1.0RC1 - OSGI refactor
 *     12/23/2008-1.1M5 Michael O'Brien 
 *        - 253701: set persistenceInitializationHelper so EntityManagerSetupImpl.undeploy() can clear the JavaSECMPInitializer
 ******************************************************************************/  
package org.eclipse.persistence.jpa;

import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.spi.*;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.PersistenceUnitLoadingException;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceInitializationHelper;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.deployment.JPAInitializer;

/**
 * This is the EclipseLink EJB 3.0 provider
 * 
 * This provider should be used by JavaEE and JavaSE users.
 */
public class PersistenceProvider implements javax.persistence.spi.PersistenceProvider {

    // provides environment-specific initialization for Java EE or OSGI
    protected PersistenceInitializationHelper initializationHelper = null;    
    
    public PersistenceProvider(){
        // by default, we will work in Java EE (or SE)
        initializationHelper = new PersistenceInitializationHelper();
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
        ClassLoader classloader = initializationHelper.getClassLoader(emName, properties);
        return createEntityManagerFactory(emName, properties, classloader);
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
     * @param classLoader The classloader to search for persistence
     * units on
     * @return EntityManagerFactory for the persistence unit,
     * or null if the provider is not the right provider
     */
    protected EntityManagerFactory createEntityManagerFactory(String emName, Map properties, ClassLoader classLoader){
        Map nonNullProperties = (properties == null) ? new HashMap() : properties;
        String name = emName;
        if (name == null){
            name = "";
        }

        JPAInitializer initializer = initializationHelper.getInitializer(classLoader, nonNullProperties);
        EntityManagerSetupImpl emSetupImpl = null;

        try {
            Enumeration<URL> resources = classLoader.getResources("META-INF/persistence.xml");
            boolean initialized = false;
            while (resources.hasMoreElements()) {
                String puName = PersistenceUnitProcessor.buildPersistenceUnitName(PersistenceUnitProcessor.computePURootURL(resources.nextElement()), name);

                synchronized (EntityManagerFactoryProvider.persistenceUnits) {
                    PersistenceUnitInfo puInfo = EntityManagerFactoryProvider.getPersistenceUnitInfo(puName);
                    if (puInfo == null) {
                        if (!initialized) {
                            initializer.initialize(nonNullProperties, initializationHelper);
                            initialized = true;
                        }
                        puInfo = EntityManagerFactoryProvider.getPersistenceUnitInfo(puName);
                    }
                    if (puInfo != null) {
                        String esiName = puName;
                        if (nonNullProperties.get(PersistenceUnitProperties.SESSION_NAME) != null) {
                            esiName = puName + nonNullProperties.get(PersistenceUnitProperties.SESSION_NAME);
                        } else {
                            if (puInfo.getProperties().get(PersistenceUnitProperties.SESSION_NAME) != null) {
                                esiName = puName + puInfo.getProperties().get(PersistenceUnitProperties.SESSION_NAME);
                            }
                        }
                        emSetupImpl = EntityManagerFactoryProvider.getEntityManagerSetupImpl(esiName);
                        if (emSetupImpl == null) {
                            initializer.callPredeploy(puInfo, nonNullProperties, initializationHelper);
                            emSetupImpl = EntityManagerFactoryProvider.getEntityManagerSetupImpl(esiName);
                        }
                    }

                    // We found a match, stop looking.
                    if (emSetupImpl != null) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw PersistenceUnitLoadingException.exceptionSearchingForPersistenceResources(classLoader, e);
        }

        //gf bug 854  Returns null if EntityManagerSetupImpl for the name doesn't exist (e.g. a non-existent PU)
        if (emSetupImpl == null) {
            return null;
        }
            
        if (!initializer.isPersistenceProviderSupported(emSetupImpl.getPersistenceUnitInfo().getPersistenceProviderClassName())){
            return null;
        }

        // synchronized to prevent overriding of the class loader
        // and also calls to predeploy and undeploy by other threads -
        // the latter may alter result of shouldRedeploy method.
        synchronized(emSetupImpl) {
            if(emSetupImpl.shouldRedeploy()) {
                SEPersistenceUnitInfo persistenceInfo = (SEPersistenceUnitInfo)emSetupImpl.getPersistenceUnitInfo();
                persistenceInfo.setClassLoader(initializationHelper.getClassLoader(emName, properties));
                if (emSetupImpl.isUndeployed()){
                    persistenceInfo.setNewTempClassLoader(initializationHelper.getClassLoader(emName, properties));
                }
            }
            
            // 253701: cache a reference to (this) PersistenceProvider's initializer for later use during undeploy()
            emSetupImpl.setPersistenceInitializationHelper(this.initializationHelper);
            
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
     * Called by the container when an EntityManagerFactory
     * is to be created.
     *
     * @param info Metadata for use by the persistence provider
     * @return EntityManagerFactory for the persistence unit
     * specified by the metadata
     * @param map A Map of integration-level properties for use
     * by the persistence provider.
     */
    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map properties){
            Map nonNullProperties = (properties == null) ? new HashMap() : properties;
        
        EntityManagerSetupImpl emSetupImpl = null;
            boolean isNew = false;
            ClassTransformer transformer = null;
        synchronized (EntityManagerFactoryProvider.emSetupImpls) {
            String puName = PersistenceUnitProcessor.buildPersistenceUnitName(info.getPersistenceUnitRootUrl(), info.getPersistenceUnitName());
            String esiName = puName;
            if (nonNullProperties.get(PersistenceUnitProperties.SESSION_NAME) != null) {
                esiName = puName + nonNullProperties.get(PersistenceUnitProperties.SESSION_NAME);
            } else {
                if (info.getProperties().get(PersistenceUnitProperties.SESSION_NAME) != null) {
                    esiName = puName + info.getProperties().get(PersistenceUnitProperties.SESSION_NAME);
                }
            }
            emSetupImpl = EntityManagerFactoryProvider.getEntityManagerSetupImpl(esiName);
            if (emSetupImpl == null){
                emSetupImpl = new EntityManagerSetupImpl();
                    isNew = true;
                emSetupImpl.setIsInContainerMode(true);        
                    // if predeploy fails then emSetupImpl shouldn't be added to FactoryProvider
                    transformer = emSetupImpl.predeploy(info, nonNullProperties);
                EntityManagerFactoryProvider.addEntityManagerSetupImpl(esiName, emSetupImpl);
            }
        }
            
            if(!isNew && !emSetupImpl.isDeployed()) {
            transformer = emSetupImpl.predeploy(info, nonNullProperties);
        }
        if (transformer != null){
            info.addTransformer(transformer);
        }
        // When EntityManagerFactory is created, the session is only partially created
        // When the factory is actually accessed, the emSetupImpl will be used to complete the session construction
        EntityManagerFactoryImpl factory = new EntityManagerFactoryImpl(emSetupImpl, nonNullProperties);

        // This code has been added to allow validation to occur without actually calling createEntityManager
        if (emSetupImpl.shouldGetSessionOnCreateFactory(nonNullProperties)) {
            factory.getServerSession();
        }
        return factory;
    }

    public LoadState isLoaded(Object entity) {
        // TODO. Need to correctly implement this
       return LoadState.UNKNOWN;
    }

    public LoadState isLoadedWithReference(Object entity, String attributeName) {
        // TODO. Need to correctly implement this
       return LoadState.UNKNOWN;
    }

    public LoadState isLoadedWithoutReference(Object entity, String attributeName) {
        // TODO. Need to correctly implement this 
        return LoadState.UNKNOWN;
    }

}
