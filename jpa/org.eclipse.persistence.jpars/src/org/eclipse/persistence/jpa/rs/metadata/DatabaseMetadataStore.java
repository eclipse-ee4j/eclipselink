/****************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.metadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 * A metadata store that stores information about existing applications in a database
 * @author tware
 *
 */
public class DatabaseMetadataStore implements MetadataStore {
    
    protected EntityManagerFactory factory = null;

    Map<String, Object> properties = null;
    
    public DatabaseMetadataStore(){
    }
    
    public EntityManagerFactory getEntityManagerFactory(){
        if (factory == null){
            Map<String, Object> bootstrapProperties = new HashMap<String, Object>();
            if (properties != null){
                bootstrapProperties.putAll(properties);
            }
            if (bootstrapProperties.get(PersistenceUnitProperties.NON_JTA_DATASOURCE) == null && bootstrapProperties.get(PersistenceUnitProperties.JDBC_URL) == null){
                bootstrapProperties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, "jdbc/jpa-rs");
            }
            bootstrapProperties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_ONLY);
            factory = Persistence.createEntityManagerFactory("jpa-rs", bootstrapProperties);
        }
        return factory;
    }
    
    public void persistMetadata(String name, String url){
        Application application = new Application(name, url);
        EntityManager em = getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        if (em.find(Application.class, application.getName()) != null){
            em.merge(application);
        } else {
            em.persist(application);
        }
        em.getTransaction().commit();
    }
    
    @SuppressWarnings("unchecked")
    public List<Application> retreiveMetadata(){
        EntityManager em = getEntityManagerFactory().createEntityManager();
        return em.createQuery("select a from Application a").getResultList();
    }
    
    public void clearMetadata(){
        EntityManager em = getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from Application a").executeUpdate();
        em.getTransaction().commit();
    }
    
    public void close(){
        if (isInitialized()){
            getEntityManagerFactory().close();
        }
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    public boolean isInitialized(){
        return factory != null;
    }

}
