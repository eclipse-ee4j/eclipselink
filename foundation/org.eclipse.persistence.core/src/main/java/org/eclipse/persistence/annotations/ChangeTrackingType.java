/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.annotations;

/**
 * An enum that is used within the ChangeTracking annotation.
 *
 * @see org.eclipse.persistence.annotations.ChangeTracking
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0
 */
public enum ChangeTrackingType {
    /**
     * An ATTRIBUTE change tracking type allows change tracking at the attribute
     * level of an object. Objects with changed attributes will be processed in
     * the commit process to include any changes in the results of the commit.
     * Unchanged objects will be ignored.
     */
    ATTRIBUTE,

    /**
     * An OBJECT change tracking policy allows an object to calculate for itself
     * whether it has changed. Changed objects will be processed in the commit
     * process to include any changes in the results of the commit.
     * Unchanged objects will be ignored.
     */
    OBJECT,

    /**
     * A DEFERRED change tracking policy defers all change detection to the
     * UnitOfWork's change detection process. Essentially, the calculateChanges()
     * method will run for all objects in a UnitOfWork.
     * This is the default ObjectChangePolicy
     */
    DEFERRED,

    /**
     * Will not set any change tracking policy, and the change tracking will be
     * determined at runtime.
     */
    AUTO
}
