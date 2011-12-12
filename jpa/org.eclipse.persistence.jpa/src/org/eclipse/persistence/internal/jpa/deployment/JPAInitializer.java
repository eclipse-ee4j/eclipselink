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
 *     tware, ssmith = 1.0 - Generic JPA deployment (OSGI, EE, SE)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.deployment;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
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
   
    // The internal loader is used by applications that do weaving to pre load classes
    // When this flag is set to false, we will not be able to weave.
    protected boolean shouldCreateInternalLoader = true;
    
    protected ClassLoader initializationClassloader = null;
        
    // Cache the initial puInfos - those used by  initialEmSetupImpls
    protected Map<String, SEPersistenceUnitInfo> initialPuInfos;
    // Cache the initial emSetupImpls - those created and predeployed by JavaSECMPInitializer.initialize method. 
    protected Map<String, EntityManagerSetupImpl> initialEmSetupImpls;

    // Initializers keyed by their initializationClassloaders 
    protected static Map<ClassLoader, JPAInitializer> initializers = new Hashtable();
    
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
    public EntityManagerSetupImpl callPredeploy(SEPersistenceUnitInfo persistenceUnitInfo, Map m, String persistenceUnitUniqueName, String sessionName) {
        AbstractSessionLog.getLog().log(SessionLog.FINER, SessionLog.JPA, "cmp_init_invoke_predeploy", persistenceUnitInfo.getPersistenceUnitName());
        Map mergedProperties = EntityManagerFactoryProvider.mergeMaps(m, persistenceUnitInfo.getProperties());
        // Bug#4452468  When globalInstrumentation is null, there is no weaving
        checkWeaving(mergedProperties);
        
        Set tempLoaderSet = PersistenceUnitProcessor.buildClassSet(persistenceUnitInfo, m);
        // Create the temp loader that will not cache classes for entities in our persistence unit
        ClassLoader tempLoader = createTempLoader(tempLoaderSet);
        persistenceUnitInfo.setNewTempClassLoader(tempLoader);

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
    public SEPersistenceUnitInfo findPersistenceUnitInfo(String puName, Map m) {
        SEPersistenceUnitInfo persistenceUnitInfo = null;
        if(initialPuInfos != null) {
            persistenceUnitInfo = initialPuInfos.get(puName);
        }
        if(persistenceUnitInfo != null) {
            return persistenceUnitInfo;
        }
        return findPersistenceUnitInfoInArchives(puName, m);
    }
    
    /**
     * Find PersistenceUnitInfo corresponding to the persistence unit name.
     * Returns null if either persistence unit either not found or provider is not supported.
     */
    protected SEPersistenceUnitInfo findPersistenceUnitInfoInArchives(String puName, Map m) {
        SEPersistenceUnitInfo persistenceUnitInfo = null;
        // mkeith - get resource name from prop and include in subsequent call
        String descriptorPath = (String) m.get(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML);
        final Set<Archive> pars;
        if (descriptorPath != null) {
            pars = PersistenceUnitProcessor.findPersistenceArchives(initializationClassloader, descriptorPath);
        } else {
            pars = PersistenceUnitProcessor.findPersistenceArchives(initializationClassloader);
        }
        try {
            for (Archive archive: pars) {
                persistenceUnitInfo = findPersistenceUnitInfoInArchive(puName, archive, m);
                if(persistenceUnitInfo != null) {
                    break;
                }
            }
        } finally {
            for (Archive archive: pars) {
                archive.close();
            }
        }
        return persistenceUnitInfo;
    }
    
    /**
     * Find PersistenceUnitInfo corresponding to the persistence unit name in the archive.
     * Returns null if either persistence unit either not found or provider is not supported.
     */
    protected SEPersistenceUnitInfo findPersistenceUnitInfoInArchive(String puName, Archive archive, Map m){
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
        AbstractSessionLog.getLog().log(SessionLog.FINER, SessionLog.JPA, "cmp_loading_entities_using_loader", classLoader);
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

    public EntityManagerSetupImpl extractInitialEmSetupImpl(String puName) {
        if(this.initialEmSetupImpls != null) {
            return this.initialEmSetupImpls.remove(puName);
        } else {
            return null;
        }
    }
    
    /**
     * This method initializes the container.  Essentially, it will try to load the
     * class that contains the list of entities and reflectively call the method that
     * contains that list.  It will then initialize the container with that list.
     */
    public void initialize(Map m) {
        boolean keepInitialMaps = keepAllPredeployedPersistenceUnits(); 
        if(keepInitialMaps) {
            this.initialPuInfos = new HashMap();
        }
        // always create initialEmSetupImpls - it's used to check for puName uniqueness in initPersistenceUnits
        this.initialEmSetupImpls = new HashMap();
        // ailitchev - copied from findPersistenceUnitInfoInArchives: mkeith - get resource name from prop and include in subsequent call
        String descriptorPath = (String) m.get(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML);
        final Set<Archive> pars;
        if (descriptorPath != null) {
            pars = PersistenceUnitProcessor.findPersistenceArchives(initializationClassloader, descriptorPath);
        } else {
            pars = PersistenceUnitProcessor.findPersistenceArchives(initializationClassloader);
        }
        try {
            for (Archive archive: pars) {
                AbstractSessionLog.getLog().log(SessionLog.FINER, SessionLog.JPA, "cmp_init_initialize", archive);
                initPersistenceUnits(archive, m);
            }
        } finally {
            for (Archive archive: pars) {
                archive.close();
            }
            this.initialEmSetupImpls = null;
        }
    }    

    /**
     * Initialize all persistence units found on initializationClassLoader.
     * Initialization is a two phase process.  First the predeploy process builds the metadata
     * and creates any required transformers.
     * Second the deploy process creates an EclipseLink session based on that metadata.
     */
    protected void initPersistenceUnits(Archive archive, Map m){
        Iterator<SEPersistenceUnitInfo> persistenceUnits = PersistenceUnitProcessor.getPersistenceUnits(archive, initializationClassloader).iterator();
        while (persistenceUnits.hasNext()) {
            SEPersistenceUnitInfo persistenceUnitInfo = persistenceUnits.next();
            if(isPersistenceProviderSupported(persistenceUnitInfo.getPersistenceProviderClassName())) {
                // puName uniquely defines the pu on a class loader
                String puName = persistenceUnitInfo.getPersistenceUnitName();
                
                // don't add puInfo that could not be used standalone (only as composite member).
                if (EntityManagerSetupImpl.mustBeCompositeMember(persistenceUnitInfo)) {
                    continue;
                }
                
                // If puName is already in the map then there are two jars containing persistence units with the same name.
                // Because both are loaded from the same classloader there is no way to distinguish between them - throw exception.
                EntityManagerSetupImpl anotherEmSetupImpl = null;
                if (initialEmSetupImpls != null){
                    anotherEmSetupImpl = this.initialEmSetupImpls.get(puName);
                }
                if(anotherEmSetupImpl != null) {
                    EntityManagerSetupImpl.throwPersistenceUnitNameAlreadyInUseException(puName, persistenceUnitInfo, anotherEmSetupImpl.getPersistenceUnitInfo());
                }
                
                // Note that session name is extracted only from puInfo, the passed properties ignored.
                String sessionName = EntityManagerSetupImpl.getOrBuildSessionName(Collections.emptyMap(), persistenceUnitInfo, puName);
                EntityManagerSetupImpl emSetupImpl = callPredeploy(persistenceUnitInfo, m, puName, sessionName);
                if (initialEmSetupImpls != null){
                    this.initialEmSetupImpls.put(puName, emSetupImpl);
                }
                if (initialPuInfos != null){
                    this.initialPuInfos.put(puName, persistenceUnitInfo);
                }
            }
        }
    }
    
    /**
     * Indicates whether initialPuInfos and initialEmSetupImpls are used.
     */
    protected boolean keepAllPredeployedPersistenceUnits() {
        return false;
    }
    
    public ClassLoader getInitializationClassLoader() {
        return this.initializationClassloader;
    }
}
