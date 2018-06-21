/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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
// Martin Vojtek - July 8/2014
package org.eclipse.persistence.jaxb.compiler;

import org.eclipse.persistence.jaxb.xmlmodel.XmlElementNillable;
import org.eclipse.persistence.jaxb.xmlmodel.XmlNullPolicy;

public class XmlNillableInfo {

    private XmlElementNillable xmlElementNillable;
    private XmlNullPolicy xmlNullPolicy;

    public XmlElementNillable getXmlElementNillable() {
        return xmlElementNillable;
    }

    public void setXmlElementNillable(XmlElementNillable xmlElementNillable) {
        this.xmlElementNillable = xmlElementNillable;
    }

    public XmlNullPolicy getXmlNullPolicy() {
        return xmlNullPolicy;
    }

    public void setXmlNullPolicy(XmlNullPolicy xmlNullPolicy) {
        this.xmlNullPolicy = xmlNullPolicy;
    }
}
