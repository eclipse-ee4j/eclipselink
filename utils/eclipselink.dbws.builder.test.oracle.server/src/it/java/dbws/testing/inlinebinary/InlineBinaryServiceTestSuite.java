/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     David McCann - Aug.14, 2012 - 2.4.1 - Initial implementation
package dbws.testing.inlinebinary;

import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.namespace.QName;

import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.soap.SOAPBinding;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.NodeList;

import dbws.testing.DBWSTestSuite;
import static dbws.testing.inlinebinary.InlineBinaryBuilderTestSuite.CREATE_TABLE;
import static dbws.testing.inlinebinary.InlineBinaryBuilderTestSuite.DROP_TABLE;
import static dbws.testing.inlinebinary.InlineBinaryBuilderTestSuite.POPULATE_TABLE;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

/**
 * Tests inline binary
 *
 */
public class InlineBinaryServiceTestSuite extends DBWSTestSuite {
    //static final String b0 = "rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAPAQEBAQEBAQEBAQEBAQEB";
    //static final String b1 = "rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAPAgICAgICAgICAgICAgIC";
    //static final String b2 = "rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAPAwMDAwMDAwMDAwMDAwMD";
    static final String b0 = "AQEBAQEBAQEBAQEBAQEB";
    static final String b1 = "AgICAgICAgICAgICAgIC";
    static final String b2 = "AwMDAwMDAwMDAwMDAwMD";

    static final String SOAP_FINDBYPK_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<findByPrimaryKey_InlinebinaryType xmlns=\"urn:inlinebinaryService\">" +
              "<id>2</id>" +
            "</findByPrimaryKey_InlinebinaryType>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_FINDALL_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<findAll_InlinebinaryType xmlns=\"urn:inlinebinaryService\" />" +
          "</env:Body>" +
        "</env:Envelope>";

    @BeforeClass
    public static void setUp() {
        if (conn == null) {
            try {
                conn = buildConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ddlCreate) {
            runDdl(conn, CREATE_TABLE, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (String s : POPULATE_TABLE) {
                    stmt.addBatch(s);
                }
                stmt.executeBatch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_TABLE, ddlDebug);
        }
    }

    @Test
    public void testService() {
        try {
            QName qname = new QName("urn:inlinebinaryService", "inlinebinaryServicePort");
            Service service = Service.create(new QName("urn:inlinebinary", "inlinebinaryService"));
            service.addPort(qname, SOAPBinding.SOAP11HTTP_BINDING, "http://" + host + ":" + port + "/inlinebinary/inlinebinary");
            Dispatch<SOAPMessage> sourceDispatch = service.createDispatch(qname, SOAPMessage.class, Service.Mode.MESSAGE);

            // TEST FINDALL
            // we expect 3 inline binary elements
            SOAPMessage request = createSOAPMessage(SOAP_FINDALL_REQUEST);
            SOAPMessage response = sourceDispatch.invoke(request);

            NodeList elements = response.getSOAPBody().getElementsByTagName("b");
            assertEquals("findAll failed:  wrong number of inline binary elements returned - expected [3] but was [" + elements.getLength() + "]", 3, elements.getLength());

            String inlineBinary = elements.item(0).getTextContent();
            assertTrue("findAll failed:  unexpected inline binary - expected [" + b0 + "] but was [" + inlineBinary + "]", (inlineBinary != null && inlineBinary.equals(b0)));

            inlineBinary = elements.item(1).getTextContent();
            assertTrue("findAll failed:  unexpected inline binary - expected [" + b1 + "] but was [" + inlineBinary + "]", (inlineBinary != null && inlineBinary.equals(b1)));

            inlineBinary = elements.item(2).getTextContent();
            assertTrue("findAll failed:  unexpected inline binary - expected [" + b2 + "] but was [" + inlineBinary + "]", (inlineBinary != null && inlineBinary.equals(b2)));

            // TEST FINDBYPK
            request = createSOAPMessage(SOAP_FINDBYPK_REQUEST);
            response = sourceDispatch.invoke(request);
            elements = response.getSOAPBody().getElementsByTagName("b");
            assertEquals("findByPk failed:  wrong number of inline binary elements returned - expected [1] but was [" + elements.getLength() + "]", 1, elements.getLength());

            inlineBinary = elements.item(0).getTextContent();
            assertTrue("findByPk failed:  unexpected inline binary - expected [" + b1 + "] but was [" + inlineBinary + "]", (inlineBinary != null && inlineBinary.equals(b1)));
        } catch (Exception x) {
            fail("Service test failed: " + x.getMessage());
        }
    }
}
