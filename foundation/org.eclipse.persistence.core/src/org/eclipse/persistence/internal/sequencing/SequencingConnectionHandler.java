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
 * <b>Purpose</b>: Define interface for getting separate sequencing connection(s)
 * <p>
 * <b>Description</b>:
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Used by SequencingManager only, to obtain separate sequencing connection(s)
 * </ul>
 * @see SequencingManager
 */
interface SequencingConnectionHandler extends SequencingLogInOut {
    public Accessor acquireAccessor();

    public void releaseAccessor(Accessor accessor);
}
