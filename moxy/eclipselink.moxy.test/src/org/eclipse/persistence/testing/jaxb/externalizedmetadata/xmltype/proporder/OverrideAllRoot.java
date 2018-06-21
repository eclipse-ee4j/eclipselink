/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype.proporder;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="root")
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class OverrideAllRoot {

    public String a;
    public String b;
    public String c;

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        OverrideAllRoot test = (OverrideAllRoot) obj;
        if(!equals(a, test.a)) {
            return false;
        }
        if(!equals(b, test.b)) {
            return false;
        }
        if(!equals(c, test.c)) {
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
