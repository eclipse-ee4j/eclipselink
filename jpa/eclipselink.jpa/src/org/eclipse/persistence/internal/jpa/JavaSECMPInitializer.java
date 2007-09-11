/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa;

import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.instrument.*;
import java.security.ProtectionDomain;

import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.Archive;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.jpa.EntityManagerFactoryProvider;
import org.eclipse.persistence.jpa.PersistenceProvider;

import javax.persistence.PersistenceException;
import javax.persistence.spi.ClassTransformer;
import org.eclipse.persistence.jpa.config.PersistenceUnitProperties;

/**
 * INTERNAL:
 *
 * JavaSECMPInitializer is used to bootstrap the deployment of EntityBeans in EJB 3.0
 * when deployed in a non-managed setting
 *
 * It is called internally by our Provider
 *
 * @see org.eclipse.persistence.jpa.EntityManagerFactoryProvider
 */
public class JavaSECMPInitializer implements PersistenceInitializationActivator {

    // Used when byte code enhancing
    public static Instrumentation globalInstrumentation;

    // The internal loader is used by applications that do weaving to pre load classes
    // When this flag is set to false, we will not be able to weave.
    protected boolean shouldCreateInternalLoader = true;

    // The JavaSECMPInitializer - a singleton
    protected static JavaSECMPInitializer javaSECMPInitializer;

    protected ClassLoader sessionClassLoader = null;

    /**
     * Get the singleton entityContainer.
     */
    public static synchronized JavaSECMPInitializer getJavaSECMPInitializer() {
        if (javaSECMPInitializer == null) {
           javaSECMPInitializer = new JavaSECMPInitializer();
        }
        initializeTopLinkLoggingFile();
        return javaSECMPInitializer;
    }

    /**
     * Return whether initialization has occured without actually triggering
     * initialization
     */
    public static boolean isSingletonInitialized(){
        return javaSECMPInitializer != null;
    }

    /**
     * User should not instantiate JavaSECMPInitializer.
     */
    protected JavaSECMPInitializer() {
        super();
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
            EntityManagerSetupImpl emSetupImpl = EntityManagerFactoryProvider.getEntityManagerSetupImpl(persistenceUnitInfo.getPersistenceUnitRootUrl()+persistenceUnitInfo.getPersistenceUnitName());
            // if we already have an EntityManagerSetupImpl this PU has already been processed.  Use the existing one
            if (emSetupImpl != null && !emSetupImpl.isUndeployed()){
                return false;
            }
            Set tempLoaderSet = PersistenceUnitProcessor.buildClassSet(persistenceUnitInfo, Thread.currentThread().getContextClassLoader());
            AbstractSessionLog.getLog().log(SessionLog.FINER, "cmp_init_invoke_predeploy", persistenceUnitInfo.getPersistenceUnitName());
            Map mergedProperties = EntityManagerFactoryProvider.mergeMaps(m, persistenceUnitInfo.getProperties());
            // Bug#4452468  When globalInstrumentation is null, there is no weaving
            String weaving = EntityManagerFactoryProvider.getConfigPropertyAsString(PersistenceUnitProperties.WEAVING, mergedProperties, null);
            if (globalInstrumentation == null) {
                if (weaving == null) {
                   mergedProperties.put(PersistenceUnitProperties.WEAVING, "false");
                   weaving = "false";
                } else if (weaving.equalsIgnoreCase("true")) {
                    throw new PersistenceException(EntityManagerSetupException.wrongWeavingPropertyValue());
                }
            }
            if ((weaving != null) && ((weaving.equalsIgnoreCase("false")) || (weaving.equalsIgnoreCase("static")))){
                shouldCreateInternalLoader=false;
            }

            // Create the temp loader that will not cache classes for entities in our persistence unit
            ClassLoader tempLoader = createTempLoader(tempLoaderSet);
            persistenceUnitInfo.setNewTempClassLoader(tempLoader);
            if (emSetupImpl == null){
                emSetupImpl = new EntityManagerSetupImpl();
                EntityManagerFactoryProvider.addEntityManagerSetupImpl(persistenceUnitInfo.getPersistenceUnitRootUrl()+persistenceUnitInfo.getPersistenceUnitName(), emSetupImpl);
            }
           
            persistenceUnitInfo.setClassLoader(getMainLoader());
    
            // A call to predeploy will partially build the session we will use
            final ClassTransformer transformer = emSetupImpl.predeploy(persistenceUnitInfo, mergedProperties);
    
            // If we got a transformer then register it 
            if ((transformer != null) && (globalInstrumentation != null)) {
                AbstractSessionLog.getLog().log(SessionLog.FINER, "cmp_init_register_transformer", persistenceUnitInfo.getPersistenceUnitName());
                globalInstrumentation.addTransformer(new ClassFileTransformer() {
                    // adapt ClassTransformer to ClassFileTransformer interface
                    public byte[] transform(
                            ClassLoader loader, String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
                        return transformer.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
                    }
                });
            } else if (transformer == null) {
                AbstractSessionLog.getLog().log(SessionLog.FINER, "cmp_init_transformer_is_null");
            } else if (globalInstrumentation == null) {
                AbstractSessionLog.getLog().log(SessionLog.FINER, "cmp_init_globalInstrumentation_is_null");
            }
            return true;
        }
        return false;
    }
    
    /**
     * Create a temporary class loader that can be used to inspect classes and then
     * thrown away.  This allows classes to be introspected prior to loading them
     * with application's main class loader enabling weaving.
     */
    protected ClassLoader createTempLoader(Collection col) {
        return createTempLoader(col, true);
    }

    protected ClassLoader createTempLoader(Collection col, boolean shouldOverrideLoadClassForCollectionMembers) {
        if (!shouldCreateInternalLoader) {
            return Thread.currentThread().getContextClassLoader();
        }

        ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
        if (!(currentLoader instanceof URLClassLoader)) {
            //we can't create a TempEntityLoader so just use the current one
            //shouldn't be a problem (and should only occur) in JavaSE
            return currentLoader;
        }
        URL[] urlPath = ((URLClassLoader)currentLoader).getURLs();
        ClassLoader tempLoader = new TempEntityLoader(urlPath, currentLoader, col, shouldOverrideLoadClassForCollectionMembers);

        AbstractSessionLog.getLog().log(SessionLog.FINER, "cmp_init_tempLoader_created", tempLoader);
        AbstractSessionLog.getLog().log(SessionLog.FINER, "cmp_init_shouldOverrideLoadClassForCollectionMembers", Boolean.valueOf(shouldOverrideLoadClassForCollectionMembers));

        return tempLoader;
    }

    public static ClassLoader getMainLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * Initialize one persistence unit.
     * Initialization is a two phase process.  First the predeploy process builds the metadata
     * and creates any required transformers.
     * Second the deploy process creates a TopLink session based on that metadata.
     */
    protected void initPersistenceUnits(Archive archive, Map m, PersistenceInitializationActivator persistenceActivator){
        Iterator<SEPersistenceUnitInfo> persistenceUnits = PersistenceUnitProcessor.getPersistenceUnits(archive, sessionClassLoader).iterator();
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
        sessionClassLoader = getMainLoader();
        final Set<Archive> pars = PersistenceUnitProcessor.findPersistenceArchives();
        for (Archive archive: pars) {
            AbstractSessionLog.getLog().log(SessionLog.FINER, "cmp_init_initialize", archive);
            initPersistenceUnits(archive, m, persistenceActivator);
        }
    }

    /**
     * INTERNAL:
     * Should be called only by the agent. (when weaving classes)
     * If succeeded return true, false otherwise.
     */
    protected static void initializeFromAgent(Instrumentation instrumentation) throws Exception {
        initializeTopLinkLoggingFile();        
        AbstractSessionLog.getLog().log(SessionLog.FINER, "cmp_init_initialize_from_main", (Object[])null);
        // Squirrel away the instrumentation for later
        globalInstrumentation = instrumentation;
        // Create JavaSECMPInitializer singleton
        javaSECMPInitializer = new JavaSECMPInitializer();
        // Initialize it
        javaSECMPInitializer.initialize(new HashMap(), javaSECMPInitializer);
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
     * Usually JavaSECMPInitializer is initialized from agent during premain
     * to ensure that the classes to be weaved haven't been loaded before initialization.
     * However, in this case initialization can't be debugged.
     * In order to be able to debug initialization specify
     * in java options -javaagent with parameter "main":
     *   -javaagent:c:\dev_main\jlib\tl\toplink-agent.jar=main
     * that causes instrumentation to be cached during premain and postpones initialization until main.
     * With initialization done in main (during the first createEntityManagerFactory call) 
     * there's a danger of the classes to be weaved being already loaded.
     * In that situation initializeFromMain should be called before any classes are loaded.
     * The sure-to-work method would be to create a new runnable class with a main method
     * consisting of just two lines: calling initializeFromMain 
     * followed by reflective call to the main method of the original runnable class.
     * The same could be achieved by calling PersistenceProvider.createEntityManagerFactory method instead 
     * of JavaSECMPInitializer.initializeFromMain method,
     * however initializeFromMain might be more convenient because it
     * doesn't require persistence unit name.
     * The method doesn't do anything if JavaSECMPInitializer has been already initialized.
     * @param m a map containing the set of properties to instantiate with.
     */
    public static void initializeFromMain(Map m) {
        if (javaSECMPInitializer != null) {
            return;
        }        
        getJavaSECMPInitializer().initialize(m, javaSECMPInitializer);
    }

    /**
     * The version of initializeFromMain that passes an empty map.
     */
    public static void initializeFromMain() {
        initializeFromMain(new HashMap());
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
