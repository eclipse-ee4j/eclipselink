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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

import org.eclipse.persistence.testing.framework.wdf.Bugzilla;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.Skip;
import org.eclipse.persistence.testing.models.wdf.jpa1.types.BasicTypesFieldAccess;
import org.eclipse.persistence.testing.models.wdf.jpa1.types.UserDefinedEnum;
import org.eclipse.persistence.testing.models.wdf.jpa1.types.UserDefinedSerializable;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestBasicFieldTypes extends JPA1Base {

    @Test
    public void testInsert() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            BasicTypesFieldAccess obj = new BasicTypesFieldAccess(0);
            obj.fill();
            env.beginTransaction(em);
            em.persist(obj);
            env.commitTransactionAndClear(em);
            verify(true, "no Exception");
            obj = em.find(BasicTypesFieldAccess.class, 0);
        } finally {
            closeEntityManager(em);
        }
    }

    private void validatePrimitive(final int id, Validator validator, String fieldName) {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            // insert the object
            env.beginTransaction(em);
            BasicTypesFieldAccess obj = new BasicTypesFieldAccess(id);
            validator.set(obj);
            em.persist(obj);
            env.commitTransactionAndClear(em);
            verify(true, "no Exception");
            obj = em.find(BasicTypesFieldAccess.class, id);
            verify(!validator.isChanged(obj), fieldName + " not persisted");
            // update unchanged object
            env.beginTransaction(em);
            obj = em.find(BasicTypesFieldAccess.class, id);
            obj.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(!obj.postUpdateWasCalled(), "postUpdate was called");
            obj = em.find(BasicTypesFieldAccess.class, id);
            verify(!validator.isChanged(obj), fieldName + " is changed");
            // update changed object
            env.beginTransaction(em);
            obj = em.find(BasicTypesFieldAccess.class, id);
            obj.clearPostUpdate();
            validator.change(obj);
            env.commitTransactionAndClear(em);
            verify(obj.postUpdateWasCalled(), "postUpdate was not called");
            obj = em.find(BasicTypesFieldAccess.class, id);
            verify(validator.isChanged(obj), fieldName + " is unchanged");
        } finally {
            closeEntityManager(em);
        }
    }

    private void validateReference(final int id, ReferenceValidator validator, String fieldName) {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            BasicTypesFieldAccess obj = new BasicTypesFieldAccess(id);
            // insert object with null-field
            env.beginTransaction(em);
            em.persist(obj);
            env.commitTransactionAndClear(em);
            obj = em.find(BasicTypesFieldAccess.class, id);
            verify(validator.isNull(obj), fieldName + " is not null");
            // delete the object again
            env.beginTransaction(em);
            em.remove(em.find(BasicTypesFieldAccess.class, id));
            env.commitTransactionAndClear(em);
            // insert object with non-null field
            env.beginTransaction(em);
            validator.set(obj);
            em.persist(obj);
            env.commitTransactionAndClear(em);
            verify(true, "no Exception");
            obj = em.find(BasicTypesFieldAccess.class, id);
            verify(!validator.isChanged(obj), fieldName + " not persisted");
            // update unchanged
            env.beginTransaction(em);
            obj = em.find(BasicTypesFieldAccess.class, id);
            obj.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(!obj.postUpdateWasCalled(), "postUpdate was called");
            obj = em.find(BasicTypesFieldAccess.class, id);
            verify(!validator.isChanged(obj), fieldName + " is changed");
            // update changed object
            env.beginTransaction(em);
            obj = em.find(BasicTypesFieldAccess.class, id);
            obj.clearPostUpdate();
            validator.change(obj);
            env.commitTransactionAndClear(em);
            verify(obj.postUpdateWasCalled(), "postUpdate was not called");
            obj = em.find(BasicTypesFieldAccess.class, id);
            verify(validator.isChanged(obj), fieldName + " is unchanged");
            // update to null
            env.beginTransaction(em);
            obj = em.find(BasicTypesFieldAccess.class, id);
            obj.clearPostUpdate();
            validator.setNull(obj);
            env.commitTransactionAndClear(em);
            verify(obj.postUpdateWasCalled(), "postUpdate was not called");
            obj = em.find(BasicTypesFieldAccess.class, id);
            verify(validator.isNull(obj), fieldName + " is not null");
        } finally {
            closeEntityManager(em);
        }
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
            verify(validator.isNull(obj), fieldName + " is not null");
            // delete the object again
            env.beginTransaction(em);
            em.remove(em.find(BasicTypesFieldAccess.class, id));
            env.commitTransactionAndClear(em);
            // insert object with non-null field
            env.beginTransaction(em);
            validator.set(obj);
            em.persist(obj);
            env.commitTransactionAndClear(em);
            verify(true, "no Exception");
            obj = em.find(BasicTypesFieldAccess.class, id);
            verify(!validator.isChanged(obj), fieldName + " not persisted");
            // update unchanged
            env.beginTransaction(em);
            obj = em.find(BasicTypesFieldAccess.class, id);
            obj.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(!obj.postUpdateWasCalled(), "postUpdate was not called");
            obj = em.find(BasicTypesFieldAccess.class, id);
            verify(!validator.isChanged(obj), fieldName + " is changed");
            // update changed
            env.beginTransaction(em);
            obj = em.find(BasicTypesFieldAccess.class, id);
            obj.clearPostUpdate();
            validator.setNull(obj);
            env.commitTransactionAndClear(em);
            verify(obj.postUpdateWasCalled(), "postUpdate was not called");
            obj = em.find(BasicTypesFieldAccess.class, id);
            verify(validator.isNull(obj), fieldName + " is not null");
            // update original
            env.beginTransaction(em);
            obj = em.find(BasicTypesFieldAccess.class, id);
            validator.set(obj);
            env.commitTransactionAndClear(em);
            obj = em.find(BasicTypesFieldAccess.class, id);
            verify(!validator.isChanged(obj), fieldName + " not persisted");
            // mutate
            env.beginTransaction(em);
            obj = em.find(BasicTypesFieldAccess.class, id);
            obj.clearPostUpdate();
            validator.mutate(obj);
            env.commitTransactionAndClear(em);
            verify(obj.postUpdateWasCalled(), "postUpdate was not called");
            obj = em.find(BasicTypesFieldAccess.class, id);
            verify(validator.isChanged(obj), fieldName + " not mutated");
            // update to null
            env.beginTransaction(em);
            obj = em.find(BasicTypesFieldAccess.class, id);
            obj.clearPostUpdate();
            validator.setNull(obj);
            env.commitTransactionAndClear(em);
            verify(obj.postUpdateWasCalled(), "postUpdate was not called");
            obj = em.find(BasicTypesFieldAccess.class, id);
            verify(validator.isNull(obj), fieldName + " is not null");
        } finally {
            closeEntityManager(em);
        }
    }

    // primitive types
    @Test
    public void testPrimitiveBoolean() {
        Validator validator = new Validator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setPrimitiveBoolean(true);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setPrimitiveBoolean(false);
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return obj.isPrimitiveBoolean() != true;
            }
        };
        validatePrimitive(1, validator, "primitiveBoolean");
    }

    @Test
    public void testPrimitiveByte() {
        Validator validator = new Validator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setPrimititveByte((byte) 17);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setPrimititveByte((byte) 23);
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return obj.getPrimititveByte() != 17;
            }
        };
        validatePrimitive(2, validator, "primitiveByte");
    }

    @Test
    public void testPrimitiveChar() {
        Validator validator = new Validator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setPrimitiveChar('A');
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setPrimitiveChar('B');
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return obj.getPrimitiveChar() != 'A';
            }
        };
        validatePrimitive(3, validator, "primitiveChar");
    }

    @Test
    public void testPrimitiveShort() {
        Validator validator = new Validator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setPrimitiveShort((short) 19);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setPrimitiveShort((short) 45);
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return obj.getPrimitiveShort() != 19;
            }
        };
        validatePrimitive(4, validator, "primitiveShort");
    }

    @Test
    public void testPrimitiveInt() {
        Validator validator = new Validator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setPrimitiveInt(88);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setPrimitiveInt(77);
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return obj.getPrimitiveInt() != 88;
            }
        };
        validatePrimitive(5, validator, "primitiveInt");
    }

    @Test
    public void testPrimitiveLong() {
        Validator validator = new Validator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setPrimitiveLong(88);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setPrimitiveLong(77);
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return obj.getPrimitiveLong() != 88;
            }
        };
        validatePrimitive(6, validator, "primitiveLong");
    }

    @Test
    public void testPrimitiveFloat() {
        Validator validator = new Validator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setPrimitiveFloat((float) 88.5);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setPrimitiveFloat((float) 77.5);
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return obj.getPrimitiveFloat() != 88.5;
            }
        };
        validatePrimitive(7, validator, "primitiveFloat");
    }

    @Test
    public void testPrimitiveDouble() {
        Validator validator = new Validator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setPrimitiveDouble(99.5);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setPrimitiveDouble(77.5);
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return obj.getPrimitiveDouble() != 99.5;
            }
        };
        validatePrimitive(8, validator, "primitiveDouble");
    }

    // wrappers of primitive types
    @Test
    public void testWrapperBoolean() {
        ReferenceValidator validator = new ReferenceValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setWrapperBoolean(Boolean.TRUE);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setWrapperBoolean(Boolean.FALSE);
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setWrapperBoolean(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getWrapperBoolean() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !obj.getWrapperBoolean().equals(Boolean.TRUE);
            }
        };
        validateReference(11, validator, "wrapperBoolean");
    }

    @Test
    public void testWrapperByte() {
        ReferenceValidator validator = new ReferenceValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setWrapperByte((byte) 17);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setWrapperByte((byte) 18);
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setWrapperByte(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getWrapperByte() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !obj.getWrapperByte().equals((byte) 17);
            }
        };
        validateReference(12, validator, "wrapperByte");
    }

    @Test
    public void testWrapperCharacter() {
        ReferenceValidator validator = new ReferenceValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setWrapperCharacter('A');
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setWrapperCharacter('B');
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setWrapperCharacter(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getWrapperCharacter() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !obj.getWrapperCharacter().equals('A');
            }
        };
        validateReference(13, validator, "wrapperCharacter");
    }

    @Test
    public void testWrapperShort() {
        ReferenceValidator validator = new ReferenceValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setWrapperShort((short) 1);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setWrapperShort((short) 2);
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setWrapperShort(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getWrapperShort() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !obj.getWrapperShort().equals((short) 1);
            }
        };
        validateReference(14, validator, "wrapperShort");
    }

    @Test
    public void testWrapperInteger() {
        ReferenceValidator validator = new ReferenceValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setWrapperInteger(1);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setWrapperInteger(2);
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setWrapperInteger(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getWrapperInteger() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !obj.getWrapperInteger().equals(1);
            }
        };
        validateReference(15, validator, "wrapperInteger");
    }

    @Test
    public void testWrapperLong() {
        ReferenceValidator validator = new ReferenceValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setWrapperLong(1L);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setWrapperLong(2L);
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setWrapperLong(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getWrapperLong() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !obj.getWrapperLong().equals(1L);
            }
        };
        validateReference(16, validator, "wrapperLong");
    }

    @Test
    public void testWrapperDouble() {
        ReferenceValidator validator = new ReferenceValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setWrapperDouble(1.0);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setWrapperDouble(2.0);
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setWrapperDouble(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getWrapperDouble() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !obj.getWrapperDouble().equals(1.0);
            }
        };
        validateReference(18, validator, "wrapperDouble");
    }

    @Test
    public void testWrapperFloat() {
        ReferenceValidator validator = new ReferenceValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setWrapperFloat(1F);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setWrapperFloat(2F);
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setWrapperFloat(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getWrapperFloat() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !obj.getWrapperFloat().equals(1F);
            }
        };
        validateReference(17, validator, "wrapperFloat");
    }

    // immutable reference types
    @Test
    public void testString2Varchar() {
        ReferenceValidator validator = new ReferenceValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setString2Varchar("VC 1");
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setString2Varchar("VC 2");
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setString2Varchar(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getString2Varchar() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !obj.getString2Varchar().equals("VC 1");
            }
        };
        validateReference(21, validator, "string2Varchar");
    }

    @Test
    public void testString2Clob() {
        ReferenceValidator validator = new ReferenceValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setString2Clob("VC 1");
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setString2Clob("VC 2");
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setString2Clob(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getString2Clob() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !obj.getString2Clob().equals("VC 1");
            }
        };
        validateReference(22, validator, "string2Clob");
    }

    @Test
    public void testBigDecimal() {
        ReferenceValidator validator = new ReferenceValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setBigDecimal(new BigDecimal("1.1"));
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setBigDecimal(new BigDecimal("2.2"));
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setBigDecimal(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getBigDecimal() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return obj.getBigDecimal().compareTo(new BigDecimal("1.1")) != 0;
            }
        };
        validateReference(23, validator, "bigDecimal");
    }

    @Test
    public void testBigInteger() {
        ReferenceValidator validator = new ReferenceValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setBigInteger(new BigInteger("11"));
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setBigInteger(new BigInteger("22"));
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setBigInteger(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getBigInteger() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !obj.getBigInteger().equals(new BigInteger("11"));
            }
        };
        validateReference(24, validator, "bigInteger");
    }

    // mutable types
    @Test
    public void testUtilDate() {
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
        validateMutable(31, validator, "utilDate");
    }

    @Test
    public void testUtilCalendar() {
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
        validateMutable(32, validator, "utilCalendar");
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
        validateMutable(33, validator, "sqlDate");
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
        validateMutable(34, validator, "sqlTime");
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
        validateMutable(35, validator, "sqlTimestamp");
    }

    // arrays
    @Test
    public void testPrimitiveByteArray2Binary() {
        final byte[] UNCHANGED = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 };
        MutableValidator validator = new MutableValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setPrimitiveByteArray2Binary(UNCHANGED);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setPrimitiveByteArray2Binary(new byte[] { 8, 1, 2, 3, 4, 5, 6, 7 });
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setPrimitiveByteArray2Binary(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getPrimitiveByteArray2Binary() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !Arrays.equals(UNCHANGED, obj.getPrimitiveByteArray2Binary());
            }

            @Override
            public void mutate(BasicTypesFieldAccess obj) {
                obj.getPrimitiveByteArray2Binary()[0] = 8;
            }
        };
        validateMutable(41, validator, "primitiveByteArray2Binary");
    }

    @Test
    @Skip(databaseNames = "org.eclipse.persistence.platform.database.MaxDBPlatform")
    public void testPrimitiveByteArray2Longvarbinary() {
        final byte[] UNCHANGED = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 };
        MutableValidator validator = new MutableValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setPrimitiveByteArray2Longvarbinary(UNCHANGED);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setPrimitiveByteArray2Longvarbinary(new byte[] { 8, 1, 2, 3, 4, 5, 6, 7 });
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setPrimitiveByteArray2Longvarbinary(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getPrimitiveByteArray2Longvarbinary() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !Arrays.equals(UNCHANGED, obj.getPrimitiveByteArray2Longvarbinary());
            }

            @Override
            public void mutate(BasicTypesFieldAccess obj) {
                obj.getPrimitiveByteArray2Longvarbinary()[0] = 8;
            }
        };
        validateMutable(42, validator, "primitiveByteArray2Longvarbinary");
    }

    @Test
    public void testPrimitiveByteArray2Blob() {
        final byte[] UNCHANGED = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 };
        MutableValidator validator = new MutableValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setPrimitiveByteArray2Blob(UNCHANGED);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setPrimitiveByteArray2Blob(new byte[] { 8, 1, 2, 3, 4, 5, 6, 7 });
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setPrimitiveByteArray2Blob(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getPrimitiveByteArray2Blob() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !Arrays.equals(UNCHANGED, obj.getPrimitiveByteArray2Blob());
            }

            @Override
            public void mutate(BasicTypesFieldAccess obj) {
                obj.getPrimitiveByteArray2Blob()[0] = 8;
            }
        };
        validateMutable(43, validator, "primitiveByteArray2Blob");
    }

    @Test
    public void testPrimitiveCharArray2Varchar() {
        final char[] UNCHANGED = new char[] { 'U', 'N', 'C', 'H', 'A', 'N', 'G', 'E', 'D' };
        MutableValidator validator = new MutableValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setPrimitiveCharArray2Varchar(UNCHANGED);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setPrimitiveCharArray2Varchar(new char[] { 'C', 'H', 'A', 'N', 'G', 'E', 'D' });
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setPrimitiveCharArray2Varchar(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getPrimitiveCharArray2Varchar() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !Arrays.equals(UNCHANGED, obj.getPrimitiveCharArray2Varchar());
            }

            @Override
            public void mutate(BasicTypesFieldAccess obj) {
                obj.getPrimitiveCharArray2Varchar()[0] = 'X';
            }
        };
        validateMutable(44, validator, "primitiveCharArray2Varchar");
    }

    @Test
    public void testPrimitiveCharArray2Clob() {
        final char[] UNCHANGED = new char[] { 'U', 'N', 'C', 'H', 'A', 'N', 'G', 'E', 'D' };
        MutableValidator validator = new MutableValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setPrimitiveCharArray2Clob(UNCHANGED);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setPrimitiveCharArray2Clob(new char[] { 'C', 'H', 'A', 'N', 'G', 'E', 'D' });
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setPrimitiveCharArray2Clob(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getPrimitiveCharArray2Clob() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !Arrays.equals(UNCHANGED, obj.getPrimitiveCharArray2Clob());
            }

            @Override
            public void mutate(BasicTypesFieldAccess obj) {
                obj.getPrimitiveCharArray2Clob()[0] = 'X';
            }
        };
        validateMutable(45, validator, "primitiveCharArray2Clob");
    }

    @Test
    public void testWrapperByteArray2Binary() {
        final Byte[] UNCHANGED = new Byte[] {(byte) 0, (byte) 1, (byte) 2,
                (byte) 3, (byte) 4, (byte) 5, (byte) 6,
                (byte) 7};
        MutableValidator validator = new MutableValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setWrapperByteArray2Binary(UNCHANGED);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setWrapperByteArray2Binary(new Byte[] {(byte) 8, (byte) 1,
                        (byte) 2, (byte) 3, (byte) 4, (byte) 5,
                        (byte) 6, (byte) 7});
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setWrapperByteArray2Binary(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getWrapperByteArray2Binary() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !Arrays.equals(UNCHANGED, obj.getWrapperByteArray2Binary());
            }

            @Override
            public void mutate(BasicTypesFieldAccess obj) {
                obj.getWrapperByteArray2Binary()[0] = (byte) 8;
            }
        };
        validateMutable(46, validator, "wrapperByteArray2Binary");
    }

    @Test
    public void testWrapperByteArray2Longvarbinary() {
        final Byte[] UNCHANGED = new Byte[] {(byte) 0, (byte) 1, (byte) 2,
                (byte) 3, (byte) 4, (byte) 5, (byte) 6,
                (byte) 7};
        MutableValidator validator = new MutableValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setWrapperByteArray2Longvarbinary(UNCHANGED);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setWrapperByteArray2Longvarbinary(new Byte[] {(byte) 8, (byte) 1,
                        (byte) 2, (byte) 3, (byte) 4, (byte) 5,
                        (byte) 6, (byte) 7});
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setWrapperByteArray2Longvarbinary(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getWrapperByteArray2Longvarbinary() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !Arrays.equals(UNCHANGED, obj.getWrapperByteArray2Longvarbinary());
            }

            @Override
            public void mutate(BasicTypesFieldAccess obj) {
                obj.getWrapperByteArray2Longvarbinary()[0] = (byte) 8;
            }
        };
        validateMutable(47, validator, "wrapperByteArray2Longvarbinary");
    }

    @Test
    public void testWrapperByteArray2Blob() {
        final Byte[] UNCHANGED = new Byte[] {(byte) 0, (byte) 1, (byte) 2,
                (byte) 3, (byte) 4, (byte) 5, (byte) 6,
                (byte) 7};
        MutableValidator validator = new MutableValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setWrapperByteArray2Blob(UNCHANGED);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setWrapperByteArray2Blob(new Byte[] {(byte) 8, (byte) 1,
                        (byte) 2, (byte) 3, (byte) 4, (byte) 5,
                        (byte) 6, (byte) 7});
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setWrapperByteArray2Blob(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getWrapperByteArray2Blob() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !Arrays.equals(UNCHANGED, obj.getWrapperByteArray2Blob());
            }

            @Override
            public void mutate(BasicTypesFieldAccess obj) {
                obj.getWrapperByteArray2Blob()[0] = (byte) 8;
            }
        };
        validateMutable(48, validator, "wrapperByteArray2Blob");
    }

    @SuppressWarnings("boxing")
    @Test
    public void testWrapperCharArray2Varchar() {
        final Character[] UNCHANGED = new Character[] { 'U', 'N', 'C', 'H', 'A', 'N', 'G', 'E', 'D' };
        MutableValidator validator = new MutableValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setWrapperCharacterArray2Varchar(UNCHANGED);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setWrapperCharacterArray2Varchar(new Character[] { 'C', 'H', 'A', 'N', 'G', 'E', 'D' });
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setWrapperCharacterArray2Varchar(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getWrapperCharacterArray2Varchar() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !Arrays.equals(UNCHANGED, obj.getWrapperCharacterArray2Varchar());
            }

            @Override
            public void mutate(BasicTypesFieldAccess obj) {
                obj.getWrapperCharacterArray2Varchar()[0] = 'X';
            }
        };
        validateMutable(49, validator, "wrapperCharArray2Varchar");
    }

    @SuppressWarnings("boxing")
    @Test
    public void testWrapperCharArray2Clob() {
        final Character[] UNCHANGED = new Character[] { 'U', 'N', 'C', 'H', 'A', 'N', 'G', 'E', 'D' };
        MutableValidator validator = new MutableValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setWrapperCharacterArray2Clob(UNCHANGED);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setWrapperCharacterArray2Clob(new Character[] { 'C', 'H', 'A', 'N', 'G', 'E', 'D' });
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setWrapperCharacterArray2Clob(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getWrapperCharacterArray2Clob() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !Arrays.equals(UNCHANGED, obj.getWrapperCharacterArray2Clob());
            }

            @Override
            public void mutate(BasicTypesFieldAccess obj) {
                obj.getWrapperCharacterArray2Clob()[0] = 'X';
            }
        };
        validateMutable(50, validator, "wrapperCharArray2Clob");
    }

    @Test
    public void testSerializable() {
        MutableValidator validator = new MutableValidator() {
            UserDefinedSerializable UNCHANGED = new UserDefinedSerializable("Unchanged");

            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setSerializable(UNCHANGED);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setSerializable(new UserDefinedSerializable("Changed"));
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setSerializable(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getSerializable() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return !UNCHANGED.equals(obj.getSerializable());
            }

            @Override
            public void mutate(BasicTypesFieldAccess obj) {
                ((UserDefinedSerializable) obj.getSerializable()).setTxt("Changed");
            }
        };
        validateMutable(51, validator, "serializable");
    }

    @Test
    public void testEnumString() {
        ReferenceValidator validator = new ReferenceValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setEnumString(UserDefinedEnum.HUGO);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setEnumString(UserDefinedEnum.EMIL);
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setEnumString(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getEnumString() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return obj.getEnumString() != UserDefinedEnum.HUGO;
            }
        };
        validateReference(52, validator, "enumString");
    }

    @Test
    public void testEnumOrdinal() {
        ReferenceValidator validator = new ReferenceValidator() {
            @Override
            public void set(BasicTypesFieldAccess obj) {
                obj.setEnumOrdinal(UserDefinedEnum.HUGO);
            }

            @Override
            public void change(BasicTypesFieldAccess obj) {
                obj.setEnumOrdinal(UserDefinedEnum.EMIL);
            }

            @Override
            public void setNull(BasicTypesFieldAccess obj) {
                obj.setEnumOrdinal(null);
            }

            @Override
            public boolean isNull(BasicTypesFieldAccess obj) {
                return obj.getEnumOrdinal() == null;
            }

            @Override
            public boolean isChanged(BasicTypesFieldAccess obj) {
                return obj.getEnumOrdinal() != UserDefinedEnum.HUGO;
            }
        };
        validateReference(53, validator, "enumOrdinal");
    }

    @Test
    @Bugzilla(bugid=309681)
    public void testNullsFAshort() throws SQLException {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        BasicTypesFieldAccess obj = new BasicTypesFieldAccess(7777);
        try {
            obj.fill();
            env.beginTransaction(em);
            em.persist(obj);
            env.commitTransactionAndClear(em);

            Connection con = env.getDataSource().getConnection();
            try {
                String stmt = "update TMP_BASIC_TYPES_FA set P_SHORT = ? where ID = 7777";
                PreparedStatement pstmt = con.prepareStatement(stmt);
                try {
                    pstmt.setNull(1, Types.SMALLINT);
                    pstmt.executeUpdate();
                } finally {
                    pstmt.close();
                }
                if (!con.getAutoCommit()) {
                    con.commit();
                }
            } finally {
                con.close();
            }

            obj = em.find(BasicTypesFieldAccess.class, 7777);
            flop("missing Exception");
        } catch (PersistenceException iae) {
            // $JL-EXC$ expected behavior
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Bugzilla(bugid = 309681)
    public void testNullsFAint() throws SQLException {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        BasicTypesFieldAccess obj = new BasicTypesFieldAccess(7778);
        try {
            obj.fill();
            env.beginTransaction(em);
            em.persist(obj);
            env.commitTransactionAndClear(em);

            Connection con = env.getDataSource().getConnection();
            try {
                String stmt = "update TMP_BASIC_TYPES_FA set P_INT = ? where ID = 7778";
                PreparedStatement pstmt = con.prepareStatement(stmt);
                try {
                    pstmt.setNull(1, Types.INTEGER);
                    pstmt.executeUpdate();
                } finally {
                    pstmt.close();
                }
                if (!con.getAutoCommit()) {
                    con.commit();
                }
            } finally {
                con.close();
            }

            obj = em.find(BasicTypesFieldAccess.class, 7778);
            flop("missing exception");
        } catch (PersistenceException iae) {
            // $JL-EXC$ expected behavior
        } finally {
            closeEntityManager(em);
        }
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
