/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.sequencing;

/**
 * <p>
 * <b>Purpose</b>: Define interface for sequencing callback factory.
 * <p>
 * <b>Description</b>: Instantiated internally by SequencingManager.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Provides sequencing callback to be called after transaction has committed.
 * </ul>
 * @see org.eclipse.persistence.sequencing.SequencingControl
 */
public interface SequencingCallbackFactory {

    /**
    * INTERNAL:
    * Create SequencingCallback.
    */
    SequencingCallback createSequencingCallback();
}
