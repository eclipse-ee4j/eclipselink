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
package org.eclipse.persistence.mappings.querykeys;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;

/**
 * <p>
 * <b>Purpose</b>: Define an alias to a database field.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Define the field that is being aliased.
 * </ul>
 */
public class DirectQueryKey extends QueryKey {
    DatabaseField field;

    /**
     * INTERNAL:
     * Return the field for the query key.
     */
    public DatabaseField getField() {
        return field;
    }

    /**
     * PUBLIC:
     * Return the field name for the query key.
     */
    public String getFieldName() {
        return getField().getName();
    }

    /**
     * PUBLIC:
     * Return the qualified field name for the query key.
     */
    public String getQualifiedFieldName() {
        return getField().getQualifiedName();
    }

    /**
     * INTERNAL:
     * Initialize any information in the receiver that requires its descriptor.
     * Set the receiver's descriptor back reference.
     * @param descriptor is the owner descriptor of the receiver.
     */
    public void initialize(ClassDescriptor descriptor) {
        super.initialize(descriptor);
        if (!getField().hasTableName()) {
            getField().setTable(descriptor.getDefaultTable());
        }
    }

    /**
     * INTERNAL:
     * override the isDirectQueryKey() method in the superclass to return true.
     * @return boolean
     */
    public boolean isDirectQueryKey() {
        return true;
    }

    /**
     * INTERNAL:
     * Set the field for the query key.
     */
    public void setField(DatabaseField field) {
        this.field = field;
    }

    /**
     * PUBLIC:
     * Set the field name for the query key.
     */
    public void setFieldName(String fieldName) {
        setField(new DatabaseField(fieldName));
    }
}
