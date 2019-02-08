/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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
// Radek Felcman - 02/2019 - Initial implementation
package org.eclipse.persistence.testing.jaxb.schemagen.classarray;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
