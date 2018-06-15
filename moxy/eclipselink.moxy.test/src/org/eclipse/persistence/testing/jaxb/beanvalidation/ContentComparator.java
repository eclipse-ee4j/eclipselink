/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.jaxb.beanvalidation;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Compares contents of two xml files. Ignores whitespaces.
 *
 * @author jungi
 */
public final class ContentComparator {

    private static final Logger LOGGER = Logger.getLogger(ContentComparator.class.getName());

    /** Creates a new instance of ContentComparator */
    private ContentComparator() {
    }

    /**
     * Compares content of two xml files. Ignores whitespaces.
     *
     * @param f1 usually golden file
     * @param f2 other file which we want to compare against golden file (or any other file)
     * @return true if both files have the same content except of whitespaces
     */
    public static boolean equalsXML(File f1, File f2) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d1 = db.parse(f1);
            Document d2 = db.parse(f2);
            return compare(d1.getDocumentElement(), d2.getDocumentElement());
        } catch (ParserConfigurationException e) {
            LOGGER.log(Level.WARNING, "Exception from test - comparing XML files", e); //NOI18N
        } catch (SAXException e) {
            LOGGER.log(Level.WARNING, "Exception from test - comparing XML files", e); //NOI18N
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Exception from test - comparing XML files", e); //NOI18N
        }
        return false;
    }

    private static boolean compare(Node n1, Node n2) {
        List<Node> l1 = new LinkedList<Node>();
        List<Node> l2 = new LinkedList<Node>();
        l1.add(n1);
        l2.add(n2);
        while (!l1.isEmpty() && !l2.isEmpty()) {
            Node m1 = l1.remove(0);
            Node m2 = l2.remove(0);
            //check basic things - node name, value, attributes - if they're OK, we can continue
            if (sameNode(m1, m2)) {
                //now compare children
                NodeList nl = m1.getChildNodes();
                for (int i = 0; i < nl.getLength(); i++) {
                    Node e = nl.item(i);
                    if (e.getNodeType() == Node.TEXT_NODE) {
                        //ignore empty places
                        if (e.getNodeValue().trim().equals("")) {
                            continue;
                        }
                    }
                    l1.add(nl.item(i));
                }
                nl = m2.getChildNodes();
                for (int i = 0; i < nl.getLength(); i++) {
                    Node e = nl.item(i);
                    if (e.getNodeType() == Node.TEXT_NODE) {
                        //ignore empty places
                        if (e.getNodeValue().trim().equals("")) {
                            continue;
                        }
                    }
                    l2.add(nl.item(i));
                }
            } else {
                //nodes are not equals - print some info
                LOGGER.warning("================================================"); //NOI18N
                LOGGER.warning("m1: " + m1.getNodeName() + "; \'" + m1.getNodeValue() + "\'"); //NOI18N
                LOGGER.warning("m2: " + m2.getNodeName() + "; \'" + m2.getNodeValue() + "\'"); //NOI18N
                LOGGER.warning("================================================"); //NOI18N
                return false;
            }
        }
        // check remains
        if (!l1.isEmpty()) {
            // missing nodes
            LOGGER.warning("================================================"); //NOI18N
            LOGGER.warning("Expected " + l1.size() + " more node(s):"); //NOI18N
            logNodes(l1);
            LOGGER.warning("================================================"); //NOI18N
            return false;
        } else if (!l2.isEmpty()) {
            // extra nodes
            LOGGER.warning("================================================"); //NOI18N
            LOGGER.warning("Got " + l2.size() + " more node(s) than expected:"); //NOI18N
            logNodes(l2);
            LOGGER.warning("================================================"); //NOI18N
            return false;
        } else {
            return true;
        }
    }

    //attrs, name, value
    private static boolean sameNode(Node n1, Node n2) {
        //check node name
        if (!n1.getNodeName().equals(n2.getNodeName())) {
            LOGGER.warning("================================================"); //NOI18N
            LOGGER.warning("Expected node: " + n1.getNodeName() + ", got: " + n2.getNodeName()); //NOI18N
            LOGGER.warning("================================================"); //NOI18N
            return false;
        }
        //check node value
        if (!((n1.getNodeValue() != null)
                ? n1.getNodeValue().equals(n2.getNodeValue())
                : (n2.getNodeValue() == null))) {
            LOGGER.warning("================================================"); //NOI18N
            LOGGER.warning("Expected node value: " + n1.getNodeValue() + ", got: " + n2.getNodeValue()); //NOI18N
            LOGGER.warning("================================================"); //NOI18N
            return false;
        }
        //check node attributes
        NamedNodeMap nnm1 = n1.getAttributes();
        NamedNodeMap nnm2 = n2.getAttributes();
        if ((nnm1 == null && nnm2 != null)
                || (nnm1 != null && nnm2 == null)) {
            return false;
        }
        if (nnm1 == null) {
            return true;
        }
        for (int i = 0; i < nnm1.getLength(); i++) {
            Node x = nnm1.item(i);
            Node y = nnm2.item(i);
            if (!(x.getNodeName().equals(y.getNodeName())
                    && x.getNodeValue().equals(y.getNodeValue()))) {
                //nodes are not equals - print some info
                LOGGER.warning("================================================"); //NOI18N
                LOGGER.warning("Expected attribute: " + x.getNodeName() + "=\'" +  x.getNodeValue() + "\'," //NOI18N
                        + " got: " + y.getNodeName() + "=\'" +  y.getNodeValue() + "\'"); //NOI18N
                LOGGER.warning("================================================"); //NOI18N
                return false;
            }
        }
        return true;
    }

    private static void logNodes(List<Node> nodes) {
        if (LOGGER.isLoggable(Level.WARNING)) {
            for(Node n : nodes) {
                LOGGER.warning("Node name: " + n.getNodeName()); //NOI18N
            }
        }
    }
}
