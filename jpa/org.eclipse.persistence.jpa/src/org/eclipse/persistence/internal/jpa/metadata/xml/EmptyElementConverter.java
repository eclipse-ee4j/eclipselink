/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//      Gordon Yorke - Inititial implementation
package org.eclipse.persistence.internal.jpa.metadata.xml;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

/**
 * Converts from empty node to true or false
 *
 * @author Gordon Yorke
 * @since EclipseLink 2.2
 */
public class EmptyElementConverter implements Converter {

    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        if ("".equals(dataValue)) {
            return Boolean.TRUE;
        }
        return session.getDatasourcePlatform().getConversionManager().convertObject(dataValue, Boolean.class);
    }

    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        return objectValue;
    }

    public void initialize(DatabaseMapping mapping, Session session) {}

    public boolean isMutable() {
        return false;
    }
}
