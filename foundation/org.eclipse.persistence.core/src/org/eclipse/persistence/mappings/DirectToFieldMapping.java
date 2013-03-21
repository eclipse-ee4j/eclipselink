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
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 ******************************************************************************/  
package org.eclipse.persistence.mappings;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.internal.sessions.AbstractRecord;

/**
 * <b>Purpose</b>: Maps an attribute to the corresponding database field type.
 * The list of field types that are supported by TopLink's direct to field mapping
 * is dependent on the relational database being used.
 * A converter can be used to convert between the object and data type if they do not match.
 *
 * @see org.eclipse.persistence.mappings.converters.Converter
 * @see org.eclipse.persistence.mappings.converters.ObjectTypeConverter
 * @see org.eclipse.persistence.mappings.converters.TypeConversionConverter
 * @see org.eclipse.persistence.mappings.converters.SerializedObjectConverter
 *
 * @author Sati
 * @since TopLink/Java 1.0
 */
public class DirectToFieldMapping extends AbstractDirectMapping implements RelationalMapping {

    /**
     * Default constructor.
     */
    public DirectToFieldMapping() {
        super();
    }

    /**
     * INTERNAL:
     */
    public boolean isRelationalMapping() {
        return true;
    }

    /**
     * PUBLIC:
     * Set the field name in the mapping.
     */
    public void setFieldName(String FieldName) {
        setField(new DatabaseField(FieldName));
    }

    protected void writeValueIntoRow(AbstractRecord row, DatabaseField field, Object fieldValue) {
        row.add(getField(), fieldValue);
    }
}
