/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.helidon.dao;

import jakarta.enterprise.context.Dependent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.eclipse.persistence.testing.helidon.models.BaseEntity;

import java.util.List;


@Dependent
public class BaseDao<T extends BaseEntity> {

    @PersistenceContext(name = "HelidonPu")
    private EntityManager entityManager;

    public void create(T entity) {
        entityManager.persist(entity);
    }

    public void remove(T entity) {
        entityManager.remove(entity);
    }

    public void flush() {
        entityManager.flush();
    }

    public T update(T entity) {
        return entityManager.merge(entity);
    }

    public T find(Class<T> clazz, long id) {
        return (T) entityManager.find(clazz, id);
    }

    public List<T> findByNamedQuery(String query) {
        Query q = entityManager.createNamedQuery(query);
        return q.getResultList();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
