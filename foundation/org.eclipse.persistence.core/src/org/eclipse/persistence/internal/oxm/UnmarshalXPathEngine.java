/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.oxm;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.oxm.record.XMLEntry;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.platform.xml.XMLNamespaceResolver;
import org.eclipse.persistence.platform.xml.XMLNodeList;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Utility class for finding XML nodes using XPath
 * expressions.</p>
 * @since Oracle TopLink 10.1.3
 */

public class UnmarshalXPathEngine <
     XML_FIELD extends Field
     > {
    
	private static UnmarshalXPathEngine instance = null;
    private XMLPlatform xmlPlatform;

    /**
     * The default constructor creates a platform instance
     */
    public UnmarshalXPathEngine() {
        xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
    }

    /**
     * Return the <code>XPathEngine</code> singleton.
     */
    public static UnmarshalXPathEngine getInstance() {
        if (instance == null) {
            instance = new UnmarshalXPathEngine();
        }
        return instance;
    }

    /**
     * Execute the XPath statement relative to the context node.
     *
     * @param contextNode the node relative to which the XPath statement will be executed
     * @param xmlField the field containing the XPath statement to be executed
     * @param namespaceResolver used to resolve namespace prefixes to the corresponding namespace URI
     * @return the first node located matching the XPath statement
     * @throws XMLPlatformException
     */
    public Object selectSingleNode(Node contextNode, XML_FIELD xmlField, XMLNamespaceResolver xmlNamespaceResolver, boolean checkForXsiNil) throws XMLMarshalException {
        try {
            if (contextNode == null) {
                return null;
            }

            XPathFragment xPathFragment = xmlField.getXPathFragment();

            // allow the platform to handle the advanced case
            if (xPathFragment.shouldExecuteSelectNodes()) {
                return xmlPlatform.selectSingleNodeAdvanced(contextNode, xmlField.getXPath(), xmlNamespaceResolver);
            }
            Object result = selectSingleNode(contextNode, xPathFragment, xmlNamespaceResolver, checkForXsiNil);
            if(result == XMLRecord.noEntry) {
                if(xmlField.getLastXPathFragment().nameIsText() || xmlField.getLastXPathFragment().isAttribute()) {
                    return result;
                } else {
                    return null;
                }
            }
            return result;
        } catch (Exception x) {
            throw XMLMarshalException.invalidXPathString(xmlField.getXPath(), x);
        }
    }

    public Object selectSingleNode(Node contextNode, XML_FIELD xmlField, XMLNamespaceResolver xmlNamespaceResolver) throws XMLMarshalException {
        return this.selectSingleNode(contextNode, xmlField, xmlNamespaceResolver, false);
    }

    private Object selectSingleNode(Node contextNode, XPathFragment xPathFragment, XMLNamespaceResolver xmlNamespaceResolver, boolean checkForXsiNil) {
        Node resultNode = getSingleNode(contextNode, xPathFragment, xmlNamespaceResolver);
        if (checkForXsiNil) {
            // Check for presence of xsi:nil="true"
            String nil = ((Element) contextNode).getAttributeNS(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.SCHEMA_NIL_ATTRIBUTE);

            if (nil.equals(Constants.BOOLEAN_STRING_TRUE)) {
                return XMLRecord.NIL;
            }
        }
        if (resultNode == null) {

     

            if(!xPathFragment.nameIsText()) {
                return XMLRecord.noEntry;
            }
            return null;
        }
        if(xPathFragment.getNextFragment() == null) {
            return resultNode;
        }

        return selectSingleNode(resultNode, xPathFragment.getNextFragment(), xmlNamespaceResolver, checkForXsiNil);
    }

    /**
     * Execute the XPath statement relative to the context node.
     *
     * @param contextNode the node relative to which the XPath statement will be executed
     * @param xmlField the field containing the XPath statement to be executed
     * @param namespaceResolver used to resolve namespace prefixes to the corresponding namespace URI
     * @return a list of nodes matching the XPath statement
     * @throws XMLPlatformException
     */
    public NodeList selectNodes(Node contextNode, XML_FIELD xmlField, XMLNamespaceResolver xmlNamespaceResolver) throws XMLMarshalException {
        return this.selectNodes(contextNode, xmlField, xmlNamespaceResolver, null);
    }

    public NodeList selectNodes(Node contextNode, XML_FIELD xmlField, XMLNamespaceResolver xmlNamespaceResolver, AbstractNullPolicy nullPolicy) throws XMLMarshalException {
        try {
            if (contextNode == null) {
                return null;
            }

            XPathFragment xPathFragment = xmlField.getXPathFragment();

            // allow the platform to handle the advanced case
            if (xPathFragment.shouldExecuteSelectNodes()) {
                return xmlPlatform.selectNodesAdvanced(contextNode, xmlField.getXPath(), xmlNamespaceResolver);
            }
            return selectNodes(contextNode, xPathFragment, xmlNamespaceResolver, nullPolicy);
        } catch (Exception x) {
            throw XMLMarshalException.invalidXPathString(xmlField.getXPath(), x);
        }
    }

    public List<XMLEntry> selectNodes(Node contextNode, List<XML_FIELD> xmlFields, XMLNamespaceResolver xmlNamespaceResolver) throws XMLMarshalException {
        List<XMLEntry> nodes = new ArrayList<XMLEntry>();
        List<XPathFragment> firstFragments = new ArrayList<XPathFragment>(xmlFields.size());
        for(XML_FIELD nextField:xmlFields) {
            firstFragments.add(nextField.getXPathFragment());
        }
        selectNodes(contextNode, firstFragments, xmlNamespaceResolver, nodes);
        return nodes;
    }

    private void selectNodes(Node contextNode, List<XPathFragment> xpathFragments, XMLNamespaceResolver xmlNamespaceResolver, List<XMLEntry> entries) {
        NodeList childNodes = contextNode.getChildNodes();
        for(int i = 0, size = childNodes.getLength(); i < size; i++) {
            Node nextChild = childNodes.item(i);
            List<XPathFragment> matchingFragments = new ArrayList<XPathFragment>();
            for(XPathFragment<XML_FIELD> nextFragment:xpathFragments) {
                if((nextChild.getNodeType() == Node.TEXT_NODE || nextChild.getNodeType() == Node.CDATA_SECTION_NODE) && nextFragment.nameIsText()) {
                    addXMLEntry(nextChild, nextFragment.getXMLField(), entries);
                } else if(nextChild.getNodeType() == Node.ATTRIBUTE_NODE && nextFragment.isAttribute()) {
                    String attributeNamespaceURI = null;
                    if (nextFragment.hasNamespace()) {
                        attributeNamespaceURI = xmlNamespaceResolver.resolveNamespacePrefix(nextFragment.getPrefix());
                    }
                    if(sameName(nextChild, nextFragment.getLocalName()) && sameNamespaceURI(nextChild, attributeNamespaceURI)) {
                        addXMLEntry(nextChild, nextFragment.getXMLField(), entries);
                    }
                } else if(nextChild.getNodeType() == Node.ELEMENT_NODE) {
                    String elementNamespaceURI = null;
                    if(nextFragment.hasNamespace()) {
                        elementNamespaceURI = xmlNamespaceResolver.resolveNamespacePrefix(nextFragment.getPrefix());
                    }
                    if(sameName(nextChild, nextFragment.getLocalName()) && sameNamespaceURI(nextChild, elementNamespaceURI)) {
                        if(nextFragment.getNextFragment() == null) {
                            addXMLEntry(nextChild, nextFragment.getXMLField(), entries);
                        } else {
                            matchingFragments.add(nextFragment.getNextFragment());
                        }
                    }
                }
            }
            if(matchingFragments.size() > 0) {
                selectNodes(nextChild, matchingFragments, xmlNamespaceResolver, entries);
            }
        }
    }

    private void addXMLEntry(Node entryNode, XML_FIELD xmlField, List<XMLEntry> entries) {
        XMLEntry entry = new XMLEntry();
        entry.setValue(entryNode);
        entry.setXMLField(xmlField);
        entries.add(entry);
    }

    private NodeList selectNodes(Node contextNode, XPathFragment xPathFragment, XMLNamespaceResolver xmlNamespaceResolver, AbstractNullPolicy nullPolicy) {
        NodeList resultNodes = getNodes(contextNode, xPathFragment, xmlNamespaceResolver, nullPolicy);

        if (xPathFragment.getNextFragment() != null) {
            Node resultNode;
            XMLNodeList result = new XMLNodeList();
            int numberOfResultNodes = resultNodes.getLength();
            for (int x = 0; x < numberOfResultNodes; x++) {
                resultNode = resultNodes.item(x);
                result.addAll(selectNodes(resultNode, xPathFragment.getNextFragment(), xmlNamespaceResolver, nullPolicy));
            }
            return result;
        }

        return resultNodes;
    }

    private Node getSingleNode(Node contextNode, XPathFragment xPathFragment, XMLNamespaceResolver xmlNamespaceResolver) {
        if (xPathFragment.isAttribute()) {
            return selectSingleAttribute(contextNode, xPathFragment, xmlNamespaceResolver);
        } else if (xPathFragment.nameIsText()) {
            return selectSingleText(contextNode);
        } else if (xPathFragment.isSelfFragment()) {
            return contextNode;
        }

        if (xPathFragment.containsIndex()) {
            return selectSingleElement(contextNode, xPathFragment, xmlNamespaceResolver, xPathFragment.getIndexValue());
        }
        return selectSingleElement(contextNode, xPathFragment, xmlNamespaceResolver);
    }

    private NodeList getNodes(Node contextNode, XPathFragment xPathFragment, XMLNamespaceResolver xmlNamespaceResolver, AbstractNullPolicy nullPolicy) {
        if (xPathFragment.isAttribute()) {
            return selectAttributeNodes(contextNode, xPathFragment, xmlNamespaceResolver);
        } else if (xPathFragment.nameIsText()) {
            return selectTextNodes(contextNode, nullPolicy);
        } else if (xPathFragment.isSelfFragment()) {
            XMLNodeList xmlNodeList = new XMLNodeList(1);
            xmlNodeList.add(contextNode);
            return xmlNodeList;
        }

        if (xPathFragment.containsIndex()) {
            return selectElementNodes(contextNode, xPathFragment, xmlNamespaceResolver, xPathFragment.getIndexValue());
        }
        return selectElementNodes(contextNode, xPathFragment, xmlNamespaceResolver);
    }

    private Node selectSingleAttribute(Node contextNode, XPathFragment xPathFragment, XMLNamespaceResolver xmlNamespaceResolver) {
        if (xPathFragment.hasNamespace()) {
            if(Node.ELEMENT_NODE == contextNode.getNodeType()) {
                String attributeNamespaceURI = xmlNamespaceResolver.resolveNamespacePrefix(xPathFragment.getPrefix());
                return contextNode.getAttributes().getNamedItemNS(attributeNamespaceURI, xPathFragment.getLocalName());
            } else {
                return null;
            }
        } else {
            if(Node.ELEMENT_NODE == contextNode.getNodeType()) {
                return contextNode.getAttributes().getNamedItem(xPathFragment.getShortName());
            } else {
                return null;
            }
        }
    }

    private NodeList selectAttributeNodes(Node contextNode, XPathFragment xPathFragment, XMLNamespaceResolver xmlNamespaceResolver) {
        XMLNodeList xmlNodeList = new XMLNodeList();

        Node child = selectSingleAttribute(contextNode, xPathFragment, xmlNamespaceResolver);
        if (null != child) {
            xmlNodeList.add(child);
        }
        return xmlNodeList;
    }

    private Node selectSingleElement(Node contextNode, XPathFragment xPathFragment, XMLNamespaceResolver xmlNamespaceResolver) {
        Node child = contextNode.getFirstChild();
        while (null != child) {
            String elementNamespaceURI = null;
            if(xmlNamespaceResolver != null) {
                elementNamespaceURI = xmlNamespaceResolver.resolveNamespacePrefix(xPathFragment.getPrefix());
            }
            if ((child.getNodeType() == Node.ELEMENT_NODE) && sameName(child, xPathFragment.getLocalName()) && sameNamespaceURI(child, elementNamespaceURI)) {
                return child;
            }
            child = child.getNextSibling();
        }
        return null;
    }

    public NodeList selectElementNodes(Node contextNode, XPathFragment xPathFragment, XMLNamespaceResolver xmlNamespaceResolver) {
        XMLNodeList xmlNodeList = new XMLNodeList();
        Node child = contextNode.getFirstChild();

        while (null != child) {
            String elementNamespaceURI = null;
            if(xmlNamespaceResolver != null) {
                elementNamespaceURI = xmlNamespaceResolver.resolveNamespacePrefix(xPathFragment.getPrefix());
            }
            if ((child.getNodeType() == Node.ELEMENT_NODE) && sameName(child, xPathFragment.getLocalName()) && sameNamespaceURI(child, elementNamespaceURI)) {
                XPathPredicate predicate = xPathFragment.getPredicate();
                if(predicate != null) {
                    XPathFragment predicateFragment = predicate.getXPathFragment();
                    if(predicateFragment.isAttribute() && child.getAttributes() != null) {
                        Attr attr = (Attr)child.getAttributes().getNamedItemNS(predicateFragment.getNamespaceURI(), predicateFragment.getLocalName());
                        if(attr != null) {
                            String attribute = attr.getValue();
                            if(xPathFragment.getPredicate().getValue().equals(attribute)) {
                                xmlNodeList.add(child);
                            }
                        }
                    }
                } else { 
                    xmlNodeList.add(child);
                }
            }
            child = child.getNextSibling();
        }
        return xmlNodeList;
    }

    private Node selectSingleElement(Node contextNode, XPathFragment xPathFragment, XMLNamespaceResolver xmlNamespaceResolver, int position) {
        Node child = contextNode.getFirstChild();

        while (null != child) {
            String elementNamespaceURI = null;
            if(xmlNamespaceResolver != null) {
                elementNamespaceURI = xmlNamespaceResolver.resolveNamespacePrefix(xPathFragment.getPrefix());
            }
            if ((child.getNodeType() == Node.ELEMENT_NODE) && sameName(child, xPathFragment.getShortName()) && sameNamespaceURI(child, elementNamespaceURI)) {
                if (0 == --position) {
                    return child;
                }
            }
            child = child.getNextSibling();
        }

        return null;
    }

    private NodeList selectElementNodes(Node contextNode, XPathFragment xPathFragment, XMLNamespaceResolver xmlNamespaceResolver, int position) {
        XMLNodeList xmlNodeList = new XMLNodeList();
        Node child = selectSingleElement(contextNode, xPathFragment, xmlNamespaceResolver, position);
        if (null != child) {
            xmlNodeList.add(child);
        }
        return xmlNodeList;
    }

    private Node selectSingleText(Node contextNode) {
        NodeList childrenNodes = contextNode.getChildNodes();
        int numberOfNodes = childrenNodes.getLength();

        if (numberOfNodes == 0) {
            return null;
        }

        if (numberOfNodes == 1) {
            Node child = childrenNodes.item(0);
            if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE) {
                return child;
            }
            return null;
        }

        String returnVal = null;
        for (int i = 0; i < numberOfNodes; i++) {
            Node next = childrenNodes.item(i);
            if (next.getNodeType() == Node.TEXT_NODE || next.getNodeType() == Node.CDATA_SECTION_NODE) {
                String val = next.getNodeValue();
                if (val != null) {
                    if (returnVal == null) {
                        returnVal = "";
                    }
                    if(next.getNodeType() == Node.CDATA_SECTION_NODE) {
                        val = val.trim();
                    }
                    returnVal += val;
                }
            }
        }

        //bug#4515249 a new text node was being created when null should have been returned
        //case where contextNode had several children but no Text children
        if (returnVal != null) {
            return contextNode.getOwnerDocument().createTextNode(returnVal);
        }
        return null;
    }

    private NodeList selectTextNodes(Node contextNode, AbstractNullPolicy nullPolicy) {
        Node n = selectSingleText(contextNode);

        XMLNodeList xmlNodeList = new XMLNodeList();

        if (n == null && nullPolicy != null) {
            if (nullPolicy.valueIsNull((Element) contextNode)) {
                if (nullPolicy.getMarshalNullRepresentation() != XMLNullRepresentationType.ABSENT_NODE) {
                    xmlNodeList.add(n);
                }
            } else {
                xmlNodeList.add(contextNode.getOwnerDocument().createTextNode(Constants.EMPTY_STRING));
            }
        } else {
            if (nullPolicy != null && nullPolicy.isNullRepresentedByXsiNil() && nullPolicy.valueIsNull((Element) contextNode))  {
                xmlNodeList.add(null);
            }else if (n != null) {
                xmlNodeList.add(n);
            }
        }
        
        return xmlNodeList;
    }

    private boolean sameNamespaceURI(Node node, String namespaceURI) {
        // HANDLE THE NULL CASE
        String nodeNamespaceURI = node.getNamespaceURI();
        if (nodeNamespaceURI == namespaceURI) {
            return true;
        }

        if ((nodeNamespaceURI == null) && namespaceURI.length() == 0) {
            return true;
        }

        if ((namespaceURI == null) && nodeNamespaceURI.length() == 0) {
            return true;
        }

        // HANDLE THE NON-NULL CASE
        return (null != nodeNamespaceURI) && nodeNamespaceURI.equals(namespaceURI);
    }

    private boolean sameName(Node node, String name) {
        return name.equals(node.getLocalName()) || name.equals(node.getNodeName());
    }

}
