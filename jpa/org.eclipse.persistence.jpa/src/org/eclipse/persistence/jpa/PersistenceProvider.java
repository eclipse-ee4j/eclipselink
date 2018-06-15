/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     tware - 1.0RC1 - OSGI refactor
//     12/23/2008-1.1M5 Michael O'Brien
//        - 253701: set persistenceInitializationHelper so EntityManagerSetupImpl.undeploy() can clear the JavaSECMPInitializer
//     12/24/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     01/08/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     01/11/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     02/04/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     02/02/2015-2.6 Dalia Abo Sheasha
//       - 458462: generateSchema throws a ClassCastException within a container
//     02/17/2015-2.6 Rick Curtis
//       - 460138: Change method visibility.
package org.eclipse.persistence.jpa;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.LoadState;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.SystemProperties;
import org.eclipse.persistence.exceptions.PersistenceUnitLoadingException;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.deployment.JPAInitializer;
import org.eclipse.persistence.internal.jpa.deployment.JavaSECMPInitializer;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.weaving.PersistenceWeaved;

/**
 * This is the EclipseLink EJB 3.0 provider
 *
 * This provider should be used by JavaEE and JavaSE users.
 */
public class PersistenceProvider implements javax.persistence.spi.PersistenceProvider, ProviderUtil {

    public PersistenceProvider(){
    }

    /**
     * Internal method to return the entity manager factory.
     */
    protected EntityManagerFactoryImpl createEntityManagerFactoryImpl(PersistenceUnitInfo puInfo, Map properties, boolean requiresConnection){
        if (puInfo != null) {
            boolean isNew = false;
            String uniqueName = null; // the name the uniquely defines the pu
            String sessionName = null;
            EntityManagerSetupImpl emSetupImpl = null;
            String puName = puInfo.getPersistenceUnitName();
            JPAInitializer initializer = getInitializer(puInfo.getPersistenceUnitName(), properties);

            try {
                if (EntityManagerSetupImpl.mustBeCompositeMember(puInfo)) {
                    // Persistence unit cannot be used standalone (only as a composite member).
                    // Still the factory will be created but attempt to createEntityManager would cause an exception.
                    emSetupImpl = new EntityManagerSetupImpl(puName, puName);
                    // Predeploy assigns puInfo and does not do anything else.
                    // The session is not created, no need to add emSetupImpl to the global map.
                    emSetupImpl.predeploy(puInfo, properties);
                    isNew = true;
                } else {
                    if (initializer.isPersistenceUnitUniquelyDefinedByName()) {
                        uniqueName = puName;
                    } else {
                        uniqueName = initializer.createUniquePersistenceUnitName(puInfo);
                    }

                    sessionName = EntityManagerSetupImpl.getOrBuildSessionName(properties, puInfo, uniqueName);
                    synchronized (EntityManagerFactoryProvider.emSetupImpls) {
                        emSetupImpl = EntityManagerFactoryProvider.getEntityManagerSetupImpl(sessionName);

                        if (emSetupImpl == null) {
                            // there may be initial emSetupImpl cached in Initializer - remove it and use.
                            emSetupImpl = initializer.extractInitialEmSetupImpl(puName);

                            if (emSetupImpl != null) {
                                // change the name
                                emSetupImpl.changeSessionName(sessionName);
                            } else {
                                // create and predeploy a new emSetupImpl
                                emSetupImpl = initializer.callPredeploy((SEPersistenceUnitInfo) puInfo, properties, uniqueName, sessionName);
                            }

                            // emSetupImpl has been already predeployed, predeploy will just increment factoryCount.
                            emSetupImpl.predeploy(emSetupImpl.getPersistenceUnitInfo(), properties);
                            EntityManagerFactoryProvider.addEntityManagerSetupImpl(sessionName, emSetupImpl);
                            isNew = true;
                        }
                    }
                }
            } catch (Exception e) {
                throw PersistenceUnitLoadingException.exceptionSearchingForPersistenceResources(initializer.getInitializationClassLoader(), e);
            }

            if (! isNew) {
                if (! uniqueName.equals(emSetupImpl.getPersistenceUnitUniqueName())) {
                    throw PersistenceUnitLoadingException.sessionNameAlreadyInUse(sessionName, uniqueName, emSetupImpl.getPersistenceUnitUniqueName());
                }

                // synchronized to prevent undeploying by other threads.
                boolean undeployed = false;
                synchronized(emSetupImpl) {
                    if (emSetupImpl.isUndeployed()) {
                        undeployed = true;
                    } else {
                        // emSetupImpl has been already predeployed, predeploy will just increment factoryCount.
                        emSetupImpl.predeploy(emSetupImpl.getPersistenceUnitInfo(), properties);
                    }
                }

                if (undeployed) {
                    // after the emSetupImpl has been obtained from emSetupImpls
                    // it has been undeployed by factory.close() in another thread - start all over again.
                    return (EntityManagerFactoryImpl) createEntityManagerFactory(puName, properties);
                }
            }

            EntityManagerFactoryImpl factory = null;
            try {
                factory = new EntityManagerFactoryImpl(emSetupImpl, properties);
                emSetupImpl.setRequiresConnection(requiresConnection);

                emSetupImpl.preInitializeCanonicalMetamodel(factory);
                // This code has been added to allow validation to occur without actually calling createEntityManager

                if (emSetupImpl.shouldGetSessionOnCreateFactory(properties)) {
                    factory.getDatabaseSession();
                }

                return factory;
            } catch (RuntimeException ex) {
                if (factory != null) {
                    factory.close();
                } else {
                    emSetupImpl.undeploy();
                }

                throw ex;
            }
        }

        return null;
    }

    /**
     * Called by Persistence class when an EntityManagerFactory
     * is to be created.
     *
     * @param emName The name of the persistence unit
     * @param properties A Map of properties for use by the
     * persistence provider. These properties may be used to
     * override the values of the corresponding elements in
     * the persistence.xml file or specify values for
     * properties not specified in the persistence.xml.
     * @return EntityManagerFactory for the persistence unit,
     * or null if the provider is not the right provider
     */
    @Override
    public EntityManagerFactory createEntityManagerFactory(String emName, Map properties){
        Map nonNullProperties = (properties == null) ? new HashMap() : properties;

        if (checkForProviderProperty(nonNullProperties)){
            String name = (emName == null) ? "" : emName;
            JPAInitializer initializer = getInitializer(name, nonNullProperties);
            return createEntityManagerFactoryImpl(initializer.findPersistenceUnitInfo(name, nonNullProperties), nonNullProperties, true);
        }

        // Not EclipseLink so return null;
        return null;
    }

    /**
     * Create database schemas and/or tables and/or create DDL
     * scripts as determined by the supplied properties.
     * <p>
     * Called by the Persistence class when schema generation is to occur as a
     * separate phase from creation of the entity manager factory.
     * <p>
     * @param info the name of the persistence unit
     * @param properties properties for schema generation; these may also
     *        contain provider-specific properties. The value of these
     *        properties override any values that may have been configured
     *        elsewhere.
     * @throws PersistenceException if insufficient or inconsistent
     *         configuration information is provided of if schema generation
     *         otherwise fails
     *
     * @since Java Persistence 2.1
     */
    @Override
    public void generateSchema(PersistenceUnitInfo info, Map properties) {
        if (checkForProviderProperty(properties)) {
            // Bug 458462 - Generate the DDL and then close. This method is
            // called when running within a container.
            createContainerEntityManagerFactoryImpl(info, properties, false).close();
        }
    }

    /**
     * Create database schemas and/or tables and/or create DDL scripts as
     * determined by the supplied properties.
     * <p>
     * Called by the Persistence class when schema generation is to occur as a
     * separate phase from creation of the entity manager factory.
     * <p>
     * @param persistenceUnitName the name of the persistence unit
     * @param properties properties for schema generation; these may also
     *        contain provider-specific properties. The value of these
     *        properties override any values that may have been configured
     *        elsewhere.
     * @throws PersistenceException if insufficient or inconsistent
     *         configuration information is provided of if schema generation
     *         otherwise fails
     *
     * @since Java Persistence 2.1
     */
    @Override
    public boolean generateSchema(String persistenceUnitName, Map properties) {
        String puName = (persistenceUnitName == null) ? "" : persistenceUnitName;
        Map nonNullProperties = (properties == null) ? new HashMap() : properties;

        // If not EclipseLink, do nothing.
        if (checkForProviderProperty(nonNullProperties)) {
            JPAInitializer initializer = getInitializer(puName, nonNullProperties);
            SEPersistenceUnitInfo puInfo = initializer.findPersistenceUnitInfo(puName, nonNullProperties);

            if (puInfo != null && checkForProviderProperty(properties)) {
                // Will cause a login if necessary, generate the DDL and then close.
                // The false indicates that we do not require a connection if
                // generating only to script. Since the user may have connected with
                // specific database credentials for DDL generation or even provided
                // a specific connection, close the emf once we're done.
                createEntityManagerFactoryImpl(puInfo, properties, false).close();
                return true;
            }
        }

        return false;
    }

    /**
     * Return JPAInitializer corresponding to the passed classLoader.
     * Note: This is written as an instance method rather than a static to allow
     * it to be overridden
     * @param emName
     * @param m
     * @return
     */
    public JPAInitializer getInitializer(String emName, Map m){
        ClassLoader classLoader = getClassLoader(emName, m);
        return JavaSECMPInitializer.getJavaSECMPInitializer(classLoader);
    }


    /**
     * Need to check that the provider property is null or set for EclipseLink
     */
    public boolean checkForProviderProperty(Map properties){
        Object provider = properties.get("javax.persistence.provider");
        if (provider != null){
            //user has specified a provider make sure it is us or abort.
            if (provider instanceof Class){
                provider = ((Class)provider).getName();
            }
            try{
                if (!(((String)provider).equals(EntityManagerFactoryProvider.class.getName()) || ((String)provider).equals(PersistenceProvider.class.getName()))){
                    return false;
                    //user has requested another provider so lets ignore this request.
                }
            }catch(ClassCastException e){
                return false;
                // not a recognized provider property value so must be another provider.
            }
        }
        return true;

    }
    /**
     * Called by the container when an EntityManagerFactory
     * is to be created.
     *
     * @param info Metadata for use by the persistence provider
     * @return EntityManagerFactory for the persistence unit
     * specified by the metadata
     * @param properties A Map of integration-level properties for use
     * by the persistence provider.
     */
    @Override
    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map properties) {
        return createContainerEntityManagerFactoryImpl(info, properties, true);
    }

    protected EntityManagerFactory createContainerEntityManagerFactoryImpl(PersistenceUnitInfo info, Map properties, boolean requiresConnection) {
        // Record that we are inside a JEE container to allow weaving for non managed persistence units.
        JavaSECMPInitializer.setIsInContainer(true);

        Map nonNullProperties = (properties == null) ? new HashMap() : properties;

        String forceTargetServer = EntityManagerFactoryProvider.getConfigPropertyAsString(SystemProperties.ENFORCE_TARGET_SERVER, null);
        if ("true".equalsIgnoreCase(forceTargetServer)) {
            nonNullProperties.remove(PersistenceUnitProperties.TARGET_SERVER);
        }

        EntityManagerSetupImpl emSetupImpl = null;
        if (EntityManagerSetupImpl.mustBeCompositeMember(info)) {
            // persistence unit cannot be used standalone (only as a composite member).
            // still the factory will be created but attempt to createEntityManager would cause an exception.
            emSetupImpl = new EntityManagerSetupImpl(info.getPersistenceUnitName(), info.getPersistenceUnitName());
            // predeploy assigns puInfo and does not do anything else.
            // the session is not created, no need to add emSetupImpl to the global map.
            emSetupImpl.predeploy(info, nonNullProperties);
        } else {
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
                    } else {
                        // emSetupImpl has been already predeployed, predeploy will just increment factoryCount.
                        transformer = emSetupImpl.predeploy(emSetupImpl.getPersistenceUnitInfo(), nonNullProperties);
                    }
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
        }

        EntityManagerFactoryImpl factory = null;
        try {
            factory = new EntityManagerFactoryImpl(emSetupImpl, nonNullProperties);
            emSetupImpl.setRequiresConnection(requiresConnection);
            emSetupImpl.preInitializeCanonicalMetamodel(factory);

            // This code has been added to allow validation to occur without actually calling createEntityManager
            if (emSetupImpl.shouldGetSessionOnCreateFactory(nonNullProperties)) {
                factory.getDatabaseSession();
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
     * Return the utility interface implemented by the persistence
     * provider.
     * @return ProviderUtil interface
     *
     * @since Java Persistence 2.0
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
    public LoadState isLoaded(Object entity){
        if (entity instanceof PersistenceWeaved){
            return isLoadedWithReference(entity, null);
        }
        return LoadState.UNKNOWN;
     }

    /**
     * Answer the classloader to use to create an EntityManager.
     * If a classloader is not found in the properties map then
     * use the current thread classloader.
     *
     * @param properties
     * @return ClassLoader
     */
    public ClassLoader getClassLoader(String emName, Map properties) {
        ClassLoader classloader = null;
        if (properties != null) {
            classloader = (ClassLoader)properties.get(PersistenceUnitProperties.CLASSLOADER);
        }
        if (classloader == null) {
            classloader = Thread.currentThread().getContextClassLoader();
        }
        return classloader;
    }
}

