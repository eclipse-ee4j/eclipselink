/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 21 October 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.idresolver;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlJoinNode;
import org.eclipse.persistence.oxm.annotations.XmlJoinNodes;

@XmlRootElement
class OrangeRef {

    @XmlJoinNode(xmlPath = "ref/@id", referencedXmlPath = "@id")
    Orange ref;

    @Override
    public String toString() {
        if (ref == null) return "null";
        return "ref" + ref.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof OrangeRef)) {
            return false;
        }
        OrangeRef o = (OrangeRef) obj;

        return this.ref.equals(o.ref);
    }

}
