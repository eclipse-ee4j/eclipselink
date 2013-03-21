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
package org.eclipse.persistence.platform.xml;

import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of the org.w3c.dom.NodeList interface
 */
public class XMLNodeList implements NodeList {
    private ArrayList nodes;

    public XMLNodeList() {
        nodes = new ArrayList();
    }

    public XMLNodeList(int size) {
        nodes = new ArrayList(size);
    }

    public int getLength() {
        return nodes.size();
    }

    public Node item(int i) {
        return (Node)nodes.get(i);
    }

    public void add(Node node) {
        nodes.add(node);
    }

    public void addAll(NodeList nodelist) {
        int size = nodelist.getLength();
        for (int i = 0; i < size; i++) {
            nodes.add(nodelist.item(i));
        }
    }
}
