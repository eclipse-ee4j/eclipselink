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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlelementrefs;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name="air-transport")
public class ViaAir extends TransportType{

    @XmlAttribute
    public String airliner;

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ViaAir)) {
            return false;
        }
        ViaAir t = (ViaAir) obj;

        return t.transportTypeID == this.transportTypeID && t.transportCost == this.transportCost && t.airliner.equals(this.airliner);
    }
}
