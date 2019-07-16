/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     11/12/2018 - Will Dazey
//       - 540929 : 'jdbc.sql-cast' property does not apply
package org.eclipse.persistence.jpa.test.property;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.property.model.AbstractParent;
import org.eclipse.persistence.jpa.test.property.model.Child;
import org.eclipse.persistence.jpa.test.property.model.Parent;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.sessions.Session;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestSQLCast {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { AbstractParent.class, Parent.class, Child.class }, properties = {
            @Property(name = "eclipselink.jdbc.sql-cast", value = "true") })
    private EntityManagerFactory emf;

    /**
     * Tests the 'eclipselink.jdbc.sql-cast' persistence property. Setting this property should make the current 
     * database platform initiate type casting.
     */
    @Test
    public void testSQLCastPropertyApplied() {
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Session session = ((EntityManagerImpl) em).getActiveSession();

        DatabasePlatform dbplatform = session.getPlatform();
        Assert.assertTrue("Persistent property 'eclipselink.jdbc.sql-cast' did not apply", dbplatform.isCastRequired());

        em.getTransaction().rollback();
    }
}
