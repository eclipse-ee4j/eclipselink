/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.mappings.transformers;

import org.eclipse.persistence.sessions.Session;

/**
 * <p><b>Purpose</b>: Allows a field to always be mapped to a constant value.
 * This allows default values to be provided for un-mapped fields.
 * @see org.eclipse.persistence.mappings.FieldTransformer
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
