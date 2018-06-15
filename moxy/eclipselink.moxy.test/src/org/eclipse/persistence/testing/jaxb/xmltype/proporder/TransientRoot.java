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
package org.eclipse.persistence.testing.jaxb.xmltype.proporder;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public class TransientRoot {

    private String parentProp;

    public String getParentProp() {
        return parentProp;
    }

    public void setParentProp(String parentProp) {
        this.parentProp = parentProp;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        TransientRoot test = (TransientRoot) obj;
        if(null == parentProp) {
            return null == test.getParentProp();
        } else {
            return parentProp.equals(test.getParentProp());
        }
    }

}
