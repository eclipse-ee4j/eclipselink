/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - May 2008, created DBWS test package
 ******************************************************************************/
package dbws.testing.attachedbinary;

//javase imports
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//java eXtension imports
import javax.activation.DataHandler;
import javax.wsdl.WSDLException;

//JUnit4 imports
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.dbws.SOAPAttachmentHandler;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.oxm.XMLMarshaller;

//testing imports
import dbws.testing.DBWSTestSuite;

public class AttachedBinaryTestSuite extends DBWSTestSuite {

    @BeforeClass
    public static void setUp() throws WSDLException {
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                "<property name=\"projectName\">attachedbinary</property>" +
                "<property name=\"targetNamespacePrefix\">ab</property>" +
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
              "<table " +
                "schemaPattern=\"%\" " +
                "tableNamePattern=\"attachedbinary\" " +
                ">" +
                "<procedure " +
                  "name=\"getBLOBById\" " +
                  "isCollection=\"false\" " +
                  "returnType=\"ab:attachedbinaryType\" " +
                  "procedurePattern=\"getBLOBById\" " +
                  "binaryAttachment=\"true\" " +
                "/>" +
              "</table>" +
            "</dbws-builder>";
        DBWSTestSuite.setUp();
    }

    public static SOAPAttachmentHandler attachmentHandler = new SOAPAttachmentHandler();

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void findAll() {
        Invocation invocation = new Invocation("findAll_attachedbinaryType");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.setAttachmentMarshaller(attachmentHandler);
        Document doc = xmlPlatform.createDocument();
        Element ec = doc.createElement("attachedbinary-collection");
        doc.appendChild(ec);
        for (Object r : (Vector)result) {
            marshaller.marshal(r, ec);
        }
        Document controlDoc = xmlParser.parse(new StringReader(ATTACHED_BINARY_COLLECTION_XML));
        assertTrue("control document not same as instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String ATTACHED_BINARY_COLLECTION_XML =
        "<?xml version = \"1.0\" encoding = \"UTF-8\"?>" +
        "<attachedbinary-collection>" +
            "<attachedbinaryType xmlns=\"urn:attachedbinary\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "<id>1</id>" +
                "<name>one</name>" +
                "<b>cid:ref1</b>" +
            "</attachedbinaryType>" +
            "<attachedbinaryType xmlns=\"urn:attachedbinary\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "<id>2</id>" +
                "<name>two</name>" +
                "<b>cid:ref2</b>" +
            "</attachedbinaryType>" +
            "<attachedbinaryType xmlns=\"urn:attachedbinary\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "<id>3</id>" +
                "<name>three</name>" +
                "<b>cid:ref3</b>" +
            "</attachedbinaryType>" +
        "</attachedbinary-collection>";

    @Test
    public void getAttachments() throws IOException {
        DataHandler dataHandler = attachmentHandler.getAttachments().get("cid:ref1");
        ByteArrayInputStream bais = (ByteArrayInputStream)dataHandler.getInputStream();
        byte[] ref = new byte[bais.available()];
        int count = bais.read(ref);
        assertEquals("wrong number of bytes returned", 15, count);
        for (int i = 0; i < count; i++) {
            assertTrue("wrong byte value returned", 1 == ref[i]);
        }
        dataHandler = attachmentHandler.getAttachments().get("cid:ref2");
        bais = (ByteArrayInputStream)dataHandler.getInputStream();
        ref = new byte[bais.available()];
        count = bais.read(ref);
        assertEquals("wrong number of bytes returned", 15, count);
        for (int i = 0; i < count; i++) {
            assertTrue("wrong byte value returned", 2 == ref[i]);
        }
        dataHandler = attachmentHandler.getAttachments().get("cid:ref3");
        bais = (ByteArrayInputStream)dataHandler.getInputStream();
        ref = new byte[bais.available()];
        count = bais.read(ref);
        assertEquals("wrong number of bytes returned", 15, count);
        for (int i = 0; i < count; i++) {
            assertTrue("wrong byte value returned", 3 == ref[i]);
        }
    }

    @Test
    public void getBLOBById() throws IOException {
        Invocation invocation = new Invocation("getBLOBById");
        Operation op = xrService.getOperation(invocation.getName());
        invocation.setParameter("pk", 1);
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        DataHandler dataHandler = (DataHandler)result;
        ByteArrayInputStream bais = (ByteArrayInputStream)dataHandler.getInputStream();
        byte[] ref = new byte[bais.available()];
        int count = bais.read(ref);
        assertEquals("wrong number of bytes returned", 15, count);
        for (int i = 0; i < count; i++) {
            assertTrue("wrong byte value returned", 1 == ref[i]);
        }
    }
}
