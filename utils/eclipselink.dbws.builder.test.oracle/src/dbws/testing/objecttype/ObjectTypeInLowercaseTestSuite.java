/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package dbws.testing.objecttype;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import javax.wsdl.WSDLException;

import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import dbws.testing.DBWSTestSuite;

public class ObjectTypeInLowercaseTestSuite extends DBWSTestSuite {

    static final String CREATE_ADDRESS_TYPE =
        "create or replace\n" +
        "TYPE address AS OBJECT(\n" +
        "   street  VARCHAR2(100 BYTE),\n" +
        "   houseNo VARCHAR2(100 BYTE),\n" +
        "   city    VARCHAR2(100 BYTE)\n" +
        ")";
    static final String CREATE_PARTNER_TYPE =
        "create or replace\n" +
        "TYPE partner AS OBJECT(\n" +
        "   firstname   VARCHAR2(50 BYTE),\n" +
        "   surname     VARCHAR2(50 BYTE),\n" +
        "   paddress    address\n" +
        ")";
    static final String CREATE_PROCEDURE =
        "create or replace procedure getUserId(userId IN varchar2, userData OUT partner) as\n" +
        "begin\n" +
        "    userData := partner('John', userId, address('Sesame Street', '42', 'Duckburgh'));\n" +
        "end getUserId;";
    static final String DROP_ADDRESS_TYPE =
        "DROP TYPE address";
    static final String DROP_PARTNER_TYPE =
        "DROP TYPE partner";
    static final String DROP_PROCEDURE =
        "DROP PROCEDURE getUserId";

    static boolean ddlCreate = false;
    static boolean ddlDrop = false;
    static boolean ddlDebug = false;

    @BeforeClass
    public static void setUp() throws WSDLException {
        if (conn == null) {
            try {
                conn = buildConnection();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        String ddlCreateProp = System.getProperty(DATABASE_DDL_CREATE_KEY, DEFAULT_DATABASE_DDL_CREATE);
        if ("true".equalsIgnoreCase(ddlCreateProp)) {
            ddlCreate = true;
        }
        String ddlDropProp = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP);
        if ("true".equalsIgnoreCase(ddlDropProp)) {
            ddlDrop = true;
        }
        String ddlDebugProp = System.getProperty(DATABASE_DDL_DEBUG_KEY, DEFAULT_DATABASE_DDL_DEBUG);
        if ("true".equalsIgnoreCase(ddlDebugProp)) {
            ddlDebug = true;
        }
        if (ddlCreate) {
            runDdl(conn, CREATE_ADDRESS_TYPE, ddlDebug);
            runDdl(conn, CREATE_PARTNER_TYPE, ddlDebug);
            runDdl(conn, CREATE_PROCEDURE, ddlDebug);
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                "<properties>" +
                    "<property name=\"projectName\">ObjectTypeInLowercaseTests</property>" +
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
                "<procedure " +
                    "name=\"getUser\" " +
                    "catalogPattern=\"TOPLEVEL\" " +
                    "procedurePattern=\"getUserId\" " +
                    "isAdvancedJDBC=\"true\" " +
                    "returnType=\"partnerType\" " +
                "/>" +
            "</dbws-builder>";
        builder = null;
        DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        String ddlDrop = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP);
        if ("true".equalsIgnoreCase(ddlDrop)) {
            runDdl(conn, DROP_PROCEDURE, ddlDebug);
            runDdl(conn, DROP_PARTNER_TYPE, ddlDebug);
            runDdl(conn, DROP_ADDRESS_TYPE, ddlDebug);
        }
    }

    @Test
    public void CustomTypeTest() {
        Invocation invocation = new Invocation("getUser");
        invocation.setParameter("userId", "Smith");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(EXPECTED_OUTPUT));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String EXPECTED_OUTPUT =
        "<partnerType xmlns=\"urn:ObjectTypeInLowercaseTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<firstname>John</firstname>" +
            "<surname>Smith</surname>" +
            "<paddress>" +
                "<street>Sesame Street</street>" +
                "<houseno>42</houseno>" +
                "<city>Duckburgh</city>" +
            "</paddress>" +
        "</partnerType>";
}
