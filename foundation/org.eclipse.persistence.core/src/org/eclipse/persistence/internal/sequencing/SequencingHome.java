/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Collection;

import org.eclipse.persistence.sequencing.SequencingControl;

/**
 * <p>
 * <b>Purpose</b>: Define interface for getting all sequencing interfaces.
 * <p>
 * <b>Description</b>: This interface accessed through
 * DatabaseSession.getSequencingHome() method.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Provides a hub for all sequencing interfaces used by DatabaseSession.
 * </ul>
 * @see org.eclipse.persistence.internal.sessions.DatabaseSessionImpl
 * @see Sequencing
 * @see org.eclipse.persistence.sequencing.SequencingControl
 * @see SequencingServer
 * @see SequencingCallback
 */
public interface SequencingHome extends SequencingLogInOut {
    Sequencing getSequencing();

    SequencingControl getSequencingControl();

    SequencingServer getSequencingServer();

    boolean isSequencingCallbackRequired();

    void onAddDescriptors(Collection descriptors);
}
