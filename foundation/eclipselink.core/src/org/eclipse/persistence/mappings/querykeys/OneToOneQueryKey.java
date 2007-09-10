/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.mappings.querykeys;

import org.eclipse.persistence.expressions.*;

/**
 * <p>
 * <b>Purpose</b>: Define an alias to a foreign one to one object.
 * <p>
 */
public class OneToOneQueryKey extends ForeignReferenceQueryKey {
    // CR#2466 removed joinCriteria because it is already in ForeignReferenceQueryKey - TW

    /**
     * INTERNAL:
     * override the isOneToOneQueryKey() method in the superclass to return true.
     * @return boolean
     */
    public boolean isOneToOneQueryKey() {
        return true;
    }
}