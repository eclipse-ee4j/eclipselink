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
//     Mike Norman - April 21, 2010
//        fix for bug 309002
package dbws.testing.bindingmodel;

//javase imports

//java eXtension imports
import javax.wsdl.WSDLException;

//JUnit4 imports
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.xr.QueryOperation;
import org.eclipse.persistence.internal.xr.SQLQueryHandler;

//domain-specific (test) imports
import dbws.testing.DBWSTestSuite;

public class BindingModelTestSuite extends DBWSTestSuite {

    static final String OPERATION = "numLogXml";
    static final String GROUP_BY_CLAUSE =
        "group by to_char(data, 'HH24') order by to_char(data, 'HH24') asc";

    @BeforeClass
    public static void setUp() throws WSDLException {
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                "<property name=\"projectName\">bindingmodel</property>" +
                "<property name=\"logLevel\">off</property>" +
                "<property name=\"username\">";
        DBWS_BUILDER_XML_PASSWORD =
                "</property><property name=\"password\">";
        DBWS_BUILDER_XML_URL =
                "</property><property name=\"url\">";
        DBWS_BUILDER_XML_DRIVER =
                "</property><property name=\"driver\">";
        DBWS_BUILDER_XML_PLATFORM =
                "</property><property name=\"platformClassname\">";
        DBWS_BUILDER_XML_MAIN =
                "</property>" +
              "</properties>" +
              "<sql " +
                "name=\"" + OPERATION + "\" " +
                "simpleXMLFormatTag=\"logXmlResult\" " +
                "xmlTag=\"numLogXml\" " +
                ">" +
                "<text><![CDATA[select to_char(data, 'HH24') as \"ora\", count(*) as \"num\" from " +
                  "log_xml where trunc(data) = to_date(?, 'YYYY-MM-DD') and tipo = ? " +
                  GROUP_BY_CLAUSE + "]]></text>" +
                  "<binding name=\"data\" type=\"xsd:string\"/>" +
                  "<binding name=\"tipo\" type=\"xsd:string\"/>" +
              "</sql>" +
            "</dbws-builder>";
        builder = null;
        DBWSTestSuite.setUp(".");
    }

    @Test
    public void checkParsedSqlText() {
        QueryOperation queryOperation =
            (QueryOperation)builder.getXrServiceModel().getOperation(OPERATION);
        SQLQueryHandler sqlQueryHandler = (SQLQueryHandler)queryOperation.getQueryHandler();
        String sqlString =  sqlQueryHandler.getSqlString();
        assertTrue("parse SQL text does not contain GROUP BY clause",
            sqlString.contains(GROUP_BY_CLAUSE));
    }
}
