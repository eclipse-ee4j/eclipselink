/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     02/20/2018-2.7 Will Dazey
 *       - 531062: Incorrect expression type created for CollectionExpression
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.jpql.model.JPQLEmbeddedValue;
import org.eclipse.persistence.jpa.test.jpql.model.JPQLEntity;
import org.eclipse.persistence.jpa.test.jpql.model.JPQLEntityId;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestComplexJPQL {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { JPQLEntity.class, JPQLEntityId.class, JPQLEmbeddedValue.class })
    private EntityManagerFactory emf;

    @Test
    public void testComplexJPQLIN() {
        EntityManager em = emf.createEntityManager();

        Query q = em.createQuery("select t0.id from JPQLEntity t0 "
                + "where (t0.string1, t0.string2) "
                + "in (select t1.string1, t1.string2 from JPQLEntity t1)");
        q.getResultList();

        if (em.isOpen()) {
            em.clear();
            em.close();
        }
    }
}