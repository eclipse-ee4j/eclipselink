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
package org.eclipse.persistence.internal.platform.database.oracle;

import java.sql.Connection;
import java.sql.SQLException;

import org.w3c.dom.Document;

import oracle.jdbc.OracleOpaque;

/**
 * INTERNAL:
 * Used the create an XMLType for XDB support.
 * This avoids having the xdb.jar requieed on the classpath if just the mapping class name is referenced.
 */
public interface XMLTypeFactory {
    Object createXML(Connection connection, Document dom) throws Exception;
    Object createXML(Connection connection, String xml) throws Exception;
    Object createXML(OracleOpaque opaque) throws SQLException;
    Document getDOM(OracleOpaque opaque) throws SQLException;
    String getString(OracleOpaque opaque) throws SQLException;

    boolean isXDBDocument(Object obj);
    Object createXMLTypeBindCallCustomParameter(Object obj);
}
