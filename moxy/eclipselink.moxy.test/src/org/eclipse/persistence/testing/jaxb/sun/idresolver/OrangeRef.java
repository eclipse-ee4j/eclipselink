/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 21 October 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.sun.idresolver;

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
