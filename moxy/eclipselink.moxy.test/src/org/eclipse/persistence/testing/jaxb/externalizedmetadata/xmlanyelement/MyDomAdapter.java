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
// dmccann - October 27/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class MyDomAdapter extends XmlAdapter<Node, Dom> {
    public static final String STUFF_STR = "stuff";

    public MyDomAdapter() {}

    public Node marshal(Dom arg0) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element elt = doc.createElement(STUFF_STR);
        elt.appendChild(doc.createTextNode(arg0.stuffStr));
        return elt;
    }

    public Dom unmarshal(Node arg0) throws Exception {
        Dom stuff = new Dom();
        Node elt = arg0.getFirstChild();
        stuff.stuffStr = elt.getNodeValue();
        return stuff;
    }
}
