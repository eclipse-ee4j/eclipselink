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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.self;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class Creditor {

    private String id;

    @XmlPath("CreditorFrstName/id/text()")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != Creditor.class) {
            return false;
        }
        Creditor test = (Creditor) obj;
        if(null == id) {
            return null == test.getId();
        } else {
            return id.equals(test.getId());
        }
    }

}
