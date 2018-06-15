/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.tests.wdf.jpa2.embeddable;

import javax.persistence.EntityManager;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.Skip;
import org.eclipse.persistence.testing.models.wdf.jpa2.embedded.EmbeddedFieldAccess;
import org.eclipse.persistence.testing.models.wdf.jpa2.embedded.EmbeddedPropertyAccess;
import org.eclipse.persistence.testing.models.wdf.jpa2.embedded.EmbeddingFieldAccess;
import org.eclipse.persistence.testing.tests.wdf.jpa2.JPA2Base;
import org.junit.Test;

public class TestEmbeddingWithMixedAccess extends JPA2Base {

    @Test
    @Skip(server=true)
    public void testFieldAccess() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            EmbeddingFieldAccess obj = new EmbeddingFieldAccess(0);
            EmbeddedFieldAccess field = new EmbeddedFieldAccess();
            field.setValue(1);
            EmbeddedPropertyAccess property = new EmbeddedPropertyAccess();
            property.setData(2);
            obj.setFieldAccess(field);
            obj.setPropertyAccess(property);

            em.persist(obj);

        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Skip(server=true)
    public void testPropertyAccess() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            EmbeddingFieldAccess obj = new EmbeddingFieldAccess(1);
            EmbeddedFieldAccess field = new EmbeddedFieldAccess();
            field.setValue(1);
            EmbeddedPropertyAccess property = new EmbeddedPropertyAccess();
            property.setData(2);
            obj.setFieldAccess(field);
            obj.setPropertyAccess(property);

            em.persist(obj);

        } finally {
            closeEntityManager(em);
        }
    }

}
