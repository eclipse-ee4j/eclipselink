/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2022 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.test.jpql;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.jpql.model.NoResultEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestWhiteSpace {

    @Emf(name = "noResultEMF", createTables = DDLGen.DROP_CREATE, classes = { NoResultEntity.class })
    private EntityManagerFactory noResultEmf;

    /**
     * Complex test of the aggregate functions in JPQL. 
     * For this test, there must be zero results in the entity table and the Entity state field 
     * must be a primitive type.
     * JPA 2.1 specification; Section 4.8.5 states aggregate functions (MIN, MAX, AVG, & SUM) 
     * must return a result of NULL if there are no values to apply the aggregate function to
     */
    @Test
    public void testNonBreakingSpace() {
        EntityManager em = noResultEmf.createEntityManager();
        try {
        	String queryString = "SELECT n FROM\u00A0NoResultEntity n";
            TypedQuery<NoResultEntity> checkQuery = em.createQuery(queryString, NoResultEntity.class);
            List<NoResultEntity> checkResult = checkQuery.getResultList();
            Assert.assertEquals("Entity table NoResultEntity must be empty for this test", 0, checkResult.size());
            
        	queryString = "SELECT n FROM NoResultEntity n WHERE n.id =\u00A0:idparam";
            checkQuery = em.createQuery(queryString, NoResultEntity.class);
            checkQuery.setParameter("idparam", 22);
            checkResult = checkQuery.getResultList();
            Assert.assertEquals("Entity table NoResultEntity must be empty for this test", 0, checkResult.size());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}