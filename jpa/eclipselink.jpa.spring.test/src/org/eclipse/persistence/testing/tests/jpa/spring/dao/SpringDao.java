/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.spring.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import test.org.eclipse.persistence.testing.models.jpa.spring.Address;
import test.org.eclipse.persistence.testing.models.jpa.spring.Route;
import test.org.eclipse.persistence.testing.models.jpa.spring.Truck;

import javax.persistence.*;

/**
 * This class is a JPA data access object and implements Spring's jpaTemplate which functions
 * as an alternative to a basic EntityManager.
 */
@Repository
@Transactional
public class SpringDao {

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    protected EntityManager entityManager;

    public void persist(Object obj) {
        entityManager.persist(obj);
    }

    public void remove(Object obj) {
        entityManager.remove(obj);
    }

    public void flush() {
        entityManager.flush();
    }

    public Object find(Object obj) {
        if (obj instanceof Truck){
            Truck t = (Truck)obj;
            return (entityManager.find(Truck.class, t.getId()));
        }else if (obj instanceof Route){
            Route r = (Route)obj;
            return (entityManager.find(Route.class, r.getId()));
        }else if (obj instanceof Address){
            Address a = (Address)obj;
            return (entityManager.find(Address.class, a.getId()));
        }
        return null;
    }

    public boolean contains(Object obj) {
        return entityManager.contains(obj);
    }

    public Object merge(Object obj) {
        return entityManager.merge(obj);
    }

    public void refresh(Object obj) {
        entityManager.refresh(obj);
    }

    public List findByNamedQuery(String query, String driverName) {
        Query q = entityManager.createNamedQuery(query);
        q.setParameter(1, driverName);
        return q.getResultList();
    }

}
