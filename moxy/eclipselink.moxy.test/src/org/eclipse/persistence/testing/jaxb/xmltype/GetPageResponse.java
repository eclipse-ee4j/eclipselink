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
// dmccann - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmltype;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getPageResponse", namespace = "http://namespace2")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getPageResponse", namespace = "http://namespace2")
public class GetPageResponse {

    @XmlElement(name = "return", namespace = "")
    private Page _return;

    /**
     *
     * @return
     *     returns Page
     */
    public Page getReturn() {
        return this._return;
    }

    /**
     *
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(Page _return) {
        this._return = _return;
    }

    public boolean equals(Object o) {
        GetPageResponse gpr;
        try {
            gpr = (GetPageResponse) o;
        } catch (ClassCastException cce) {
            return false;
        }
        if (gpr.getReturn() == null) {
            return this.getReturn() == null;
        }
        return this.getReturn().equals(gpr.getReturn());
    }
}
