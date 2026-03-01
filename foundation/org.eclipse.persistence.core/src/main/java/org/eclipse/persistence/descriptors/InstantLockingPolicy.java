/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.descriptors;

import java.time.Instant;

import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ModifyQuery;

/**
 * Version policy used for optimistic locking with {@link Instant} field.
 */
public class InstantLockingPolicy extends JavaTimeLockingPolicy<Instant> {

    /**
     * Create a new instance of version policy used for optimistic locking
     * with {@link Instant} field.
     * Defaults to using the time retrieved from the server.
     */
    public InstantLockingPolicy() {
        super();
    }

    /**
     * Create a new instance of version policy used for optimistic locking
     * with {@link Instant} field.
     * Defaults to using the time retrieved from the server.
     *
     * @param field the field where the write lock value will be stored
     */
    public InstantLockingPolicy(DatabaseField field) {
        super(field);
    }

    @Override
    int compareJavaTimeLockValues(Instant value1, Instant value2) {
        return value1.compareTo(value2);
    }

    @Override
    Class<Instant> getDefaultJavaTimeLockFieldType() {
        return ClassConstants.TIME_INSTANT;
    }

    @Override
    Instant getBaseJavaTimeValue() {
        // LocalDateTime is immutable so constant is safe
        return Instant.MIN;
    }

    @Override
    Instant getInitialJavaTimeWriteValue(AbstractSession session) {
        switch (getTimeSource()) {
            case Local:
                return Instant.now();
            case Server:
                AbstractSession readSession = session.getSessionForClass(getDescriptor().getJavaClass());
                Platform platform = session.getDatasourcePlatform();
                while (readSession.isUnitOfWork()) {
                    readSession = readSession.getParent()
                            .getSessionForClass(getDescriptor().getJavaClass());
                }
                return platform.convertObject(
                        session.executeQuery(platform.getTimestampQuery()), ClassConstants.TIME_INSTANT);
            default:
                return null;
        }
    }

    @Override
    Instant getNewJavaTimeLockValue(ModifyQuery query) {
        return getInitialJavaTimeWriteValue(query.getSession());
    }

    @Override
    Instant getJavaTimeValueToPutInCache(AbstractRecord row, AbstractSession session) {
        if (isStoredInCache()) {
            return session.getDatasourcePlatform()
                    .convertObject(row.get(getWriteLockField()), ClassConstants.TIME_INSTANT);
        } else {
            return null;
        }
    }

    @Override
    Instant getWriteJavaTimeLockValue(Object domainObject, Object primaryKey, AbstractSession session) {
        Instant writeLockFieldValue = null;
        if (isStoredInCache()) {
            writeLockFieldValue = (Instant) session.getIdentityMapAccessorInstance()
                    .getWriteLockValue(primaryKey, domainObject.getClass(), getDescriptor());
        } else {
            //CR#2281 notStoredInCache prevent ClassCastException
            Object lockValue = lockValueFromObject(domainObject);
            if (lockValue != null) {
                if (lockValue instanceof Instant) {
                    writeLockFieldValue = (Instant) lockValueFromObject(domainObject);
                } else {
                    throw OptimisticLockException.needToMapJavaSqlTimestampWhenStoredInObject();
                }
            }
        }
        return writeLockFieldValue;
    }

    @Override
    boolean isNewerJavaTimeVersion(Instant current, Object domainObject, Object primaryKey, AbstractSession session) {
        Instant writeLockFieldValue;
        if (isStoredInCache()) {
            writeLockFieldValue = (Instant) session.getIdentityMapAccessorInstance()
                    .getWriteLockValue(primaryKey, domainObject.getClass(), getDescriptor());
        } else {
            writeLockFieldValue = (Instant)lockValueFromObject(domainObject);
        }

        return isNewerJavaTimeVersion(current, writeLockFieldValue);

    }

    @Override
    boolean isNewerJavaTimeVersion(AbstractRecord row, Object domainObject, Object primaryKey, AbstractSession session) {
        Instant writeLockFieldValue;
        Instant newWriteLockFieldValue = session.getDatasourcePlatform()
                .convertObject(row.get(getWriteLockField()), ClassConstants.TIME_INSTANT);
        if (isStoredInCache()) {
            writeLockFieldValue = (Instant) session.getIdentityMapAccessorInstance()
                    .getWriteLockValue(primaryKey, domainObject.getClass(), getDescriptor());
        } else {
            writeLockFieldValue = (Instant) lockValueFromObject(domainObject);
        }
        return isNewerJavaTimeVersion(newWriteLockFieldValue, writeLockFieldValue);
    }

    @Override
    boolean isNewerJavaTimeVersion(Instant first, Instant second) {
        // 2.5.1.6 if the write lock value is null, then what ever we have is treated as newer.
        if (first == null) {
            return false;
        }
        // bug 6342382: first is not null, second is null, so we know first>second.
        if (second == null) {
            return true;
        }
        return first.isAfter(second);
    }

    @Override
    public int getVersionDifference(Object currentValue, Object domainObject, Object primaryKeys, AbstractSession session) {
        Instant writeLockFieldValue;
        Instant newWriteLockFieldValue = (Instant)currentValue;
        if (newWriteLockFieldValue == null) {
            return 0;//merge it as either the object is new or being forced merged.
        }
        if (isStoredInCache()) {
            writeLockFieldValue = (Instant) session.getIdentityMapAccessorInstance().getWriteLockValue(primaryKeys, domainObject.getClass(), getDescriptor());
        } else {
            writeLockFieldValue = (Instant) lockValueFromObject(domainObject);
        }
        if ((newWriteLockFieldValue.equals(writeLockFieldValue))) {
            return 0;
        }
        if ((writeLockFieldValue != null) && (!newWriteLockFieldValue.isAfter(writeLockFieldValue))) {
            return -1;
        }
        return 1;
    }

}
