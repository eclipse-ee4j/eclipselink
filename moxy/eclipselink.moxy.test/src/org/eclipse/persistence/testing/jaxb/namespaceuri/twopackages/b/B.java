/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.namespaceuri.twopackages.b;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class B {

    private String c;

    @XmlPath("one/two:three/text()")
    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        B test = (B) obj;
        if(null == c) {
            return null == test.getC();
        } else {
            return c.equals(test.getC());
        }
    }

}
