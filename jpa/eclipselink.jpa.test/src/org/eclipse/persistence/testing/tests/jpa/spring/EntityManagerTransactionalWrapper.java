package org.eclipse.persistence.testing.tests.jpa.spring;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


/**
 * This class extends EntityManagerWrapper to enable certain functions of the 
 * injected EntityManager to be wrapped in transactions by the @Transactional annotation.
 */
@Repository     //Used to signal data exception translations
@Transactional
public class EntityManagerTransactionalWrapper extends EntityManagerWrapper {

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    public void setEntityManager(EntityManager entityManager) {
        this.em = entityManager; 
    }

    public void persist(Object obj) {
        em.persist(obj);
    }

    public void remove(Object obj) {
        em.remove(obj);
    }

    public void flush() {
        em.flush();  
    }
    
}
