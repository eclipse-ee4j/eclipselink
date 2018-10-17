/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2018 IBM Corporation. All rights reserved.
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
//     10/01/2018: Will Dazey
//       - #253: Add support for embedded constructor results with CriteriaBuilder
package org.eclipse.persistence.jpa.test.criteria;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CompoundSelection;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.eclipse.persistence.jpa.test.criteria.model.L1;
import org.eclipse.persistence.jpa.test.criteria.model.L1Model;
import org.eclipse.persistence.jpa.test.criteria.model.L1_;
import org.eclipse.persistence.jpa.test.criteria.model.L2;
import org.eclipse.persistence.jpa.test.criteria.model.L2Model;
import org.eclipse.persistence.jpa.test.criteria.model.L2_;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestCriteriaBuilder {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { L1.class, L2.class })
    private EntityManagerFactory emf;

    /**
     * Merging ElementCollections on Oracle fails when EclipseLink generates 
     * a DELETE SQL statement with a WHERE clause containing a CLOB.
     * 
     * @throws Exception
     */
    @Test
    public void testCriteriaCompoundSelectionModel() throws Exception {
        //Populate the database
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        L2 l2 = new L2(1, "L2-1");
        L2 l2_2 = new L2(2, "L2-2");

        L1 l1 = new L1(1, "L1-1", l2);
        L1 l1_2 = new L1(2, "L1-2", l2_2);

        em.persist(l2);
        em.persist(l2_2);
        em.persist(l1);
        em.persist(l1_2);

        em.getTransaction().commit();
        em.clear();

        em = emf.createEntityManager();

        //Test CriteriaBuilder
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<L1Model> query = builder.createQuery(L1Model.class);
        final Root<L1> root = query.from(L1.class);
        final Join<L1, L2> l1ToL2 = root.join(L1_.l2);
        final CompoundSelection<L2Model> selection_l2 = builder.construct(L2Model.class, l1ToL2.get(L2_.id), l1ToL2.get(L2_.name));
        final CompoundSelection<L1Model> selection = builder.construct(L1Model.class, root.get(L1_.id), root.get(L1_.name), selection_l2);
        query.select(selection);

        TypedQuery<L1Model> q = em.createQuery(query);
        List<L1Model> l1List = q.getResultList();
        if (l1List != null && !l1List.isEmpty()) {
            for (L1Model l1m : l1List) {
                Assert.assertNotNull(l1m.getL2());
            }
        }

        em.close();
    }
}
