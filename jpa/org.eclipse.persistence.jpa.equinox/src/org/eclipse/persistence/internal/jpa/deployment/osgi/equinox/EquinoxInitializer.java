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
 *     tware, ssmith = 1.0 - Equinox-specific deployment
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.deployment.osgi.equinox;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;
import org.eclipse.persistence.internal.jpa.deployment.JPAInitializer;
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
    public EquinoxInitializer(ClassLoader bundleClassloader, Map m) {
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
     * Temp ClassLoaders are no longer used to load classes in order to process
     * annotations so the initializationClassLoader associated with the initializer
     * should suffice.
     */
    @SuppressWarnings("unchecked")
    protected ClassLoader createTempLoader(Collection classNames) {
        return this.initializationClassloader;
    }

    /**
     * Temp ClassLoaders are no longer used to load classes in order to process
     * annotations so the initializationClassLoader associated with the initializer
     * should suffice.
     */
    protected ClassLoader createTempLoader(Collection classNames, boolean shouldOverrideLoadClassForCollectionMembers) {
       	return initializationClassloader;
    }
    

    /**
     * Register a transformer.  In this case, we register our transformer with the weaving service
     * provide in our Equinox JPA implementation.  Note: This requires that the eclipselink.equinox.weaving service
     * be installed and available
     * @param transformer
     * @param persistenceUnitInfo
     */
    @SuppressWarnings("unchecked")
	public void registerTransformer(final ClassTransformer transformer, PersistenceUnitInfo persistenceUnitInfo, Map properties){
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
    
}
