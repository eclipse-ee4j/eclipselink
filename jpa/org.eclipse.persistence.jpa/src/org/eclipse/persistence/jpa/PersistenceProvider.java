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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     tware - 1.0RC1 - OSGI refactor
 *     12/23/2008-1.1M5 Michael O'Brien 
 *        - 253701: set persistenceInitializationHelper so EntityManagerSetupImpl.undeploy() can clear the JavaSECMPInitializer
 ******************************************************************************/  
package org.eclipse.persistence.jpa;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.*;

import org.eclipse.persistence.exceptions.PersistenceUnitLoadingException;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceInitializationHelper;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.deployment.JPAInitializer;
import org.eclipse.persistence.internal.weaving.PersistenceWeaved;

/**
 * This is the EclipseLink EJB 3.0 provider
 * 
 * This provider should be used by JavaEE and JavaSE users.
 */
public class PersistenceProvider implements javax.persistence.spi.PersistenceProvider, ProviderUtil {

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
        boolean isNew = false;
        // the name that uniquely defines persistence unit
        String uniqueName;
        String sessionName;
        try {            
            SEPersistenceUnitInfo puInfo;
            puInfo = initializer.findPersistenceUnitInfo(name, nonNullProperties, initializationHelper);
            // either persistence unit not found or provider not supported
            if(puInfo == null) {
                return null;
            }
            
            if(initializer.isPersistenceUnitUniquelyDefinedByName()) {
                uniqueName = name;
            } else {
                uniqueName = initializer.createUniquePersistenceUnitName(puInfo);
            }
                
            sessionName = EntityManagerSetupImpl.getOrBuildSessionName(nonNullProperties, puInfo, uniqueName);
            synchronized (EntityManagerFactoryProvider.emSetupImpls) {
                emSetupImpl = EntityManagerFactoryProvider.getEntityManagerSetupImpl(sessionName);
                if(emSetupImpl == null) {
                    // there may be initial emSetupImpl (possible only in SE that uses agent) remove it and use.
                    emSetupImpl = EntityManagerFactoryProvider.initialEmSetupImpls.remove(uniqueName);
                    if(emSetupImpl != null) {
                        // change the name
                        emSetupImpl.changeSessionName(sessionName);
                        //  make sure we grab the classloader that may have 
                        // been provided by the user. I.E. a DynamicClassLoader
                        puInfo.setClassLoader(classLoader);
                    }
                    if(emSetupImpl == null) {
                        // create and predeploy a new emSetupImpl
                        emSetupImpl = initializer.callPredeploy(puInfo, nonNullProperties, initializationHelper, uniqueName, sessionName);
                    }
                    // emSetupImpl has been already predeployed, predeploy will just increment factoryCount.
                    emSetupImpl.predeploy(emSetupImpl.getPersistenceUnitInfo(), nonNullProperties);
                    EntityManagerFactoryProvider.addEntityManagerSetupImpl(sessionName, emSetupImpl);
                    isNew = true;
                }
            }
        } catch (Exception e) {
            throw PersistenceUnitLoadingException.exceptionSearchingForPersistenceResources(classLoader, e);
        }

        if(!isNew) {
            if(!uniqueName.equals(emSetupImpl.getPersistenceUnitUniqueName())) {
                throw PersistenceUnitLoadingException.sessionNameAlreadyInUse(sessionName, uniqueName, emSetupImpl.getPersistenceUnitUniqueName());
            }
            
            // synchronized to prevent undeploying by other threads.
            boolean undeployed = false;
            synchronized(emSetupImpl) {
                if(emSetupImpl.isUndeployed()) {
                    undeployed = true;
                }
                
                // emSetupImpl has been already predeployed, predeploy will just increment factoryCount.
                emSetupImpl.predeploy(emSetupImpl.getPersistenceUnitInfo(), nonNullProperties);
            }
            if(undeployed) {
                // after the emSetupImpl has been obtained from emSetupImpls
                // it has been undeployed by factory.close() in another thread - start all over again.
                return createEntityManagerFactory(emName, properties, classLoader);
            }
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
        String uniqueName = PersistenceUnitProcessor.buildPersistenceUnitName(info.getPersistenceUnitRootUrl(), info.getPersistenceUnitName());
        String sessionName = EntityManagerSetupImpl.getOrBuildSessionName(nonNullProperties, info, uniqueName);
        synchronized (EntityManagerFactoryProvider.emSetupImpls) {
            emSetupImpl = EntityManagerFactoryProvider.getEntityManagerSetupImpl(sessionName);
            if (emSetupImpl == null){
                emSetupImpl = new EntityManagerSetupImpl(uniqueName, sessionName);
                isNew = true;
                emSetupImpl.setIsInContainerMode(true);        
                // if predeploy fails then emSetupImpl shouldn't be added to FactoryProvider
                transformer = emSetupImpl.predeploy(info, nonNullProperties);
                EntityManagerFactoryProvider.addEntityManagerSetupImpl(sessionName, emSetupImpl);
            }
        }
            
        if(!isNew) {
            if(!uniqueName.equals(emSetupImpl.getPersistenceUnitUniqueName())) {
                throw PersistenceUnitLoadingException.sessionNameAlreadyInUse(sessionName, uniqueName, emSetupImpl.getPersistenceUnitUniqueName());
            }
            // synchronized to prevent undeploying by other threads.
            boolean undeployed = false;
            synchronized(emSetupImpl) {
                if(emSetupImpl.isUndeployed()) {
                    undeployed = true;
                }
                
                // emSetupImpl has been already predeployed, predeploy will just increment factoryCount.
                transformer = emSetupImpl.predeploy(emSetupImpl.getPersistenceUnitInfo(), nonNullProperties);
            }
            if(undeployed) {
                // after the emSetupImpl has been obtained from emSetupImpls
                // it has been undeployed by factory.close() in another thread - start all over again.
                return createContainerEntityManagerFactory(info, properties);
            }
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

    /**
     * Return the utility interface implemented by the persistence
     * provider.
     * @return ProviderUtil interface
     *
     * @since Java Persistence 2.0
     */
    public ProviderUtil getProviderUtil(){
        return this;
    }


    /**
     * If the provider determines that the entity has been provided
     * by itself and that the state of the specified attribute has
     * been loaded, this method returns <code>LoadState.LOADED</code>.
     * If the provider determines that the entity has been provided
     * by itself and that either entity attributes with <code>FetchType.EAGER</code> 
     * have not been loaded or that the state of the specified
     * attribute has not been loaded, this methods returns
     * <code>LoadState.NOT_LOADED</code>.
     * If a provider cannot determine the load state, this method
     * returns <code>LoadState.UNKNOWN</code>.
     * The provider's implementation of this method must not obtain
     * a reference to an attribute value, as this could trigger the
     * loading of entity state if the entity has been provided by a
     * different provider.
     * @param entity
     * @param attributeName  name of attribute whose load status is
     *        to be determined
     * @return load status of the attribute
     */
    public LoadState isLoadedWithoutReference(Object entity, String attributeName){
        if (entity instanceof PersistenceWeaved){
            return isLoadedWithReference(entity, attributeName);
        }
        return LoadState.UNKNOWN;
     }
    
    /**
     * If the provider determines that the entity has been provided
     * by itself and that the state of the specified attribute has
     * been loaded, this method returns <code>LoadState.LOADED</code>.
     * If a provider determines that the entity has been provided
     * by itself and that either the entity attributes with <code>FetchType.EAGER</code>
     * have not been loaded or that the state of the specified
     * attribute has not been loaded, this method returns
     * return <code>LoadState.NOT_LOADED</code>.
     * If the provider cannot determine the load state, this method
     * returns <code>LoadState.UNKNOWN</code>.
     * The provider's implementation of this method is permitted to
     * obtain a reference to the attribute value.  (This access is
     * safe because providers which might trigger the loading of the
     * attribute state will have already been determined by
     * <code>isLoadedWithoutReference</code>. )
     *
     * @param entity
     * @param attributeName  name of attribute whose load status is
     *        to be determined
     * @return load status of the attribute
     */
    public LoadState isLoadedWithReference(Object entity, String attributeName){
        Iterator<EntityManagerSetupImpl> setups = EntityManagerFactoryProvider.getEmSetupImpls().values().iterator();
        while (setups.hasNext()){
            EntityManagerSetupImpl setup = setups.next();
            if (setup.isDeployed()){
                Boolean isLoaded = EntityManagerFactoryImpl.isLoaded(entity, setup.getSession());
                if (isLoaded != null){
                    if (isLoaded.booleanValue() && attributeName != null){
                        isLoaded = EntityManagerFactoryImpl.isLoaded(entity, attributeName, setup.getSession());
                    }
                    if (isLoaded != null){
                        return isLoaded.booleanValue() ? LoadState.LOADED : LoadState.NOT_LOADED;
                    }
                }
            }
        }
        return LoadState.UNKNOWN;
     }
    
    /**
     * If the provider determines that the entity has been provided
     * by itself and that the state of all attributes for which
     * <code>FetchType.EAGER</code> has been specified have been loaded, this 
     * method returns <code>LoadState.LOADED</code>.
     * If the provider determines that the entity has been provided
     * by itself and that not all attributes with <code>FetchType.EAGER</code> 
     * have been loaded, this method returns <code>LoadState.NOT_LOADED</code>.
     * If the provider cannot determine if the entity has been
     * provided by itself, this method returns <code>LoadState.UNKNOWN</code>.
     * The provider's implementation of this method must not obtain
     * a reference to any attribute value, as this could trigger the
     * loading of entity state if the entity has been provided by a
     * different provider.
     * @param entity whose loaded status is to be determined
     * @return load status of the entity
     */
    public LoadState isLoaded(Object entity){
        if (entity instanceof PersistenceWeaved){
            return isLoadedWithReference(entity, null);
        }
        return LoadState.UNKNOWN;
     }
}

