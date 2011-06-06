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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.platform;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class XMLComparerTestCases extends OXTestCase {
    private XMLComparer xmlComparer;
    private DocumentBuilder documentBuilder;
    private Document document;

    public XMLComparerTestCases(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        xmlComparer = new XMLComparer();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        documentBuilder = dbf.newDocumentBuilder();
        document = documentBuilder.newDocument();
    }

    public void testNode_BothNull() {
        assertTrue(xmlComparer.isNodeEqual(null, null));
    }

    public void testNode_controlNull() {
        assertFalse(xmlComparer.isNodeEqual(null, document));
    }

    public void testNode_testNull() {
        assertFalse(xmlComparer.isNodeEqual(document, null));
    }

    public void testNode_differentTypes() {
        Text test = document.createTextNode("a");
        assertFalse(xmlComparer.isNodeEqual(document, test));
    }

    public void testAttribute_same1() {
        Attr control = document.createAttribute("a");
        Attr test = document.createAttribute("a");
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testAttribute_same2() {
        Attr control = document.createAttributeNS("a", "b");
        Attr test = document.createAttributeNS("a", "b");
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testAttribute_same3() {
        Attr control = document.createAttribute("a");
        control.setValue("c");
        Attr test = document.createAttribute("a");
        test.setValue("c");
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testAttribute_same4() {
        Attr control = document.createAttributeNS("a", "b");
        control.setValue("c");
        Attr test = document.createAttributeNS("a", "b");
        test.setValue("c");
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testAttribute_same5() {
        Attr control = document.createAttributeNS("ns1:a", "b");
        control.setValue("c");
        Attr test = document.createAttributeNS("ns1:a", "b");
        test.setValue("c");
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testAttribute_diff1() {
        Attr control = document.createAttribute("a");
        Attr test = document.createAttribute("x");
        assertFalse(xmlComparer.isNodeEqual(control, test));

    }

    public void testAttribute_diff2a() {
        Attr control = document.createAttributeNS("a", "b");
        Attr test = document.createAttribute("a");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testAttribute_diff2b() {
        Attr control = document.createAttributeNS("a", "b");
        Attr test = document.createAttributeNS("a", "y");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testAttribute_diff2c() {
        Attr control = document.createAttributeNS("a", "b");
        Attr test = document.createAttributeNS("x", "b");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testAttribute_diff3a() {
        Attr control = document.createAttribute("a");
        control.setValue("c");
        Attr test = document.createAttribute("a");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testAttribute_diff3b() {
        Attr control = document.createAttribute("a");
        control.setValue("c");
        Attr test = document.createAttribute("a");
        test.setValue("z");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testAttribute_diff4a() {
        Attr control = document.createAttributeNS("a", "b");
        control.setValue("c");
        Attr test = document.createAttributeNS("a", "b");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testAttribute_diff4b() {
        Attr control = document.createAttributeNS("a", "b");
        control.setValue("c");
        Attr test = document.createAttributeNS("a", "b");
        test.setValue("z");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testAttribute_diff5a() {
        Attr control = document.createAttributeNS("ns1:a", "b");
        control.setValue("c");
        Attr test = document.createAttributeNS("a", "b");
        test.setValue("c");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testAttribute_diff5b() {
        Attr control = document.createAttributeNS("ns1:a", "b");
        control.setValue("c");
        Attr test = document.createAttributeNS("ns2:a", "b");
        test.setValue("c");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testCDATASection_same() {
        CDATASection control = document.createCDATASection("a");
        CDATASection test = document.createCDATASection("a");
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testCDATASection_diff() {
        CDATASection control = document.createCDATASection("a");
        CDATASection test = document.createCDATASection("x");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testComment_same() {
        Comment control = document.createComment("a");
        Comment test = document.createComment("a");
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testComment_diff() {
        Comment control = document.createComment("a");
        Comment test = document.createComment("b");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testDocument_same1() {
        Document control = documentBuilder.newDocument();
        Document test = documentBuilder.newDocument();
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testDocument_same2() {
        DOMImplementation domImpl = documentBuilder.getDOMImplementation();
        DocumentType controlDocumentType = domImpl.createDocumentType("a", "b", "c");
        Document control = domImpl.createDocument(null, "a", controlDocumentType);
        DocumentType testDocumentType = domImpl.createDocumentType("a", "b", "c");
        Document test = domImpl.createDocument(null, "a", testDocumentType);
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testDocument_diff2a() {
        DOMImplementation domImpl = documentBuilder.getDOMImplementation();
        DocumentType controlDocumentType = domImpl.createDocumentType("a", "b", "c");
        Document control = domImpl.createDocument(null, "a", controlDocumentType);
        Document test = documentBuilder.newDocument();
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testDocument_diff2b() {
        DOMImplementation domImpl = documentBuilder.getDOMImplementation();
        DocumentType controlDocumentType = domImpl.createDocumentType("a", "b", "c");
        Document control = domImpl.createDocument(null, "a", controlDocumentType);
        DocumentType testDocumentType = domImpl.createDocumentType("x", "y", "z");
        Document test = domImpl.createDocument(null, "x", testDocumentType);
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testDocument_diff3() {
        Document control = documentBuilder.newDocument();
        Element child = control.createElement("theroot");
        control.appendChild(child);
        Document test = documentBuilder.newDocument();
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testDocumentFragment_same() {
        DocumentFragment control = document.createDocumentFragment();
        control.appendChild(document.createElement("a1"));
        control.appendChild(document.createElement("a2"));
        DocumentFragment test = document.createDocumentFragment();
        test.appendChild(document.createElement("a1"));
        test.appendChild(document.createElement("a2"));
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testDocumentFragment_diffa() {
        DocumentFragment control = document.createDocumentFragment();
        control.appendChild(document.createElement("a1"));
        control.appendChild(document.createElement("a2"));
        DocumentFragment test = document.createDocumentFragment();
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testDocumentFragment_diffb() {
        DocumentFragment control = document.createDocumentFragment();
        control.appendChild(document.createElement("a1"));
        control.appendChild(document.createElement("a2"));
        DocumentFragment test = document.createDocumentFragment();
        test.appendChild(document.createElement("a1"));
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testDocumentFragment_diffc() {
        DocumentFragment control = document.createDocumentFragment();
        control.appendChild(document.createElement("a1"));
        control.appendChild(document.createElement("a2"));
        DocumentFragment test = document.createDocumentFragment();
        test.appendChild(document.createElement("a2"));
        test.appendChild(document.createElement("a1"));
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testDocumentType_same1() {
        DOMImplementation domImpl = documentBuilder.getDOMImplementation();
        DocumentType control = domImpl.createDocumentType("a", null, null);
        DocumentType test = domImpl.createDocumentType("a", null, null);
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testDocumentType_same2() {
        DOMImplementation domImpl = documentBuilder.getDOMImplementation();
        DocumentType control = domImpl.createDocumentType("a", "b", null);
        DocumentType test = domImpl.createDocumentType("a", "b", null);
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testDocumentType_same3() {
        DOMImplementation domImpl = documentBuilder.getDOMImplementation();
        DocumentType control = domImpl.createDocumentType("a", "b", "c");
        DocumentType test = domImpl.createDocumentType("a", "b", "c");
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testDocumentType_diff1() {
        DOMImplementation domImpl = documentBuilder.getDOMImplementation();
        DocumentType control = domImpl.createDocumentType("a", null, null);
        DocumentType test = domImpl.createDocumentType("x", null, null);
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testDocumentType_diff2a() {
        DOMImplementation domImpl = documentBuilder.getDOMImplementation();
        DocumentType control = domImpl.createDocumentType("a", "b", null);
        DocumentType test = domImpl.createDocumentType("a", null, null);
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testDocumentType_diff2b() {
        DOMImplementation domImpl = documentBuilder.getDOMImplementation();
        DocumentType control = domImpl.createDocumentType("a", "b", null);
        DocumentType test = domImpl.createDocumentType("a", "y", null);
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testDocumentType_diff3a() {
        DOMImplementation domImpl = documentBuilder.getDOMImplementation();
        DocumentType control = domImpl.createDocumentType("a", "b", "c");
        DocumentType test = domImpl.createDocumentType("a", "b", null);
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testDocumentType_diff3b() {
        DOMImplementation domImpl = documentBuilder.getDOMImplementation();
        DocumentType control = domImpl.createDocumentType("a", "b", "c");
        DocumentType test = domImpl.createDocumentType("a", "b", "z");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testElement_same1() {
        Element control = document.createElement("a");
        Element test = document.createElement("a");
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testElement_same2() {
        Element control = document.createElementNS("a", "b");
        Element test = document.createElementNS("a", "b");
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testElement_same3() {
        Element control = document.createElement("a");
        control.setAttribute("a1", "c1");
        control.setAttribute("a2", "c2");
        Element test = document.createElement("a");
        test.setAttribute("a1", "c1");
        test.setAttribute("a2", "c2");
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testElement_same4() {
        Element control = document.createElement("a");
        control.appendChild(document.createElement("a1"));
        control.appendChild(document.createElement("a2"));
        Element test = document.createElement("a");
        test.appendChild(document.createElement("a1"));
        test.appendChild(document.createElement("a2"));
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testElement_same5() {
        Element control = document.createElementNS("ns1:a", "b");
        Element test = document.createElementNS("ns1:a", "b");
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testElement_same6() {
        Element control = document.createElementNS("ns1", "ns1:a");
        control.setAttributeNS("ns1", "ns1:a1", "c1");
        control.setAttributeNS("ns1", "ns1:a2", "c2");
        Element test = document.createElementNS("ns1", "ns1:a");
        test.setAttributeNS("ns1", "ns1:a1", "c1");
        test.setAttributeNS("ns1", "ns1:a2", "c2");
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testElement_diff1() {
        Element control = document.createElement("a");
        Element test = document.createElement("x");
        assertFalse(xmlComparer.isNodeEqual(control, test));

    }

    public void testElement_diff2a() {
        Element control = document.createElementNS("a", "b");
        Element test = document.createElement("a");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testElement_diff2b() {
        Element control = document.createElementNS("a", "b");
        Element test = document.createElementNS("a", "y");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testElement_diff2c() {
        Element control = document.createElementNS("a", "b");
        Element test = document.createElementNS("x", "b");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testElement_diff3a() {
        Element control = document.createElement("a");
        control.setAttribute("a1", "c1");
        control.setAttribute("a2", "c2");
        Element test = document.createElement("a");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testElement_diff3b() {
        Element control = document.createElement("a");
        control.setAttribute("a1", "c1");
        control.setAttribute("a2", "c2");
        Element test = document.createElement("a");
        test.setAttribute("a1", "c1");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testElement_diff3c() {
        Element control = document.createElement("a");
        control.setAttribute("a1", "c1");
        control.setAttribute("a2", "c2");
        Element test = document.createElement("a");
        test.setAttribute("a2", "c2");
        test.setAttribute("a1", "c1");
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testElement_diff4a() {
        Element control = document.createElement("a");
        control.appendChild(document.createElement("a1"));
        control.appendChild(document.createElement("a2"));
        Element test = document.createElement("a");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testElement_diff4b() {
        Element control = document.createElement("a");
        control.appendChild(document.createElement("a1"));
        control.appendChild(document.createElement("a2"));
        Element test = document.createElement("a");
        test.appendChild(document.createElement("a1"));
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testElement_diff4c() {
        Element control = document.createElement("a");
        control.appendChild(document.createElement("a1"));
        control.appendChild(document.createElement("a2"));
        Element test = document.createElement("a");
        test.appendChild(document.createElement("a2"));
        test.appendChild(document.createElement("a1"));
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testElement_diff5a() {
        Element control = document.createElementNS("ns1:a", "b");
        Element test = document.createElementNS("a", "b");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testElement_diff5b() {
        Element control = document.createElementNS("ns1:a", "b");
        Element test = document.createElementNS("ns2:a", "b");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testElement_diff6a() {
        Element control = document.createElementNS("ns1", "ns1:a");
        control.setAttributeNS("ns1", "ns1:a1", "c1");
        control.setAttributeNS("ns1", "ns1:a2", "c2");
        Element test = document.createElementNS("ns1", "ns1:a");
        test.setAttributeNS("ns1", "ns1:a2", "c2");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testElement_diff6b() {
        Element control = document.createElementNS("ns1", "ns1:a");
        control.setAttributeNS("ns1", "ns1:a1", "c1");
        control.setAttributeNS("ns1", "ns1:a2", "c2");
        Element test = document.createElementNS("ns1", "ns1:a");
        test.setAttributeNS("ns1", "ns1:a1", "c2");
        test.setAttributeNS("ns1", "ns1:a2", "c2");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testElement_diff6c() {
        Element control = document.createElementNS("ns1", "ns1:a");
        control.setAttributeNS("ns1", "ns1:a1", "c1");
        control.setAttributeNS("ns1", "ns1:a2", "c2");
        Element test = document.createElementNS("ns1", "ns1:a");
        test.setAttributeNS("ns1", "ns1:a2", "c2");
        test.setAttributeNS("ns1", "ns1:a3", "c3");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testEntityReference_same() {
        EntityReference control = document.createEntityReference("a");
        EntityReference test = document.createEntityReference("a");
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testEntityReference_diff() {
        EntityReference control = document.createEntityReference("a");
        EntityReference test = document.createEntityReference("x");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testProcessingInstruction_same() {
        ProcessingInstruction control = document.createProcessingInstruction("a", "b");
        ProcessingInstruction test = document.createProcessingInstruction("a", "b");
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testProcessingInstruction_diffa() {
        ProcessingInstruction control = document.createProcessingInstruction("a", "b");
        ProcessingInstruction test = document.createProcessingInstruction("a", "y");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testProcessingInstruction_diffb() {
        ProcessingInstruction control = document.createProcessingInstruction("a", "b");
        ProcessingInstruction test = document.createProcessingInstruction("x", "b");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }

    public void testText_same() {
        Text control = document.createTextNode("a");
        Text test = document.createTextNode("a");
        assertTrue(xmlComparer.isNodeEqual(control, test));
    }

    public void testText_diff() {
        Text control = document.createTextNode("a");
        Text test = document.createTextNode("b");
        assertFalse(xmlComparer.isNodeEqual(control, test));
    }
}
