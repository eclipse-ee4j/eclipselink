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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jpa; 

import java.util.Map;

import javax.persistence.EntityManagerFactory; 
import javax.persistence.Query; 
import org.eclipse.persistence.internal.jpa.*; 
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.jpa.JpaEntityManager; 
import org.eclipse.persistence.queries.*; 
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.server.Server; 
import org.eclipse.persistence.sessions.server.ServerSession; 
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.factories.SessionFactory; 
import org.eclipse.persistence.queries.FetchGroupTracker;


/** 
 * This sample illustrates the JPA helper methods that may be of use 
 * to EclipseLink customers attempting to leverage EclipseLink specific functionality. 
 * 
 * @author dclarke, gpelletie 
 */ 
public class JpaHelper { 
    /** 
     * Verify if the JPA provider is EclipseLink. If you are in a container 
     * and not in a transaction this method may incorrectly return false. 
     * It is always more reliable to check isEclipseLink on the EMF or Query. 
     */ 
    public static boolean isEclipseLink(javax.persistence.EntityManager em) {
        return getEntityManager(em) != null; 
    } 

    /** 
     * Verify if the JPA provider is EclipseLink 
     */ 
    public static boolean isEclipseLink(EntityManagerFactory emf) { 
        try { 
            getEntityManagerFactory(emf); 
        } catch (IllegalArgumentException iae) { 
            return false; 
        } 

        return true; 
    } 

    /** 
     * Verify if the JPA provider is EclipseLink 
     */ 
    public static boolean isEclipseLink(Query query) { 
        return query instanceof JpaQuery;
    } 

    /** 
     * Determine if the JPA query is a EclipseLink ReportQuery. Useful for 
     * frameworks that want to determine which get_X_Query method they can 
     * safely invoke. 
     */ 
    public static boolean isReportQuery(Query query) { 
        return isEclipseLink(query) && getDatabaseQuery(query).isReportQuery();
    } 

    /** 
     * Access the internal EclipseLink query wrapped within the JPA query. A 
     * EclipseLink JPA created from JP QL  contains a ReportQuery if multiple 
     * items or a non-entity type is being returned. This method will fail 
     * if a single entity type is being returned as the query is a ReadAllQuery. 
     * 
     * @see JpaHelper#getReadAllQuery 
     */ 
    public static ReportQuery getReportQuery(Query query) { 
        DatabaseQuery dbQuery = getDatabaseQuery(query);
        if (dbQuery.isReportQuery()) { 
            return (ReportQuery)dbQuery; 
        } 

        throw new IllegalArgumentException(ExceptionLocalization.buildMessage("jpa_helper_invalid_report_query" + query.getClass()));
    }

    /** 
     * Access the internal EclipseLink query wrapped within the JPA query.
     */ 
    public static DatabaseQuery getDatabaseQuery(Query query) { 
        if (query instanceof JpaQuery) {
            return ((JpaQuery)query).getDatabaseQuery();
        }

        throw new IllegalArgumentException(ExceptionLocalization.buildMessage("jpa_helper_invalid_query" + query.getClass()));
    }

    /** 
     * Access the internal EclipseLink query wrapped within the JPA query. A EclipseLink 
     * JPA created from JP QL only contains a ReadAllQuery if only a single entity 
     * type is being returned. 
     * 
     * A ReadAllQuery is the super class of a ReportQuery so this method will 
     * always work for either a ReportQuery or ReadAllQuery. 
     */ 
    public static ReadAllQuery getReadAllQuery(Query query) {
        DatabaseQuery dbQuery = getDatabaseQuery(query);
        if (dbQuery.isReadAllQuery()) { 
            return (ReadAllQuery)dbQuery; 
        } 

        throw new IllegalArgumentException(ExceptionLocalization.buildMessage("jpa_helper_invalid_read_all_query" + query.getClass()));           
    } 

    /** 
     * Create a EclipseLink JPA query dynamically given a EclipseLink query. 
     */ 
    public static Query createQuery(DatabaseQuery query, javax.persistence.EntityManager em) { 
        return getEntityManager(em).createQuery(query);
    } 

    /** 
     * Convert a JPA entityManager into a EclipseLink specific one. This will work 
     * both within a JavaSE deployment as well as within a container where the 
     * EntityManager may be wrapped. 
     * 
     * In the case where the container is not in a transaction it may return null 
     * for its delegate. When this happens the only way to access an EntityManager 
     * is to use the EntityManagerFactory to create a temporary one where the 
     * application manage its lifecycle. 
     */ 
    public static JpaEntityManager getEntityManager(javax.persistence.EntityManager entityManager) { 
        if (entityManager instanceof JpaEntityManager) { 
            return (JpaEntityManager)entityManager; 
        }

        if (entityManager.getDelegate() != null) { 
            return getEntityManager((javax.persistence.EntityManager)entityManager.getDelegate()); 
        }

        return null; 
    } 

    /** 
     * Given a JPA EntityManagerFactory attempt to cast it to a EclipseLink EMF.
     * 
     * Although this method currently returns an instance of EntityManagerFactoryImpl, it
     * is recommended that users cast to JpaEntityManagerFactory.  Future versions of EclipseLink will
     * return that interface from this method instead
     * 
     * @see JpaEntityManagerFactory
     */ 
    public static EntityManagerFactoryImpl getEntityManagerFactory(EntityManagerFactory emf) { 
        if (emf instanceof EntityManagerFactoryImpl) { 
            return ((EntityManagerFactoryImpl)emf); 
        }
        throw new IllegalArgumentException(ExceptionLocalization.buildMessage("jpa_helper_invalid_entity_manager_factory", new Object[]{emf.getClass()}));
    }

    /** 
     * Given an EntityManager return the EntityManagerFactory that created it.  This method must be called
     * on an open entity manager and will return null if called on a closed entityManager.
     * 
     * This method will return null for non-EclipseLink EntityManagers.
     * 
     * @see JpaEntityManagerFactory
     */ 
    public static JpaEntityManagerFactory getEntityManagerFactory(javax.persistence.EntityManager em) { 
        JpaEntityManager entityManager = getEntityManager(em);
        if (entityManager != null){
            if (entityManager.getEntityManagerFactory() != null){
                return ((EntityManagerFactoryDelegate)entityManager.getEntityManagerFactory()).getOwner();
            }
        }
        return null;
    }

    /** 
     * Retrieve the shared database session from the EMF. 
     */ 
    public static DatabaseSession getDatabaseSession(EntityManagerFactory emf) {
        return getEntityManagerFactory(emf).getDatabaseSession(); 
    }

    /** 
     * Retrieve the shared server session from the EMF. 
     */ 
    public static Server getServerSession(EntityManagerFactory emf) {
        return getEntityManagerFactory(emf).getServerSession(); 
    }

    /** 
     * Retrieve the shared session broker from the EMF. 
     */ 
    public static SessionBroker getSessionBroker(EntityManagerFactory emf) {
        return getEntityManagerFactory(emf).getSessionBroker(); 
    }

    /** 
     * Create a EclipseLink EMF given a ServerSession that has already been created 
     * and logged in. 
     */ 
    public static javax.persistence.EntityManagerFactory createEntityManagerFactory(Server session) { 
        return new EntityManagerFactoryImpl((ServerSession)session); 
    } 

    /** 
     * Create a EclipseLink EMF using a session name and sessions.xml. This is 
     * equivalent to using the EclipseLink.session-xml and EclipseLink.session-name PU 
     * properties with the exception that no persistence.xml is required. 
     * 
     * The application would be required to manage this singleton EMF. 
     */ 
    public static EntityManagerFactory createEntityManagerFactory(String sessionName) { 
        SessionFactory sf = new SessionFactory(sessionName); 
        // Verify that shared session is a ServerSession 
        return new EntityManagerFactoryImpl((ServerSession)sf.getSharedSession()); 
    } 

    
    
    /**
     * If the object has a fetch group then the whole object is read in.
     */
    public static void loadUnfetchedObject(FetchGroupTracker entity) {
        if(entity._persistence_getFetchGroup() != null) {
            EntityManagerImpl.processUnfetchedAttribute(entity, null);
        }
    }

}
