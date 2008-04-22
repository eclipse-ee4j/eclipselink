package org.eclipse.persistence.testing.tests.jpa.spring.dao;

import java.util.List;

import org.eclipse.persistence.testing.models.jpa.spring.*;

import org.springframework.orm.jpa.support.JpaDaoSupport;


/**
 * This class is a JPA data access object and implements Spring's jpaTemplate which functions 
 * as an alternative to a basic EntityManager.
 */
public class SpringDao extends JpaDaoSupport {
    
    public void persist(Object obj) {
        getJpaTemplate().persist(obj);
    }

    public void remove(Object obj) {
        getJpaTemplate().remove(obj); 
    }

    public void flush() {
        getJpaTemplate().flush();
    }
    
    public Object find(Object obj) {
        if (obj instanceof Truck){
            Truck t = (Truck)obj;
            return (getJpaTemplate().find(Truck.class, t.getId()));
        }else if (obj instanceof Route){
            Route r = (Route)obj;
            return (getJpaTemplate().find(Route.class, r.getId()));
        }else if (obj instanceof Address){
            Address a = (Address)obj;
            return (getJpaTemplate().find(Address.class, a.getId()));
        }
        return null;
    }

    public boolean contains(Object obj) {
        return getJpaTemplate().contains(obj);
    }

    public Object merge(Object obj) {
        return getJpaTemplate().merge(obj);
    }
    
    public void refresh(Object obj) {
        getJpaTemplate().refresh(obj);
    }

    public List findByNamedQuery(String query, String driverName) {
        return getJpaTemplate().findByNamedQuery(query, driverName);
    }
    
    public List findByQuery(String query) {
        return getJpaTemplate().find(query);
    }
    
}
