/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Guy Pelletier - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.jpa.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.PersistenceUnitLoadingException;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.config.persistenceunit.MetadataSource;
import org.eclipse.persistence.internal.jpa.deployment.JPAInitializer;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.PersistenceProvider;
// TODO: JPA RS
//import org.eclipse.persistence.jpa.rs.PersistenceContextFactory;
//import org.eclipse.persistence.jpa.rs.PersistenceContextFactoryProvider;
//import org.eclipse.persistence.jpa.rs.PersistenceFactoryBase;
import org.eclipse.persistence.sessions.factories.SessionManager;

/**
 * JPA scripting API implementation.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class RuntimeFactory {
    
    public EntityManagerFactory createEntityManagerFactory(PersistenceUnit pu) {
        EntityManagerSetupImpl emSetupImpl = null;
        boolean isNew = false;
        // the name that uniquely defines persistence unit
        String name = pu.getName();
        
        pu.setProperty(PersistenceUnitProperties.METADATA_SOURCE, new MetadataSource(pu));
        SEPersistenceUnitInfo puInfo = (SEPersistenceUnitInfo) pu.getPersistenceUnitInfo();
        
        JPAInitializer initializer = new PersistenceProvider().getInitializer(name, null);
        Map<String, Object> props = new HashMap<String, Object>();

        String uniqueName = initializer.createUniquePersistenceUnitName(puInfo);
        String sessionName = EntityManagerSetupImpl.getOrBuildSessionName(props, puInfo, uniqueName);
        
        try {
            synchronized (EntityManagerFactoryProvider.emSetupImpls) {
                emSetupImpl = EntityManagerFactoryProvider.getEntityManagerSetupImpl(sessionName);

                if (emSetupImpl != null) {
                    if (puInfo.getClassLoader() != emSetupImpl.getPersistenceUnitInfo().getClassLoader()) {
                        emSetupImpl.undeploy();
                        EntityManagerFactoryProvider.getEmSetupImpls().remove(sessionName);
                        
                        if (SessionManager.getManager().getSessions().containsKey(sessionName)) {
                            SessionManager.getManager().destroySession(sessionName);
                        }
                        
                        emSetupImpl = null;
                    }
                }

                if (emSetupImpl == null) {
                    // there may be initial emSetupImpl cached in Initializer - remove it and use.
                    emSetupImpl = initializer.extractInitialEmSetupImpl(name);
                    
                    if (emSetupImpl == null) {
                        // create and predeploy a new emSetupImpl
                        emSetupImpl = initializer.callPredeploy(puInfo, props, uniqueName, sessionName);
                    } else {
                        // change the name
                        emSetupImpl.changeSessionName(sessionName);
                    }
                    
                    // emSetupImpl has been already predeployed, predeploy will just increment factoryCount.
                    emSetupImpl.predeploy(emSetupImpl.getPersistenceUnitInfo(), props);
                    EntityManagerFactoryProvider.addEntityManagerSetupImpl(sessionName, emSetupImpl);
                    isNew = true;
                }
            }

        } catch (Exception e) {
            throw PersistenceUnitLoadingException.exceptionSearchingForPersistenceResources(initializer.getInitializationClassLoader(), e);
        }

        if (!isNew) {
            if (!uniqueName.equals(emSetupImpl.getPersistenceUnitUniqueName())) {
                throw PersistenceUnitLoadingException.sessionNameAlreadyInUse(sessionName, uniqueName, emSetupImpl.getPersistenceUnitUniqueName());
            }

            // synchronized to prevent undeploying by other threads.
            boolean undeployed = false;
            synchronized (emSetupImpl) {
                if (emSetupImpl.isUndeployed()) {
                    undeployed = true;
                }

                // emSetupImpl has been already predeployed, predeploy will just increment factoryCount.
                emSetupImpl.predeploy(emSetupImpl.getPersistenceUnitInfo(), props);
            }
            if (undeployed) {
                // after the emSetupImpl has been obtained from emSetupImpls
                // it has been undeployed by factory.close() in another thread -
                // start all over again.
                return createEntityManagerFactory(pu);
            }
        }

        EntityManagerFactoryImpl factory = null;
        
        try {
            factory = new EntityManagerFactoryImpl(emSetupImpl, props);

            // This code has been added to allow validation to occur without
            // actually calling createEntityManager
            if (emSetupImpl.shouldGetSessionOnCreateFactory(props)) {
                factory.getDatabaseSession();
            }
        } catch (RuntimeException ex) {
            if (factory != null) {
                factory.close();
            } else {
                emSetupImpl.undeploy();
            }
            
            throw ex;
        }

        return factory;

    }
    
    public static RuntimeFactory getInstance() {
        return new RuntimeFactory();
    }

}
