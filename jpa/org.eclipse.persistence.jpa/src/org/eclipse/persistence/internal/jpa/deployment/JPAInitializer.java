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
import org.eclipse.persistence.jpa.Archive;
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

    /**
     * This is used by OSGi related bundles.  In some OSGi implementations (like Eclipse PDE), the
     * resources in a bundle will potentially be stored somewhere other than ".".  This stores the prefix
     * under which the resources are stored.
     */
    public static String BUNDLE_RESOURCE_PREFIX = "";
    
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
    public EntityManagerSetupImpl callPredeploy(SEPersistenceUnitInfo persistenceUnitInfo, Map m, PersistenceInitializationHelper persistenceHelper, String persistenceUnitUniqueName, String sessionName) {
        AbstractSessionLog.getLog().log(SessionLog.FINER, "cmp_init_invoke_predeploy", persistenceUnitInfo.getPersistenceUnitName());
        Map mergedProperties = EntityManagerFactoryProvider.mergeMaps(m, persistenceUnitInfo.getProperties());
        // Bug#4452468  When globalInstrumentation is null, there is no weaving
        checkWeaving(mergedProperties);
        
        Set tempLoaderSet = PersistenceUnitProcessor.buildClassSet(persistenceUnitInfo, persistenceHelper.getClassLoader(persistenceUnitInfo.getPersistenceUnitName(), m), m);
        // Create the temp loader that will not cache classes for entities in our persistence unit
        ClassLoader tempLoader = createTempLoader(tempLoaderSet);
        persistenceUnitInfo.setNewTempClassLoader(tempLoader);
        persistenceUnitInfo.setClassLoader(persistenceHelper.getClassLoader(persistenceUnitInfo.getPersistenceUnitName(), m));

        EntityManagerSetupImpl emSetupImpl = new EntityManagerSetupImpl(persistenceUnitUniqueName, sessionName);

        // A call to predeploy will partially build the session we will use
        final ClassTransformer transformer = emSetupImpl.predeploy(persistenceUnitInfo, mergedProperties);

        // After preDeploy it's impossible to weave again - so may substitute the temporary classloader with the real one.
        // The temporary classloader could be garbage collected even if the puInfo is cached for the future use by other emSetupImpls.
        persistenceUnitInfo.setNewTempClassLoader(persistenceUnitInfo.getClassLoader());
        
        registerTransformer(transformer, persistenceUnitInfo, m);
        return emSetupImpl;
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
     * Find PersistenceUnitInfo corresponding to the persistence unit name.
     * Returns null if either persistence unit either not found or provider is not supported.
     */
    public SEPersistenceUnitInfo findPersistenceUnitInfo(String puName, Map m, PersistenceInitializationHelper persistenceHelper) {
        SEPersistenceUnitInfo persistenceUnitInfo = null;
        persistenceUnitInfo = EntityManagerFactoryProvider.initialPuInfos.get(puName);
        if(persistenceUnitInfo != null) {
            return persistenceUnitInfo;
        }
        return findPersistenceUnitInfoInArchives(puName, m, persistenceHelper);
    }
    
    /**
     * Find PersistenceUnitInfo corresponding to the persistence unit name.
     * Returns null if either persistence unit either not found or provider is not supported.
     */
    protected SEPersistenceUnitInfo findPersistenceUnitInfoInArchives(String puName, Map m, PersistenceInitializationHelper persistenceHelper) {
        SEPersistenceUnitInfo persistenceUnitInfo = null;
        // mkeith - get resource name from prop and include in subsequent call
        String descriptorPath = (String) m.get(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML);
        final Set<Archive> pars;
        if (descriptorPath != null) {
            pars = PersistenceUnitProcessor.findPersistenceArchives(initializationClassloader, descriptorPath);
        } else {
            pars = PersistenceUnitProcessor.findPersistenceArchives(initializationClassloader);
        }
        for (Archive archive: pars) {
            persistenceUnitInfo = findPersistenceUnitInfoInArchive(puName, archive, m, persistenceHelper);
            if(persistenceUnitInfo != null) {
                break;
            }
        }
        return persistenceUnitInfo;
    }
    
    /**
     * Find PersistenceUnitInfo corresponding to the persistence unit name in the archive.
     * Returns null if either persistence unit either not found or provider is not supported.
     */
    protected SEPersistenceUnitInfo findPersistenceUnitInfoInArchive(String puName, Archive archive, Map m, PersistenceInitializationHelper persistenceActivator){
        Iterator<SEPersistenceUnitInfo> persistenceUnits = PersistenceUnitProcessor.getPersistenceUnits(archive, initializationClassloader).iterator();
        while (persistenceUnits.hasNext()) {
            SEPersistenceUnitInfo persistenceUnitInfo = persistenceUnits.next();
            if(isPersistenceProviderSupported(persistenceUnitInfo.getPersistenceProviderClassName()) &&  persistenceUnitInfo.getPersistenceUnitName().equals(puName)) {
                return persistenceUnitInfo;
            }
        }
        return null;
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
    public abstract void registerTransformer(final ClassTransformer transformer, PersistenceUnitInfo persistenceUnitInfo, Map properties);

    /**
     * Indicates whether puName uniquely defines the persistence unit.
     */
    public boolean isPersistenceUnitUniquelyDefinedByName() {
        return true;
    }
    
    /**
     * In case persistence unit is not uniquely defined by name
     * this method is called to generate a unique name.
     */
    public String createUniquePersistenceUnitName(PersistenceUnitInfo puInfo) {
        return PersistenceUnitProcessor.buildPersistenceUnitName(puInfo.getPersistenceUnitRootUrl(), puInfo.getPersistenceUnitName());
    }
}
