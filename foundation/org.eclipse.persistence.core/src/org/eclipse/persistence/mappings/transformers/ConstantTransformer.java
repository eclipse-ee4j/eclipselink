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
package org.eclipse.persistence.mappings.transformers;

import org.eclipse.persistence.sessions.Session;

/**
 * <p><b>Purpose</b>: Allows a field to always be mapped to a constant value.
 * This allows default values to be provided for un-mapped fields.
 * @see FieldTransformer
 * @author  James Sutherland
 * @since   10.1.3
 */
public class ConstantTransformer extends FieldTransformerAdapter {
    protected Object value;

    public ConstantTransformer() {
        super();
    }

    /**
     * PUBLIC:
     * Return a constant transformer for the constant value.
     */
    public ConstantTransformer(Object value) {
        this.value = value;
    }

    /**
     * PUBLIC:
     * Return the value of the constant.
     */
    public Object getValue() {
        return value;
    }

    /**
     * PUBLIC:
     * Set the value of the constant.
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * INTERNAL:
     * Always return the constant value.
     */
    public Object buildFieldValue(Object object, String fieldName, Session session) {
        return value;
    }
}
