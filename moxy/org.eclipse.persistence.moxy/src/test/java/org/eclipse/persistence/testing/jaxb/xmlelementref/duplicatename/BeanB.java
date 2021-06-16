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
//     mmacivor - 2010-03-09 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelementref.duplicatename;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="bean-b")
public class BeanB {
    @XmlElementRef(name="value")
    public JAXBElement<Integer> value;

    public boolean equals(Object obj) {
        if(!(obj instanceof BeanB)) {
            return false;
        }

        BeanB bean = (BeanB)obj;
        JAXBElement<Integer> objValue = bean.value;

        if(objValue == null && this.value == null) {
            return true;
        }
        if(objValue != null ^ this.value != null) {
            return false;
        }

        return (objValue.getValue().equals(this.value.getValue()) && objValue.getName().equals(this.value.getName()));
    }
}
