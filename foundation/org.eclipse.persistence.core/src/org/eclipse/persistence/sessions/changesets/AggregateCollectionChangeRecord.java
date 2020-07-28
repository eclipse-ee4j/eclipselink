/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.sessions.changesets;

import java.util.List;

import org.eclipse.persistence.internal.sessions.ObjectChangeSet;

/**
 * <p>
 * <b>Purpose</b>: Define the Public interface for the Aggregate Collection Change Record.
 * <p>
 * <b>Description</b>: This interface is meant to clarify the public protocol into TopLink.
 */
public interface AggregateCollectionChangeRecord extends ChangeRecord {

    /**
     * ADVANCED:
     * Return the values representing the changed AggregateCollection.
     * @return java.util.Vector
     */
    public List<ObjectChangeSet> getChangedValues();
}
