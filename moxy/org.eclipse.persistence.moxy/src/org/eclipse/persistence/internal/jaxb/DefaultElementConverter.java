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
* bdoughan - July 30/2009 - 2.0 - Initial implementation
******************************************************************************/
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

    public void initialize(DatabaseMapping mapping, Session session) {
    }

    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        if(dataValue instanceof Element) {
            Element element = (Element) dataValue;
            if(element.getTextContent().length() == 0) {
                element.setTextContent(defaultValue);
            }
        }
        return dataValue;
    }

    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        return objectValue;
    }

    public boolean isMutable() {
        return false;
    }

    public Object convertDataValueToObjectValue(Object dataValue, Session session, XMLUnmarshaller unmarshaller) {
        return convertDataValueToObjectValue(dataValue, session);
    }

    public Object convertObjectValueToDataValue(Object objectValue, Session session, XMLMarshaller marshaller) {
        return convertObjectValueToDataValue(objectValue, session);
    }

}