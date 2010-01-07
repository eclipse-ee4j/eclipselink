/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
    
    public int executeNativeQuery(String string) {
        return em.createNativeQuery(string).executeUpdate();
    }
    
}
