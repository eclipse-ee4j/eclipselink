/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
