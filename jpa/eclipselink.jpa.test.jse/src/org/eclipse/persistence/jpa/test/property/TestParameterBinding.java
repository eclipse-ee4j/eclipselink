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
//     12/06/2018 - Will Dazey
//       - 542491: Add new 'eclipselink.jdbc.force-bind-parameters' property to force enable binding
package org.eclipse.persistence.jpa.test.property;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.property.model.AbstractParent;
import org.eclipse.persistence.jpa.test.property.model.Child;
import org.eclipse.persistence.jpa.test.property.model.Parent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestParameterBinding {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { AbstractParent.class, Parent.class, Child.class }, properties = {
            @Property(name = "eclipselink.jdbc.force-bind-parameters", value = "true") })
    private EntityManagerFactory emf;

    /**
     * Tests the 'eclipselink.jdbc.force-bind-parameters' persistence property. 
     */
    @Test
    public void testForceBindAllFunctionParameters() {
        EntityManager em = emf.createEntityManager();

        TypedQuery<Child> query = em.createQuery("SELECT c FROM Child c WHERE c.id = abs(?1)", Child.class);
        query.setParameter(1, 12);
        //query.setHint("eclipselink.jdbc.bind-parameters", true);
        query.getResultList();

        Assert.assertFalse("Expected query parameter binding to not be set for the DatabaseCall", ((JpaQuery)query).getDatabaseQuery().getCall().isUsesBindingSet());
    }
} 
