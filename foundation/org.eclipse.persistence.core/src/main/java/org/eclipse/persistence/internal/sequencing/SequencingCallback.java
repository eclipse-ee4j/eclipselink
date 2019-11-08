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
package org.eclipse.persistence.internal.sequencing;

import org.eclipse.persistence.internal.databaseaccess.Accessor;

/**
 * <p>
 * <b>Purpose</b>: Define interface for sequencing callback.
 * <p>
 * <b>Description</b>: Objects implementing this interface
 *  produced by SequencingCallbackFactory.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Provides sequencing callback to be called after transaction.
 * </ul>
 * @see org.eclipse.persistence.sequencing.SequencingControl
 */
public interface SequencingCallback {

    /**
    * INTERNAL:
    * Called after transaction has committed by the object that owns it:
    * Accessor in non-jta case, SynchronizationListener in jta case.
    * Should not be called after rollback.
    */
    void afterCommit(Accessor accessor);
}
