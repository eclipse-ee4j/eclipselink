/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionsAdd {

    @XmlJavaTypeAdapter(MyAdapter.class)
    @XmlElementRef
    public List<String> txnType;

    public boolean equals(Object o) {
        TransactionsAdd ta;
        try {
            ta = (TransactionsAdd) o;
        } catch (ClassCastException cce) {
            return false;
        }
        if (ta.txnType == null) {
            return false;
        }
        if (txnType.size() != ta.txnType.size()) {
            return false;
        }
        for (int i=0; i<txnType.size(); i++) {
            if (!(txnType.get(i).equals(ta.txnType.get(i)))) {
                return false;
            }
        }
        return true;
    }
}
