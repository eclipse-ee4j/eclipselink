/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle. All rights reserved.
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

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.Archive;
import org.eclipse.persistence.jpa.rs.metadata.Application;
import org.eclipse.persistence.jpa.rs.metadata.MetadataStore;
import org.eclipse.persistence.jpa.rs.util.InMemoryArchive;

/**
 * Factory for the creation of persistence contexts (JPA and JAXB). These
 * contexts are for the persistence of both the meta-model as well as the
 * dynamic persistence contexts for the hosted applications.
 * 
 * @author douglas.clarke, tom.ware
 */
@Singleton
public class PersistenceFactory {    
    
    /** Holds all the PersistenceContexts available to this factory by name **/
	private Map<String, PersistenceContext> persistenceContexts = new HashMap<String, PersistenceContext>();
    
	/** used to keep information about bootstrapped PersistenceContexts in a persistent store **/
    private MetadataStore metadataStore;
	
    public PersistenceFactory(){
    }
    
    /**
     * Create a PersistenceContext based on a persistence xml provided in full text as a String
     * @param name
     * @param persistenceXML
     * @param originalProperties
     * @param replace
     * @return
     */
    public PersistenceContext bootstrapPersistenceContext(String name, String persistenceXML, Map<String, ?> originalProperties, boolean replace){
        ByteArrayInputStream stream = new ByteArrayInputStream(persistenceXML.getBytes());
        InMemoryArchive archive = new InMemoryArchive(stream);
        return bootstrapPersistenceContext(name, archive, null, originalProperties, replace);
    }
    
    /**
     * Create a PersistenceContext based on a persistence xml provided through a URL
     * @param name
     * @param persistenceXMLURL
     * @param originalProperties
     * @param replace
     * @return
     * @throws IOException
     */
    public PersistenceContext bootstrapPersistenceContext(String name, URL persistenceXMLURL, Map<String, ?> originalProperties, boolean replace) throws IOException{
        InMemoryArchive archive = new InMemoryArchive(persistenceXMLURL.openStream());
        return bootstrapPersistenceContext(name, archive, persistenceXMLURL.toString(), originalProperties, replace);
    }
    
    /**
     * Create a persistence Context based on a persistence xml provided as a Stream
     * @param name
     * @param persistenceXMLStream
     * @param originalProperties
     * @param replace
     * @return
     */
    public PersistenceContext bootstrapPersistenceContext(String name, InputStream persistenceXMLStream, Map<String, ?> originalProperties, boolean replace){
        InMemoryArchive archive = new InMemoryArchive(persistenceXMLStream);
        return bootstrapPersistenceContext(name, archive, null, originalProperties, replace);
    }
 
    /**
     * This is the main PersistenceContext bootstrapping method.  It takes an Archive as its source of a persistence.xml
     * file and bootstraps a persistence context based on that archive.
     * 
     * If the persistence.xml location is provided, that location will be stored in the metadata source so that it
     * can be retrieved at bootstrap time.
     * 
     * @param name
     * @param archive
     * @param persistenceXMLLocation
     * @param originalProperties
     * @param replace
     * @return
     */
    public PersistenceContext bootstrapPersistenceContext(String name, Archive archive, String persistenceXMLLocation, Map<String, ?> originalProperties, boolean replace){
        initialize();
        PersistenceContext persistenceContext = getPersistenceContext(name);
        if (persistenceContext == null || replace){
            DynamicClassLoader dcl = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
            Map<String, Object> properties = createProperties(dcl, originalProperties);
            properties.putAll(originalProperties);
            persistenceContext = new PersistenceContext(archive, properties, dcl);
    
            persistenceContexts.put(name, persistenceContext);
            // TODO store persistence units that were composed from a stream or a String rather than just a URL
            if (persistenceXMLLocation != null){
                storeApplicationMetadata(name, persistenceXMLLocation);
            }
        } else {
            System.out.println("PersistenceContext " + name + " already started.  Using existing.");
        }
        return persistenceContext;
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
        initialize();
        PersistenceContext persistenceContext = null;
        if (!replace){
            persistenceContext = getPersistenceContext(name);
        }
        if (persistenceContext == null){
            persistenceContext = new PersistenceContext(name, (EntityManagerFactoryImpl)emf, baseURI);
            persistenceContexts.put(name, persistenceContext);
        }
        persistenceContext.setBaseURI(baseURI);
        return persistenceContext;
    }
 
    /**
     * Close the PersistenceContext of a given name and clean it out of our list of PersistenceContexts
     * @param name
     */
    public void closePersistenceContext(String name){
        PersistenceContext context = persistenceContexts.get(name);
        if (context != null){
            context.getEmf().close();
            persistenceContexts.remove(name);
        }
    }
    
    /**
     * Stop the factory.  Remove all the PersistenceContexts.
     */
    public void close(){
        for (String key: persistenceContexts.keySet()){
            persistenceContexts.get(key).stop();
        }
        if (metadataStore != null){
            metadataStore.close();
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
    
    public synchronized PersistenceContext getPersistenceContext(String name){
        initialize();
        return persistenceContexts.get(name);
    }
    
    public Set<String> getPersistenceContextNames(){
        Set<String> contextNames = new HashSet<String>();
        contextNames.addAll(persistenceContexts.keySet());
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

    public MetadataStore getMetadataStore() {
        return metadataStore;
    }
    
    /**
     * Initialize the factory by checking any existing metadata store for persistence units.
     */
    public void initialize(){
        initialize(new HashMap<String, Object>());
    }
    
    /**
     * Initialize the factory by checking any existing metadata store for persistence units.
     * @param properties
     */
    public void initialize(Map<String, Object> properties){
        if (metadataStore != null && !metadataStore.isInitialized()){
            List<Application> applications = metadataStore.retreiveMetadata();
            for (Application application: applications){
                try{
                    URL persistenceXMLURL = new URL( application.getPersistenceXMLURL());
                    bootstrapPersistenceContext(application.getName(), persistenceXMLURL, properties, false);
                } catch (Exception e){
                    // TODO proper logging
                    System.out.println("Exception bootstrapping PersistenceFactory for application "  + application.getName());
                    e.printStackTrace();
                }
            }
        }
    }

    public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }
    
    /**
     * Ask the metadata store to store a given persistence unit.
     * Currently only persistence units that are defined by a location string (usually a URL) can be saved.
     * @param applicationName
     * @param persistenceXMLURL
     */
    public void storeApplicationMetadata(String applicationName, String persistenceXMLURL){
        if (metadataStore != null){
            metadataStore.persistMetadata(applicationName, persistenceXMLURL);
        } else {
            // TODO Real Logging
            System.out.println("Unable to store metadata.  No MetadataStore has been configured.");
        }
    }
    
}
