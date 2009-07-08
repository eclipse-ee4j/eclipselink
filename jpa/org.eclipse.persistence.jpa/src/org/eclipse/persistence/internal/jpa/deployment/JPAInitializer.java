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
 *     tware, ssmith = 1.0 - Generic JPA deployment (OSGI, EE, SE)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.deployment;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

/**
 * Base class for all JPA initialization classes.  This is an abstract class that provides the framework
 * for JPA initialization (finding and initializing persistence units).  Subclasses implement the abstract methods
 * to provide customized functionality
 * 
 * @see JavaSESMPInitializer
 * @see OSGiInitializer
 * @See EquinoxInitializer
 * @author tware
 *
 */
public abstract class JPAInitializer {

    // The internal loader is used by applications that do weaving to pre load classes
    // When this flag is set to false, we will not be able to weave.
    protected boolean shouldCreateInternalLoader = true;
    
    protected ClassLoader initializationClassloader = null;

    /**
     * Initialize the logging file if it is specified by the system property.
     */
    public static void initializeTopLinkLoggingFile() {        
        String loggingFile = System.getProperty(PersistenceUnitProperties.LOGGING_FILE);
        try {
            if (loggingFile != null) {
                AbstractSessionLog.getLog().setWriter(new FileWriter(loggingFile));
            }
        } catch (IOException e) {
            AbstractSessionLog.getLog().log(SessionLog.WARNING, "cmp_init_default_logging_file_is_invalid",loggingFile,e);
        }
    }
    
    /**
     * predeploy (with deploy) is one of the two steps required in deployment of entities
     * This method will prepare to call predeploy, call it and finally register the
     * transformer returned to be used for weaving.
     */
    public boolean callPredeploy(PersistenceUnitInfo persistenceUnitInfo, Map m, PersistenceInitializationHelper persistenceHelper) {
        // we will only attempt to deploy when EclipseLink is specified as the provider or the provider is unspecified
        String providerClassName = persistenceUnitInfo.getPersistenceProviderClassName();
        if (isPersistenceProviderSupported(providerClassName)){
            // Bug 210280/215865: this decoded URL path + PU name [puName] must be used as the key in the EMSetup map below
            String sessionName = (String)m.get(PersistenceUnitProperties.SESSION_NAME);
            if (sessionName == null){
                sessionName = (String)persistenceUnitInfo.getProperties().get(PersistenceUnitProperties.SESSION_NAME);
            }
            if (sessionName == null) sessionName = "";
            String emName = PersistenceUnitProcessor.buildPersistenceUnitName(persistenceUnitInfo.getPersistenceUnitRootUrl(),persistenceUnitInfo.getPersistenceUnitName()) + sessionName;
            
            EntityManagerSetupImpl emSetupImpl = EntityManagerFactoryProvider.getEntityManagerSetupImpl(emName);
            // if we already have an EntityManagerSetupImpl this PU has already been processed.  Use the existing one
            if (emSetupImpl != null && !emSetupImpl.isUndeployed()){
                return false;
            }
            Set tempLoaderSet = PersistenceUnitProcessor.buildClassSet(persistenceUnitInfo, Thread.currentThread().getContextClassLoader());
            AbstractSessionLog.getLog().log(SessionLog.FINER, "cmp_init_invoke_predeploy", persistenceUnitInfo.getPersistenceUnitName());
            Map mergedProperties = EntityManagerFactoryProvider.mergeMaps(m, persistenceUnitInfo.getProperties());
            // Bug#4452468  When globalInstrumentation is null, there is no weaving
            checkWeaving(mergedProperties);
            
            if (persistenceUnitInfo instanceof SEPersistenceUnitInfo){
                // Create the temp loader that will not cache classes for entities in our persistence unit
                ClassLoader tempLoader = createTempLoader(tempLoaderSet);
                ((SEPersistenceUnitInfo)persistenceUnitInfo).setNewTempClassLoader(tempLoader);
                ((SEPersistenceUnitInfo)persistenceUnitInfo).setClassLoader(persistenceHelper.getClassLoader(persistenceUnitInfo.getPersistenceUnitName(), m));
            }
            if (emSetupImpl == null){
                emSetupImpl = new EntityManagerSetupImpl();
                // Bug 210280/215865: use the decoded URL path + PU name as the key in the EMSetup map - to handle paths with spaces
                EntityManagerFactoryProvider.addEntityManagerSetupImpl(emName, emSetupImpl);
                ++ EntityManagerFactoryProvider.PUIUsageCount;
            }
           
    
            // A call to predeploy will partially build the session we will use
            final ClassTransformer transformer = emSetupImpl.predeploy(persistenceUnitInfo, mergedProperties);
    
            registerTransformer(transformer, persistenceUnitInfo);
            return true;
        }
        return false;
    }
    
    /**
     * Check whether weaving is possible and update the properties and variable as appropriate
     * @param properties The list of properties to check for weaving and update if weaving is not needed
     */
    public abstract void checkWeaving(Map properties);

    /**
     * Create a temporary class loader that can be used to inspect classes and then
     * thrown away.  This allows classes to be introspected prior to loading them
     * with application's main class loader enabling weaving.
     */
    protected abstract ClassLoader createTempLoader(Collection col);

    protected abstract ClassLoader createTempLoader(Collection col, boolean shouldOverrideLoadClassForCollectionMembers);
    
    /**
     * This method initializes the container.  Essentially, it will try to load the
     * class that contains the list of entities and reflectively call the method that
     * contains that list.  It will then initialize the container with that list.
     * If succeeded return true, false otherwise.
     */
    public void initialize(Map m, PersistenceInitializationHelper persistenceHelper) {
        initializeClassLoader(persistenceHelper);
        final Set<Archive> pars = PersistenceUnitProcessor.findPersistenceArchives(initializationClassloader);
        for (Archive archive: pars) {
            AbstractSessionLog.getLog().log(SessionLog.FINER, "cmp_init_initialize", archive);
            initPersistenceUnits(archive, m, persistenceHelper);
        }
    }
    
    /**
     * Initialize one persistence unit.
     * Initialization is a two phase process.  First the predeploy process builds the metadata
     * and creates any required transformers.
     * Second the deploy process creates an EclipseLink session based on that metadata.
     */
    protected void initPersistenceUnits(Archive archive, Map m, PersistenceInitializationHelper persistenceActivator){
        Iterator<SEPersistenceUnitInfo> persistenceUnits = PersistenceUnitProcessor.getPersistenceUnits(archive, initializationClassloader).iterator();
        while (persistenceUnits.hasNext()) {
            SEPersistenceUnitInfo persistenceUnitInfo = persistenceUnits.next();
            EntityManagerFactoryProvider.persistenceUnits.put(PersistenceUnitProcessor.buildPersistenceUnitName(persistenceUnitInfo.getPersistenceUnitRootUrl(), persistenceUnitInfo.getPersistenceUnitName()), persistenceUnitInfo);
            callPredeploy(persistenceUnitInfo, m, persistenceActivator);
        }
    }

    /**
     * Returns whether the given persistence provider class is supported by this implementation
     * @param providerClassName
     * @return
     */
    public boolean isPersistenceProviderSupported(String providerClassName){
        return (providerClassName == null) || providerClassName.equals("") || providerClassName.equals(EntityManagerFactoryProvider.class.getName()) || providerClassName.equals(PersistenceProvider.class.getName());
    }
    
    /**
     * Create a list of java.lang.Class that contains the classes of all the entities
     * that we will be deploying.
     */
    protected Set loadEntityClasses(Collection entityNames, ClassLoader classLoader) {
        Set entityClasses = new HashSet();

        // Load the classes using the loader passed in
        AbstractSessionLog.getLog().log(SessionLog.FINER, "cmp_loading_entities_using_loader", classLoader);
        for (Iterator iter = entityNames.iterator(); iter.hasNext();) {
            String entityClassName = (String)iter.next();
            try {
                entityClasses.add(classLoader.loadClass(entityClassName));
            } catch (ClassNotFoundException cnfEx) {
                throw ValidationException.entityClassNotFound(entityClassName, classLoader, cnfEx);
            }
        }
        return entityClasses;
    }
    
    /**
     * Register a transformer.  This method should be overridden to provide the appropriate transformer
     * registration for the environment
     * @param transformer
     * @param persistenceUnitInfo
     */
    public abstract void registerTransformer(final ClassTransformer transformer, PersistenceUnitInfo persistenceUnitInfo);

    /**
     * Initialize the classloader to be used during persistence unit initialization.  This method should
     * be overridden to provide initialization appropriate to the environment
     * @param persistenceHelper
     */
    public void initializeClassLoader(PersistenceInitializationHelper persistenceHelper){
    }
}
