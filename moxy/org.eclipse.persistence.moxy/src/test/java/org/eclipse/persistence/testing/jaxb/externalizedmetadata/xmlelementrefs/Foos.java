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
// dmccann - December 04/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementrefs;

import java.util.List;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlTransient;

@jakarta.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
//@jakarta.xml.bind.annotation.XmlRootElement(name="my-foos")
public class Foos {
    //@jakarta.xml.bind.annotation.XmlElementWrapper(name="items")
    //@jakarta.xml.bind.annotation.XmlElementRefs({
    //    @jakarta.xml.bind.annotation.XmlElementRef(name="integer-root", namespace="myns"),
    //    @jakarta.xml.bind.annotation.XmlElementRef(name="root")
    //})
    public List<Object> items;
    public List<Object> stuff;

    @XmlTransient
    public boolean accessedViaMethod = false;

    public List<Object> getItemList() {
        accessedViaMethod = true;
        return items;
    }

    public void setItemList(List<Object> items) {
        this.items = items;
    }
}
