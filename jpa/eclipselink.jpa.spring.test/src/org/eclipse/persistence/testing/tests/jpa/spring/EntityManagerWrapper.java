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
package org.eclipse.persistence.testing.tests.jpa.spring;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import test.org.eclipse.persistence.testing.models.jpa.spring.Address;
import test.org.eclipse.persistence.testing.models.jpa.spring.Route;
import test.org.eclipse.persistence.testing.models.jpa.spring.Truck;


/** 
 * This class wraps an EntityManager.
 * It allows EntityManager implementations to override the basic operations with their requirements.
 * For example, when certain functions need to be wrapped in transactions.
 */
public class EntityManagerWrapper {

    protected EntityManager em;
    
    // Constructors //
    
    public EntityManagerWrapper() {
    }
    
    public EntityManagerWrapper(EntityManagerFactory emf) {
        this.em = emf.createEntityManager();
    }
    
    public EntityManagerWrapper(EntityManager em) {
        this.em = em;
    }
         
    
    // EntityManager Operations //
    
    public void persist(Object obj) {
        em.persist(obj);
        em.flush();
    }

    public void remove(Object obj) {
        em.remove(obj);
        em.flush();
    }

    public void flush() {
        em.flush();
    }
    
    public Object find(Object obj) {
        if (obj instanceof Truck){
            Truck t = (Truck)obj;
            return (em.find(Truck.class, t.getId()));
        }else if (obj instanceof Route){
            Route r = (Route)obj;
            return (em.find(Route.class, r.getId()));
        }else if (obj instanceof Address){
            Address a = (Address)obj;
            return (em.find(Address.class, a.getId()));
        }
        return null;
    }

    public boolean contains(Object obj) {
        return em.contains(obj);
    }

    public Object merge(Object obj) {
        return em.merge(obj);
    }
    
    public void refresh(Object obj) {
        em.refresh(obj);
    }
    
    public Query createNativeQuery(String string) {
        return em.createNativeQuery(string);
    }
    
    public int executeNativeQuery(String string) {
        return em.createNativeQuery(string).executeUpdate();
    }
    
    public Query createNativeQuery(String string, Class clazz) {
        return em.createNativeQuery(string, clazz);
    }

    public Query createNamedQuery(String string) {
        return em.createNamedQuery(string);
    }

    public Query createQuery(String string) {
        return em.createQuery(string);
    }
   
}
