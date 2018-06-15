/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     matt.macivor - added as part of fix for SDO 2.1.1 TCK typeHelper test cases
package org.eclipse.persistence.sdo.helper.metadata;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sessions.Session;

public class TypeStringConverter implements XMLConverter {

    private SDOTypeHelper typeHelper;

    public TypeStringConverter(SDOTypeHelper helper) {
        typeHelper = helper;
    }
    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public void initialize(DatabaseMapping mapping, Session session) {

    }

    @Override
    public Object convertObjectValueToDataValue(Object object, Session session, XMLMarshaller marshaller) {
        return convertObjectValueToDataValue(object, session);
    }

    @Override
    public Object convertDataValueToObjectValue(Object value, Session session, XMLUnmarshaller unmarshaller) {
        return convertDataValueToObjectValue(value, session);
    }

    @Override
    public Object convertObjectValueToDataValue(Object object, Session session) {
        if(!(object instanceof SDOType)) {
            return null;
        }
        SDOType type = (SDOType)object;
        String typeString = type.getURI() + "#" + type.getName();

        return typeString;
    }

    @Override
    public Object convertDataValueToObjectValue(Object value, Session session) {
        if(!(value instanceof String)) {
            return null;
        }

        String typeString = (String)value;
        int poundIndex = typeString.indexOf('#');
        String URI = typeString.substring(0, poundIndex);
        String name = typeString.substring(poundIndex + 1);
        SDOType type = (SDOType)this.typeHelper.getType(URI, name);
        return type;
    }


}
