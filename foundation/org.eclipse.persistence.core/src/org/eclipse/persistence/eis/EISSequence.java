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
package org.eclipse.persistence.eis;

import org.eclipse.persistence.sequencing.QuerySequence;

/**
 * <p>The <code>EISSequence</code> class allows access to sequence resources
 * using custom read (ValueReadQuery) and update (DataModifyQuery) queries and a
 * user specified preallocation size.  This allows sequencing to be performed
 * using stored procedures, and access to sequence resources that are not
 * supported by the other sequencing types provided by TopLink.
 *
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class EISSequence extends QuerySequence {
    public EISSequence() {
        super();
        setShouldSelectBeforeUpdate(true);
    }

    public EISSequence(String name) {
        super(name);
        setShouldSelectBeforeUpdate(true);
    }

    public EISSequence(String name, int size) {
        super(name, size);
        setShouldSelectBeforeUpdate(true);
    }

    public boolean equals(Object obj) {
        if (obj instanceof EISSequence) {
            return equalNameAndSize(this, (EISSequence)obj);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
