/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package dbws.testing.inlinebinary;

//javase imports
import java.io.StringReader;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//java eXtension imports
import javax.wsdl.WSDLException;

//JUnit4 imports
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.oxm.XMLMarshaller;

//testing imports
import dbws.testing.DBWSTestSuite;

public class InlineBinaryTestSuite extends DBWSTestSuite {

    @BeforeClass
    public static void setUp() throws WSDLException {
        DBWS_BUILDER_XML_USERNAME =
           "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
           "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
             "<properties>" +
               "<property name=\"projectName\">inlinebinary</property>" +
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
               "tableNamePattern=\"inlinebinary\" " +
              "/>" +
           "</dbws-builder>";
        DBWSTestSuite.setUp();
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void findAll() {
        Invocation invocation = new Invocation("findAll_inlinebinaryType");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        Element ec = doc.createElement("inlinebinary-collection");
        doc.appendChild(ec);
        for (Object r : (Vector)result) {
            marshaller.marshal(r, ec);
        }
        Document controlDoc = xmlParser.parse(new StringReader(INLINEBINARY_COLLECTION_XML));
        assertTrue("control document not same as instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String INLINEBINARY_COLLECTION_XML =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<inlinebinary-collection>" +
            "<inlinebinaryType xmlns=\"urn:inlinebinary\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "<id>1</id>" +
                "<name>one</name>" +
                "<b>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAPAQEBAQEBAQEBAQEBAQEB</b>" +
            "</inlinebinaryType>" +
            "<inlinebinaryType xmlns=\"urn:inlinebinary\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "<id>2</id>" +
                "<name>two</name>" +
                "<b>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAPAgICAgICAgICAgICAgIC</b>" +
            "</inlinebinaryType>" +
            "<inlinebinaryType xmlns=\"urn:inlinebinary\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "<id>3</id>" +
                "<name>three</name>" +
                "<b>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAPAwMDAwMDAwMDAwMDAwMD</b>" +
            "</inlinebinaryType>" +
        "</inlinebinary-collection>";
}
