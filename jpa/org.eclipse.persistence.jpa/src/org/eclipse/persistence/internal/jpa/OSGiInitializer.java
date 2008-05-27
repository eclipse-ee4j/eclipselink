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
 *     tware, ssmith - bootstrap the deployment of Entities
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.deployment.Archive;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.config.PersistenceUnitProperties;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

/**
 * INTERNAL:
 *
 * OSGiInitializer is used to bootstrap the deployment of Entities
 * when deployed in a non-managed OSGi environment
 *
 * It is called internally by our Provider
 *
 * @see org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider
 */
public class OSGiInitializer implements PersistenceInitializationActivator {
	private ClassLoader classLoader;
	
    // The internal loader is used by applications that do weaving to pre load classes
    // When this flag is set to false, we will not be able to weave.
    protected boolean shouldCreateInternalLoader = true;

    public OSGiInitializer(ClassLoader classLoader) {
    	this.classLoader = classLoader;
    }

   /**
    * predeploy (with deploy) is one of the two steps required in deployment of entities
    * This method will prepare to call predeploy, call it and finally register the
    * transformer returned to be used for weaving.
    */
   protected boolean callPredeploy(SEPersistenceUnitInfo persistenceUnitInfo, Map m, PersistenceInitializationActivator persistenceActivator) {
       // we will only attempt to deploy when TopLink is specified as the provider or the provider is unspecified
       String providerClassName = persistenceUnitInfo.getPersistenceProviderClassName();
       if (persistenceActivator.isPersistenceProviderSupported(providerClassName)){
           String puName = PersistenceUnitProcessor.buildPersistenceUnitName(persistenceUnitInfo.getPersistenceUnitRootUrl(),persistenceUnitInfo.getPersistenceUnitName());
           EntityManagerSetupImpl emSetupImpl = EntityManagerFactoryProvider.getEntityManagerSetupImpl(puName);
           // if we already have an EntityManagerSetupImpl this PU has already been processed.  Use the existing one
           if (emSetupImpl != null && !emSetupImpl.isUndeployed()){
               return false;
           }
           AbstractSessionLog.getLog().log(SessionLog.FINER, "cmp_init_invoke_predeploy", persistenceUnitInfo.getPersistenceUnitName());
           Map mergedProperties = EntityManagerFactoryProvider.mergeMaps(m, persistenceUnitInfo.getProperties());
           //TODO: weaving forced off in OSGi 
           String weaving = "false";
           mergedProperties.put(PersistenceUnitProperties.WEAVING, weaving);
           shouldCreateInternalLoader=false;

           persistenceUnitInfo.setNewTempClassLoader(classLoader);
            if (emSetupImpl == null){
               emSetupImpl = new EntityManagerSetupImpl();
               EntityManagerFactoryProvider.addEntityManagerSetupImpl(persistenceUnitInfo.getPersistenceUnitRootUrl()+persistenceUnitInfo.getPersistenceUnitName(), emSetupImpl);
           }         
           persistenceUnitInfo.setClassLoader(classLoader);
   
           // A call to predeploy will partially build the session we will use
           emSetupImpl.predeploy(persistenceUnitInfo, mergedProperties);
           return true;
       }
       return false;
   }

    /**
     * Initialize one persistence unit.
     * Initialization is a two phase process.  First the predeploy process builds the metadata
     * and creates any required transformers.
     * Second the deploy process creates a TopLink session based on that metadata.
     */
    protected void initPersistenceUnits(Archive archive, Map m, PersistenceInitializationActivator persistenceActivator){
		Iterator<SEPersistenceUnitInfo> persistenceUnits = PersistenceUnitProcessor.getPersistenceUnits(archive, classLoader).iterator();
        while (persistenceUnits.hasNext()) {
            SEPersistenceUnitInfo persistenceUnitInfo = persistenceUnits.next();
            callPredeploy(persistenceUnitInfo, m, persistenceActivator);
        }
    }

    /**
     * This method initializes the container.  Essentially, it will try to load the
     * class that contains the list of entities and reflectively call the method that
     * contains that list.  It will then initialize the container with that list.
     * If succeeded return true, false otherwise.
     */
    public void initialize(Map m, PersistenceInitializationActivator persistenceActivator) {
        final Set<Archive> pars = PersistenceUnitProcessor.findPersistenceArchives(classLoader);
        for (Archive archive: pars) {
            AbstractSessionLog.getLog().log(SessionLog.FINER, "cmp_init_initialize", archive);
            initPersistenceUnits(archive, m, persistenceActivator);
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

    /*********************************/
    /***** Temporary Classloader *****/
    /*********************************/
    /**
     * This class loader is provided at initialization time to allow us to temporarily load
     * domain classes so we can examine them for annotations.  After they are loaded we will throw this
     * class loader away.  Transformers can then be registered on the real class loader to allow
     * weaving to occur.
     * 
     * It selectively loads classes based on the list of classnames it is instantiated with.  Classes
     * not on that list are allowed to be loaded by the parent.
     */
    public class TempEntityLoader extends URLClassLoader {
        Collection classNames;
        boolean shouldOverrideLoadClassForCollectionMembers;
        
        //added to resolved gf #589 - without this, the orm.xml url would be returned twice 
        public Enumeration<URL> getResources(String name) throws java.io.IOException {
            return this.getParent().getResources(name);
        }

        public TempEntityLoader(URL[] urls, ClassLoader parent, Collection classNames, boolean shouldOverrideLoadClassForCollectionMembers) {
            super(urls, parent);
            this.classNames = classNames;
            this.shouldOverrideLoadClassForCollectionMembers = shouldOverrideLoadClassForCollectionMembers;
        }

        public TempEntityLoader(URL[] urls, ClassLoader parent, Collection classNames) {
            this(urls, parent, classNames, true);
        }

        // Indicates if the classLoad should be overridden for the passed className.
        // Returns true in case the class should NOT be loaded by parent classLoader.
        protected boolean shouldOverrideLoadClass(String name) {
            if (shouldOverrideLoadClassForCollectionMembers) {
                // Override classLoad if the name is in collection
                return (classNames != null) && classNames.contains(name);
            } else {
                // Directly opposite: Override classLoad if the name is NOT in collection.
                // Forced to check for java. and javax. packages here, because even if the class
                // has been loaded by parent loader we would load it again
                // (see comment in loadClass)
                return !name.startsWith("java.") && !name.startsWith("javax.") && ((classNames == null) || !classNames.contains(name));
            }
        }

        protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (shouldOverrideLoadClass(name)) {
                // First, check if the class has already been loaded.
                // Note that the check only for classes loaded by this loader,
                // it doesn't return true if the class has been loaded by parent loader
                // (forced to live with that because findLoadedClass method defined as final protected:
                //  neither can override it nor call it on the parent loader)
                Class c = findLoadedClass(name);
                if (c == null) {
                    c = findClass(name);
                }
                if (resolve) {
                    resolveClass(c);
                }
                return c;
            } else {
                return super.loadClass(name, resolve);
            }
        }
    }

}
