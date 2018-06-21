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
package org.eclipse.persistence.mappings.converters;

import java.io.Serializable;

import org.eclipse.persistence.core.mappings.converters.CoreConverter;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.sessions.Session;

/**
 * <p><b>Purpose</b>: Conversion interface to allow conversion between object and data types.
 * This can be used in any mapping to convert between the object and data types without requiring code
 * placed in the object model.
 * TopLink provides several common converters, but the application can also define it own.
 *
 * @see DirectToFieldMapping#setConverter(Converter)
 * @see DirectCollectionMapping#setValueConverter(Converter)
 * @see ObjectTypeConverter
 * @see TypeConversionConverter
 *
 * @author James Sutherland
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public interface Converter extends CoreConverter<DatabaseMapping, Session>, Serializable {

    /**
     * PUBLIC:
     * Convert the object's representation of the value to the databases' data representation.
     * For example this could convert between a Calendar Java type and the sql.Time datatype.
     */
    @Override
    Object convertObjectValueToDataValue(Object objectValue, Session session);

    /**
     * PUBLIC:
     * Convert the databases' data representation of the value to the object's representation.
     * For example this could convert between an sql.Time datatype and the Java Calendar type.
     */
    @Override
    Object convertDataValueToObjectValue(Object dataValue, Session session);

    /**
     * PUBLIC:
     * If the converter converts the value to a mutable value, i.e.
     * a value that can have its' parts changed without being replaced,
     * then it must return true.  If the value is not mutable, cannot be changed without
     * replacing the whole value then false must be returned.
     * This is used within the UnitOfWork to determine how to clone.
     */
    public boolean isMutable();

    /**
     * PUBLIC:
     * Allow for any initialization.
     */
    @Override
    void initialize(DatabaseMapping mapping, Session session);
}
