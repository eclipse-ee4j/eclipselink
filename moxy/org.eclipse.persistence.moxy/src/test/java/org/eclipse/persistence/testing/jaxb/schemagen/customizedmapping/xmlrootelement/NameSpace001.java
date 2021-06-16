/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlrootelement;

import jakarta.xml.bind.annotation.*;

/**
 * Mapping:  Class to Element Declaration
 * if @XmlRootElement.namespace() is not ##default, then
 * the XML {target namespace} is equal to @XmlRootElement.namespace()
 */
@XmlType(propOrder={"b001", "bt01", "d001", "f001", "i001", "l001", "s001", "sh01"})
@XmlRootElement(namespace="http://www.example.com/NameSpace")
public class NameSpace001 {

    public String  s001;
    public boolean b001;
    public int     i001;
    public byte    bt01;
    public short   sh01;
    public long    l001;
    public float   f001;
    public double  d001;

    public NameSpace001() {}
}
