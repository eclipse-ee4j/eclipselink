/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.Archive;
import org.eclipse.persistence.jpa.rs.util.InMemoryArchive;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;

/**
 * Factory for the creation of persistence contexts (JPA and JAXB). These
 * contexts are for the persistence of both the meta-model as well as the
 * dynamic persistence contexts for the hosted applications.
 * 
 * @author douglas.clarke, tom.ware
 */
@Singleton
public class PersistenceFactory {    
	
    public PersistenceFactory(){
    }
    
    /**
     * Bootstrap a PersistenceContext based on an pre-existing EntityManagerFactory
     * @param name
     * @param emf
     * @param baseURI
     * @param replace
     * @return
     */
    public PersistenceContext bootstrapPersistenceContext(String name, EntityManagerFactory emf, URI baseURI, boolean replace){
        PersistenceContext persistenceContext = new PersistenceContext(name, (EntityManagerFactoryImpl)emf, baseURI);
        persistenceContext.setBaseURI(baseURI);
        return persistenceContext;
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

    public PersistenceContext get(String persistenceUnit, URI defaultURI, Map<String, Object> initializationProperties) {
        PersistenceContext app = null;
        try{
            DynamicClassLoader dcl = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(PersistenceUnitProperties.CLASSLOADER, dcl);
            if (initializationProperties != null){
                properties.putAll(initializationProperties);
            }
            
            EntityManagerFactory factory =  Persistence.createEntityManagerFactory(persistenceUnit, properties);

            if (factory != null){
                app = bootstrapPersistenceContext(persistenceUnit, factory, defaultURI, true);
            }
        } catch (Exception e){
            e.printStackTrace();
            JPARSLogger.fine("exception_creating_persistence_context", new Object[]{persistenceUnit, e.toString()});
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
                        contextNames.add(info.getPersistenceUnitName());
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return contextNames;
    }
    
}
