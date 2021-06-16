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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "firstElement",
    "secondElement",
    "thirdElement",
    "fourthElement",
    "withoutNamespaceSubElement"
})
@XmlRootElement(name = "RootElement")
public class RootElement {

    @XmlElement(name = "FirstElement", required = true)
    @Valid
    @NotNull
    protected FirstType firstElement;
    @XmlElement(name = "SecondElement", required = true)
    @Valid
    @NotNull
    protected SecondType secondElement;
    @XmlElement(name = "ThirdElement", required = true)
    @Valid
    @NotNull
    protected ThirdType thirdElement;
    @XmlElement(name = "FourthElement", required = true)
    @Valid
    @NotNull
    protected FourthType fourthElement;
    @XmlElement(name = "WithoutNamespaceSubElement", required = true)
    @Valid
    @NotNull
    protected WithoutNamespaceType withoutNamespaceSubElement;

    public FirstType getFirstElement() {
        return firstElement;
    }

    public void setFirstElement(FirstType value) {
        this.firstElement = value;
    }

    public SecondType getSecondElement() {
        return secondElement;
    }

    public void setSecondElement(SecondType value) {
        this.secondElement = value;
    }

    public ThirdType getThirdElement() {
        return thirdElement;
    }

    public void setThirdElement(ThirdType value) {
        this.thirdElement = value;
    }

    public FourthType getFourthElement() {
        return fourthElement;
    }

    public void setFourthElement(FourthType value) {
        this.fourthElement = value;
    }

    public WithoutNamespaceType getWithoutNamespaceSubElement() {
        return withoutNamespaceSubElement;
    }

    public void setWithoutNamespaceSubElement(WithoutNamespaceType value) {
        this.withoutNamespaceSubElement = value;
    }

}
