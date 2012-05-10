/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

    
    public int executeNativeQuery(String string) {
        em.getTransaction().begin();
        int count = em.createNativeQuery(string).executeUpdate();
        em.getTransaction().commit();
        return count;
    }
}
