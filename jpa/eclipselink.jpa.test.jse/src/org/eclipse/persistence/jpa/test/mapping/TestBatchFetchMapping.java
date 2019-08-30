/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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
//     12/02/2019-3.0 - Alexandre Jacob
//       - 541046: @JoinFetch doesn't work with default value
package org.eclipse.persistence.jpa.test.mapping;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.mapping.model.JF1;
import org.eclipse.persistence.jpa.test.mapping.model.JF2;
import org.eclipse.persistence.jpa.test.mapping.model.JF3;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestBatchFetchMapping {

    @Emf(createTables = DDLGen.NONE, classes = { JF1.class, JF2.class, JF3.class },
        properties = {
            @Property(name = "eclipselink.logging.level.sql", value = "FINEST")
        })
    private EntityManagerFactory emf;

    @Test
    public void testBatchFetchWithDefaultValue() {
        EntityManager em = emf.createEntityManager();

        ForeignReferenceMapping mapping = (ForeignReferenceMapping)em.unwrap(ServerSession.class).getDescriptor(JF1.class).getMappingForAttributeName("jf2");

        Assert.assertEquals(mapping.getJoinFetch(), ForeignReferenceMapping.INNER_JOIN);
    }

    @Test
    public void testBatchFetchWithExplicitValue() {
        EntityManager em = emf.createEntityManager();

        ForeignReferenceMapping mapping = (ForeignReferenceMapping)em.unwrap(ServerSession.class).getDescriptor(JF2.class).getMappingForAttributeName("jf3");

        Assert.assertEquals(mapping.getJoinFetch(), ForeignReferenceMapping.OUTER_JOIN);
    }
}
