/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - December 04/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementrefs;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlTransient;

@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
//@javax.xml.bind.annotation.XmlRootElement(name="my-foos")
public class Foos {
    //@javax.xml.bind.annotation.XmlElementWrapper(name="items")
    //@javax.xml.bind.annotation.XmlElementRefs({
    //    @javax.xml.bind.annotation.XmlElementRef(name="integer-root", namespace="myns"),
    //    @javax.xml.bind.annotation.XmlElementRef(name="root")
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
