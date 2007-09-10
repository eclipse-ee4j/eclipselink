/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.mappings;

import org.eclipse.persistence.mappings.converters.*;

/**
 * <p><b>Purpose</b>: The serialized object mapping can be used to store an arbitrary object or set of objects into a database blob field.
 * It uses the Java serializer so the target must be serializable.
 * Note this functionality has been somewhat replaced by SerializedObjectConverter which can be
 * used to obtain the same functionality on DirectToField and DirectCollection mappings.
 *
 * @see SerializedObjectConverter
 *
 * @since TopLink/Java 1.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.mappings.converters.SerializedObjectConverter}
 */
public class SerializedObjectMapping extends DirectToFieldMapping {

    /**
     * PUBLIC:
     * Default constructor.
     */
    public SerializedObjectMapping() {
        setConverter(new SerializedObjectConverter(this));
    }

    /**
     * INTERNAL:
     * Return and cast the converter.
     */
    public SerializedObjectConverter getSerializedObjectConverter() {
        return (SerializedObjectConverter)getConverter();
    }

    /**
     * INTERNAL:
     */
    public boolean isSerializedObjectMapping() {
        return true;
    }
}