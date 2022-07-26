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
// Contributors:
//     04/19/2022: Jody Grassel
//       - Issue 579726: CriteriaBuilder neg() only returns Integer type, instead of it's argument expression type.
package org.eclipse.persistence.jpa.test.criteria;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

import org.eclipse.persistence.jpa.test.criteria.model.CoalesceEntity;
import org.eclipse.persistence.jpa.test.criteria.model.CritEntity;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestNegFunction {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = { CritEntity.class })
    private EntityManagerFactory emf;

    @Test
    public void testNegFunction() throws Exception {
        EntityManager em = emf.createEntityManager();

        try {
            CritEntity ce = new CritEntity();
            ce.setId(1);
            ce.setValue(new BigDecimal("3.14"));

            em.getTransaction().begin();
            em.persist(ce);;
            em.getTransaction().commit();
            em.clear();

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<DecHolder> criteriaQuery = criteriaBuilder.createQuery(DecHolder.class);
            Root<CritEntity> entityRoot = criteriaQuery.from(CritEntity.class);

            Collection<Selection<?>> selections = new ArrayList<Selection<?>>();

            Expression<BigDecimal> valExpr = entityRoot.get("value");

            selections.add(criteriaBuilder.sum(
                  criteriaBuilder.<BigDecimal> selectCase()
                        .when(criteriaBuilder.equal(entityRoot.get("id"), 0), valExpr)
                        .otherwise(criteriaBuilder.neg(valExpr))));

            criteriaQuery.multiselect(selections.toArray(new Selection[] {}));

            List<DecHolder> retList = em.createQuery(criteriaQuery).getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    public static class DecHolder {
        private BigDecimal value;

        public DecHolder(Object value) {
            super();
            this.value = (BigDecimal) value;
        }

        public BigDecimal getValue() {
            return value;
         }

         public void setValue(BigDecimal value) {
            this.value = value;
         }

         @Override
         public String toString() {
            return "DecHolder [value=" + value + "]";
         }
    }
}
