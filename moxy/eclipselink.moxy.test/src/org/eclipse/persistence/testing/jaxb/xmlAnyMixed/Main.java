/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.jaxb.xmlAnyMixed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "main", propOrder = {
        "rootAnyMixed",
        "rootAny"
})
@XmlRootElement
public class Main {

    @XmlElement(name = "RootAnyMixed", required = true)
    protected RootAnyMixed rootAnyMixed;
    @XmlElement(name = "RootAny", required = true)
    protected RootAny rootAny;

    public RootAnyMixed getRootAnyMixed() {
        return rootAnyMixed;
    }

    public void setRootAnyMixed(RootAnyMixed rootAnyMixed) {
        this.rootAnyMixed = rootAnyMixed;
    }

    public RootAny getRootAny() {
        return rootAny;
    }

    public void setRootAny(RootAny rootAny) {
        this.rootAny = rootAny;
    }
}
