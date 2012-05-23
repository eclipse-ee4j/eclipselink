/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.tests.wdf.jpa1.embedded;

import java.util.Date;

import javax.persistence.EntityManager;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.EmbeddedFieldAccess;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.EmbeddedPropertyAccess;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.EmbeddingPropertyAccess;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestEmbeddingWithPropertyAccess extends JPA1Base {

    private static final long MYSQL_TIMESTAMP_PRECISION = 1000;
    private static final long UNCHANGED = (987654321L / MYSQL_TIMESTAMP_PRECISION) * MYSQL_TIMESTAMP_PRECISION;
    private static final long CHANGED = (123456789L / MYSQL_TIMESTAMP_PRECISION) * MYSQL_TIMESTAMP_PRECISION;

    @Test
    public void testInsert() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            EmbeddingPropertyAccess obj = new EmbeddingPropertyAccess(0);
            obj.fill();
            env.beginTransaction(em);
            em.persist(obj);
            env.commitTransactionAndClear(em);
            verify(true, "no Exception");
            obj = em.find(EmbeddingPropertyAccess.class, new Integer(0));
        } finally {
            closeEntityManager(em);
        }
    }

    private EmbeddingPropertyAccess find(EntityManager em, int id) {
        return em.find(EmbeddingPropertyAccess.class, new Integer(id));
    }

    private void validateMutable(final int id, MutableValidator validator, String fieldName) {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            EmbeddingPropertyAccess obj = new EmbeddingPropertyAccess(id);
            // insert object with null-field
            env.beginTransaction(em);
            em.persist(obj);
            env.commitTransactionAndClear(em);
            obj = find(em, id);
            verify(validator.isNull(obj), fieldName + " is not null");
            // delete the object again
            env.beginTransaction(em);
            em.remove(find(em, id));
            env.commitTransactionAndClear(em);
            // insert object with non-null field
            env.beginTransaction(em);
            validator.set(obj);
            em.persist(obj);
            env.commitTransactionAndClear(em);
            verify(true, "no Exception");
            obj = find(em, id);
            verify(!validator.isChanged(obj), fieldName + " not persisted");
            // update unchanged
            env.beginTransaction(em);
            obj = find(em, id);
            obj.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(!obj.postUpdateWasCalled(), "postUpdate was not called");
            obj = find(em, id);
            verify(!validator.isChanged(obj), fieldName + " is changed");
            // update changed
            env.beginTransaction(em);
            obj = find(em, id);
            obj.clearPostUpdate();
            validator.setNull(obj);
            env.commitTransactionAndClear(em);
            verify(obj.postUpdateWasCalled(), "postUpdate was not called");
            obj = find(em, id);
            verify(validator.isNull(obj), fieldName + " is not null");
            // update original
            env.beginTransaction(em);
            obj = find(em, id);
            validator.set(obj);
            env.commitTransactionAndClear(em);
            obj = find(em, id);
            verify(!validator.isChanged(obj), fieldName + " not persisted");
            // mutate
            env.beginTransaction(em);
            obj = find(em, id);
            obj.clearPostUpdate();
            validator.mutate(obj);
            env.commitTransactionAndClear(em);
            verify(obj.postUpdateWasCalled(), "postUpdate was not called");
            obj = find(em, id);
            verify(validator.isChanged(obj), fieldName + " not mutated");
            // update to null
            env.beginTransaction(em);
            obj = find(em, id);
            obj.clearPostUpdate();
            validator.setNull(obj);
            env.commitTransactionAndClear(em);
            verify(obj.postUpdateWasCalled(), "postUpdate was not called");
            obj = find(em, id);
            verify(validator.isNull(obj), fieldName + " is not null");
        } finally {
            closeEntityManager(em);
        }
    }

    /*
     * currently (PFD 21.01.2006) nesting of different access types is not allowed. so i made the test method private
     */
    @SuppressWarnings("unused")
    private void testEmbeddedFieldAccess() {
        MutableValidator validator = new MutableValidator() {
            private final void initEmbedded(final EmbeddingPropertyAccess obj) {
                if (null == obj.getFieldAccess()) {
                    obj.setFieldAccess(new EmbeddedFieldAccess());
                }
            }

            private final void verifyConsistency(EmbeddedFieldAccess field) {
                if (field.retrieveDate() == null) {
                    verify(0L == field.retrieveTime(), "inconsistent value (time=" + field.retrieveTime()
                            + ") in embedded field");
                    return;
                }
                final long dateValue = field.retrieveDate().getTime();
                final long timeValue = field.retrieveTime();
                verify(dateValue == -timeValue, "inconsistent values (date.getTime()=" + dateValue + ", time=" + timeValue
                        + ") in embedded field");
            }

            public void mutate(EmbeddingPropertyAccess obj) {
                initEmbedded(obj);
                verifyConsistency(obj.getFieldAccess());
                if (isNull(obj)) {
                    flop("cant modify an embedded field which is null");
                }
                obj.getFieldAccess().retrieveDate().setTime(CHANGED);
                obj.getFieldAccess().changeTime(-CHANGED);
            }

            public boolean isNull(EmbeddingPropertyAccess obj) {
                initEmbedded(obj);
                verifyConsistency(obj.getFieldAccess());
                return obj.getFieldAccess().retrieveDate() == null && obj.getFieldAccess().retrieveTime() == 0L;
            }

            public void setNull(EmbeddingPropertyAccess obj) {
                initEmbedded(obj);
                verifyConsistency(obj.getFieldAccess());
                obj.getFieldAccess().changeDate(null);
                obj.getFieldAccess().changeTime(0L);
            }

            public void set(EmbeddingPropertyAccess obj) {
                initEmbedded(obj);
                verifyConsistency(obj.getFieldAccess());
                obj.getFieldAccess().changeDate(new Date(UNCHANGED));
                obj.getFieldAccess().changeTime(-UNCHANGED);
            }

            public void change(EmbeddingPropertyAccess obj) {
                initEmbedded(obj);
                verifyConsistency(obj.getFieldAccess());
                obj.getFieldAccess().changeDate(new Date(CHANGED));
                obj.getFieldAccess().changeTime(-CHANGED);
            }

            public boolean isChanged(EmbeddingPropertyAccess obj) {
                initEmbedded(obj);
                verifyConsistency(obj.getFieldAccess());
                if (isNull(obj)) {
                    return true;
                }
                return obj.getFieldAccess().retrieveDate().getTime() != UNCHANGED
                        || obj.getFieldAccess().retrieveTime() != -UNCHANGED;
            }
        };
        validateMutable(47, validator, "fieldAccess");
    }

    @Test
    public void testEmbeddedPropertyAccess() {
        MutableValidator validator = new MutableValidator() {
            private final void initEmbedded(final EmbeddingPropertyAccess obj) {
                if (null == obj.getPropertyAccess()) {
                    obj.setPropertyAccess(new EmbeddedPropertyAccess());
                }
            }

            private final void verifyConsistency(EmbeddedPropertyAccess field) {
                if (field.getDate() == null) {
                    verify(0L == field.getTime(), "inconsistent value (time=" + field.getTime() + ") in embedded field");
                    return;
                }
                final long dateValue = field.getDate().getTime();
                final long timeValue = field.getTime();
                verify(dateValue == -timeValue, "inconsistent values (date.getTime()=" + dateValue + ", time=" + timeValue
                        + ") in embedded field");
            }

            public void mutate(EmbeddingPropertyAccess obj) {
                initEmbedded(obj);
                verifyConsistency(obj.getPropertyAccess());
                obj.getPropertyAccess().setDate(new Date(CHANGED));
                obj.getPropertyAccess().setTime(-CHANGED);
            }

            public boolean isNull(EmbeddingPropertyAccess obj) {
                initEmbedded(obj);
                verifyConsistency(obj.getPropertyAccess());
                return obj.getPropertyAccess().getDate() == null && obj.getPropertyAccess().getTime() == 0L;
            }

            public void setNull(EmbeddingPropertyAccess obj) {
                initEmbedded(obj);
                verifyConsistency(obj.getPropertyAccess());
                obj.getPropertyAccess().setDate(null);
                obj.getPropertyAccess().setTime(0L);
            }

            public void set(EmbeddingPropertyAccess obj) {
                initEmbedded(obj);
                verifyConsistency(obj.getPropertyAccess());
                obj.getPropertyAccess().setDate(new Date(UNCHANGED));
                obj.getPropertyAccess().setTime(-UNCHANGED);
            }

            public void change(EmbeddingPropertyAccess obj) {
                initEmbedded(obj);
                verifyConsistency(obj.getPropertyAccess());
                obj.getPropertyAccess().setDate(new Date(CHANGED));
                obj.getPropertyAccess().setTime(-CHANGED);
            }

            public boolean isChanged(EmbeddingPropertyAccess obj) {
                initEmbedded(obj);
                verifyConsistency(obj.getPropertyAccess());
                if (isNull(obj)) {
                    return true;
                }
                return obj.getPropertyAccess().getDate().getTime() != UNCHANGED
                        || obj.getPropertyAccess().getTime() != -UNCHANGED;
            }
        };
        validateMutable(48, validator, "propertyAccess");
    }

    private interface Validator {
        void set(EmbeddingPropertyAccess obj);

        void change(EmbeddingPropertyAccess obj);

        boolean isChanged(EmbeddingPropertyAccess obj);
    }

    private interface ReferenceValidator extends Validator {
        boolean isNull(EmbeddingPropertyAccess obj);

        void setNull(EmbeddingPropertyAccess obj);
    }

    private interface MutableValidator extends ReferenceValidator {
        void mutate(EmbeddingPropertyAccess obj);
    }
}
