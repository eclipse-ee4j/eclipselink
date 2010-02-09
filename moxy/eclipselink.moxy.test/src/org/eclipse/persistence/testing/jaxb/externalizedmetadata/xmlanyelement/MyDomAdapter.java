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
 * dmccann - October 27/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class MyDomAdapter extends XmlAdapter<Element, java.util.List<Object>> {
    public MyDomAdapter() {}
    
    public Element marshal(java.util.List<Object> arg0) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element elt = doc.createElement("stuff");
        elt.appendChild(doc.createTextNode("667"));
        return elt;
    }

    public java.util.List<Object> unmarshal(Element arg0) throws Exception {
        java.util.List listToReturn = new java.util.ArrayList<Object>();
        listToReturn.add(arg0);
        return listToReturn;
    }
}
