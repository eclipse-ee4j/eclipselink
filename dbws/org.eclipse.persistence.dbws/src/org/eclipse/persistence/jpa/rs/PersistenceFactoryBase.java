/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * 		dclarke/tware - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.Archive;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSConfigurationException;
import org.eclipse.persistence.jpa.rs.logging.LoggingLocalization;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;

/**
 * Manages the PersistenceContexts that are used by a JPA-RS deployment.  Provides a single point to bootstrap
 * and look up PersistenceContexts 
 * @author tware
 *
 */
public class PersistenceFactoryBase implements PersistenceContextFactory {
    
    protected Map<String, PersistenceContext> dynamicPersistenceContexts = new HashMap<String, PersistenceContext>();

    
    /**
     * Bootstrap a PersistenceContext based on an pre-existing EntityManagerFactory
     * @param name
     * @param emf
     * @param baseURI
     * @param replace
     * @return
     */
    public PersistenceContext bootstrapPersistenceContext(String name, EntityManagerFactory emf, URI baseURI, String version, boolean replace){
        PersistenceContext persistenceContext = new PersistenceContext(name, (EntityManagerFactoryImpl)emf, baseURI);
        persistenceContext.setBaseURI(baseURI);
        persistenceContext.setVersion(version);
        return persistenceContext;
    }

    /**
     * Stop the factory.  Remove all the PersistenceContexts.
     */
    public void close(){
        for (String key: dynamicPersistenceContexts.keySet()){
            dynamicPersistenceContexts.get(key).stop();
        }
        synchronized (this) {
            dynamicPersistenceContexts.clear();
        }
    }
    
    /**
     * Close the PersistenceContext of a given name and clean it out of our list of PersistenceContexts
     * @param name
     */
    public void closePersistenceContext(String name){
        synchronized (this) {
            PersistenceContext context = dynamicPersistenceContexts.get(name);
            if (context != null){
                context.stop();
                dynamicPersistenceContexts.remove(name);
            }
        }
    }
    
    /**
     * Provide an initial set of properties for bootstrapping PersistenceContexts.
     * @param dcl
     * @param originalProperties
     * @return
     */
    protected static Map<String, Object> createProperties(DynamicClassLoader dcl, Map<String, ?> originalProperties) {
        Map<String, Object> properties = new HashMap<String, Object>();

        properties.put(PersistenceUnitProperties.CLASSLOADER, dcl);
        properties.put(PersistenceUnitProperties.WEAVING, "static");

        // For now we'll copy the connection info from admin PU
        for (Map.Entry<String, ?> entry : originalProperties.entrySet()) {
            if (entry.getKey().startsWith("javax") || entry.getKey().startsWith("eclipselink.log") || entry.getKey().startsWith("eclipselink.target-server")) {
                properties.put(entry.getKey(), entry.getValue());
            }
        }
        return properties;
    }

    public PersistenceContext get(String persistenceUnit, URI defaultURI, String version, Map<String, Object> initializationProperties) {
        PersistenceContext app = getDynamicPersistenceContext(persistenceUnit);
        if (app == null){
            try{
                DynamicClassLoader dcl = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
                Map<String, Object> properties = new HashMap<String, Object>();
                properties.put(PersistenceUnitProperties.CLASSLOADER, dcl);
                if (initializationProperties != null){
                    properties.putAll(initializationProperties);
                }
                
                EntityManagerFactoryImpl factory = (EntityManagerFactoryImpl) Persistence.createEntityManagerFactory(persistenceUnit, properties);
                ClassLoader sessionLoader = factory.getServerSession().getLoader();
                if (!DynamicClassLoader.class.isAssignableFrom(sessionLoader.getClass()) ) {
                    properties = new HashMap<String, Object>();
                    dcl = new DynamicClassLoader(sessionLoader);
                    properties.put(PersistenceUnitProperties.CLASSLOADER, dcl);
                    if (initializationProperties != null){
                        properties.putAll(initializationProperties);
                    }
                    factory.refreshMetadata(properties);
                }
    
                if (factory != null){
                    app = bootstrapPersistenceContext(persistenceUnit, factory, defaultURI, version, true);
                    if (app != null){
                        synchronized (this) {
                            dynamicPersistenceContexts.put(persistenceUnit, app);
                        }
                    }
                }
            } catch (Exception e){
                JPARSLogger.exception("exception_creating_persistence_context", new Object[]{persistenceUnit, e.toString()}, e);
            }
        }
        
        if ((app != null) && (!app.isWeavingEnabled())) {
            throw new JPARSConfigurationException(LoggingLocalization.buildMessage("weaving_required_for_relationships", new Object[] { persistenceUnit }));
        }
        
        return app;
    }

    
    public Set<String> getPersistenceContextNames(){
        Set<String> contextNames = new HashSet<String>();
        try{
            Set<Archive> archives = PersistenceUnitProcessor.findPersistenceArchives();
            for (Archive archive : archives){
                List<SEPersistenceUnitInfo> infos = PersistenceUnitProcessor.processPersistenceArchive(archive, Thread.currentThread().getContextClassLoader());
                for (SEPersistenceUnitInfo info: infos){
                    if (!info.getPersistenceUnitName().equals("jpa-rs")){
                        if (EntityManagerSetupImpl.mustBeCompositeMember(info)) {
                            continue;
                        }
                        contextNames.add(info.getPersistenceUnitName());
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        contextNames.addAll(dynamicPersistenceContexts.keySet());
        return contextNames;
    }
    
    public PersistenceContext getDynamicPersistenceContext(String name){
        synchronized (this) {
            return dynamicPersistenceContexts.get(name);
        }
    }
}
