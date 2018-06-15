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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.platform.database.oracle.xdb;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.persistence.internal.platform.database.oracle.XMLTypeFactory;
import org.w3c.dom.Document;

import oracle.jdbc.OracleOpaque;
import oracle.sql.OPAQUE;
import oracle.xdb.XMLType;
import oracle.xdb.dom.XDBDocument;

/**
 * INTERNAL:
 * Used the create an XMLType for XDB support.
 * This avoids having the xdb.jar requieed on the classpath if just the mapping class name is referenced.
 */
public class XMLTypeFactoryImpl implements XMLTypeFactory {

    @Override
    public Object createXML(Connection connection, Document dom) throws Exception {
        return XMLType.createXML(connection, dom);
    }

    @Override
    public Object createXML(Connection connection, String xml) throws Exception {
        return XMLType.createXML(connection, xml);
    }

    @Override
    public Object createXML(OracleOpaque opaque) throws SQLException {
        return createXMLType(opaque);
    }

    @Override
    public Document getDOM(OracleOpaque opaque) throws SQLException {
        XMLType xml = createXMLType(opaque);
        return xml.getDocument();
    }

    @Override
    public String getString(OracleOpaque opaque) throws SQLException {
        XMLType xmlType = createXMLType(opaque);
        String xmlString = xmlType.getStringVal();
        // Oracle 12c appends a \n character to the xml string
        if (xmlString.endsWith("\n")) {
            xmlString = xmlString.substring(0, xmlString.length() - 1);
        }
        xmlType.close();
        return xmlString;
    }

    @Override
    public boolean isXDBDocument(Object obj) {
        return obj instanceof XDBDocument;
    }

    @Override
    public Object createXMLTypeBindCallCustomParameter(Object obj) {
        return new XMLTypeBindCallCustomParameter(obj);
    }

    private XMLType createXMLType(OracleOpaque opaque) throws SQLException {
        return XMLType.createXML((OPAQUE) opaque);
    }
}
