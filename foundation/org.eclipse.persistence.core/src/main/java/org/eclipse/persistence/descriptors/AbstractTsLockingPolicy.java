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
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ModifyQuery;

/**
 * Common timestamp version policy used for optimistic locking.
 */
public abstract class AbstractTsLockingPolicy<T> extends VersionLockingPolicy {

    /** Time from the server. */
    public static final int SERVER_TIME = 1;
    /** Local time. */
    public static final int LOCAL_TIME = 2;

    // Mapping of timestamp class name to corresponding AbstractTsLockingPolicy factory method
    private static final Map<String, LockingPolicySupplier> FACTORY = Map.of(
            Timestamp.class.getName(), AbstractTsLockingPolicy::createTimestampLockingPolicy,
            LocalDateTime.class.getName(), AbstractTsLockingPolicy::createLocalDateTimeLockingPolicy,
            Instant.class.getName(), AbstractTsLockingPolicy::createInstantLockingPolicy
    );

    /**
     * AbstractTsLockingPolicy factory method.
     *
     * @param typeName raw {@code MetadataClass} name
     * @param field version field
     */
    public static AbstractTsLockingPolicy<?> create(String typeName, DatabaseField field) {
        LockingPolicySupplier factory = FACTORY.get(typeName);
        if (factory == null) {
            throw new UnsupportedOperationException(String.format("Cannot create AbstractTsLockingPolicy for %s", typeName));
        }
        return factory.create(field);
    }

    private int retrieveTimeFrom;

    /**
     * Creates an instance of timestamp version policy used for optimistic locking.
     * Defaults to using the time retrieved from the server.
     */
    public AbstractTsLockingPolicy() {
        super();
        this.useServerTime();
    }

    /**
     * Creates an instance of timestamp version policy used for optimistic locking.
     * Defaults to using the time retrieved from the server.
     *
     * @param fieldName the field where the write lock value will be stored
     */
    public AbstractTsLockingPolicy(String fieldName) {
        super(fieldName);
        this.useServerTime();
    }

    /**
     * Creates an instance of timestamp version policy used for optimistic locking.
     * Defaults to using the time retrieved from the server.
     *
     * @param field the field where the write lock value will be stored
     */
    public AbstractTsLockingPolicy(DatabaseField field) {
        super(field);
        this.useServerTime();
    }

    /*
     * Following methods mapping removes unsafe casts in child classes.
     */

    /**
     * This method compares two writeLockValues.
     * The writeLockValues should be non-null and of {@link LocalDateTime},
     * {@link Instant} or {@link Timestamp} type.
     *
     * @param value1 the 1st value to compare
     * @param value2 the 2nd value to compare
     * @return {@code -1} if value1 is less (older) than value2,
     *         {@code 0} if value1 equals value2,
     *         {@code 1} if value1 is greater (newer) than value2.
     * @throws NullPointerException if the passed value is null
     * @throws ClassCastException if the passed value is of a wrong type.
     */
    abstract int compareTsLockValues(T value1, T value2);

    @Override
    @SuppressWarnings("unchecked")
    public int compareWriteLockValues(Object value1, Object value2) {
        return compareTsLockValues((T) value1, (T) value2);
    }

    /**
     * Return the default timestamp locking filed java type.
     *
     * @return the default timestamp locking filed java type
     */
    abstract Class<T> getDefaultTsLockFieldType();

    @Override
    @SuppressWarnings("unchecked")
    protected <C> Class<C> getDefaultLockingFieldType() {
        return (Class<C>) getDefaultTsLockFieldType();
    }

    /**
     * Return base value that is older than all other values, it is used in the place of
     * {@code null} in some situations.
     *
     * @return timestamp base value
     */
    abstract T getBaseTsValue();

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getBaseValue() {
        return (C) getBaseTsValue();
    }

    /**
     * Return initial locking value.
     *
     * @param session the database session
     * @return the initial locking value
     */
    abstract T getInitialTsWriteValue(AbstractSession session);

    @Override
    @SuppressWarnings("unchecked")
    protected <C> C getInitialWriteValue(AbstractSession session) {
        return (C) getInitialTsWriteValue(session);
    }

    /**
     * Returns new write lock value from either the cache or the object stored in the query.
     *
     * @param query modify query
     * @return the new timestamp value
     */
    abstract T getNewTsLockValue(ModifyQuery query);

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getNewLockValue(ModifyQuery query) {
        return (C) getNewTsLockValue(query);
    }

    /**
     * Return the value that should be stored in the identity map.
     * If the value is stored in the object, then return a null.
     *
     * @param row the data row, e.g. database row
     * @param session the database session
     * @return the value that should be stored in the identity map
     */
    abstract T getTsValueToPutInCache(AbstractRecord row, AbstractSession session);

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getValueToPutInCache(AbstractRecord row, AbstractSession session) {
        return (C) getTsValueToPutInCache(row, session);
    }

    /**
     * Return the optimistic lock value for the object.
     *
     * @param domainObject the domain object (entity instance)
     * @param primaryKey the primary key
     * @param session the database session
     * @return the optimistic lock value for the object
     */
    abstract T getWriteTsLockValue(Object domainObject, Object primaryKey, AbstractSession session);

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getWriteLockValue(Object domainObject, Object primaryKey, AbstractSession session) {
        return (C) getWriteTsLockValue(domainObject, primaryKey, session);
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
    abstract boolean isNewerTsVersion(T current, Object domainObject, Object primaryKey, AbstractSession session);

    /**
     * INTERNAL:
     * Compares the value with the value from the object (or cache).
     * Will return true if the currentValue is newer than the domainObject.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean isNewerVersion(Object current, Object domainObject, Object primaryKey, AbstractSession session) {
        return isNewerTsVersion((T) current, domainObject, primaryKey, session);
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
    abstract boolean isNewerTsVersion(AbstractRecord row, Object domainObject, Object primaryKey, AbstractSession session);

    @Override
    public boolean isNewerVersion(AbstractRecord row, Object domainObject, Object primaryKey, AbstractSession session) {
        return isNewerTsVersion(row, domainObject, primaryKey, session);
    }

    /**
     * Compares two version values.
     *
     * @param first first version value
     * @param second second version value
     * @return value of {@code true} if the {@code first} is newer than the {@code second}
     *         or {@code false} otherwise
     */
    abstract boolean isNewerTsVersion(T first, T second) ;

    @Override
    @SuppressWarnings("unchecked")
    public boolean isNewerVersion(Object first, Object second) {
        return isNewerTsVersion((T) first, (T) second);
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
     * Timestamp versioning should not be able to do this.
     * Override the superclass behavior.
     *
     * @param value the source value
     */
    @Override
    protected Number incrementWriteLockValue(Number value) {
        return null;
    }

    /**
     * Set time source policy.
     *
     * @param useServer set this policy to get the time from the server when {@code true}
     *                  or from the local machine when {@code false}
     */
    public void setUsesServerTime(boolean useServer) {
        if (useServer) {
            useServerTime();
        } else {
            useLocalTime();
        }
    }

    /**
     * Set time source policy to get the time from the local machine.
     */
    public void useLocalTime() {
        retrieveTimeFrom = LOCAL_TIME;
    }

    /**
     * Set time source policy to get the time from the server.
     */
    public void useServerTime() {
        retrieveTimeFrom = SERVER_TIME;
    }

    /**
     * Return whether time source policy uses local time.
     *
     * @return value of {@code true} when policy uses local time or {@code false} otherwise.
     */
    public boolean usesLocalTime() {
        return retrieveTimeFrom == LOCAL_TIME;
    }

    /**
     * Return whether time source policy uses server time.
     *
     * @return value of {@code true} when policy uses server time or {@code false} otherwise.
     */
    public boolean usesServerTime() {
        return retrieveTimeFrom == SERVER_TIME;
    }

    // TimestampLockingPolicy factory method
    private static TimestampLockingPolicy createTimestampLockingPolicy(DatabaseField field) {
        return new TimestampLockingPolicy(field);
    }

    // LocalDateTimeLockingPolicy factory method
    private static LocalDateTimeLockingPolicy createLocalDateTimeLockingPolicy(DatabaseField field) {
        return new LocalDateTimeLockingPolicy(field);
    }

    // InstantLockingPolicy factory method
    private static InstantLockingPolicy createInstantLockingPolicy(DatabaseField field) {
        return new InstantLockingPolicy(field);
    }

    // AbstractTsLockingPolicy factory interface
    @FunctionalInterface
    private interface LockingPolicySupplier {
        AbstractTsLockingPolicy<?> create(DatabaseField field);
    }

}
