/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.spring;

import jakarta.persistence.EntityManagerFactory;


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
