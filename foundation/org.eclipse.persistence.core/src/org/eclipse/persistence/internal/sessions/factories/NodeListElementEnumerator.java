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
package org.eclipse.persistence.internal.sessions.factories;

import org.w3c.dom.*;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose</b>: This class is used to enumerate over the XML elements.  This is because
 * some parsers fail to ignore whitespaces and may include them as test elements.
 * This enumerator will ensure that only NodeElements are returned.
 * @since TopLink 4.0
 * @author Gordon Yorke
 */
public class NodeListElementEnumerator {
    protected int index;
    protected NodeList list;

    public NodeListElementEnumerator(NodeList list) {
        this.index = 0;
        this.list = list;
        int length = list.getLength();
        while ((index < length) && (list.item(index).getNodeType() != Node.ELEMENT_NODE)) {
            ++this.index;
        }
    }

    public boolean hasMoreNodes() {
        return index < list.getLength();
    }

    public Node nextNode() {
        Node result = list.item(index);
        ++index;
        int length = list.getLength();
        while ((index < length) && (list.item(index).getNodeType() != Node.ELEMENT_NODE)) {
            ++this.index;
        }
        return result;
    }
}
