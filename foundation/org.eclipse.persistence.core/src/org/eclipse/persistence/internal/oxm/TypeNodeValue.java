/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.oxm;

import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;

public class TypeNodeValue extends NodeValue {

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object objectValue, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        return false;
    }

    @Override
    public boolean isMarshalNodeValue() {
        return false;
    }

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
