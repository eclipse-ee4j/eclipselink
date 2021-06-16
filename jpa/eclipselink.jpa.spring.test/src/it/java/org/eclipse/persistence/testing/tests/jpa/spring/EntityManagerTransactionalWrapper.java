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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;

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
