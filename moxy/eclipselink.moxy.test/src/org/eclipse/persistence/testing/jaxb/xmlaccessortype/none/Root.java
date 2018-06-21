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
//     Blaise Doughan - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlaccessortype.none;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class Root {

    private Mapped mapped;
    private Unmapped unmapped;

    @XmlElement
    public Mapped getMapped() {
        return mapped;
    }

    public void setMapped(Mapped mapped) {
        this.mapped = mapped;
    }

    public Unmapped getUnmapped() {
        return unmapped;
    }

    public void setUnmapped(Unmapped unmapped) {
        this.unmapped = unmapped;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != Root.class ) {
            return false;
        }
        Root test = (Root) obj;
        if(!equals(mapped, test.getMapped())) {
            return false;
        }
        if(!equals(unmapped, test.getUnmapped())) {
            return false;
        }
        return true;
    }

    private boolean equals(Object control, Object test) {
        if(null == control) {
            return null == test;
        }
        return control.equals(test);
    }

}
