/*
 * Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2018, 2019 IBM Corporation. All rights reserved.
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
//     04/11/2018 - Will Dazey
//       - 533148 : Add the eclipselink.jpa.sql-call-deferral property
package org.eclipse.persistence.jpa.test.property;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.property.model.AbstractParent;
import org.eclipse.persistence.jpa.test.property.model.Child;
import org.eclipse.persistence.jpa.test.property.model.Parent;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestSQLCallDeferall {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { AbstractParent.class, Parent.class, Child.class }, properties = {
            @Property(name = "eclipselink.jpa.sql-call-deferral", value = "false") })
    private EntityManagerFactory emf;

    /**
     * Tests the 'eclipselink.jpa.sql-call-deferral' persistence property. Child entity sets a Foreign Key constraint
     * that would lead EclipseLink to order SQL operations into a ForeignKey constraint violation. However, with
     * the property set, the operations are ordered without deferral reordering.
     */
    @Test
    public void testSQLDeferralProperty() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Parent parent = new Parent();
            em.persist(parent);

            Child child = new Child();
            child.setParent(parent);
            em.persist(child);

            // expects: INSERT order: AbstractParent, Parent, Child
            // ECL default: INSERT order: AbstractParent, Child, Parent(deferred)
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }
}
