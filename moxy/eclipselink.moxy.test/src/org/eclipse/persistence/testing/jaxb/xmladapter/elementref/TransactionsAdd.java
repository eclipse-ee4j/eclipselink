/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - December 17/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.elementref;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
