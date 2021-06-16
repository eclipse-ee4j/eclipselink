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
//     11/10/2011-2.4 Guy Pelletier
//       - 357474: Address primaryKey option from tenant discriminator column
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
    @Override
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

    @Override
    protected void writeValueIntoRow(AbstractRecord row, DatabaseField field, Object fieldValue) {
        row.put(getField(), fieldValue);
    }
}
