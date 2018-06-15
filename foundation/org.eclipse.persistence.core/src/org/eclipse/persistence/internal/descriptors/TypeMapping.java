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
package org.eclipse.persistence.internal.descriptors;

import org.eclipse.persistence.mappings.TypedAssociation;

/**
 * <p><b>Purpose</b>: Used to define object-type converter object->data values mapping.
 * This is used for the deployment XML mapping.
 *
 * @author James Sutherland
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class TypeMapping extends TypedAssociation {

    /**
     * Default constructor.
     */
    public TypeMapping() {
        super();
    }

    /**
     * Create an association.
     */
    public TypeMapping(Object key, Object value) {
        super(key, value);
    }
}
