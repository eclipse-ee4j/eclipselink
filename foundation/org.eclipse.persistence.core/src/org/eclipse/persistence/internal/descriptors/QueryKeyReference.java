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
package org.eclipse.persistence.internal.descriptors;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.Association;

/**
 * <p><b>Purpose</b>: Used to define variable 1-1 mapping query key field references.
 * This is used for the deployment XML mapping.
 *
 * @author James Sutherland
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class QueryKeyReference extends Association {

    /**
     * Default constructor.
     */
    public QueryKeyReference() {
        super();
    }

    /**
     * Create an association.
     */
    public QueryKeyReference(DatabaseField field, String queryKey) {
        super(field, queryKey);
    }
}
