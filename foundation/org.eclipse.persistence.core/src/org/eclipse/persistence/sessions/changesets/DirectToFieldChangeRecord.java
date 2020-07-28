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


/**
 * <p>
 * <b>Purpose</b>: This interface provides public API to the class responsible for holding the change made to a directToFieldMapping.
 * <p>
 * <b>Description</b>: This changeRecord stores the value that the direct to field was changed to.
 */
public interface DirectToFieldChangeRecord extends ChangeRecord {

    /**
     * ADVANCED:
     * Returns the new value assigned during the change
     * @return java.lang.Object
     */
    public Object getNewValue();

    /**
     * ADVANCED:
     * Return the old value of the attribute represented by this ChangeRecord.
     */
    public Object getOldValue();
}
