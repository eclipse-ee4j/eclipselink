/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.jpa.test.property;

import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.TemporalType;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.criteria.model.CoalesceTimestampEntity;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestDerbyDateLiteral {

    @Emf(name = "derbyDateLiteralFreshEMF",
         createTables = DDLGen.DROP_CREATE,
         classes = { CoalesceTimestampEntity.class },
         properties = {
             @Property(name = "eclipselink.jdbc.native-sql", value = "true"),
             @Property(name = "eclipselink.logging.level", value = "FINE"),
             @Property(name = "eclipselink.logging.level.sql", value = "FINE"),
             @Property(name = "eclipselink.logging.parameters", value = "true")
         })
    private EntityManagerFactory emf;

    @Test
    public void testRealLogPathBetweenDateLiteralsCoercesToTimestamp() {
        DatabasePlatform platform = (DatabasePlatform) emf.unwrap(EntityManagerFactoryImpl.class)
                .getDatabaseSession().getDatasourcePlatform();

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(new CoalesceTimestampEntity(1, "r1", Timestamp.valueOf("2026-04-13 17:57:41")));
            em.persist(new CoalesceTimestampEntity(2, "r2", Timestamp.valueOf("2026-04-14 17:57:41")));
            em.getTransaction().commit();

            AbstractDirectMapping mapping = (AbstractDirectMapping) emf.unwrap(EntityManagerFactoryImpl.class)
                    .getDatabaseSession()
                    .getClassDescriptor(CoalesceTimestampEntity.class)
                    .getMappingForAttributeName("dateValue");
            Assert.assertNotNull("Expected dateValue mapping", mapping);
            Assert.assertNotNull("Expected dateValue mapping field", mapping.getField());
            mapping.setConverter(null);
            mapping.getField().setType(null);
            mapping.getField().setSqlType(DatabaseField.NULL_SQL_TYPE);
            mapping.getField().setTypeName(null);
            Assert.assertEquals("Expected NULL_SQL_TYPE precondition",
                    DatabaseField.NULL_SQL_TYPE, mapping.getField().getSqlType());

            Query query = em.createQuery(
                    "SELECT e.id FROM CoalesceTimestampEntity e "
                            + "WHERE (e.dateValue BETWEEN ?1 AND ?2) "
                            + "ORDER BY e.dateValue DESC");
            query.setHint(QueryHints.BIND_PARAMETERS, HintValues.FALSE);
            query.setParameter(1, Date.valueOf("2026-04-13"), TemporalType.DATE);
            query.setParameter(2, Date.valueOf("2026-04-14"), TemporalType.DATE);
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
}
