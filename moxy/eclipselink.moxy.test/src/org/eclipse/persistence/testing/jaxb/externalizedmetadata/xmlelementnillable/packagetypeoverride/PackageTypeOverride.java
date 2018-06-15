/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Martin Vojtek - July 8/2014
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementnillable.packagetypeoverride;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlElementNillable;

@XmlRootElement
@XmlElementNillable(nillable = false)
public class PackageTypeOverride {
    private String a;
    private String b;
    private String c;
    private String d;
    private String e;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    @XmlElement(nillable = true)
    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    @XmlElement(nillable = true)
    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

}
