/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

public class UnmarshalChildElementNSTestCases extends OXTestCase {
    private XMLUnmarshaller xmlUnmarshaller;

    public UnmarshalChildElementNSTestCases(String name) {
        super(name);
    }

    @Override
    public void setUp() {
        MotorcycleProject project = new MotorcycleProject();
        XMLContext xmlContext = new XMLContext(project);
        xmlUnmarshaller = xmlContext.createUnmarshaller();
    }

    public void testUnmarshal() {
        try {
            Element element = getDOMFromStringContent(getInstanceDocumentAsString());

            NodeList nl = element.getElementsByTagNameNS("http://www.example.com", "Motorcycle");
            Node node = nl.item(0);
            xmlUnmarshaller.unmarshal(node);
        } catch (XMLMarshalException xmlmex) {
            xmlmex.printStackTrace();
        }
    }

    private String getInstanceDocumentAsString() {
        String doc = "<ins:InsurableVehicle xmlns:ins=\"http://www.example.com\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:nsx=\"http://www.example.com\">\n" +
        "  <ins:Motorcycle xsi:type=\"nsx:SportBike\">\n" +
        "    <nsx:license-number>123456</nsx:license-number>\n" +
        "    <nsx:engine-size>600</nsx:engine-size>\n" +
        "  </ins:Motorcycle>\n" +
        "</ins:InsurableVehicle>";
        return doc;
    }

    Element getDOMFromStringContent(String s) {
        byte currentXMLBytes[] = s.getBytes();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(currentXMLBytes);

        try {
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            fac.setNamespaceAware(true);
            Document doc = fac.newDocumentBuilder().parse(byteArrayInputStream);
            return doc.getDocumentElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
