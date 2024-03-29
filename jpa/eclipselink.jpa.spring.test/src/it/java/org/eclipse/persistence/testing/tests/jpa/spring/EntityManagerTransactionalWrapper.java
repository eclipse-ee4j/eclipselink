/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
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

    @Override
    public void persist(Object obj) {
        em.persist(obj);
    }

    @Override
    public void remove(Object obj) {
        em.remove(obj);
    }

    @Override
    public void flush() {
        em.flush();
    }

    @Override
    public int executeNativeQuery(String string) {
        return em.createNativeQuery(string).executeUpdate();
    }

}
