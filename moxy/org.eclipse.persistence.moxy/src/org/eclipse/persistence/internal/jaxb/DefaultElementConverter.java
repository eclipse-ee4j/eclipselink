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
// bdoughan - July 30/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.internal.jaxb;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.sessions.Session;
import org.w3c.dom.Element;

/**
 * If there is no text node then apply the default value.
 * <pre> @XmlElement(required = true, defaultValue = "default")
 * protected Object a;</pre>
 */
public class DefaultElementConverter implements XMLConverter {

    private String defaultValue;

    public DefaultElementConverter(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public void initialize(DatabaseMapping mapping, Session session) {
    }

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        if(dataValue instanceof Element) {
            Element element = (Element) dataValue;
            if(element.getTextContent().length() == 0) {
                element.setTextContent(defaultValue);
            }
        }
        return dataValue;
    }

    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        return objectValue;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session, XMLUnmarshaller unmarshaller) {
        return convertDataValueToObjectValue(dataValue, session);
    }

    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session, XMLMarshaller marshaller) {
        return convertObjectValueToDataValue(objectValue, session);
    }

}
