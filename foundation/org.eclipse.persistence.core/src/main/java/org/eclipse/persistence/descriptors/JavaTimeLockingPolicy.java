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
import java.time.LocalDateTime;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ModifyQuery;

/**
 * Common {@link VersionLockingPolicy} based on {@code java.time} classes
 * used for optimistic locking.
 */
public abstract class JavaTimeLockingPolicy<T> extends VersionLockingPolicy {

    private TimeSource timeSource;

    /**
     * Creates an instance of timestamp version policy used for optimistic locking.
     * Defaults to using the time retrieved from the server.
     */
    public JavaTimeLockingPolicy() {
        super();
        this.timeSource = TimeSource.Server;
    }

    /**
     * Creates an instance of timestamp version policy used for optimistic locking.
     * Defaults to using the time retrieved from the server.
     *
     * @param field the field where the write lock value will be stored
     */
    public JavaTimeLockingPolicy(DatabaseField field) {
        super(field);
        this.timeSource = TimeSource.Server;
    }

    /*
     * Following methods mapping removes unsafe casts in child classes.
     * Abstract methods are pkg only visible to avoid them in the API.
     */

    /**
     * This method compares two writeLockValues.
     * The writeLockValues should be non-null and of {@link LocalDateTime},
     * or {@link Instant} type.
     *
     * @param value1 the 1st value to compare
     * @param value2 the 2nd value to compare
     * @return {@code -1} if value1 is less (older) than value2,
     *         {@code 0} if value1 equals value2,
     *         {@code 1} if value1 is greater (newer) than value2.
     * @throws NullPointerException if the passed value is null
     * @throws ClassCastException if the passed value is of a wrong type.
     */
    abstract int compareJavaTimeLockValues(T value1, T value2);

    @Override
    @SuppressWarnings("unchecked")
    public int compareWriteLockValues(Object value1, Object value2) {
        return compareJavaTimeLockValues((T) value1, (T) value2);
    }

    /**
     * Return the default timestamp locking filed java type.
     *
     * @return the default timestamp locking filed java type
     */
    abstract Class<T> getDefaultJavaTimeLockFieldType();

    @Override
    @SuppressWarnings("unchecked")
    protected <C> Class<C> getDefaultLockingFieldType() {
        return (Class<C>) getDefaultJavaTimeLockFieldType();
    }

    /**
     * Return base value that is older than all other values, it is used in the place of
     * {@code null} in some situations.
     *
     * @return timestamp base value
     */
    abstract T getBaseJavaTimeValue();

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getBaseValue() {
        return (C) getBaseJavaTimeValue();
    }

    /**
     * Return initial locking value.
     *
     * @param session the database session
     * @return the initial locking value
     */
    abstract T getInitialJavaTimeWriteValue(AbstractSession session);

    @Override
    @SuppressWarnings("unchecked")
    protected <C> C getInitialWriteValue(AbstractSession session) {
        return (C) getInitialJavaTimeWriteValue(session);
    }

    /**
     * Returns new write lock value from either the cache or the object stored in the query.
     *
     * @param query modify query
     * @return the new timestamp value
     */
    abstract T getNewJavaTimeLockValue(ModifyQuery query);

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getNewLockValue(ModifyQuery query) {
        return (C) getNewJavaTimeLockValue(query);
    }

    /**
     * Return the value that should be stored in the identity map.
     * If the value is stored in the object, then return a null.
     *
     * @param row the data row, e.g. database row
     * @param session the database session
     * @return the value that should be stored in the identity map
     */
    abstract T getJavaTimeValueToPutInCache(AbstractRecord row, AbstractSession session);

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getValueToPutInCache(AbstractRecord row, AbstractSession session) {
        return (C) getJavaTimeValueToPutInCache(row, session);
    }

    /**
     * Return the optimistic lock value for the object.
     *
     * @param domainObject the domain object (entity instance)
     * @param primaryKey the primary key
     * @param session the database session
     * @return the optimistic lock value for the object
     */
    abstract T getWriteJavaTimeLockValue(Object domainObject, Object primaryKey, AbstractSession session);

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getWriteLockValue(Object domainObject, Object primaryKey, AbstractSession session) {
        return (C) getWriteJavaTimeLockValue(domainObject, primaryKey, session);
    }

    /**
     * Compares two version values from the current value and from the object (or cache).
     *
     * @param current the current value
     * @param domainObject the domain object (entity instance)
     * @param primaryKey the primary key
     * @param session the database session
     * @return value of {@code true} if the {@code first} is newer than the {@code second}
     *         or {@code false} otherwise
     */
    abstract boolean isNewerJavaTimeVersion(T current, Object domainObject, Object primaryKey, AbstractSession session);

    /**
     * Compares the value with the value from the object (or cache).
     * Will return true if the currentValue is newer than the domainObject.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean isNewerVersion(Object current, Object domainObject, Object primaryKey, AbstractSession session) {
        return isNewerJavaTimeVersion((T) current, domainObject, primaryKey, session);
    }

    /**
     * Compares two version values from the row and from the object (or cache).
     *
     * @param row the data row, e.g. database row
     * @param domainObject the domain object (entity instance)
     * @param primaryKey the primary key
     * @param session the database session
     * @return value of {@code true} if the {@code first} is newer than the {@code second}
     *         or {@code false} otherwise
     */
    abstract boolean isNewerJavaTimeVersion(AbstractRecord row, Object domainObject, Object primaryKey, AbstractSession session);

    @Override
    public boolean isNewerVersion(AbstractRecord row, Object domainObject, Object primaryKey, AbstractSession session) {
        return isNewerJavaTimeVersion(row, domainObject, primaryKey, session);
    }

    /**
     * Compares two version values.
     *
     * @param first first version value
     * @param second second version value
     * @return value of {@code true} if the {@code first} is newer than the {@code second}
     *         or {@code false} otherwise
     */
    abstract boolean isNewerJavaTimeVersion(T first, T second) ;

    @Override
    @SuppressWarnings("unchecked")
    public boolean isNewerVersion(Object first, Object second) {
        return isNewerJavaTimeVersion((T) first, (T) second);
    }

    /**
     * Return an expression that updates the write lock.
     *
     * @param builder the expression builder
     * @param session the database session
     */
    @Override
    public Expression getWriteLockUpdateExpression(ExpressionBuilder builder, AbstractSession session) {
        return builder.currentTimeStamp();
    }

    /**
     * Time-stamp versioning should not be able to do this.
     * Override the superclass behavior.
     *
     * @param value the source value
     */
    @Override
    protected Number incrementWriteLockValue(Number value) {
        return null;
    }

    /**
     * Set time-stamp source policy.
     *
     * @param timeSource set this policy to retrieve the time-stamp from the server
     *                   when set to {@link TimeSource#Server} or from the local machine
     *                   when set to {@link TimeSource#Local}
     */
    public void setTimeSource(TimeSource timeSource) {
        this.timeSource = timeSource;
    }

    /**
     * Get time-stamp source policy.
     *
     * @return the time-stamp source policy
     */
    public TimeSource getTimeSource() {
        return timeSource;
    }

    /**
     * Time-stamp source policy.
     */
    public enum TimeSource  {
        /** Retrieve from the server. */
        Server,
        /** Retrieve from the local machine. */
        Local
    }

}
