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
 *     tware - 1.0RC1 - refactor for OSGi
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.deployment;

import java.util.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;
import java.lang.instrument.*;
import java.security.ProtectionDomain;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.jpa.Archive;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.logging.SessionLog;

import javax.persistence.PersistenceException;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;

/**
 * INTERNAL:
 *
 * JavaSECMPInitializer is used to bootstrap the deployment of EntityBeans in EJB 3.0
 * when deployed in a non-managed setting
 *
 * It is called internally by our Provider
 *
 * @see org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider
 */
public class JavaSECMPInitializer extends JPAInitializer {

    // Used when byte code enhancing
    public static Instrumentation globalInstrumentation;
    // Adding this flag because globalInstrumentation could be set to null after weaving is done.
    protected static boolean usesAgent;
    // Indicates whether has been initialized - that could be done only once.
    protected static boolean isInitialized;
    // Indicates whether puInfos of the initial emSetupImpls (EntityManagerFactoryProvider.initialEmSetupImpls)
    // should be cached forever in EntityManagerFactoryProvider.initialPuInfos.
    // The flag is only relevant in usesAgent case - puInfos never kept otherwise.
    // In useAgent case both cases settings could work: 
    //   true is obviously more efficient (puInfo is read only once);
    //   false will work the same way as in non agent case (app. server case) - good for testing.
    public static boolean keepInitialPuInfos = true;

    /**
     * Get the singleton entityContainer.
     */
    public static synchronized JavaSECMPInitializer getJavaSECMPInitializer() {
        if(!isInitialized) {
            initializeTopLinkLoggingFile();  
            JavaSECMPInitializer initializer = new JavaSECMPInitializer();
            initializer.initialize(new HashMap());
            return initializer;
        }  else {
            return new JavaSECMPInitializer();
        }
    }

    public static synchronized JavaSECMPInitializer getJavaSECMPInitializer(ClassLoader loader) {
        if(!isInitialized) {
            initializeTopLinkLoggingFile();  
            JavaSECMPInitializer initializer = new JavaSECMPInitializer(loader);
            initializer.initialize(new HashMap());
            return initializer;
        }  else {
            return new JavaSECMPInitializer(loader);
        }
    }

    /**
     * User should not instantiate JavaSECMPInitializer.
     */
    protected JavaSECMPInitializer() {
        super();
    }

    protected JavaSECMPInitializer(ClassLoader loader) {
        super();
        this.initializationClassloader = loader;
    }

    /**
     * Check whether weaving is possible and update the properties and variable as appropriate
     * @param properties The list of properties to check for weaving and update if weaving is not needed
     */
    public void checkWeaving(Map properties){
        String weaving = EntityManagerFactoryProvider.getConfigPropertyAsString(PersistenceUnitProperties.WEAVING, properties, null);
        // Check usesAgent instead of globalInstrumentation!=null because globalInstrumentation is set to null after initialization,
        // but we still have to keep weaving so that the resulting projects correspond to the woven (during initialization) classes.
        if (!usesAgent) {
            if (weaving == null) {
               properties.put(PersistenceUnitProperties.WEAVING, "false");
               weaving = "false";
            } else if (weaving.equalsIgnoreCase("true")) {
                throw new PersistenceException(EntityManagerSetupException.wrongWeavingPropertyValue());
            }
        }
        if ((weaving != null) && ((weaving.equalsIgnoreCase("false")) || (weaving.equalsIgnoreCase("static")))){
            shouldCreateInternalLoader = false;
        }
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
    
    /**
     * INTERNAL:
     * Should be called only by the agent. (when weaving classes)
     * If succeeded return true, false otherwise.
     */
    protected static void initializeFromAgent(Instrumentation instrumentation) throws Exception {
        initializeTopLinkLoggingFile();        
        AbstractSessionLog.getLog().log(SessionLog.FINER, "cmp_init_initialize_from_agent", (Object[])null);
        // Squirrel away the instrumentation for later
        globalInstrumentation = instrumentation;
        (new JavaSECMPInitializer()).initialize(new HashMap());
    }
    
    /**
     * Usually JavaSECMPInitializer is initialized from agent during premain
     * to ensure that the classes to be weaved haven't been loaded before initialization.
     * However, in this case initialization can't be debugged.
     * In order to be able to debug initialization specify
     * in java options -javaagent with parameter "main":  (note: a separate eclipselink-agent.jar is no longer required)
     *   -javaagent:c:\trunk\eclipselink.jar=main
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
     * doesn't require a persistence unit name.
     * The method doesn't do anything if JavaSECMPInitializer has been already initialized.
     * @param m - a map containing the set of properties to instantiate with.
     */
    public static void initializeFromMain(Map m) {
        initializeTopLinkLoggingFile();        
        (new JavaSECMPInitializer()).initialize(m);
    }

    /**
     * The version of initializeFromMain that passes an empty map.
     */
    public static void initializeFromMain() {
        initializeFromMain(new HashMap());
    }

    /**
     * This method initializes the container.  Essentially, it will try to load the
     * class that contains the list of entities and reflectively call the method that
     * contains that list.  It will then initialize the container with that list.
     * If succeeded return true, false otherwise.
     */
    public void initialize(Map m) {
        if(!isInitialized) {
            if(globalInstrumentation != null) {
                usesAgent = true;
                if(this.initializationClassloader == null) {
                    this.initializationClassloader = Thread.currentThread().getContextClassLoader();
                }
                EntityManagerFactoryProvider.initialEmSetupImpls = new HashMap<String, EntityManagerSetupImpl>();
                if(keepInitialPuInfos) {
                    EntityManagerFactoryProvider.initialPuInfos = new HashMap<String, SEPersistenceUnitInfo>();
                }
                final Set<Archive> pars = PersistenceUnitProcessor.findPersistenceArchives(initializationClassloader);
                try {
                    PersistenceInitializationHelper initializationHelper = new PersistenceInitializationHelper();
                    for (Archive archive: pars) {
                        AbstractSessionLog.getLog().log(SessionLog.FINER, "cmp_init_initialize", archive);
                        initPersistenceUnits(archive, m, initializationHelper);
                    }
                } finally {
                    for (Archive archive: pars) {
                        archive.close();
                    }
                }
                // all the transformers have been added to instrumentation, don't need it any more.
                globalInstrumentation = null;
            }
            isInitialized = true;
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
            // This code only called when usesAgent - no need for uniqueName, puName uniquely defines the pu.
            String puName = persistenceUnitInfo.getPersistenceUnitName();
            
            // If puName is already in the map then there are two jars containing persistence units with the same name.
            // Because both are loaded from the same classloader there is no way to distinguish between them - throw exception.
            EntityManagerSetupImpl anotherEmSetupImpl = EntityManagerFactoryProvider.initialEmSetupImpls.get(puName);
            if(anotherEmSetupImpl != null) {
                String puUrl;
                String anotherPuUrl;
                try {
                    puUrl = URLDecoder.decode(persistenceUnitInfo.getPersistenceUnitRootUrl().toString(), "UTF8");
                    anotherPuUrl = URLDecoder.decode(anotherEmSetupImpl.getPersistenceUnitInfo().getPersistenceUnitRootUrl().toString(), "UTF8");
                } catch (UnsupportedEncodingException e) {
                    puUrl = persistenceUnitInfo.getPersistenceUnitRootUrl().toString();
                    anotherPuUrl = anotherEmSetupImpl.getPersistenceUnitInfo().getPersistenceUnitRootUrl().toString();
                }
                throw PersistenceUnitLoadingException.persistenceUnitNameAlreadyInUse(puName, puUrl, anotherPuUrl);
            }
            
            // Note that session name is extracted only from puInfo, the passed properties ignored.
            String sessionName = EntityManagerSetupImpl.getOrBuildSessionName(Collections.emptyMap(), persistenceUnitInfo, puName);
            EntityManagerSetupImpl emSetupImpl = callPredeploy(persistenceUnitInfo, m, persistenceActivator, puName, sessionName);
            EntityManagerFactoryProvider.initialEmSetupImpls.put(puName, emSetupImpl);
            if(keepInitialPuInfos) {
                EntityManagerFactoryProvider.initialPuInfos.put(puName, persistenceUnitInfo);
            }
        }
    }

    /**
     * Register a transformer.  In this case, we use the instrumentation to add a transformer for the
     * JavaSE environment
     * @param transformer
     * @param persistenceUnitInfo
     */
    public void registerTransformer(final ClassTransformer transformer, PersistenceUnitInfo persistenceUnitInfo, Map properties){
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
    }
    
    /**
     * Indicates whether puName uniquely defines the persistence unit.
     * usesAgent means that it is a stand alone SE case.
     * Otherwise it could be an application server case where different persistence units
     * may have the same name: that could happen if they are loaded by different classloaders;
     * the common case is the same persistence unit jar deployed in several applications.
     */
    public boolean isPersistenceUnitUniquelyDefinedByName() {
        return usesAgent;
    }
    
    /**
     * Indicates whether initialization has already occurred.
     */
    public static boolean isInitialized() {
        return isInitialized;
    }
    
    /**
     * Indicates whether Java agent and globalInstrumentation was used.
     */
    public static boolean usesAgent() {
        return usesAgent;
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
