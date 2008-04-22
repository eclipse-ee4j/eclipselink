package org.eclipse.persistence.testing.tests.jpa.spring;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.eclipse.persistence.testing.models.jpa.spring.Address;
import org.eclipse.persistence.testing.models.jpa.spring.Truck;
import org.eclipse.persistence.testing.models.jpa.spring.Route;


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
