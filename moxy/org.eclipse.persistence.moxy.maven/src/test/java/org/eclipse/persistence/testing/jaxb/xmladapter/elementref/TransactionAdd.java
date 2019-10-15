/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - December 17/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.elementref;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionAdd {

    @XmlJavaTypeAdapter(MyAdapter.class)
    @XmlElementRef
    public String txnType;

    public boolean equals(Object o) {
        TransactionAdd ta;
        try {
            ta = (TransactionAdd) o;
        } catch (ClassCastException cce) {
            return false;
        }
        return this.txnType.equals(ta.txnType);
    }
}
