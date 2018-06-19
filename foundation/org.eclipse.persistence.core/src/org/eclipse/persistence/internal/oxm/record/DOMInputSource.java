/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     bdoughan - June 24/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.internal.oxm.record;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class DOMInputSource extends InputSource {

    Node node;

    public DOMInputSource(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return this.node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

}
