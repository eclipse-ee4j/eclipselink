/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.mappings.querykeys;

/**
 * <p>
 * <b>Purpose</b>: Define an alias to a foreign one to one object.
 * </p>
 */
public class OneToOneQueryKey extends ForeignReferenceQueryKey {

    /**
     * Default constructor.
     */
    public OneToOneQueryKey() {
        super();
    }

    // CR#2466 removed joinCriteria because it is already in ForeignReferenceQueryKey - TW

    /**
     * INTERNAL:
     * override the isOneToOneQueryKey() method in the superclass to return true.
     * @return boolean
     */
    @Override
    public boolean isOneToOneQueryKey() {
        return true;
    }
}
