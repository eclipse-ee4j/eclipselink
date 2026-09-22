/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
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
//     Sebastien Tardif - regression tests for XRServiceFactory secure XML factories
package dbws.testing.xrservicefactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.junit.Test;

import org.eclipse.persistence.internal.xr.XRServiceFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class XRServiceFactorySecureXmlTestSuite {

    static final String NS = "http://example.com/eclipselink/dbws";
    static final String NAMESPACED_XML =
        "<ns:root xmlns:ns=\"" + NS + "\"><ns:child>ok</ns:child></ns:root>";
    static final String SECRET = "SECRET_MARKER_MUST_NOT_APPEAR";

    @Test
    public void documentBuilderParsesNamespacedXml() throws Exception {
        Document doc = parse(NAMESPACED_XML);
        Element root = doc.getDocumentElement();
        assertEquals(NS, root.getNamespaceURI());
        assertEquals("root", root.getLocalName());
        assertEquals("ok", root.getTextContent());
    }

    @Test
    public void documentBuilderDoesNotResolveExternalEntity() throws Exception {
        Path secret = Files.createTempFile("eclipselink-xxe", ".txt");
        try {
            Files.writeString(secret, SECRET, StandardCharsets.UTF_8);
            String xml = "<!DOCTYPE root [<!ENTITY xxe SYSTEM \"" + secret.toUri() + "\">]>"
                + "<ns:root xmlns:ns=\"" + NS + "\"><ns:child>&xxe;</ns:child></ns:root>";
            try {
                Document doc = parse(xml);
                assertFalse("external entity must not be resolved",
                    doc.getDocumentElement().getTextContent().contains(SECRET));
            } catch (SAXException expected) {
                assertFalse(expected.getMessage() != null && expected.getMessage().contains(SECRET));
            }
        } finally {
            Files.deleteIfExists(secret);
        }
    }

    @Test
    public void transformerCopiesNamespacedXml() throws Exception {
        Document doc = parse(NAMESPACED_XML);
        Transformer transformer = XRServiceFactory.getTransformer();
        assertNotNull(transformer);
        StringWriter out = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(out));
        String result = out.toString();
        assertTrue(result.contains(NS));
        assertTrue(result.contains("ok"));
    }

    @Test
    public void transformerDoesNotResolveExternalEntity() throws Exception {
        Path secret = Files.createTempFile("eclipselink-xxe-xslt", ".txt");
        try {
            Files.writeString(secret, SECRET, StandardCharsets.UTF_8);
            String xml = "<!DOCTYPE root [<!ENTITY xxe SYSTEM \"" + secret.toUri() + "\">]>"
                + "<root>&xxe;</root>";
            Transformer transformer = XRServiceFactory.getTransformer();
            assertNotNull(transformer);
            StringWriter out = new StringWriter();
            try {
                transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(out));
                assertFalse("external entity must not be resolved", out.toString().contains(SECRET));
            } catch (TransformerException expected) {
                assertFalse(out.toString().contains(SECRET));
            }
        } finally {
            Files.deleteIfExists(secret);
        }
    }

    private static Document parse(String xml) throws Exception {
        DocumentBuilder builder = XRServiceFactory.getDocumentBuilder();
        assertNotNull(builder);
        return builder.parse(new InputSource(new StringReader(xml)));
    }
}
