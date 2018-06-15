/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlaccessortype.none;

import javax.xml.bind.annotation.XmlElement;

public class Unmapped {

    public Unmapped(String value) {
    }

    @XmlElement
    private UnmappedTwo unmapped;
    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != Unmapped.class ) {
            return false;
        }
        return true;
    }

}
