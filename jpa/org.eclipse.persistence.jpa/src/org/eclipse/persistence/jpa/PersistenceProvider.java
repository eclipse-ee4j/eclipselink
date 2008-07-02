/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
 ******************************************************************************/  
package org.eclipse.persistence.jpa;

import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;

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
        // get a class loader to use with this specific EM
        ClassLoader currentLoader = initializationHelper.getClassLoader(emName, nonNullProperties);

        try {
            Enumeration<URL> resources = currentLoader.getResources("META-INF/persistence.xml");
            boolean initialized = false;
            while (resources.hasMoreElements()) {
                String puName = PersistenceUnitProcessor.buildPersistenceUnitName(PersistenceUnitProcessor.computePURootURL(resources.nextElement()), name);
                
                synchronized (EntityManagerFactoryProvider.emSetupImpls){
                    emSetupImpl = EntityManagerFactoryProvider.getEntityManagerSetupImpl(puName);
                    if (emSetupImpl == null || emSetupImpl.isUndeployed()){
                        if (!initialized){                           
                            initializer.initialize(nonNullProperties, initializationHelper);
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
            throw PersistenceUnitLoadingException.exceptionSearchingForPersistenceResources(currentLoader, e);
        }

        //gf bug 854  Returns null if EntityManagerSetupImpl for the name doesn't exist (e.g. a non-existant PU)
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
            
            emSetupImpl = EntityManagerFactoryProvider.getEntityManagerSetupImpl(puName);
            if (emSetupImpl == null){
                emSetupImpl = new EntityManagerSetupImpl();
                    isNew = true;
                emSetupImpl.setIsInContainerMode(true);        
                    // if predeploy fails then emSetupImpl shouldn't be added to FactoryProvider
                    transformer = emSetupImpl.predeploy(info, nonNullProperties);
                EntityManagerFactoryProvider.addEntityManagerSetupImpl(puName, emSetupImpl);
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

}
