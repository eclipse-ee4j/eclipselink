/*******************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Martin Vojtek - July 8/2014
 ******************************************************************************/
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
