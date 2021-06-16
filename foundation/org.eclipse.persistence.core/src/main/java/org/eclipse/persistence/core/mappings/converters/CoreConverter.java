/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.5 - initial implementation
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
