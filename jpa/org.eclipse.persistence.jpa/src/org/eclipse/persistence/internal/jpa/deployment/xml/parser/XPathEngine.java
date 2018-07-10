/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.deployment.xml.parser;

import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * INTERNAL
 * Utility class for finding XML nodes using XPath expressions.
 */
public class XPathEngine {
    private static final String ATTRIBUTE = "@";
    private static final String TEXT = "text()";
    private static final String ALL_CHILDREN = "child::*";
    private static final String NAMESPACE_URI = "http://java.sun.com/xml/ns/persistence/orm";
    private static XPathEngine instance = null;

    private XPathEngine() {
        super();
    }

    /**
     * Return the <code>XPathEngine</code> singleton.
     */
    public static XPathEngine getInstance() {
        if (instance == null) {
            instance = new XPathEngine();
        }
        return instance;
    }

    /**
     * Execute the XPath statement relative to the context node.
     *
     * @param contextNode the node relative to which the XPath statement will be executed
     * @return the first node located matching the XPath statement
     * @throws XMLPlatformException
     */
    public Node selectSingleNode(Node contextNode, String[] xPathFragments) {
        if (contextNode == null) {
            return null;
        }

        return selectSingleNode(contextNode, xPathFragments, 0);
    }

    private Node selectSingleNode(Node contextNode, String[] xPathFragments, int index) {
        Node resultNode = getSingleNode(contextNode, xPathFragments[index]);
        if ((resultNode == null) || (xPathFragments.length == (index + 1))) {
            return resultNode;
        }

        return selectSingleNode(resultNode, xPathFragments, index + 1);
    }

    /**
     * Execute the XPath statement relative to the context node.
     *
     * @param contextNode the node relative to which the XPath statement will be executed
     * @return a list of nodes matching the XPath statement
     * @throws XMLPlatformException
     */
    public NodeList selectNodes(Node contextNode, String[] xPathFragments) {
        if (contextNode == null) {
            return null;
        }

        return selectNodes(contextNode, xPathFragments, 0);
    }

    private NodeList selectNodes(Node contextNode, String[] xPathFragments, int index) {

        NodeList resultNodes = getNodes(contextNode, xPathFragments[index]);

        if (xPathFragments.length != index + 1) {
            Node resultNode;
            XMLNodeList result = new XMLNodeList();
            int numberOfResultNodes = resultNodes.getLength();
            for (int x = 0; x < numberOfResultNodes; x++) {
                resultNode = resultNodes.item(x);
                result.addAll(selectNodes(resultNode, xPathFragments, index + 1));
            }
            return result;
        }

        return resultNodes;
    }

    private Node getSingleNode(Node contextNode, String xPathFragment) {
        if (xPathFragment.startsWith(ATTRIBUTE)) {
            return selectSingleAttribute(contextNode, xPathFragment);
        } else if (TEXT.equals(xPathFragment)) {
            return selectSingleText(contextNode);
        }
        return selectSingleElement(contextNode, xPathFragment);
    }

    private NodeList getNodes(Node contextNode, String xPathFragment) {
        if (xPathFragment.startsWith(ATTRIBUTE)) {
            return selectAttributeNodes(contextNode, xPathFragment);
        } else if (TEXT.equals(xPathFragment)) {
            return selectTextNodes(contextNode);
        } else if (xPathFragment.equals(ALL_CHILDREN)) {
            return selectChildElements(contextNode);
        }
        return selectElementNodes(contextNode, xPathFragment);

    }

    private Node selectSingleAttribute(Node contextNode, String xPathFragment) {
        Element contextElement = (Element)contextNode;
        return contextElement.getAttributeNode(xPathFragment.substring(1));
    }

    private NodeList selectAttributeNodes(Node contextNode, String xPathFragment) {
        XMLNodeList xmlNodeList = new XMLNodeList();

        Node child = selectSingleAttribute(contextNode, xPathFragment);
        if (null != child) {
            xmlNodeList.add(child);
        }
        return xmlNodeList;
    }

    private Node selectSingleElement(Node contextNode, String xPathFragment) {
        Node child = contextNode.getFirstChild();
        while (null != child) {
            if ((child.getNodeType() == Node.ELEMENT_NODE) && sameName(child, xPathFragment) && sameNamespaceURI(child, NAMESPACE_URI)) {
                return child;
            }

            child = child.getNextSibling();
        }
        return null;
    }

    private NodeList selectChildElements(Node contextNode) {
        XMLNodeList xmlNodeList = new XMLNodeList();
        Node child = contextNode.getFirstChild();

        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                xmlNodeList.add(child);
            }
            child = child.getNextSibling();
        }

        return xmlNodeList;
    }

    private NodeList selectElementNodes(Node contextNode, String xPathFragment) {
        XMLNodeList xmlNodeList = new XMLNodeList();
        Node child = contextNode.getFirstChild();

        while (null != child) {
            if ((child.getNodeType() == Node.ELEMENT_NODE) && sameName(child, xPathFragment) && sameNamespaceURI(child, NAMESPACE_URI)) {
                xmlNodeList.add(child);
            }

            child = child.getNextSibling();
        }

        return xmlNodeList;
    }

    private Node selectSingleText(Node contextNode) {
        NodeList childrenNodes = contextNode.getChildNodes();

        if (childrenNodes.getLength() == 0) {
            return null;
        }

        if (childrenNodes.getLength() == 1) {
            Node child = childrenNodes.item(0);
            if (child.getNodeType() == Node.TEXT_NODE) {
                return child;
            }
            return null;
        }

        String returnVal = null;
        for (int i = 0; i < childrenNodes.getLength(); i++) {
            Node next = childrenNodes.item(i);
            if (next.getNodeType() == Node.TEXT_NODE) {
                String val = ((Text)next).getNodeValue();
                if (val != null) {
                    if (returnVal == null) {
                        returnVal = "";
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
