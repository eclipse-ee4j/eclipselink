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
package org.eclipse.persistence.mappings.querykeys;

/**
 * <p>
 * <b>Purpose</b>: Define an alias to a foreign one to one object.
 * </p>
 */
public class OneToOneQueryKey extends ForeignReferenceQueryKey {
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
