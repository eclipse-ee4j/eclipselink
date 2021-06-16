/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - December 03/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementref;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlTransient;

@jakarta.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
//@jakarta.xml.bind.annotation.XmlRootElement
public class Foo {
    //@jakarta.xml.bind.annotation.XmlElementRef(type=Bar.class)
    public Bar item;

    @XmlTransient
    public boolean accessedViaMethod = false;

    public Bar getBarItem() {
        accessedViaMethod = true;
        return item;
    }

    public void setBarItem(Bar item) {
        this.item = item;
    }

    public boolean equals(Object obj){
        if(obj instanceof Foo){
            if(item == null){
                if(((Foo)obj).item != null){
                    return false;
                }
                return true;
            }else{
                 return item.equals(((Foo)obj).item);
            }
        }
        return false;
    }

}
