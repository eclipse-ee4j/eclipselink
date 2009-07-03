/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.platform.xml.XMLNamespaceResolver;
import org.eclipse.persistence.platform.xml.XMLNodeList;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Utility class for finding XML nodes using XPath 
 * expressions.</p>
 * @since Oracle TopLink 10.1.3
 */

public class UnmarshalXPathEngine {
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
    public Object selectSingleNode(Node contextNode, XMLField xmlField, XMLNamespaceResolver xmlNamespaceResolver) throws XMLMarshalException {
        try {
            if (contextNode == null) {
                return null;
            }

            XPathFragment xPathFragment = xmlField.getXPathFragment();

            // allow the platform to handle the advanced case
            if (xPathFragment.shouldExecuteSelectNodes()) {
                return xmlPlatform.selectSingleNodeAdvanced(contextNode, xmlField.getXPath(), xmlNamespaceResolver);
            }
            Object result = selectSingleNode(contextNode, xPathFragment, xmlNamespaceResolver);
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

    private Object selectSingleNode(Node contextNode, XPathFragment xPathFragment, XMLNamespaceResolver xmlNamespaceResolver) {
        Node resultNode = getSingleNode(contextNode, xPathFragment, xmlNamespaceResolver);
        
        if (resultNode == null) {
            if(!xPathFragment.nameIsText()) {
                return XMLRecord.noEntry;
            }
            return null;
        }
        if(xPathFragment.getNextFragment() == null) {
            return resultNode;
        }

        return selectSingleNode(resultNode, xPathFragment.getNextFragment(), xmlNamespaceResolver);
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
    public NodeList selectNodes(Node contextNode, XMLField xmlField, XMLNamespaceResolver xmlNamespaceResolver) throws XMLMarshalException {
        try {
            if (contextNode == null) {
                return null;
            }

            XPathFragment xPathFragment = xmlField.getXPathFragment();

            // allow the platform to handle the advanced case
            if (xPathFragment.shouldExecuteSelectNodes()) {
                return xmlPlatform.selectNodesAdvanced(contextNode, xmlField.getXPath(), xmlNamespaceResolver);
            }
            return selectNodes(contextNode, xPathFragment, xmlNamespaceResolver);
        } catch (Exception x) {
            throw XMLMarshalException.invalidXPathString(xmlField.getXPath(), x);
        }
    }

    private NodeList selectNodes(Node contextNode, XPathFragment xPathFragment, XMLNamespaceResolver xmlNamespaceResolver) {
        NodeList resultNodes = getNodes(contextNode, xPathFragment, xmlNamespaceResolver);

        if (xPathFragment.getNextFragment() != null) {
            Node resultNode;
            XMLNodeList result = new XMLNodeList();
            int numberOfResultNodes = resultNodes.getLength();
            for (int x = 0; x < numberOfResultNodes; x++) {
                resultNode = resultNodes.item(x);
                result.addAll(selectNodes(resultNode, xPathFragment.getNextFragment(), xmlNamespaceResolver));
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

    private NodeList getNodes(Node contextNode, XPathFragment xPathFragment, XMLNamespaceResolver xmlNamespaceResolver) {
        if (xPathFragment.isAttribute()) {
            return selectAttributeNodes(contextNode, xPathFragment, xmlNamespaceResolver);
        } else if (xPathFragment.nameIsText()) {
            return selectTextNodes(contextNode);
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
            Element contextElement = (Element)contextNode;
            String attributeNamespaceURI = xmlNamespaceResolver.resolveNamespacePrefix(xPathFragment.getPrefix());
            return contextElement.getAttributeNodeNS(attributeNamespaceURI, xPathFragment.getLocalName());
        } else {
            Element contextElement = (Element)contextNode;
            return contextElement.getAttributeNode(xPathFragment.getShortName());
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
                xmlNodeList.add(child);
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
                        returnVal = new String();
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

    private NodeList selectTextNodes(Node contextNode) {
        Node n = selectSingleText(contextNode);

        XMLNodeList xmlNodeList = new XMLNodeList();
        if (n != null) {
            xmlNodeList.add(n);
        }
        return xmlNodeList;
    }

    private boolean sameNamespaceURI(Node node, String namespaceURI) {
        // HANDLE THE NULL CASE
        String nodeNamespaceURI = node.getNamespaceURI();
        if (nodeNamespaceURI == namespaceURI) {
            return true;
        }

        if ((nodeNamespaceURI == null) && namespaceURI.equals("")) {
            return true;
        }

        if ((namespaceURI == null) && nodeNamespaceURI.equals("")) {
            return true;
        }

        // HANDLE THE NON-NULL CASE
        return (null != nodeNamespaceURI) && nodeNamespaceURI.equals(namespaceURI);
    }

    private boolean sameName(Node node, String name) {
        return name.equals(node.getLocalName()) || name.equals(node.getNodeName());
    }
}
