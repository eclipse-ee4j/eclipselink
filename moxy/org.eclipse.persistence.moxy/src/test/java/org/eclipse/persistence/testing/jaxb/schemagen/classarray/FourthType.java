/*
 * Copyright (c) 2019, 2021 Oracle and/or its affiliates. All rights reserved.
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
// Radek Felcman - 02/2019 - Initial implementation
package org.eclipse.persistence.testing.jaxb.schemagen.classarray;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FourthType", namespace = "http://fourth.temp.com/", propOrder = {
        "fourthSubElement"
})
public class FourthType {

    @XmlElement(name = "FourthSubElement", namespace = "http://fourth.temp.com/", required = true)
    protected String fourthSubElement;

    public String getFourthSubElement() {
        return fourthSubElement;
    }

    public void setFourthSubElement(String value) {
        this.fourthSubElement = value;
    }

}
