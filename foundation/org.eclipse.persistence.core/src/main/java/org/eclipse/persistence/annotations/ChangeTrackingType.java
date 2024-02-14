/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.annotations;

/**
 * An enum that is used within the {@linkplain ChangeTracking} annotation.
 *
 * @see ChangeTracking
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0
 */
public enum ChangeTrackingType {
    /**
     * Allows change tracking at the attribute
     * level of an object. Objects with changed attributes will be processed in
     * the commit process to include any changes in the results of the commit.
     * <p>
     * Unchanged objects will be ignored.
     */
    ATTRIBUTE,

    /**
     * Allows an object to calculate for itself
     * whether it has changed. Changed objects will be processed in the commit
     * process to include any changes in the results of the commit.
     * <p>
     * Unchanged objects will be ignored.
     */
    OBJECT,

    /**
     * Defers all change detection to the {@linkplain org.eclipse.persistence.sessions.UnitOfWork}'s change detection process.
     * Essentially, the {@linkplain org.eclipse.persistence.internal.sessions.UnitOfWorkImpl#calculateChanges(java.util.Map, org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet, boolean, boolean)}
     * method will run for all objects in a {@linkplain org.eclipse.persistence.sessions.UnitOfWork}.
     * <p>
     * This is the default {@linkplain org.eclipse.persistence.descriptors.changetracking.ObjectChangePolicy}
     */
    DEFERRED,

    /**
     * Will not set any change tracking policy, and the change tracking will be
     * determined at runtime.
     */
    AUTO
}
