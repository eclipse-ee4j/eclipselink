/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Denise Smith - September 2013
package org.eclipse.persistence.testing.jaxb.json.attribute;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public  class SimpleBean {
    @XmlAttribute
    private String attr;

    @XmlAttribute
    private String attr2;

    public String elem1;

    public String elem2;

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getAttr2() {
        return attr2;
    }

    public void setAttr2(String attr2) {
        this.attr2 = attr2;
    }

    public boolean equals(Object obj){
        if(obj instanceof SimpleBean){
            return (attr == null && ((SimpleBean)obj).attr ==null)  || (attr.equals(((SimpleBean)obj).attr)) &&
            (attr2 == null && ((SimpleBean)obj).attr2 ==null)  || (attr2.equals(((SimpleBean)obj).attr2)) &&
            (elem1 == null && ((SimpleBean)obj).elem1 ==null)  || (elem1.equals(((SimpleBean)obj).elem1)) &&
            (elem2 == null && ((SimpleBean)obj).elem2 ==null)  || (elem2.equals(((SimpleBean)obj).elem2));
        }
        return false;
    }
}
