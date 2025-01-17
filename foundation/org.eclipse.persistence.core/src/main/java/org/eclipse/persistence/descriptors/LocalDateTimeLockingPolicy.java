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

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ModifyQuery;
/**
 * Version policy used for optimistic locking with {@link LocalDateTime} field.
 */
public class LocalDateTimeLockingPolicy extends AbstractTsLockingPolicy<LocalDateTime> {

    /**
     * Create a new instance of version policy used for optimistic locking
     * with {@link LocalDateTime} field.
     * Defaults to using the time retrieved from the server.
     */
    public LocalDateTimeLockingPolicy() {
        super();
    }

    /**
     * Create a new instance of version policy used for optimistic locking
     * with {@link LocalDateTime} field.
     * Defaults to using the time retrieved from the server.
     *
     * @param fieldName the field where the write lock value will be stored
     */
    public LocalDateTimeLockingPolicy(String fieldName) {
        super(fieldName);
    }

    /**
     * Create a new instance of version policy used for optimistic locking
     * with {@link LocalDateTime} field.
     * Defaults to using the time retrieved from the server.
     *
     * @param field the field where the write lock value will be stored
     */
    LocalDateTimeLockingPolicy(DatabaseField field) {
        super(field);
    }

    @Override
    int compareTsLockValues(LocalDateTime value1, LocalDateTime value2) {
        return value1.compareTo(value2);
    }

    @Override
    Class<LocalDateTime> getDefaultTsLockFieldType() {
        return ClassConstants.LOCAL_DATETIME;
    }

    @Override
    LocalDateTime getBaseTsValue() {
        // LocalDateTime is immutable so constant is safe
        return LocalDateTime.MIN;
    }

    @Override
    LocalDateTime getInitialTsWriteValue(AbstractSession session) {
        if (usesLocalTime()) {
            return LocalDateTime.now();
        }
        if (usesServerTime()) {
            AbstractSession readSession = session.getSessionForClass(getDescriptor().getJavaClass());
            while (readSession.isUnitOfWork()) {
                readSession = readSession.getParent()
                        .getSessionForClass(getDescriptor().getJavaClass());
            }
            Timestamp ts = readSession.getDatasourceLogin()
                    .getDatasourcePlatform()
                    .getTimestampFromServer(session, readSession.getName());
            return ts.toLocalDateTime();
        }
        return null;
    }

    @Override
    LocalDateTime getNewTsLockValue(ModifyQuery query) {
        return getInitialTsWriteValue(query.getSession());
    }

    @Override
    LocalDateTime getTsValueToPutInCache(AbstractRecord row, AbstractSession session) {
        if (isStoredInCache()) {
            return session.getDatasourcePlatform()
                    .convertObject(row.get(getWriteLockField()), ClassConstants.LOCAL_DATETIME);
        } else {
            return null;
        }
    }

    @Override
    LocalDateTime getWriteTsLockValue(Object domainObject, Object primaryKey, AbstractSession session) {
        LocalDateTime writeLockFieldValue = null;
        if (isStoredInCache()) {
            writeLockFieldValue = (LocalDateTime) session.getIdentityMapAccessorInstance()
                    .getWriteLockValue(primaryKey, domainObject.getClass(), getDescriptor());
        } else {
            //CR#2281 notStoredInCache prevent ClassCastException
            Object lockValue = lockValueFromObject(domainObject);
            if (lockValue != null) {
                if (lockValue instanceof LocalDateTime) {
                    writeLockFieldValue = (LocalDateTime) lockValueFromObject(domainObject);
                } else {
                    throw OptimisticLockException.needToMapJavaSqlTimestampWhenStoredInObject();
                }
            }
        }
        return writeLockFieldValue;
    }

    @Override
    boolean isNewerTsVersion(LocalDateTime current, Object domainObject, Object primaryKey, AbstractSession session) {
        LocalDateTime writeLockFieldValue;
        if (isStoredInCache()) {
            writeLockFieldValue = (LocalDateTime) session.getIdentityMapAccessorInstance()
                    .getWriteLockValue(primaryKey, domainObject.getClass(), getDescriptor());
        } else {
            writeLockFieldValue = (LocalDateTime)lockValueFromObject(domainObject);
        }

        return isNewerTsVersion(current, writeLockFieldValue);

    }

    @Override
    boolean isNewerTsVersion(AbstractRecord row, Object domainObject, Object primaryKey, AbstractSession session) {
        LocalDateTime writeLockFieldValue;
        LocalDateTime newWriteLockFieldValue = session.getDatasourcePlatform()
                .convertObject(row.get(getWriteLockField()), ClassConstants.LOCAL_DATETIME);
        if (isStoredInCache()) {
            writeLockFieldValue = (LocalDateTime) session.getIdentityMapAccessorInstance()
                    .getWriteLockValue(primaryKey, domainObject.getClass(), getDescriptor());
        } else {
            writeLockFieldValue = (LocalDateTime) lockValueFromObject(domainObject);
        }
        return isNewerTsVersion(newWriteLockFieldValue, writeLockFieldValue);
    }

    @Override
    boolean isNewerTsVersion(LocalDateTime first, LocalDateTime second) {
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
        LocalDateTime writeLockFieldValue;
        LocalDateTime newWriteLockFieldValue = (LocalDateTime)currentValue;
        if (newWriteLockFieldValue == null) {
            return 0;//merge it as either the object is new or being forced merged.
        }
        if (isStoredInCache()) {
            writeLockFieldValue = (LocalDateTime) session.getIdentityMapAccessorInstance().getWriteLockValue(primaryKeys, domainObject.getClass(), getDescriptor());
        } else {
            writeLockFieldValue = (LocalDateTime) lockValueFromObject(domainObject);
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
