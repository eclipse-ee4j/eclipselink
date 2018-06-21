/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.eclipse.persistence.testing.framework.wdf.Bugzilla;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.Skip;
import org.eclipse.persistence.testing.models.wdf.jpa1.types.BasicTypesPropertyAccess;
import org.eclipse.persistence.testing.models.wdf.jpa1.types.UserDefinedEnum;
import org.eclipse.persistence.testing.models.wdf.jpa1.types.UserDefinedSerializable;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestBasicPropertyTypes extends JPA1Base {

    @Test
    public void testInsert() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            BasicTypesPropertyAccess obj = new BasicTypesPropertyAccess(0);
            obj.fill();
            env.beginTransaction(em);
            em.persist(obj);
            env.commitTransactionAndClear(em);
            verify(true, "no Exception");
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(0));
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
            BasicTypesPropertyAccess obj = new BasicTypesPropertyAccess(id);
            validator.set(obj);
            em.persist(obj);
            env.commitTransactionAndClear(em);
            verify(true, "no Exception");
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            verify(!validator.isChanged(obj), fieldName + " not persisted");
            // update unchanged object
            env.beginTransaction(em);
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            obj.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(!obj.postUpdateWasCalled(), "postUpdate was called -> before image fails");
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            verify(!validator.isChanged(obj), fieldName + " is changed");
            // update changed object
            env.beginTransaction(em);
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            obj.clearPostUpdate();
            validator.change(obj);
            env.commitTransactionAndClear(em);
            verify(obj.postUpdateWasCalled(), "postUpdate was not called -> before image fails");
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            verify(validator.isChanged(obj), fieldName + " is unchanged");
        } finally {
            closeEntityManager(em);
        }
    }

    private void validateReference(final int id, ReferenceValidator validator, String fieldName) {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            BasicTypesPropertyAccess obj = new BasicTypesPropertyAccess(id);
            // insert object with null-field
            env.beginTransaction(em);
            em.persist(obj);
            env.commitTransactionAndClear(em);
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            verify(validator.isNull(obj), fieldName + " is not null");
            // delete the object again
            env.beginTransaction(em);
            em.remove(em.find(BasicTypesPropertyAccess.class, new Integer(id)));
            env.commitTransactionAndClear(em);
            // insert object with non-null field
            env.beginTransaction(em);
            validator.set(obj);
            em.persist(obj);
            env.commitTransactionAndClear(em);
            verify(true, "no Exception");
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            verify(!validator.isChanged(obj), fieldName + " not persisted");
            // update unchanged
            env.beginTransaction(em);
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            obj.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(!obj.postUpdateWasCalled(), "postUpdate was called");
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            verify(!validator.isChanged(obj), fieldName + " is changed");
            // update changed object
            env.beginTransaction(em);
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            obj.clearPostUpdate();
            validator.change(obj);
            env.commitTransactionAndClear(em);
            verify(obj.postUpdateWasCalled(), "postUpdate was not called");
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            verify(validator.isChanged(obj), fieldName + " is unchanged");
            // update to null
            env.beginTransaction(em);
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            obj.clearPostUpdate();
            validator.setNull(obj);
            env.commitTransactionAndClear(em);
            verify(obj.postUpdateWasCalled(), "postUpdate was not called");
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            verify(validator.isNull(obj), fieldName + " is not null");
        } finally {
            closeEntityManager(em);
        }
    }

    private void validateMutable(final int id, MutableValidator validator, String fieldName) {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            BasicTypesPropertyAccess obj = new BasicTypesPropertyAccess(id);
            // insert object with null-field
            env.beginTransaction(em);
            em.persist(obj);
            env.commitTransactionAndClear(em);
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            verify(validator.isNull(obj), fieldName + " is not null");
            // delete the object again
            env.beginTransaction(em);
            em.remove(em.find(BasicTypesPropertyAccess.class, new Integer(id)));
            env.commitTransactionAndClear(em);
            // insert object with non-null field
            env.beginTransaction(em);
            validator.set(obj);
            em.persist(obj);
            env.commitTransactionAndClear(em);
            verify(true, "no Exception");
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            verify(!validator.isChanged(obj), fieldName + " not persisted");
            // update unchanged
            env.beginTransaction(em);
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            obj.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(!obj.postUpdateWasCalled(), "postUpdate was not called");
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            verify(!validator.isChanged(obj), fieldName + " is changed");
            // update changed
            env.beginTransaction(em);
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            obj.clearPostUpdate();
            validator.setNull(obj);
            env.commitTransactionAndClear(em);
            verify(obj.postUpdateWasCalled(), "postUpdate was not called");
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            verify(validator.isNull(obj), fieldName + " is not null");
            // update original
            env.beginTransaction(em);
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            validator.set(obj);
            env.commitTransactionAndClear(em);
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            verify(!validator.isChanged(obj), fieldName + " not persisted");
            // mutate
            env.beginTransaction(em);
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            obj.clearPostUpdate();
            validator.mutate(obj);
            env.commitTransactionAndClear(em);
            verify(obj.postUpdateWasCalled(), "postUpdate was not called");
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            verify(validator.isChanged(obj), fieldName + " not mutated");
            // update to null
            env.beginTransaction(em);
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            obj.clearPostUpdate();
            validator.setNull(obj);
            env.commitTransactionAndClear(em);
            verify(obj.postUpdateWasCalled(), "postUpdate was not called");
            obj = em.find(BasicTypesPropertyAccess.class, new Integer(id));
            verify(validator.isNull(obj), fieldName + " is not null");
        } finally {
            closeEntityManager(em);
        }
    }

    // primitive types
    @Test
    public void testPrimitiveBoolean() {
        Validator validator = new Validator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveBoolean(true);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveBoolean(false);
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return obj.getPrimitiveBoolean() != true;
            }
        };
        validatePrimitive(1, validator, "primitiveBoolean");
    }

    @Test
    public void testPrimitiveByte() {
        Validator validator = new Validator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setPrimititveByte((byte) 17);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setPrimititveByte((byte) 23);
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return obj.getPrimititveByte() != 17;
            }
        };
        validatePrimitive(2, validator, "primitiveByte");
    }

    @Test
    public void testPrimitiveChar() {
        Validator validator = new Validator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveChar('A');
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveChar('B');
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return obj.getPrimitiveChar() != 'A';
            }
        };
        validatePrimitive(3, validator, "primitiveChar");
    }

    @Test
    public void testPrimitiveShort() {
        Validator validator = new Validator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveShort((short) 19);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveShort((short) 45);
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return obj.getPrimitiveShort() != 19;
            }
        };
        validatePrimitive(4, validator, "primitiveShort");
    }

    @Test
    public void testPrimitiveInt() {
        Validator validator = new Validator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveInt(88);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveInt(77);
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return obj.getPrimitiveInt() != 88;
            }
        };
        validatePrimitive(5, validator, "primitiveInt");
    }

    @Test
    public void testPrimitiveLong() {
        Validator validator = new Validator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveLong(88);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveLong(77);
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return obj.getPrimitiveLong() != 88;
            }
        };
        validatePrimitive(6, validator, "primitiveLong");
    }

    @Test
    public void testPrimitiveFloat() {
        Validator validator = new Validator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveFloat((float) 88.5);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveFloat((float) 77.5);
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return obj.getPrimitiveFloat() != 88.5;
            }
        };
        validatePrimitive(7, validator, "primitiveFloat");
    }

    @Test
    public void testPrimitiveDouble() {
        Validator validator = new Validator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveDouble(99.5);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveDouble(77.5);
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return obj.getPrimitiveDouble() != 99.5;
            }
        };
        validatePrimitive(8, validator, "primitiveDouble");
    }

    // wrappers of primitive types
    @Test
    public void testWrapperBoolean() {
        ReferenceValidator validator = new ReferenceValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setWrapperBoolean(Boolean.TRUE);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setWrapperBoolean(Boolean.FALSE);
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setWrapperBoolean(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.isWrapperBoolean() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !obj.isWrapperBoolean().equals(Boolean.TRUE);
            }
        };
        validateReference(11, validator, "wrapperBoolean");
    }

    @Test
    public void testWrapperByte() {
        ReferenceValidator validator = new ReferenceValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setWrapperByte(new Byte((byte) 17));
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setWrapperByte(new Byte((byte) 18));
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setWrapperByte(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getWrapperByte() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !obj.getWrapperByte().equals(new Byte((byte) 17));
            }
        };
        validateReference(12, validator, "wrapperByte");
    }

    @Test
    public void testWrapperCharacter() {
        ReferenceValidator validator = new ReferenceValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setWrapperCharacter(new Character('A'));
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setWrapperCharacter(new Character('B'));
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setWrapperCharacter(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getWrapperCharacter() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !obj.getWrapperCharacter().equals(new Character('A'));
            }
        };
        validateReference(13, validator, "wrapperCharacter");
    }

    @Test
    public void testWrapperShort() {
        ReferenceValidator validator = new ReferenceValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setWrapperShort(new Short((short) 1));
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setWrapperShort(new Short((short) 2));
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setWrapperShort(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getWrapperShort() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !obj.getWrapperShort().equals(new Short((short) 1));
            }
        };
        validateReference(14, validator, "wrapperShort");
    }

    @Test
    public void testWrapperInteger() {
        ReferenceValidator validator = new ReferenceValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setWrapperInteger(new Integer(1));
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setWrapperInteger(new Integer(2));
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setWrapperInteger(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getWrapperInteger() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !obj.getWrapperInteger().equals(new Integer(1));
            }
        };
        validateReference(15, validator, "wrapperInteger");
    }

    @Test
    public void testWrapperLong() {
        ReferenceValidator validator = new ReferenceValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setWrapperLong(new Long(1));
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setWrapperLong(new Long(2));
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setWrapperLong(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getWrapperLong() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !obj.getWrapperLong().equals(new Long(1));
            }
        };
        validateReference(16, validator, "wrapperLong");
    }

    @Test
    public void testWrapperDouble() {
        ReferenceValidator validator = new ReferenceValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setWrapperDouble(new Double(1));
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setWrapperDouble(new Double(2));
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setWrapperDouble(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getWrapperDouble() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !obj.getWrapperDouble().equals(new Double(1));
            }
        };
        validateReference(18, validator, "wrapperDouble");
    }

    @Test
    public void testWrapperFloat() {
        ReferenceValidator validator = new ReferenceValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setWrapperFloat(new Float(1));
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setWrapperFloat(new Float(2));
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setWrapperFloat(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getWrapperFloat() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !obj.getWrapperFloat().equals(new Float(1));
            }
        };
        validateReference(17, validator, "wrapperFloat");
    }

    // immutable reference types
    @Test
    public void testString2Varchar() {
        ReferenceValidator validator = new ReferenceValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setString2Varchar("VC 1");
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setString2Varchar("VC 2");
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setString2Varchar(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getString2Varchar() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !obj.getString2Varchar().equals("VC 1");
            }
        };
        validateReference(21, validator, "string2Varchar");
    }

    @Test
    public void testString2Clob() {
        ReferenceValidator validator = new ReferenceValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setString2Clob("VC 1");
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setString2Clob("VC 2");
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setString2Clob(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getString2Clob() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !obj.getString2Clob().equals("VC 1");
            }
        };
        validateReference(22, validator, "string2Clob");
    }

    @Test
    public void testBigDecimal() {
        ReferenceValidator validator = new ReferenceValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setBigDecimal(new BigDecimal("1.1"));
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setBigDecimal(new BigDecimal("2.2"));
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setBigDecimal(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getBigDecimal() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return obj.getBigDecimal().compareTo(new BigDecimal("1.1")) != 0;
            }
        };
        validateReference(23, validator, "bigDecimal");
    }

    @Test
    public void testBigInteger() {
        ReferenceValidator validator = new ReferenceValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setBigInteger(new BigInteger("11"));
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setBigInteger(new BigInteger("22"));
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setBigInteger(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getBigInteger() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !obj.getBigInteger().equals(new BigInteger("11"));
            }
        };
        validateReference(24, validator, "bigInteger");
    }

    // mutable types
    @Test
    public void testUtilDate() {
        MutableValidator validator = new MutableValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setUtilDate(new Date(1000));
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setUtilDate(new Date(2000));
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setUtilDate(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getUtilDate() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !obj.getUtilDate().equals(new Date(1000));
            }

            public void mutate(BasicTypesPropertyAccess obj) {
                obj.getUtilDate().setTime(2000);
            }
        };
        validateMutable(31, validator, "utilDate");
    }

    @Test
    public void testUtilCalendar() {
        MutableValidator validator = new MutableValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setUtilCalendar(new GregorianCalendar(2005, 9, 8, 10, 49));
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setUtilCalendar(new GregorianCalendar(2005, 9, 9, 10, 49));
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setUtilCalendar(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getUtilCalendar() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !obj.getUtilCalendar().equals(new GregorianCalendar(2005, 9, 8, 10, 49));
            }

            public void mutate(BasicTypesPropertyAccess obj) {
                obj.getUtilCalendar().set(2005, 9, 9);
            }
        };
        validateMutable(32, validator, "utilCalendar");
    }

    @Test
    public void testSqlDate() {
        MutableValidator validator = new MutableValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setSqlDate(java.sql.Date.valueOf("2005-09-08"));
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setSqlDate(java.sql.Date.valueOf("2005-09-09"));
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setSqlDate(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getSqlDate() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !obj.getSqlDate().equals(java.sql.Date.valueOf("2005-09-08"));
            }

            public void mutate(BasicTypesPropertyAccess obj) {
                obj.getSqlDate().setTime(java.sql.Date.valueOf("2005-09-09").getTime());
            }
        };
        validateMutable(33, validator, "sqlDate");
    }

    @Test
    public void testSqlTime() {
        MutableValidator validator = new MutableValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setSqlTime(java.sql.Time.valueOf("10:49:00"));
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setSqlTime(java.sql.Time.valueOf("11:49:00"));
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setSqlTime(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getSqlTime() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !obj.getSqlTime().equals(java.sql.Time.valueOf("10:49:00"));
            }

            public void mutate(BasicTypesPropertyAccess obj) {
                obj.getSqlTime().setTime(java.sql.Time.valueOf("11:49:00").getTime());
            }
        };
        validateMutable(34, validator, "sqlTime");
    }

    @Test
    public void testSqlTimestamp() {
        MutableValidator validator = new MutableValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setSqlTimestamp(new java.sql.Timestamp(1000));
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setSqlTimestamp(new java.sql.Timestamp(2000));
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setSqlTimestamp(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getSqlTimestamp() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !obj.getSqlTimestamp().equals(new java.sql.Timestamp(1000));
            }

            public void mutate(BasicTypesPropertyAccess obj) {
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
            public void set(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveByteArray2Binary(UNCHANGED);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveByteArray2Binary(new byte[] { 8, 1, 2, 3, 4, 5, 6, 7 });
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveByteArray2Binary(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getPrimitiveByteArray2Binary() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !Arrays.equals(UNCHANGED, obj.getPrimitiveByteArray2Binary());
            }

            public void mutate(BasicTypesPropertyAccess obj) {
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
            public void set(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveByteArray2Longvarbinary(UNCHANGED);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveByteArray2Longvarbinary(new byte[] { 8, 1, 2, 3, 4, 5, 6, 7 });
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveByteArray2Longvarbinary(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getPrimitiveByteArray2Longvarbinary() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !Arrays.equals(UNCHANGED, obj.getPrimitiveByteArray2Longvarbinary());
            }

            public void mutate(BasicTypesPropertyAccess obj) {
                obj.getPrimitiveByteArray2Longvarbinary()[0] = 8;
            }
        };
        validateMutable(42, validator, "primitiveByteArray2Longvarbinary");
    }

    @Test
    public void testPrimitiveByteArray2Blob() {
        final byte[] UNCHANGED = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 };
        MutableValidator validator = new MutableValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveByteArray2Blob(UNCHANGED);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveByteArray2Blob(new byte[] { 8, 1, 2, 3, 4, 5, 6, 7 });
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveByteArray2Blob(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getPrimitiveByteArray2Blob() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !Arrays.equals(UNCHANGED, obj.getPrimitiveByteArray2Blob());
            }

            public void mutate(BasicTypesPropertyAccess obj) {
                obj.getPrimitiveByteArray2Blob()[0] = 8;
            }
        };
        validateMutable(43, validator, "primitiveByteArray2Blob");
    }

    @Test
    public void testPrimitiveCharArray2Varchar() {
        final char[] UNCHANGED = new char[] { 'U', 'N', 'C', 'H', 'A', 'N', 'G', 'E', 'D' };
        MutableValidator validator = new MutableValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveCharArray2Varchar(UNCHANGED);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveCharArray2Varchar(new char[] { 'C', 'H', 'A', 'N', 'G', 'E', 'D' });
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveCharArray2Varchar(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getPrimitiveCharArray2Varchar() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !Arrays.equals(UNCHANGED, obj.getPrimitiveCharArray2Varchar());
            }

            public void mutate(BasicTypesPropertyAccess obj) {
                obj.getPrimitiveCharArray2Varchar()[0] = 'X';
            }
        };
        validateMutable(44, validator, "primitiveCharArray2Varchar");
    }

    @Test
    public void testPrimitiveCharArray2Clob() {
        final char[] UNCHANGED = new char[] { 'U', 'N', 'C', 'H', 'A', 'N', 'G', 'E', 'D' };
        MutableValidator validator = new MutableValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveCharArray2Clob(UNCHANGED);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveCharArray2Clob(new char[] { 'C', 'H', 'A', 'N', 'G', 'E', 'D' });
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setPrimitiveCharArray2Clob(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getPrimitiveCharArray2Clob() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !Arrays.equals(UNCHANGED, obj.getPrimitiveCharArray2Clob());
            }

            public void mutate(BasicTypesPropertyAccess obj) {
                obj.getPrimitiveCharArray2Clob()[0] = 'X';
            }
        };
        validateMutable(45, validator, "primitiveCharArray2Clob");
    }

    @Test
    public void testWrapperByteArray2Binary() {
        final Byte[] UNCHANGED = new Byte[] { Byte.valueOf((byte) 0), Byte.valueOf((byte) 1), Byte.valueOf((byte) 2),
                Byte.valueOf((byte) 3), Byte.valueOf((byte) 4), Byte.valueOf((byte) 5), Byte.valueOf((byte) 6),
                Byte.valueOf((byte) 7), };
        MutableValidator validator = new MutableValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setWrapperByteArray2Binary(UNCHANGED);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setWrapperByteArray2Binary(new Byte[] { Byte.valueOf((byte) 8), Byte.valueOf((byte) 1),
                        Byte.valueOf((byte) 2), Byte.valueOf((byte) 3), Byte.valueOf((byte) 4), Byte.valueOf((byte) 5),
                        Byte.valueOf((byte) 6), Byte.valueOf((byte) 7), });
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setWrapperByteArray2Binary(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getWrapperByteArray2Binary() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !Arrays.equals(UNCHANGED, obj.getWrapperByteArray2Binary());
            }

            public void mutate(BasicTypesPropertyAccess obj) {
                obj.getWrapperByteArray2Binary()[0] = Byte.valueOf((byte) 8);
            }
        };
        validateMutable(46, validator, "wrapperByteArray2Binary");
    }

    @Test
    public void testWrapperByteArray2Longvarbinary() {
        final Byte[] UNCHANGED = new Byte[] { Byte.valueOf((byte) 0), Byte.valueOf((byte) 1), Byte.valueOf((byte) 2),
                Byte.valueOf((byte) 3), Byte.valueOf((byte) 4), Byte.valueOf((byte) 5), Byte.valueOf((byte) 6),
                Byte.valueOf((byte) 7), };
        MutableValidator validator = new MutableValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setWrapperByteArray2Longvarbinary(UNCHANGED);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setWrapperByteArray2Longvarbinary(new Byte[] { Byte.valueOf((byte) 8), Byte.valueOf((byte) 1),
                        Byte.valueOf((byte) 2), Byte.valueOf((byte) 3), Byte.valueOf((byte) 4), Byte.valueOf((byte) 5),
                        Byte.valueOf((byte) 6), Byte.valueOf((byte) 7), });
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setWrapperByteArray2Longvarbinary(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getWrapperByteArray2Longvarbinary() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !Arrays.equals(UNCHANGED, obj.getWrapperByteArray2Longvarbinary());
            }

            public void mutate(BasicTypesPropertyAccess obj) {
                obj.getWrapperByteArray2Longvarbinary()[0] = Byte.valueOf((byte) 8);
            }
        };
        validateMutable(47, validator, "wrapperByteArray2Longvarbinary");
    }

    @Test
    public void testWrapperByteArray2Blob() {
        final Byte[] UNCHANGED = new Byte[] { Byte.valueOf((byte) 0), Byte.valueOf((byte) 1), Byte.valueOf((byte) 2),
                Byte.valueOf((byte) 3), Byte.valueOf((byte) 4), Byte.valueOf((byte) 5), Byte.valueOf((byte) 6),
                Byte.valueOf((byte) 7), };
        MutableValidator validator = new MutableValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setWrapperByteArray2Blob(UNCHANGED);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setWrapperByteArray2Blob(new Byte[] { Byte.valueOf((byte) 8), Byte.valueOf((byte) 1),
                        Byte.valueOf((byte) 2), Byte.valueOf((byte) 3), Byte.valueOf((byte) 4), Byte.valueOf((byte) 5),
                        Byte.valueOf((byte) 6), Byte.valueOf((byte) 7) });
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setWrapperByteArray2Blob(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getWrapperByteArray2Blob() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !Arrays.equals(UNCHANGED, obj.getWrapperByteArray2Blob());
            }

            public void mutate(BasicTypesPropertyAccess obj) {
                obj.getWrapperByteArray2Blob()[0] = Byte.valueOf((byte) 8);
            }
        };
        validateMutable(48, validator, "wrapperByteArray2Blob");
    }

    @SuppressWarnings("boxing")
    @Test
    public void testWrapperCharArray2Varchar() {
        final Character[] UNCHANGED = new Character[] { 'U', 'N', 'C', 'H', 'A', 'N', 'G', 'E', 'D' };
        MutableValidator validator = new MutableValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setWrapperCharacterArray2Varchar(UNCHANGED);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setWrapperCharacterArray2Varchar(new Character[] { 'C', 'H', 'A', 'N', 'G', 'E', 'D' });
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setWrapperCharacterArray2Varchar(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getWrapperCharacterArray2Varchar() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !Arrays.equals(UNCHANGED, obj.getWrapperCharacterArray2Varchar());
            }

            public void mutate(BasicTypesPropertyAccess obj) {
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
            public void set(BasicTypesPropertyAccess obj) {
                obj.setWrapperCharacterArray2Clob(UNCHANGED);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setWrapperCharacterArray2Clob(new Character[] { 'C', 'H', 'A', 'N', 'G', 'E', 'D' });
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setWrapperCharacterArray2Clob(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getWrapperCharacterArray2Clob() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !Arrays.equals(UNCHANGED, obj.getWrapperCharacterArray2Clob());
            }

            public void mutate(BasicTypesPropertyAccess obj) {
                obj.getWrapperCharacterArray2Clob()[0] = 'X';
            }
        };
        validateMutable(50, validator, "wrapperCharArray2Clob");
    }

    @Test
    public void testSerializable() {
        MutableValidator validator = new MutableValidator() {
            UserDefinedSerializable UNCHANGED = new UserDefinedSerializable("Unchanged");

            public void set(BasicTypesPropertyAccess obj) {
                obj.setSerializable(UNCHANGED);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setSerializable(new UserDefinedSerializable("Changed"));
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setSerializable(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getSerializable() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return !UNCHANGED.equals(obj.getSerializable());
            }

            public void mutate(BasicTypesPropertyAccess obj) {
                ((UserDefinedSerializable) obj.getSerializable()).setTxt("Changed");
            }
        };
        validateMutable(51, validator, "serializable");
    }

    @Test
    public void testEnumString() {
        ReferenceValidator validator = new ReferenceValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setEnumString(UserDefinedEnum.HUGO);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setEnumString(UserDefinedEnum.EMIL);
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setEnumString(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getEnumString() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return obj.getEnumString() != UserDefinedEnum.HUGO;
            }
        };
        validateReference(52, validator, "enumString");
    }

    @Test
    public void testEnumOrdinal() {
        ReferenceValidator validator = new ReferenceValidator() {
            public void set(BasicTypesPropertyAccess obj) {
                obj.setEnumOrdinal(UserDefinedEnum.HUGO);
            }

            public void change(BasicTypesPropertyAccess obj) {
                obj.setEnumOrdinal(UserDefinedEnum.EMIL);
            }

            public void setNull(BasicTypesPropertyAccess obj) {
                obj.setEnumOrdinal(null);
            }

            public boolean isNull(BasicTypesPropertyAccess obj) {
                return obj.getEnumOrdinal() == null;
            }

            public boolean isChanged(BasicTypesPropertyAccess obj) {
                return obj.getEnumOrdinal() != UserDefinedEnum.HUGO;
            }
        };
        validateReference(53, validator, "enumOrdinal");
    }

    @Test
    @Bugzilla(bugid=309681)
    public void testNullsPAshort() throws SQLException {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        BasicTypesPropertyAccess obj = new BasicTypesPropertyAccess(8888);
        try {
            obj.fill();
            env.beginTransaction(em);
            em.persist(obj);
            env.commitTransactionAndClear(em);

            Connection con = env.getDataSource().getConnection();
            try {
                String stmt = "update TMP_BASIC_TYPES_PA set P_SHORT = ? where ID = 8888";
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

            obj = em.find(BasicTypesPropertyAccess.class, new Integer(8888));
            flop("missing exception");
        } catch (PersistenceException iae) {
            // $JL-EXC$ expected behavior
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Bugzilla(bugid = 309681)
    public void testNullsPAint() throws SQLException {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        BasicTypesPropertyAccess obj = new BasicTypesPropertyAccess(8889);
        try {
            obj.fill();
            env.beginTransaction(em);
            em.persist(obj);
            env.commitTransactionAndClear(em);

            Connection con = env.getDataSource().getConnection();
            try {
                String stmt = "update TMP_BASIC_TYPES_PA set P_INT = ? where ID = 8889";
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

            obj = em.find(BasicTypesPropertyAccess.class, new Integer(8889));
            flop("missing exception");
        } catch (PersistenceException iae) {
            // $JL-EXC$ expected behavior
        } finally {
            closeEntityManager(em);
        }
    }

    private interface Validator {
        void set(BasicTypesPropertyAccess obj);

        void change(BasicTypesPropertyAccess obj);

        boolean isChanged(BasicTypesPropertyAccess obj);
    }

    private interface ReferenceValidator extends Validator {
        boolean isNull(BasicTypesPropertyAccess obj);

        void setNull(BasicTypesPropertyAccess obj);
    }

    private interface MutableValidator extends ReferenceValidator {
        void mutate(BasicTypesPropertyAccess obj);
    }
}
