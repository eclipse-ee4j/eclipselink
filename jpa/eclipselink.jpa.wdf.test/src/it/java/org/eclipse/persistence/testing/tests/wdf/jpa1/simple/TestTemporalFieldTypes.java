/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.tests.wdf.jpa1.simple;

import java.util.Date;
import java.util.GregorianCalendar;

import jakarta.persistence.EntityManager;

import org.junit.Assert;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.types.BasicTypesFieldAccess;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestTemporalFieldTypes extends JPA1Base {

    @Test
    /**
     * just for the case that all other methods are skipped
     */
    public void dummyTestMethod() {
        return;
    }

    private void validateMutable(final int id, MutableValidator validator, String fieldName) {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            BasicTypesFieldAccess obj = new BasicTypesFieldAccess(id);
            // insert object with null-field
            env.beginTransaction(em);
            em.persist(obj);
            env.commitTransactionAndClear(em);
            obj = em.find(BasicTypesFieldAccess.class, id);
            Assert.assertTrue(fieldName + " is not null", validator.isNull(obj));
            // delete the object again
            env.beginTransaction(em);
            em.remove(em.find(BasicTypesFieldAccess.class, id));
            env.commitTransactionAndClear(em);
            // insert object with non-null field
            env.beginTransaction(em);
            validator.set(obj);
            em.persist(obj);
            env.commitTransactionAndClear(em);
            obj = em.find(BasicTypesFieldAccess.class, id);
            Assert.assertTrue(fieldName + " not persisted", !validator.isChanged(obj));
            // update unchanged
            env.beginTransaction(em);
            obj = em.find(BasicTypesFieldAccess.class, id);
            obj.clearPostUpdate();
            env.commitTransactionAndClear(em);
            Assert.assertTrue("postUpdate was not called", !obj.postUpdateWasCalled());
            obj = em.find(BasicTypesFieldAccess.class, id);
            Assert.assertTrue(fieldName + " is changed", !validator.isChanged(obj));
            // update changed
            env.beginTransaction(em);
            obj = em.find(BasicTypesFieldAccess.class, id);
            obj.clearPostUpdate();
            validator.setNull(obj);
            env.commitTransactionAndClear(em);
            Assert.assertTrue("postUpdate was not called", obj.postUpdateWasCalled());
            obj = em.find(BasicTypesFieldAccess.class, id);
            Assert.assertTrue(fieldName + " is not null", validator.isNull(obj));
            // update original
            env.beginTransaction(em);
            obj = em.find(BasicTypesFieldAccess.class, id);
            validator.set(obj);
            env.commitTransactionAndClear(em);
            obj = em.find(BasicTypesFieldAccess.class, id);
            Assert.assertTrue(fieldName + " not persisted", !validator.isChanged(obj));
            // mutate
            env.beginTransaction(em);
            obj = em.find(BasicTypesFieldAccess.class, id);
            obj.clearPostUpdate();
            validator.mutate(obj);
            env.commitTransactionAndClear(em);
            Assert.assertTrue("postUpdate was not called", obj.postUpdateWasCalled());
            obj = em.find(BasicTypesFieldAccess.class, id);
            Assert.assertTrue(fieldName + " not mutated", validator.isChanged(obj));
            // update to null
            env.beginTransaction(em);
            obj = em.find(BasicTypesFieldAccess.class, id);
            obj.clearPostUpdate();
            validator.setNull(obj);
            env.commitTransactionAndClear(em);
            Assert.assertTrue("postUpdate was not called", obj.postUpdateWasCalled());
            obj = em.find(BasicTypesFieldAccess.class, id);
            Assert.assertTrue(fieldName + " is not null", validator.isNull(obj));
        } finally {
            closeEntityManager(em);
        }
    }

    // mutable types
    @Test
    public void testUtilDateAsTimestamp() {
        MutableValidator validator = new MutableValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setUtilDate(new Date(1000));
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setUtilDate(new Date(2000));
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setUtilDate(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getUtilDate() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !obj.getUtilDate().equals(new Date(1000));
            }

            @Override
            public void mutate(BasicTypesFieldAccess obj) {
                obj.getUtilDate().setTime(2000);
            }
        };
        validateMutable(1, validator, "utilDate");
    }

    @Test
    public void testUtilCalendarAsTimestamp() {
        MutableValidator validator = new MutableValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setUtilCalendar(new GregorianCalendar(2005, 9, 8, 10, 49));
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setUtilCalendar(new GregorianCalendar(2005, 9, 9, 10, 49));
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setUtilCalendar(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getUtilCalendar() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !obj.getUtilCalendar().equals(new GregorianCalendar(2005, 9, 8, 10, 49));
            }

            @Override
            public void mutate(BasicTypesFieldAccess obj) {
                obj.getUtilCalendar().set(2005, 9, 9);
            }
        };
        validateMutable(2, validator, "utilCalendar");
    }

    @Test
    public void testUtilCalendarAsDate() {
        MutableValidator validator = new MutableValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setUtilCalendar(new GregorianCalendar(2005, 9, 8));
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setUtilCalendar(new GregorianCalendar(2005, 9, 9));
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setUtilCalendar(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getUtilCalendar() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !obj.getUtilCalendar().equals(new GregorianCalendar(2005, 9, 8));
            }

            @Override
            public void mutate(BasicTypesFieldAccess obj) {
                obj.getUtilCalendar().set(2005, 9, 9);
            }
        };
        validateMutable(3, validator, "utilCalendar");
    }

    @Test
    public void testUtilCalendarAsTime() {
        MutableValidator validator = new MutableValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setUtilCalendar(new GregorianCalendar(1970, 1, 1, 10, 49));
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setUtilCalendar(new GregorianCalendar(1970, 1, 1, 11, 49));
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setUtilCalendar(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getUtilCalendar() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !obj.getUtilCalendar().equals(new GregorianCalendar(1970, 1, 1, 10, 49));
            }

            @Override
            public void mutate(BasicTypesFieldAccess obj) {
                obj.getUtilCalendar().set(1970, 1, 1, 11, 49);
            }
        };
        validateMutable(4, validator, "utilCalendar");
    }

    @Test
    public void testSqlDate() {
        MutableValidator validator = new MutableValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setSqlDate(java.sql.Date.valueOf("2005-09-08"));
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setSqlDate(java.sql.Date.valueOf("2005-09-09"));
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setSqlDate(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getSqlDate() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !obj.getSqlDate().equals(java.sql.Date.valueOf("2005-09-08"));
            }

            @Override
            public void mutate(BasicTypesFieldAccess obj) {
                obj.getSqlDate().setTime(java.sql.Date.valueOf("2005-09-09").getTime());
            }
        };
        validateMutable(5, validator, "sqlDate");
    }

    @Test
    public void testSqlTime() {
        MutableValidator validator = new MutableValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setSqlTime(java.sql.Time.valueOf("10:49:00"));
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setSqlTime(java.sql.Time.valueOf("11:49:00"));
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setSqlTime(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getSqlTime() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !obj.getSqlTime().equals(java.sql.Time.valueOf("10:49:00"));
            }

            @Override
            public void mutate(BasicTypesFieldAccess obj) {
                obj.getSqlTime().setTime(java.sql.Time.valueOf("11:49:00").getTime());
            }
        };
        validateMutable(6, validator, "sqlTime");
    }

    @Test
    public void testSqlTimestamp() {
        MutableValidator validator = new MutableValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setSqlTimestamp(new java.sql.Timestamp(1000));
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setSqlTimestamp(new java.sql.Timestamp(2000));
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setSqlTimestamp(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getSqlTimestamp() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !obj.getSqlTimestamp().equals(new java.sql.Timestamp(1000));
            }

            @Override
            public void mutate(BasicTypesFieldAccess obj) {
                obj.getSqlTimestamp().setTime(2000);
            }
        };
        validateMutable(7, validator, "sqlTimestamp");
    }

    // mutable types
    @Test
    public void testUtilDateAsTime() {
        MutableValidator validator = new MutableValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setUtilDate(new Date(1000));
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setUtilDate(new Date(2000));
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setUtilDate(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getUtilDate() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !obj.getUtilDate().equals(new Date(1000));
            }

            @Override
            public void mutate(BasicTypesFieldAccess obj) {
                obj.getUtilDate().setTime(2000);
            }
        };
        validateMutable(8, validator, "utilDate");
    }

    // mutable types
    @Test
    public void testUtilDateAsDate() {
        MutableValidator validator = new MutableValidator() {
            final long millis = java.sql.Date.valueOf("2005-09-08").getTime();
            final long changedMillis = java.sql.Date.valueOf("2005-09-09").getTime();

            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setUtilDate(new java.util.Date(millis));
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setUtilDate(new java.util.Date(changedMillis));
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setUtilDate(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getUtilDate() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !obj.getUtilDate().equals(new java.util.Date(millis));
            }

            @Override
            public void mutate(BasicTypesFieldAccess obj) {
                obj.getUtilDate().setTime(changedMillis);
            }
        };
        validateMutable(9, validator, "utilDate");
    }

    private interface Validator {
        void set(BasicTypesFieldAccess obj);

        void change(BasicTypesFieldAccess obj);

        boolean isChanged(BasicTypesFieldAccess obj);
    }

    private interface ReferenceValidator extends Validator {
        boolean isNull(BasicTypesFieldAccess obj);

        void setNull(BasicTypesFieldAccess obj);
    }

    private interface MutableValidator extends ReferenceValidator {
        void mutate(BasicTypesFieldAccess obj);
    }
}
