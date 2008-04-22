package org.eclipse.persistence.testing.tests.jpa.spring;

import javax.persistence.EntityManagerFactory;


/**
 * This class extends the EntityManagerWrapper to enable certain functions 
 * on the instantiated EntityManager to be wrapped in transactions.
 */
public class EntityManagerTransactionWrapper extends EntityManagerWrapper {

    public EntityManagerTransactionWrapper(EntityManagerFactory emf){
        super(emf);
    }
    
    public void persist(Object obj) {
        em.getTransaction().begin();
        em.persist(obj);
        em.getTransaction().commit();
    }

    public void remove(Object obj) {
        em.getTransaction().begin();
        em.remove(obj);
        em.getTransaction().commit();
    }

    public void flush() {
        em.getTransaction().begin();
        em.flush();
        em.getTransaction().commit();   
    }

}
