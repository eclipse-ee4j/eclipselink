/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        if ("".equals(dataValue)) {
            return Boolean.TRUE;
        }
        return session.getDatasourcePlatform().getConversionManager().convertObject(dataValue, Boolean.class);
    }

    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        return objectValue;
    }

    @Override
    public void initialize(DatabaseMapping mapping, Session session) {}

    @Override
    public boolean isMutable() {
        return false;
    }
}
