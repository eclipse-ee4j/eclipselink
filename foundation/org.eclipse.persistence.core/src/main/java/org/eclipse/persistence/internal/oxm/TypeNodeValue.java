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
package org.eclipse.persistence.internal.oxm;

import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;

public class TypeNodeValue extends NodeValue {

    @Override
    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object objectValue, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        return false;
    }

    @Override
    public boolean isMarshalNodeValue() {
        return false;
    }

    @Override
    public void attribute(UnmarshalRecord unmarshalRecord, String namespaceURI, String localName, String value) {
        //assume this is being called for xsi:type field
        if (value != null) {
            String namespace = null;
            int colonIndex = value.indexOf(Constants.COLON);
            if (colonIndex > -1) {
                String prefix = value.substring(0, colonIndex);
                namespace = unmarshalRecord.resolveNamespacePrefix(prefix);
                value = value.substring(colonIndex + 1);
            }
            unmarshalRecord.setTypeQName(new QName(namespace, value));
        }
    }

    @Override
    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        return false;
    }

}
