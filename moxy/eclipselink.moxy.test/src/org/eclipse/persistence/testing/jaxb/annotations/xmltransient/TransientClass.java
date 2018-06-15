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
//     Rick Barkhouse - 2012-03-27 - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmltransient;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlTransient
@XmlRootElement
@XmlType(name="",propOrder={"s001" , "b001"})
public class TransientClass {

    private String s001;
    private boolean b001;

    public String getS001() {
        return s001;
    }

    public void setS001(String s001) {
        this.s001 = s001;
    }

    public boolean isB001() {
        return b001;
    }

    public void setB001(boolean b001) {
        this.b001 = b001;
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 == null) {
            return false;
        }

        TransientClass anObj = (TransientClass) arg0;

        return (anObj.b001 == this.b001) && (anObj.s001.equals(this.s001));
    }

}
