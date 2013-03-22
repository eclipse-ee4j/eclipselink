/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.core.mappings.converters;

import org.eclipse.persistence.core.mappings.CoreMapping;
import org.eclipse.persistence.core.sessions.CoreSession;

/**
 * INTERNAL
 * A abstraction of converter capturing behavior common to all persistence 
 * types.
 */
public interface CoreConverter<
    MAPPING extends CoreMapping, 
    SESSION extends CoreSession> {

    /**
     * PUBLIC:
     * Convert the object's representation of the value to the databases' data representation.
     * For example this could convert between a Calendar Java type and the sql.Time datatype.
     */
    Object convertObjectValueToDataValue(Object objectValue, SESSION session);

    /**
     * PUBLIC:
     * Convert the databases' data representation of the value to the object's representation.
     * For example this could convert between an sql.Time datatype and the Java Calendar type.
     */
    Object convertDataValueToObjectValue(Object dataValue, SESSION session);

    /**
     * PUBLIC:
     * Allow for any initialization.
     */
    void initialize(MAPPING mapping, SESSION session);

}
