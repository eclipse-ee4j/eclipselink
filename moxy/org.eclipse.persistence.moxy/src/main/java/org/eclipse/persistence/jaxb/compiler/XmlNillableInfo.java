/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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
