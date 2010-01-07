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
 *     tware, ssmith = 1.0 - Equinox-specific deployment
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.deployment.osgi.equinox;

import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;

import org.eclipse.osgi.baseadaptor.BaseData;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleFile;
import org.eclipse.osgi.baseadaptor.bundlefile.ZipBundleFile;
import org.eclipse.osgi.baseadaptor.loader.ClasspathEntry;
import org.eclipse.osgi.baseadaptor.loader.ClasspathManager;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegate;
import org.eclipse.osgi.internal.baseadaptor.DefaultClassLoader;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;
import org.eclipse.persistence.internal.jpa.deployment.JPAInitializer;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceInitializationHelper;
import org.eclipse.persistence.internal.jpa.deployment.osgi.CompositeClassLoader;
import org.eclipse.persistence.jpa.equinox.weaving.IWeaver;
import org.eclipse.persistence.jpa.osgi.Activator;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;


/**
 * Specialized JPA bootstrap class for Equinox Environments.  Makes use of hooks provided in the
 * eclipselink.equinox.weaving fragment to allow dynamic weaving when JPA is used in Equinox
 * @author tware
 *
 */
@SuppressWarnings("restriction")
public class EquinoxInitializer extends JPAInitializer {
   
    /**
     * @param bundleClassloader for the bundle containing a persistence.xml
     * @param m a map containing the set of properties to instantiate with.
     */
    @SuppressWarnings("unchecked")
    public EquinoxInitializer(ClassLoader bundleClassloader, Map m, PersistenceInitializationHelper helper) {
        this.initializationClassloader = bundleClassloader;
    }

    /**
     * Check whether weaving is possible and update the properties and variable as appropriate
     * @param properties The list of properties to check for weaving and update if weaving is not needed
     */
    @SuppressWarnings("unchecked")
    public void checkWeaving(Map properties){
        String weaving = EntityManagerFactoryProvider.getConfigPropertyAsString(PersistenceUnitProperties.WEAVING, properties, null);
        if (weaving == null) {
            weaving = "false";
            properties.put(PersistenceUnitProperties.WEAVING, weaving);
        }
        if ((weaving != null) && ((weaving.equalsIgnoreCase("false")) || (weaving.equalsIgnoreCase("static")))){
            shouldCreateInternalLoader=false;
        }
    }
    
    /**
     * Create a temporary class loader that can be used to inspect classes and then
     * thrown away.  This allows classes to be introspected prior to loading them
     * with application's main class loader enabling weaving.
     */
    @SuppressWarnings("unchecked")
    protected ClassLoader createTempLoader(Collection classNames) {
        return createTempLoader(classNames, true);
    }

    /**
     * Here we build the temporary classloader that our JPA implementation requires for weaving.
     * 
     * We inspect the current class loader and attempt to build our temporary classloader from its
     * classpath.
     * 
     * Note: This classloader will attempt to load Entities, MappedSuperclasses and Embeddables
     * without allowing them to be loaded by other class loaders.
     */
    @SuppressWarnings({ "unchecked" })
    protected ClassLoader createTempLoader(Collection classNames, boolean shouldOverrideLoadClassForCollectionMembers) {
        if (!shouldCreateInternalLoader) {
            return initializationClassloader;
        }
       if (initializationClassloader instanceof CompositeClassLoader) {
            // The first classloader in the composite with be the one passed
            // as a property if it was supplied.  A CompositeClassLoader's
            // list of loaders will never be empty.
            CompositeClassLoader compositeLoader = (CompositeClassLoader) initializationClassloader;
            ClassLoader firstLoader = compositeLoader.getClassLoaders().get(0);
            if (firstLoader instanceof DefaultClassLoader) {
                DefaultClassLoader baseClassLoader = (DefaultClassLoader) firstLoader;
                ClassLoaderDelegate delegate = baseClassLoader.getDelegate();
                ProtectionDomain domain = baseClassLoader.getDomain();
                ClasspathManager classpathManager = baseClassLoader.getClasspathManager();
                ClasspathEntry[] entries = classpathManager.getHostClasspathEntries();
                String[] classpath = new String[entries.length];
                for (int i = 0; i < entries.length; i++) {
                    BundleFile bundleFile = entries[i].getBundleFile();
                    if (bundleFile instanceof ZipBundleFile) {
                        classpath[i] = ".";  // the classpath entry is the root of the jar/zip
                    } else {
                        classpath[i] = bundleFile.getBaseFile().getAbsolutePath();
                    }
                }
                BaseData bundledata = classpathManager.getBaseData();
                
                DefaultClassLoader tempLoader = new TempEquinoxEntityLoader(baseClassLoader, delegate, domain, bundledata, classpath, classNames, true);
                 
                AbstractSessionLog.getLog().log(SessionLog.FINER, "cmp_init_tempLoader_created", tempLoader);
                AbstractSessionLog.getLog().log(SessionLog.FINER, "cmp_init_shouldOverrideLoadClassForCollectionMembers", Boolean.valueOf(shouldOverrideLoadClassForCollectionMembers));

                return tempLoader;
        	} else {
        		return compositeLoader;
        	} 
        } else {
        	return initializationClassloader;
        }
    }
    

    /**
     * Register a transformer.  In this case, we register our transformer with the weaving service
     * provide in our Equinox JPA implementation.  Note: This requires that the eclipselink.equinox.weaving service
     * be installed and available
     * @param transformer
     * @param persistenceUnitInfo
     */
    @SuppressWarnings("unchecked")
	public void registerTransformer(final ClassTransformer transformer, PersistenceUnitInfo persistenceUnitInfo){
        // If we got a transformer then register it 
        if (transformer != null) {
            AbstractSessionLog.getLog().log(SessionLog.FINER, "cmp_init_register_transformer", persistenceUnitInfo.getPersistenceUnitName());
            IWeaver weavingService = new EquinoxWeaver(transformer);
            Activator.getContext().registerService(IWeaver.class.getName(), weavingService, new Hashtable());
            AbstractSessionLog.getLog().log(SessionLog.FINER, "Registering Weaving Service");
        } else if (transformer == null) {
            AbstractSessionLog.getLog().log(SessionLog.FINER, "cmp_init_transformer_is_null");
        }
    }

    /**
     * Temporary classloader that can override loading of classes in a persistence unit
     */
    @SuppressWarnings("unchecked")
	private class TempEquinoxEntityLoader extends DefaultClassLoader {
		Collection classNames;
        boolean shouldOverrideLoadClassForCollectionMembers = true;

        public TempEquinoxEntityLoader(ClassLoader parent, ClassLoaderDelegate delegate, ProtectionDomain domain, BaseData bundledata, String[] classpath){
            super(parent,delegate,domain,bundledata,classpath);
            initialize();
        }

        public TempEquinoxEntityLoader(ClassLoader parent, ClassLoaderDelegate delegate, ProtectionDomain domain, BaseData bundledata, String[] classpath, Collection classNames, boolean shouldOverrideLoadClassForCollectionMembers) {
            this(parent,delegate,domain,bundledata,classpath);
            this.classNames = classNames;
            this.shouldOverrideLoadClassForCollectionMembers = shouldOverrideLoadClassForCollectionMembers;
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
                    c = findLocalClass(name); // to look local and avoid delegate
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
