/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.tests.wdf.jpa2.embeddable;

import jakarta.persistence.EntityManager;

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
